package de.boetzmeyer.observerengine;

/**
 * @author timbotzmeyer <br>
 *         An observer is going to be invoked by the observer engine, if the
 *         state, which life-cycle is tracked, changes. If the engine detects a
 *         modification of that state, it searches, if an object of a class with
 *         the name that is returned from the method 'getActionClass' is
 *         currently registered at the engine. The action class has to be of
 *         type {@link IStateObserver}. If this is true, the engine invokes the
 *         callback method 'stateChanged' on the registered action class object.
 */
public interface IObserver extends IRecordable {

	/**
	 * get the internal ID of the observed state
	 * 
	 * @return get the internal ID of the observed state
	 */
	long getState();

	/**
	 * get the internal ID of the observed state group
	 * 
	 * @return the internal ID of the observed state group
	 */
	long getStateGroup();

	/**
	 * get the name of the action class, that has to implement the
	 * {@link IStateObserver} interface.
	 * 
	 * @return the name of the action class
	 */
	String getActionClass();

	/**
	 * Get a description for which purpose this observer has been introduced.
	 * 
	 * @return the observer description
	 */
	String getDescription();
}
