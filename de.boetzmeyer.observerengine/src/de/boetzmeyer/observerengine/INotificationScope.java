package de.boetzmeyer.observerengine;

public interface INotificationScope extends IRecordable {
	long getObserver();

	long getModule();
}
