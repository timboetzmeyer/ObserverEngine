package de.boetzmeyer.observerengine;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class StateGroupLink implements IStateGroupLink {
	private static final long DEFAULT_PRIMARYKEY = 0L;
	private static final long DEFAULT_SERVERREPLICATIONVERSION = 0L;
	private static final long DEFAULT_SOURCE = 0L;
	private static final long DEFAULT_DESTINATION = 0L;

	/**
	 * 
	 * the table attribute names
	 */
	public static final String[] ATTRIBUTE_NAMES = new String[] { "Source", "Destination" };

	/**
	 * 
	 * the table header column names
	 */
	public static final String[] TABLE_HEADER = new String[] { "State Group", "Sub Group" };

	private boolean m_bDirty = false;
	private CommunicationModel m_db;
	/**
	 * 
	 * the primary Key
	 */
	private long m_PrimaryKey = DEFAULT_PRIMARYKEY;

	/**
	 * 
	 * the version number of this record on the server
	 */
	private long m_ServerReplicationVersion = DEFAULT_SERVERREPLICATIONVERSION;

	/**
	 * 
	 * 
	 */
	private long m_Source = DEFAULT_SOURCE;

	/**
	 * 
	 * 
	 */
	private long m_Destination = DEFAULT_DESTINATION;

	public synchronized final boolean isDirty() {
		return this.m_bDirty;
	}

	public synchronized final void setDirty(final boolean bDirty) {
		this.m_bDirty = bDirty;
	}

	public synchronized final boolean isValid() {
		if (this.getSource() == 0L) {
			return false;
		}
		if (this.getDestination() == 0L) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * returns the reason why the entity is in an invalid state
	 */
	public synchronized final String getErrorDescription() {
		final StringBuilder s = new StringBuilder();
		if (this.getSource() == 0L) {
			s.append("  State Group fehlt!");
		}
		if (this.getDestination() == 0L) {
			s.append("  Sub Group fehlt!");
		}
		return s.toString();
	}

	public final CommunicationModel getDB() {
		return this.m_db;
	}

	public final synchronized boolean sameContent(final IRecordable theOther) {
		if (!(theOther instanceof StateGroupLink)) {
			return false;
		}
		final StateGroupLink other = (StateGroupLink) theOther;
		if (null == other)
			return false;

		if (other.m_PrimaryKey != this.m_PrimaryKey)
			return false;

		if (other.m_ServerReplicationVersion != this.m_ServerReplicationVersion)
			return false;

		if (other.m_Source != this.m_Source)
			return false;

		if (other.m_Destination != this.m_Destination)
			return false;

		return true;
	}

	private void increaseServerReplicationVersion() {
		this.setServerReplicationVersion(1 + this.getServerReplicationVersion());
	}

	public final void overwrite(final StateGroupLink source) {
		this.overwrite(source, false);
	}

	public final void overwrite(final StateGroupLink source, final boolean bIncreaseVersion) {
		if (source != null) {
			if (bIncreaseVersion) {
				this.increaseServerReplicationVersion();
			}

			this.setSource(source.getSource(), true);
			this.setDestination(source.getDestination(), true);
		}
	}

	private StateGroupLink() {
		this.m_db = null;
	}

	public final StateGroupLink copy() {
		final StateGroupLink recordCopy = new StateGroupLink();

		synchronized (this) {
			recordCopy.m_PrimaryKey = this.m_PrimaryKey;
			recordCopy.m_ServerReplicationVersion = this.m_ServerReplicationVersion;
			recordCopy.m_Source = this.m_Source;
			recordCopy.m_Destination = this.m_Destination;
		}

		return recordCopy;
	}

	public static final StateGroupLink createBySource(final long lID) {
		final StateGroupLink record = generate();

		if (record != null) {
			record.m_Source = lID;
		}

		return record;
	}

	public static final StateGroupLink createByDestination(final long lID) {
		final StateGroupLink record = generate();

		if (record != null) {
			record.m_Destination = lID;
		}

		return record;
	}

	private StateGroupLink(final CommunicationModel db, final boolean bRecursive, final boolean bNewID) {
		this.m_db = db;

		if (bNewID) {
			this.m_PrimaryKey = IDGenerator.createPrimaryKey();
		}

		if (bRecursive) {
			final StateGroup refSource = StateGroup.createNewTree(db);

			if (refSource != null) {
				this.m_Source = refSource.getPrimaryKey();
			}

			final StateGroup refDestination = StateGroup.createNewTree(db);

			if (refDestination != null) {
				this.m_Destination = refDestination.getPrimaryKey();
			}

		}

		if (this.m_db != null) {
			this.m_db.addStateGroupLink(this);
		}
	}

	public static final StateGroupLink createNewTree(final CommunicationModel db) {
		return new StateGroupLink(db, true, true);
	}

	public static final StateGroupLink createNew(final CommunicationModel db) {
		return new StateGroupLink(db, false, true);
	}

	public static final StateGroupLink createNew(final CommunicationModel db, final boolean bNewID) {
		return new StateGroupLink(db, false, bNewID);
	}

	public static final StateGroupLink create(final CommunicationModel db, final boolean bRecursive) {
		return new StateGroupLink(db, bRecursive, true);
	}

	public static final StateGroupLink generate() {
		StateGroupLink record = new StateGroupLink();
		record.m_PrimaryKey = IDGenerator.createPrimaryKey();
		return record;
	}

	static final StateGroupLink generateWithoutKey() {
		return new StateGroupLink();
	}

	public final synchronized long getPrimaryKey() {
		return this.m_PrimaryKey;
	}

	/**
	 * sets the primary key
	 */
	final void setPrimaryKey(final long value) {
		this.m_PrimaryKey = value;
	}

	public final synchronized long getServerReplicationVersion() {
		return this.m_ServerReplicationVersion;
	}

	/**
	 * 
	 * the version number of this record on the server
	 */
	public final boolean setServerReplicationVersion(long value) {
		return this.setServerReplicationVersion(value, false);
	}

	public final synchronized boolean setServerReplicationVersion(long value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_ServerReplicationVersion = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final synchronized long getSource() {
		return this.m_Source;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setSource(long value) {
		return this.setSource(value, false);
	}

	public final synchronized boolean setSource(long value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_Source = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final synchronized long getDestination() {
		return this.m_Destination;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setDestination(long value) {
		return this.setDestination(value, false);
	}

	public final synchronized boolean setDestination(long value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_Destination = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final StateGroup getSourceRef() {
		StateGroup record = null;
		final ISource server = ServerFactory.create();
		if (server != null) {
			record = server.findByIDStateGroup(this.getSource());
		}
		return record;
	}

	public final StateGroup getDestinationRef() {
		StateGroup record = null;
		final ISource server = ServerFactory.create();
		if (server != null) {
			record = server.findByIDStateGroup(this.getDestination());
		}
		return record;
	}

	public final boolean equals(final Object obj) {
		if (obj instanceof StateGroupLink) {
			return ((StateGroupLink) obj).getPrimaryKey() == this.getPrimaryKey();
		}
		return false;
	}

	public final int hashCode() {
		return (int) (this.m_PrimaryKey % 1000);
	}

	public final String toString() {
		final ISource server = ServerFactory.create();

		final StringBuilder strBuilder = new StringBuilder();

		final StateGroup firstRecord = server.findByIDStateGroup(this.m_Source);

		if (firstRecord != null) {
			strBuilder.append(firstRecord.toString());
		}

		strBuilder.append(" -> ");

		final StateGroup secondRecord = server.findByIDStateGroup(this.m_Destination);

		if (secondRecord != null) {
			strBuilder.append(secondRecord.toString());
		}

		return strBuilder.toString();
	}

	public static final StateGroupLink load(final Node node, final CommunicationModel db) {
		final StateGroupLink stategrouplink = new StateGroupLink();
		stategrouplink.m_db = db;
		if (node != null) {
			final NodeList listElementChildren = node.getChildNodes();
			final int nNodeCount = listElementChildren.getLength();
			for (int i = 0; i < nNodeCount; i++) {
				final Node nodeChild = listElementChildren.item(i);
				if (XMLParser.isElementNode(nodeChild)) {
					final String strNodeName = nodeChild.getNodeName();
					if (strNodeName.equals("PrimaryKey")) {
						stategrouplink.m_PrimaryKey = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("ServerReplicationVersion")) {
						stategrouplink.m_ServerReplicationVersion = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("Source")) {
						stategrouplink.m_Source = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("Destination")) {
						stategrouplink.m_Destination = XMLParser.loadLong(nodeChild);
						continue;
					}

				}
			}
		}

		return stategrouplink;
	}

	public final String toXML() {
		final StringBuffer strBuf = new StringBuffer();
		synchronized (this) {
			strBuf.append("<RECORD>");
			strBuf.append("<PrimaryKey>");
			strBuf.append(Long.toString(this.m_PrimaryKey));
			strBuf.append("</PrimaryKey>");

			strBuf.append("<ServerReplicationVersion>");
			strBuf.append(Long.toString(this.m_ServerReplicationVersion));
			strBuf.append("</ServerReplicationVersion>");

			strBuf.append("<Source>");
			strBuf.append(Long.toString(this.m_Source));
			strBuf.append("</Source>");

			strBuf.append("<Destination>");
			strBuf.append(Long.toString(this.m_Destination));
			strBuf.append("</Destination>");

			strBuf.append("</RECORD>");
		}

		return strBuf.toString();
	}

	public final String differences(final IRecordable theOther) {
		if (!(theOther instanceof StateGroupLink)) {
			return "";
		}
		final StateGroupLink other = (StateGroupLink) theOther;
		final StringBuilder s = new StringBuilder();
		if (this.m_Source != other.m_Source) {
			s.append("State Group:  ");
			final StateGroup refThis = this.getSourceRef();
			if (refThis != null) {
				s.append(refThis.toString());
			} else {
				s.append("---");
			}
			s.append(" --> ");
			final StateGroup refOther = other.getSourceRef();
			if (refOther != null) {
				s.append(refOther.toString());
			} else {
				s.append("---");
			}
			s.append("\r\n");
		}
		if (this.m_Destination != other.m_Destination) {
			s.append("Sub Group:  ");
			final StateGroup refThis = this.getDestinationRef();
			if (refThis != null) {
				s.append(refThis.toString());
			} else {
				s.append("---");
			}
			s.append(" --> ");
			final StateGroup refOther = other.getDestinationRef();
			if (refOther != null) {
				s.append(refOther.toString());
			} else {
				s.append("---");
			}
			s.append("\r\n");
		}
		return s.toString();
	}

	public static final StateGroup getCycle(final List<StateGroupLink> listDependencies,
			final List<StateGroup> listRecords, final StateGroupLink newDependency) {
		StateGroup cycleRecord = null;

		final Set<StateGroupLink> setDependenciesCopy;
		synchronized (listDependencies) {
			setDependenciesCopy = new HashSet<StateGroupLink>(listDependencies);
		}

		final List<StateGroup> listRecordsCopy;
		synchronized (listRecords) {
			listRecordsCopy = new ArrayList<StateGroup>(listRecords);
		}

		if (setDependenciesCopy.contains(newDependency)) {
			setDependenciesCopy.remove(newDependency);
		}

		setDependenciesCopy.add(newDependency);

		for (int i = 0, nRecordCount = listRecordsCopy.size(); (i < nRecordCount) && (null == cycleRecord); i++) {
			final StateGroup nextRecord = listRecordsCopy.get(i);

			cycleRecord = getCycle(new ArrayList<StateGroupLink>(setDependenciesCopy), listRecordsCopy, nextRecord);
		}

		return cycleRecord;
	}

	private static final StateGroup getCycle(final List<StateGroupLink> listDependencies,
			final List<StateGroup> listRecords, final StateGroup record) {
		// here will happen a RECURSIVE CALL
		// =================================
		if (record != null) {
			final List<StateGroupLink> listDependenciesOfRecord = new ArrayList<StateGroupLink>();

			for (int i = 0, nDepCount = listDependencies.size(); i < nDepCount; i++) {
				final StateGroupLink nextDependency = listDependencies.get(i);

				if (nextDependency != null) {
					if (nextDependency.getSource() == record.getPrimaryKey()) {
						listDependenciesOfRecord.add(nextDependency);
					}
				}
			}
		}
		return null;
	}

	public final boolean save(final OutputStreamWriter out) {
		boolean bSaved = false;

		if (out != null) {
			synchronized (this) {
				XMLParser.writeLine(0, out, "RECORD", null, true);

				final String strValue0 = "" + this.m_PrimaryKey;
				XMLParser.writeLine(1, out, "PrimaryKey", strValue0, true);

				final String strValue1 = "" + this.m_ServerReplicationVersion;
				XMLParser.writeLine(1, out, "ServerReplicationVersion", strValue1, true);

				final String strValue2 = "" + this.m_Source;
				XMLParser.writeLine(1, out, "Source", strValue2, true);

				final String strValue3 = "" + this.m_Destination;
				XMLParser.writeLine(1, out, "Destination", strValue3, true);

				XMLParser.writeLine(0, out, "RECORD", null, false);
			}
		}

		return bSaved;
	}

	public final boolean save() {
		boolean bSaved = false;

		final ISource server = ServerFactory.create();

		if (server != null) {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("<SET>");
			strBuf.append("<ClientName>");
			strBuf.append(Settings.getClientName());
			strBuf.append("</ClientName>");
			strBuf.append("<UserName>");
			strBuf.append(Settings.getUserName());
			strBuf.append("</UserName>");
			strBuf.append("<CallbackPort>");
			strBuf.append(Settings.getCallbackPort());
			strBuf.append("</CallbackPort>");
			strBuf.append("<StateGroupLink>");
			strBuf.append(this.toXML());
			strBuf.append("</StateGroupLink>");
			strBuf.append("</SET>");

			bSaved = server.update(strBuf.toString());

			this.setDirty(!bSaved);
		}

		return bSaved;
	}

	public static final void sortByPrimaryKey(final List<StateGroupLink> list, final boolean ascending) {
		Collections.sort(list, new ComparatorPrimaryKey(ascending));
	}

	public static final void sortByServerReplicationVersion(final List<StateGroupLink> list, final boolean ascending) {
		Collections.sort(list, new ComparatorServerReplicationVersion(ascending));
	}

	public static final void sortBySource(final List<StateGroupLink> list, final boolean ascending) {
		Collections.sort(list, new ComparatorSource(ascending));
	}

	public static final void sortByDestination(final List<StateGroupLink> list, final boolean ascending) {
		Collections.sort(list, new ComparatorDestination(ascending));
	}

	public static final class ComparatorPrimaryKey implements Comparator<StateGroupLink> {
		private final boolean ascending;

		public ComparatorPrimaryKey(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateGroupLink obj1, final StateGroupLink obj2) {
			final StateGroupLink stategrouplink1;
			final StateGroupLink stategrouplink2;
			if (this.ascending) {
				stategrouplink1 = obj1;
				stategrouplink2 = obj2;
			} else {
				stategrouplink1 = obj2;
				stategrouplink2 = obj1;
			}

			final Long long1 = new Long(stategrouplink1.m_PrimaryKey);
			final Long long2 = new Long(stategrouplink2.m_PrimaryKey);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorServerReplicationVersion implements Comparator<StateGroupLink> {
		private final boolean ascending;

		public ComparatorServerReplicationVersion(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateGroupLink obj1, final StateGroupLink obj2) {
			final StateGroupLink stategrouplink1;
			final StateGroupLink stategrouplink2;
			if (this.ascending) {
				stategrouplink1 = obj1;
				stategrouplink2 = obj2;
			} else {
				stategrouplink1 = obj2;
				stategrouplink2 = obj1;
			}

			final Long long1 = new Long(stategrouplink1.m_ServerReplicationVersion);
			final Long long2 = new Long(stategrouplink2.m_ServerReplicationVersion);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorSource implements Comparator<StateGroupLink> {
		private final boolean ascending;

		public ComparatorSource(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateGroupLink obj1, final StateGroupLink obj2) {
			final StateGroupLink stategrouplink1;
			final StateGroupLink stategrouplink2;
			if (this.ascending) {
				stategrouplink1 = obj1;
				stategrouplink2 = obj2;
			} else {
				stategrouplink1 = obj2;
				stategrouplink2 = obj1;
			}

			final StateGroup ref1 = stategrouplink1.getSourceRef();
			final StateGroup ref2 = stategrouplink2.getSourceRef();
			if ((ref1 != null) && (ref2 != null)) {
				return ref1.toString().compareTo(ref2.toString());
			}
			return 0;
		}
	}

	public static final class ComparatorDestination implements Comparator<StateGroupLink> {
		private final boolean ascending;

		public ComparatorDestination(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateGroupLink obj1, final StateGroupLink obj2) {
			final StateGroupLink stategrouplink1;
			final StateGroupLink stategrouplink2;
			if (this.ascending) {
				stategrouplink1 = obj1;
				stategrouplink2 = obj2;
			} else {
				stategrouplink1 = obj2;
				stategrouplink2 = obj1;
			}

			final StateGroup ref1 = stategrouplink1.getDestinationRef();
			final StateGroup ref2 = stategrouplink2.getDestinationRef();
			if ((ref1 != null) && (ref2 != null)) {
				return ref1.toString().compareTo(ref2.toString());
			}
			return 0;
		}
	}

	/*
	 * export this object to a database export fragment
	 */
	public final boolean export(final CommunicationModel dbExport) {
		boolean bExported = false;
		if (dbExport != null) {
			bExported = dbExport.addStateGroupLink(this);
			final StateGroup record2 = ServerFactory.create().findByIDStateGroup(this.m_Source);
			if (record2 != null) {
				record2.export(dbExport);
			}
			final StateGroup record3 = ServerFactory.create().findByIDStateGroup(this.m_Destination);
			if (record3 != null) {
				record3.export(dbExport);
			}
		}
		return bExported;
	}

	/*
	 * export this object to a database export fragment
	 */
	public final boolean contextExport(final CommunicationModel dbExport, final boolean recursive) {
		boolean bExported = false;
		if (dbExport != null) {
			bExported = this.export(dbExport);
		}
		return bExported;
	}

	/*
	 * export this object instance to an XML file
	 */
	public final boolean export(final String strFilePath) {
		boolean bExported = false;

		final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);

		if (out != null) {
			XMLParser.writeLine(0, out, "Transfer", null, true);

			bExported = this.export(out);

			XMLParser.writeLine(0, out, "Transfer", null, false);

			XMLParser.close(out);
		}

		return bExported;
	}

	/*
	 * export this object into a file output stream
	 */
	public final synchronized boolean export(final OutputStreamWriter out) {
		XMLParser.writeLine(1, out, "StateGroupLink", null, true);

		XMLParser.writeLine(2, out, "PrimaryKey", Long.toString(this.m_PrimaryKey), true);
		XMLParser.writeLine(2, out, "ServerReplicationVersion", Long.toString(this.m_ServerReplicationVersion), true);
		XMLParser.writeLine(2, out, "Source", Long.toString(this.m_Source), true);
		XMLParser.writeLine(2, out, "Destination", Long.toString(this.m_Destination), true);

		XMLParser.writeLine(1, out, "StateGroupLink", null, false);

		final StateGroup source = this.getSourceRef();
		if (source != null) {
			source.export(out);
		}

		final StateGroup destination = this.getDestinationRef();
		if (destination != null) {
			destination.export(out);
		}

		return true;
	}

}
