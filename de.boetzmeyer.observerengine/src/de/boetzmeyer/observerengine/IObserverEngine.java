package de.boetzmeyer.observerengine;

interface IObserverEngine {

	boolean addStateChangeListener(final IState inState, final IStateObserver inStateObserver);

	boolean removeStateChangeListener(final IState inState, final IStateObserver inStateObserver);

	long update(final IState inState, final String inValue);

	String getValue(final IState inState);

}