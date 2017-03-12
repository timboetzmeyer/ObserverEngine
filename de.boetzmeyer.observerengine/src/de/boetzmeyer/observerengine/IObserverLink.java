package de.boetzmeyer.observerengine;

interface IObserverLink extends IRecordable {
	long getSource();

	long getDestination();
}
