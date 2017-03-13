package de.boetzmeyer.observerengine;

import java.util.Date;

/**
 * @author timbotzmeyer
 * 
 *         If a state changes the value while its lifetime, an object of type
 *         {@link IStateChange} is generated and dispatched to the observers of
 *         this state.
 *         
 *         @see {@link IStateObserver}
 *
 */
public interface IStateChange extends IRecordable {
	
	/**
	 * Get the internal ID of the state.
	 * 
	 * @return the internal ID of the state
	 */
	long getState();

	/**
	 * Get the new value of the state.
	 * 
	 * @return the new value of the state
	 */
	String getStateValue();

	/**
	 * Get the time of the state change.
	 * 
	 * @return the time of the state change
	 */
	Date getChangeTime();
}
