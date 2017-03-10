package de.boetzmeyer.observerengine;

import java.util.Date;
import java.util.List;

/**
 * @author Tim
 * 
 *         The administration interface allows to start and stop the engine. It
 *         also allows the client to get statistical information about the model
 *         of the engine.
 *
 */
public interface IObserverEngineAdmin extends IObserverEngine {

	/**
	 * Starts the observer engine.
	 */
	void start();

	/**
	 * Stops the observer engine.
	 */
	void stop();

	/**
	 * Get a list of modified states since the given date.
	 * 
	 * @param inLastQuery
	 *            the date to compare the changes
	 * @return a list of modified dates since the given date
	 */
	List<IState> getModifiedStates(final Date inLastQuery);

	/**
	 * Get all states that are known to the engine.
	 * 
	 * @return all states that are known to the engine
	 */
	List<IState> getStates();

	/**
	 * Get all modules that are known to the engine.
	 * 
	 * @return all modules that are known to the engine
	 */
	List<IModule> getModules();

	/**
	 * Get all observers of a state that are known to the engine.
	 * 
	 * @return all observers of a state that are known to the engine
	 */
	List<IObserver> getStateObservers(final IState inState);

	/**
	 * Get all observers of a state in a module that are known to the engine.
	 * 
	 * @return all observers of a state in a module that are known to the engine
	 */
	List<IObserver> getStateObservers(final IState inState, final IModule inModule);

	/**
	 * Get all observers of a module that are known to the engine.
	 * 
	 * @return all observers of a module that are known to the engine
	 */
	List<IObserver> getModuleObservers(final IModule inModule);

}
