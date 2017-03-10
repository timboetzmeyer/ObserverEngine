package de.boetzmeyer.observerengine;

public interface IObserverLink extends IRecordable {
	long getSource();

	long getDestination();
}
