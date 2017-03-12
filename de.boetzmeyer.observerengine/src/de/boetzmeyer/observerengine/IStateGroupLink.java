package de.boetzmeyer.observerengine;

interface IStateGroupLink extends IRecordable {
	long getSource();

	long getDestination();
}
