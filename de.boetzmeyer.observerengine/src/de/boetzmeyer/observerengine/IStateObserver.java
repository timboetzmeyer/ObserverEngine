package de.boetzmeyer.observerengine;

/**
 * @author timbotzmeyer
 * 
 *         The interface that tracks the changes of a state while its lifetime.
 *         Classes, that are interested in changes of this state, have to
 *         implement it.
 *
 */
public interface IStateObserver {
	
	/**
	 * This method is invoked on every registered observer by the observer
	 * engine, if the observed state changes.
	 * 
	 * @param inStateChange
	 *            the modified state
	 */
	void stateChanged(final IStateChange inStateChange);
}
