package de.boetzmeyer.observerengine;

/**
 * @author Tim <br>
 *         <br>
 *         This is interface for the interacting of a client application with
 *         the observer engine. As the name says, the observer engine implements
 *         the good old observer pattern. The advantage of the observer pattern
 *         is the inversion of control principle as it is used in most
 *         frameworks and also application code in these and former days. The
 *         easy way of registering an observer at an object to follow the
 *         changes of this object in its life cycle has made it become one of
 *         the most often used software design patterns. <br>
 *         <br>
 *         A problem that often arises when the amount of project code grows is
 *         the increasing complexity of the communication between the different
 *         objects that listen to their state changes. The static compile-time
 *         view of the code does not show the complexity that is existent at
 *         runtime. Message cycles, that exist at runtime are invisible at the
 *         time, the code is created. The freedom, the observer pattern offers,
 *         is losing a lot its benefits, when the project grows and grows. <br>
 *         <br>
 *         At this point, the observer engine comes into play. The unlimited
 *         freedom of listening to every reachable object ends at the borders of
 *         the configured observer model. The observer model is designed at
 *         compile-time. The well-known addListener semantics is more
 *         restrictive than it is in the standard observer pattern. An object is
 *         only allowed to register itself as the listener of another object (or
 *         state) at runtime, if it has been configured as an observer of that
 *         object (or state) in the observer model at compile-time. Observers of
 *         a state are organized in the way that each observer notifies only a
 *         modular segment of the whole application. As a best practice, a big
 *         software is build of feature oriented modules or plug-ins. One
 *         observer should notify only one module of the application. Two
 *         different observers should not notify the same module from two
 *         different code paths. In the best case, this is not efficient. In a
 *         worse case, this can lead to real problems.<br>
 *         <br>
 *         Within the observer engine, we are talking of states, modules and
 *         observers: An observer can be listening to a state. The observer is
 *         going to be invoked by the observer engine, if the state has been
 *         changed. Observers are associated with one or more modules, they keep
 *         in-sync with the current application state.
 *
 */
public interface IObserverEngine {

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

	/**
	 * Get the state with the given name.
	 * 
	 * @param inStateName
	 *            the name of the state
	 * @return the state with the given name
	 */
	IState getState(final String inStateName);

	/**
	 * Get the state with the given internal ID.
	 * 
	 * @param inStateID
	 *            the internalID of the state
	 * @return the state with the given internal ID
	 */
	IState getState(final long inStateID);
}