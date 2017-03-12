package de.boetzmeyer.observerengine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

final class ObserverEngineImpl implements IObserverEngineAdmin {
	private final ISource model;
	private final Map<String, State> stateCache = new HashMap<String, State>();
	private final ObserverNotifier observerNotifier;

	public ObserverEngineImpl(final String observerModelDir, final int inMaxHistoryEntries, final int inUpdateIntervalInMillis,
			final int inCleanupCounter) {
		Settings.setLocaleDatabaseDir(observerModelDir);
		model = ServerFactory.create();
		for (State state : model.listState()) {
			stateCache.put(state.getStateName(), state);
		}
		observerNotifier = new ObserverNotifier(model, inMaxHistoryEntries, inUpdateIntervalInMillis, inCleanupCounter);
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
		observerNotifier.start();
	}

	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#stop()
	 */
	@Override
	public void stop() {
		observerNotifier.stop();
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
			observerNotifier.notifyChange(stateChange, observerCalls, alreadyCalled);
		}
		return observerCalls.get();
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

	/* (non-Javadoc)
	 * @see de.boetzmeyer.observerengine.IObserverEngine#getModifiedStates(java.util.Date)
	 */
	@Override
	public List<IState> getModifiedStates(final Date inLastQuery) {
		final List<IState> modifiedStates = new ArrayList<IState>();
		final List<StateChange> changesSince = observerNotifier.getChangesSince(inLastQuery);
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
