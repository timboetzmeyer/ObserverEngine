package de.boetzmeyer.observerengine;

/**
 * @author timbotzmeyer
 *
 *         A module is a logical unit or compilation unit of an application.
 *         Within the observer model, they are used to group observers into
 *         logical units. A module defines a scope of reachable objects. As a
 *         best practice, one state should only have one observer per module.
 *         This state observer uses only the module scope for dispatching state
 *         changes within the module.
 */
public interface IModule extends IRecordable {

	/**
	 * Get the name of the module or plug-in
	 * 
	 * @return the name of the module or plug-in
	 */
	String getModuleName();

	/**
	 * Get the description of the module or plug-in
	 * 
	 * @return the description of the module or plug-in
	 */
	String getDescription();
}
