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

final class ObserverNotifier implements Runnable {
	private final ISource model;
	private final int maxHistoryEntries;
	private final int updateIntervalInMillis;
	private final int cleanupCounter;
	private final ExecutorService executorService;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final AtomicInteger updateCounter = new AtomicInteger(0);
	private final Map<State, Set<IStateObserver>> stateObservers = new HashMap<State, Set<IStateObserver>>();
	private Date lastQueryTime;

	public ObserverNotifier(final ISource inModel, final int inMaxHistoryEntries,
			final int inUpdateIntervalInMillis, final int inCleanupCounter) {
		model = inModel;
		maxHistoryEntries = inMaxHistoryEntries;
		updateIntervalInMillis = inUpdateIntervalInMillis;
		cleanupCounter = inCleanupCounter;
		executorService = Executors.newSingleThreadExecutor();
		lastQueryTime = new Date();
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
	
	public void notifyChange(final StateChange inStateChange, final AtomicLong inObserverCalls,
			final Set<String> inAlreadyCalled) {
		if (inStateChange != null) {
			final List<Observer> stateObservers = model.referencesObserverByState(inStateChange.getPrimaryKey());
			for (Observer observer : stateObservers) {
				notifyObserver(observer, inStateChange, inObserverCalls, inAlreadyCalled);
			}
		}
	}


	public List<StateChange> getChangesSince(final Date inLastQuery) {
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

	public void start() {
		running.set(true);
		executorService.execute(this);
	}

	public void stop() {
		running.set(false);
		executorService.shutdown();
	}

	public boolean addStateChangeListener(final IStateObserver inStateObserver, final State registeredState) {
		Set<IStateObserver> observers = stateObservers.get(registeredState);
		if (observers == null) {
			observers = new HashSet<IStateObserver>();
			stateObservers.put(registeredState, observers);
		}
		return observers.add(inStateObserver);
	}

	public boolean removeStateChangeListener(final IStateObserver inStateObserver, final State registeredState) {
		Set<IStateObserver> observers = stateObservers.get(registeredState);
		if (observers == null) {
			observers = new HashSet<IStateObserver>();
			stateObservers.put(registeredState, observers);
		}
		return observers.remove(inStateObserver);
	}

	public void notifyObserver(final Observer observer, final StateChange inStateChange,
			final AtomicLong inObserverCalls, final Set<String> inAlreadyCalled) {
		if (observer != null) {
			try {
				final String observerClassName = observer.getActionClass();
				invokeObserver(observerClassName, inStateChange, inAlreadyCalled);
				final List<ObserverLink> dependentObserverLinks = model
						.referencesObserverLinkByDestination(observer.getPrimaryKey());
				for (ObserverLink observerLink : dependentObserverLinks) {
					final Observer dependentObserver = observerLink.getDestinationRef();
					notifyObserver(dependentObserver, inStateChange, inObserverCalls, inAlreadyCalled);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void invokeObserver(final String inObserverClassName, final StateChange inStateChange,
			final Set<String> inAlreadyCalled) {
		if (inObserverClassName.trim().isEmpty() == false) {
			if (inAlreadyCalled.contains(inObserverClassName.trim())) {
				System.err.println(inObserverClassName.trim() + " was already called.");
			} else {
				inAlreadyCalled.add(inObserverClassName.trim());
			}
			final State state = inStateChange.getStateRef();
			final IStateObserver stateObserver = getStateObserver(state, inObserverClassName.trim());
			if (stateObserver != null) {
				try {
					stateObserver.stateChanged(inStateChange);
				} catch (final Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	private IStateObserver getStateObserver(final IState inState, final String inObserverClass) {
		final Set<IStateObserver> observers = this.stateObservers.get(inState);
		if (observers != null) {
			for (IStateObserver stateObserver : observers) {
				if (inObserverClass.equals(stateObserver.getClass().getName())) {
					return stateObserver;
				}
			}
		}
		return null;
	}

}
