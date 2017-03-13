package de.boetzmeyer.observerengine;

/**
 * @author timbotzmeyer
 *
 *         A state is a named object which can be observed while the its whole
 *         lifetime. Every change of the state is published to the registered
 *         observers by the engine.
 */
public interface IState extends IRecordable {

	/**
	 * Get the name of the state.
	 * 
	 * @return the name of the state
	 */
	String getStateName();

	/**
	 * Get the default value of the state.
	 * 
	 * @return the default value of the state
	 */
	String getDefaultValue();

}
