package de.boetzmeyer.observerengine;

public interface IObserver extends IRecordable {
	long getState();

	long getStateGroup();

	String getActionClass();

	String getDescription();
}
