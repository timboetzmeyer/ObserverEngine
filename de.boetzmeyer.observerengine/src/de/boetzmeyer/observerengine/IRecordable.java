package de.boetzmeyer.observerengine;

import java.io.Serializable;

interface IRecordable extends Serializable {
	long getPrimaryKey();
}
