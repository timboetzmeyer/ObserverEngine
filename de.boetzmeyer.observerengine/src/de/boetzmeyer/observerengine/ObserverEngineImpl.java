package de.boetzmeyer.observerengine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

final class ObserverEngineImpl implements Runnable, IObserverEngineAdmin {
	private final ISource model;
	private final int maxHistoryEntries;
	private final int updateIntervalInMillis;
	private final int cleanupCounter;
	private final ExecutorService executorService;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final AtomicInteger updateCounter = new AtomicInteger(0);
	private final Map<String, State> stateCache = new HashMap<String, State>();
	private final ObserverNotifier observerNotifier;
	private Date lastQueryTime;

	public ObserverEngineImpl(final String observerModelDir, final int inMaxHistoryEntries, final int inUpdateIntervalInMillis,
			final int inCleanupCounter) {
		Settings.setLocaleDatabaseDir(observerModelDir);
		model = ServerFactory.create();
		maxHistoryEntries = inMaxHistoryEntries;
		updateIntervalInMillis = inUpdateIntervalInMillis;
		cleanupCounter = inCleanupCounter;
		executorService = Executors.newSingleThreadExecutor();
		lastQueryTime = new Date();
		final List<State> modelStates = model.listState();
		for (State state : modelStates) {
			stateCache.put(state.getStateName(), state);
		}
		observerNotifier = new ObserverNotifier(model);
	}

	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#addStateChangeListener(de.boetzmeyer.observerengine.State, de.boetzmeyer.observerengine.IStateObserver)
	 */
	@Override
	public boolean addStateChangeListener(final String inState, final IStateObserver inStateObserver) {
		if ((inState == null) || (inStateObserver != null)) {
			return false;
		}
		final State registeredState = this.stateCache.get(inState);
		if (registeredState == null) {
			return false;
		}
		return observerNotifier.addStateChangeListener(inStateObserver, registeredState);
	}

	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#removeStateChangeListener(de.boetzmeyer.observerengine.State, de.boetzmeyer.observerengine.IStateObserver)
	 */
	@Override
	public boolean removeStateChangeListener(final String inState, final IStateObserver inStateObserver) {
		if ((inState == null) || (inStateObserver != null)) {
			return false;
		}
		final State registeredState = this.stateCache.get(inState);
		if (registeredState == null) {
			return false;
		}
		return observerNotifier.removeStateChangeListener(inStateObserver, registeredState);
	}

	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#start()
	 */
	@Override
	public void start() {
		running.set(true);
		executorService.execute(this);
	}

	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#stop()
	 */
	@Override
	public void stop() {
		running.set(false);
		executorService.shutdown();
	}

	@Override
	public void run() {
		while (running.get()) {
			final List<StateChange> stateChanges = this.getChangesSince(lastQueryTime);
			lastQueryTime = new Date();
			for (StateChange stateChange : stateChanges) {
				notifyChange(stateChange, new AtomicLong(0), new HashSet<String>());
			}
			try {
				Thread.sleep(updateIntervalInMillis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			final boolean cleanup = (updateCounter.incrementAndGet() % cleanupCounter) == 0;
			if (cleanup) {
				cleanupHistory();
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#update(de.boetzmeyer.observerengine.IState, java.lang.String)
	 */
	@Override
	public long update(final String inState, final String inValue) {
		final AtomicLong observerCalls = new AtomicLong(0);
		final Set<String> alreadyCalled = new HashSet<String>();
		if (inState == null) {
			throw new IllegalArgumentException("Input state must not be null");
		}
		final State state = stateCache.get(inState);
		if (state != null) {
			final StateChange stateChange = StateChange.generate();
			stateChange.setChangeTime(new Date());
			stateChange.setState(state.getPrimaryKey());
			stateChange.setStateValue(inValue);
			stateChange.save();
			notifyChange(stateChange, observerCalls, alreadyCalled);
		}
		return observerCalls.get();
	}

	private void notifyChange(final StateChange inStateChange, final AtomicLong inObserverCalls,
			final Set<String> inAlreadyCalled) {
		if (inStateChange != null) {
			final List<Observer> stateObservers = model.referencesObserverByState(inStateChange.getPrimaryKey());
			for (Observer observer : stateObservers) {
				observerNotifier.notifyObserver(observer, inStateChange, inObserverCalls, inAlreadyCalled);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#getValue(de.boetzmeyer.observerengine.IState)
	 */
	@Override
	public String getValue(final String inStateName) {
		final State state = stateCache.get(inStateName);
		if (state == null) {
			throw new IllegalArgumentException("Input state must not be null");
		}
		final List<StateChange> changes = model.referencesStateChangeByState(state.getPrimaryKey());
		if (changes.size() > 0) {
			StateChange.sortByChangeTime(changes, false);
			final StateChange lastChange = changes.get(0);
			return lastChange.getStateValue();
		} else {
			return state.getDefaultValue();
		}
	}

	private void cleanupHistory() {
		final List<State> allStates = model.listState();
		for (State state : allStates) {
			final List<StateChange> changes = model.referencesStateChangeByState(state.getPrimaryKey());
			if (changes.size() > maxHistoryEntries) {
				StateChange.sortByChangeTime(changes, false);
				for (int i = maxHistoryEntries; i < changes.size(); i++) {
					final StateChange oldStateChange = changes.get(i);
					if (oldStateChange != null) {
						model.deleteStateChange(oldStateChange.getPrimaryKey());
					}
				}
			}
		}
	}

	private List<StateChange> getChangesSince(final Date inLastQuery) {
		final List<StateChange> modifiedStates = new ArrayList<StateChange>();
		final List<State> allStates = model.listState();
		for (State state : allStates) {
			final List<StateChange> changes = model.referencesStateChangeByState(state.getPrimaryKey());
			if (changes.size() > 0) {
				StateChange.sortByChangeTime(changes, false);
				final StateChange stateChange = changes.get(0);
				if (stateChange.getChangeTime().after(inLastQuery)) {
					modifiedStates.add(stateChange);
				}
			}
		}
		return modifiedStates;
	}

	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#getModifiedStates(java.util.Date)
	 */
	@Override
	public List<IState> getModifiedStates(final Date inLastQuery) {
		final List<IState> modifiedStates = new ArrayList<IState>();
		final List<StateChange> changesSince = getChangesSince(inLastQuery);
		for (StateChange stateChange : changesSince) {
			final IState state = stateChange.getStateRef();
			if (state != null) {
				modifiedStates.add(state);
			}
		}
		return modifiedStates;
	}
	
	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#getModuleObservers(de.boetzmeyer.observerengine.IModule)
	 */
	@Override
	public List<IObserver> getModuleObservers(final IModule inModule) {
		final Set<IObserver> observers = new HashSet<IObserver>();
		final List<NotificationScope> scopes = model.referencesNotificationScopeByModule(inModule.getPrimaryKey());
		for (NotificationScope notificationScope : scopes) {
			final Observer o = notificationScope.getObserverRef();
			if (o != null) {
				observers.add(o);
			}
		}
		return new ArrayList<IObserver>();
	}

	@Override
	public List<IState> getStates() {
		final List<IState> states = new ArrayList<IState>();
		for (State state : model.listState()) {
			states.add(state);
		}
		return states;
	}

	@Override
	public List<IModule> getModules() {
		final List<IModule> modules = new ArrayList<IModule>();
		for (Module module : model.listModule()) {
			modules.add(module);
		}
		return modules;
	}

	@Override
	public List<IObserver> getStateObservers(final IState inState) {
		final List<IObserver> observers = new ArrayList<IObserver>();
		if (inState != null) {
			for (Observer observer : model.referencesObserverByState(inState.getPrimaryKey())) {
				observers.add(observer);
			}
		}
		return observers;
	}

	@Override
	public List<IObserver> getStateObservers(final IState inState, final IModule inModule) {
		final List<IObserver> observers = new ArrayList<IObserver>();
		if (inState != null) {
			for (Observer observer : model.referencesObserverByState(inState.getPrimaryKey())) {
				if (inModule != null) {
					final List<NotificationScope> scopes = model.referencesNotificationScopeByModule(inModule.getPrimaryKey());
					for (NotificationScope notificationScope : scopes) {
						final Module module = notificationScope.getModuleRef();
						if (inModule.equals(module)) {
							observers.add(observer);
						}
					}
				} else {
					observers.add(observer);
				}
			}
		}
		return observers;
	}

	@Override
	public List<IObserver> getObservers() {
		final List<IObserver> observers = new ArrayList<IObserver>();
		for (IObserver observer : model.listObserver()) {
			observers.add(observer);
		}
		return observers;
	}
}
