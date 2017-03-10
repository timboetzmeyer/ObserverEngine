package de.boetzmeyer.observerengine;

import java.util.Date;

public interface IStateChange extends IRecordable {
	long getState();

	String getStateValue();

	Date getChangeTime();
}
