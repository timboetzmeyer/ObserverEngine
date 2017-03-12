package de.boetzmeyer.observerengine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

final class ObserverNotifier {
	private final ISource model;
	private final Map<State, Set<IStateObserver>> stateObservers = new HashMap<State, Set<IStateObserver>>();
	
	public ObserverNotifier(final ISource inModel) {
		model = inModel;
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
