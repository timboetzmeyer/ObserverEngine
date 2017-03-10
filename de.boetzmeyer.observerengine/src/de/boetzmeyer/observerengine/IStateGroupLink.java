package de.boetzmeyer.observerengine;

public interface IStateGroupLink extends IRecordable {
	long getSource();

	long getDestination();
}
