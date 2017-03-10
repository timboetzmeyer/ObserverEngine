package de.boetzmeyer.observerengine;

public interface IState extends IRecordable {
	String getStateName();

	String getDefaultValue();

	long getStateType();
}
