package de.boetzmeyer.observerengine;

interface INotificationScope extends IRecordable {
	long getObserver();

	long getModule();
}
