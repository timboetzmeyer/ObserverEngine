package de.boetzmeyer.observerengine;

/**
 * @author Tim
 * 
 *         This is interface for the interacting of a client application with
 *         the observer engine.
 *
 */
interface IObserverEngine {

	/**
	 * Method for adding an observer of a state that is known in the engine.
	 * 
	 * @param inStateID
	 *            the state the observer is interested in
	 * @param inStateObserver
	 *            the implementation of the {@link IStateObserver} interface
	 *            with the method stateChanged that is invoked on changes of the
	 *            state
	 * @return true, if the observer could be added to the engine
	 */
	boolean addStateChangeListener(final String inStateID, final IStateObserver inStateObserver);

	/**
	 * Method for removing an observer of a state that is known in the engine.
	 * 
	 * @param inStateID
	 *            the state the observer is interested in
	 * @param inStateObserver
	 *            the implementation of the {@link IStateObserver} interface
	 *            with the method stateChanged that is invoked on changes of the
	 *            state
	 * @return true, if the observer could be removed from the engine
	 */
	boolean removeStateChangeListener(final String inStateID, final IStateObserver inStateObserver);

	/**
	 * Method to update a state in the engine.
	 * 
	 * @param inState
	 *            the state that has to be known in the engine
	 * @param inValue
	 *            the new value of the state that is going to be published by
	 *            the engine
	 * @return the number of direct observer calls for this state
	 */
	long update(final String inState, final String inValue);

	/**
	 * Get the current value of the state.
	 * 
	 * @param inState
	 *            the state the client application is interested in
	 * @return the current value of the state or 'null' if the state does not
	 *         exist in the engine
	 */
	String getValue(final String inState);

}