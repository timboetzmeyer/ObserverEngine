package de.boetzmeyer.observerengine;

import java.io.Serializable;

interface IRecordable extends Serializable {
	long getPrimaryKey();

	long getServerReplicationVersion();

	boolean setServerReplicationVersion(final long lVersion);

	boolean save();

	String differences(final IRecordable recordable);

	boolean sameContent(final IRecordable recordable);
}
