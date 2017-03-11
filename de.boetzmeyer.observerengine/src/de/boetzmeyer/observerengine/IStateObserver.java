package de.boetzmeyer.observerengine;

public interface IStateObserver {
	void stateChanged(final IStateChange inStateChange);
}
