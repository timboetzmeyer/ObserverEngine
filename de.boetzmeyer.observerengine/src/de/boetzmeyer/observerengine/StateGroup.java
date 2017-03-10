package de.boetzmeyer.observerengine;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class StateGroup implements Serializable, IRecordable, IStateGroup {
	private static final long DEFAULT_PRIMARYKEY = 0L;
	private static final long DEFAULT_SERVERREPLICATIONVERSION = 0L;
	private static final String DEFAULT_GROUPNAME = "";
	private static final String DEFAULT_DESCRIPTION = "";

	/**
	 * 
	 * the table attribute names
	 */
	public static final String[] ATTRIBUTE_NAMES = new String[] { "GroupName", "Description" };

	/**
	 * 
	 * the table header column names
	 */
	public static final String[] TABLE_HEADER = new String[] { "Group Name", "Description" };

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
	private String m_GroupName = DEFAULT_GROUPNAME;

	/**
	 * 
	 * 
	 */
	private String m_Description = DEFAULT_DESCRIPTION;

	public synchronized final boolean isDirty() {
		return this.m_bDirty;
	}

	public synchronized final void setDirty(final boolean bDirty) {
		this.m_bDirty = bDirty;
	}

	public synchronized final boolean isValid() {
		if (this.getGroupName().length() == 0) {
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
		if (this.getGroupName().length() == 0) {
			s.append("  Group Name fehlt!");
		}
		return s.toString();
	}

	public final CommunicationModel getDB() {
		return this.m_db;
	}

	public final synchronized boolean sameContent(final IRecordable theOther) {
		if (!(theOther instanceof StateGroup)) {
			return false;
		}
		final StateGroup other = (StateGroup) theOther;
		if (null == other)
			return false;

		if (other.m_PrimaryKey != this.m_PrimaryKey)
			return false;

		if (other.m_ServerReplicationVersion != this.m_ServerReplicationVersion)
			return false;

		if (!other.m_GroupName.equals(this.m_GroupName))
			return false;

		if (!other.m_Description.equals(this.m_Description))
			return false;

		return true;
	}

	private void increaseServerReplicationVersion() {
		this.setServerReplicationVersion(1 + this.getServerReplicationVersion());
	}

	public final void overwrite(final StateGroup source) {
		this.overwrite(source, false);
	}

	public final void overwrite(final StateGroup source, final boolean bIncreaseVersion) {
		if (source != null) {
			if (bIncreaseVersion) {
				this.increaseServerReplicationVersion();
			}

			this.setGroupName(source.getGroupName(), true);
			this.setDescription(source.getDescription(), true);
		}
	}

	private StateGroup() {
		this.m_db = null;
	}

	public final StateGroup copy() {
		final StateGroup recordCopy = new StateGroup();

		synchronized (this) {
			recordCopy.m_PrimaryKey = this.m_PrimaryKey;
			recordCopy.m_ServerReplicationVersion = this.m_ServerReplicationVersion;
			recordCopy.m_GroupName = this.m_GroupName;
			recordCopy.m_Description = this.m_Description;
		}

		return recordCopy;
	}

	private StateGroup(final CommunicationModel db, final boolean bRecursive, final boolean bNewID) {
		this.m_db = db;

		if (bNewID) {
			this.m_PrimaryKey = IDGenerator.createPrimaryKey();
		}

		if (this.m_db != null) {
			this.m_db.addStateGroup(this);
		}
	}

	public static final StateGroup createNewTree(final CommunicationModel db) {
		return new StateGroup(db, true, true);
	}

	public static final StateGroup createNew(final CommunicationModel db) {
		return new StateGroup(db, false, true);
	}

	public static final StateGroup createNew(final CommunicationModel db, final boolean bNewID) {
		return new StateGroup(db, false, bNewID);
	}

	public static final StateGroup create(final CommunicationModel db, final boolean bRecursive) {
		return new StateGroup(db, bRecursive, true);
	}

	public static final StateGroup generate() {
		StateGroup record = new StateGroup();
		record.m_PrimaryKey = IDGenerator.createPrimaryKey();
		return record;
	}

	static final StateGroup generateWithoutKey() {
		return new StateGroup();
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

	public final synchronized String getGroupName() {
		return this.m_GroupName;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setGroupName(String value) {
		return this.setGroupName(value, false);
	}

	public final synchronized boolean setGroupName(String value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_GroupName = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final synchronized String getDescription() {
		return this.m_Description;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setDescription(String value) {
		return this.setDescription(value, false);
	}

	public final synchronized boolean setDescription(String value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_Description = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final boolean equals(final Object obj) {
		if (obj instanceof StateGroup) {
			return ((StateGroup) obj).getPrimaryKey() == this.getPrimaryKey();
		}
		return false;
	}

	public final int hashCode() {
		return (int) (this.m_PrimaryKey % 1000);
	}

	public final String toString() {
		return "" + this.m_GroupName;
	}

	public static final StateGroup load(final Node node, final CommunicationModel db) {
		final StateGroup stategroup = new StateGroup();
		stategroup.m_db = db;
		if (node != null) {
			final NodeList listElementChildren = node.getChildNodes();
			final int nNodeCount = listElementChildren.getLength();
			for (int i = 0; i < nNodeCount; i++) {
				final Node nodeChild = listElementChildren.item(i);
				if (XMLParser.isElementNode(nodeChild)) {
					final String strNodeName = nodeChild.getNodeName();
					if (strNodeName.equals("PrimaryKey")) {
						stategroup.m_PrimaryKey = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("ServerReplicationVersion")) {
						stategroup.m_ServerReplicationVersion = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("GroupName")) {
						stategroup.m_GroupName = XMLParser.loadString(nodeChild);
						continue;
					}

					if (strNodeName.equals("Description")) {
						stategroup.m_Description = XMLParser.loadString(nodeChild);
						continue;
					}

				}
			}
		}

		return stategroup;
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

			strBuf.append("<GroupName>");
			strBuf.append(XMLParser.escapeSequence(this.m_GroupName));
			strBuf.append("</GroupName>");

			strBuf.append("<Description>");
			strBuf.append(XMLParser.escapeSequence(this.m_Description));
			strBuf.append("</Description>");

			strBuf.append("</RECORD>");
		}

		return strBuf.toString();
	}

	public final String differences(final IRecordable theOther) {
		if (!(theOther instanceof StateGroup)) {
			return "";
		}
		final StateGroup other = (StateGroup) theOther;
		final StringBuilder s = new StringBuilder();
		if (!this.m_GroupName.equals(other.m_GroupName)) {
			s.append("Group Name:  ");
			s.append(this.m_GroupName);
			s.append(" --> ");
			s.append(other.m_GroupName);
			s.append("\r\n");
		}
		if (!this.m_Description.equals(other.m_Description)) {
			s.append("Description:  ");
			s.append(this.m_Description);
			s.append(" --> ");
			s.append(other.m_Description);
			s.append("\r\n");
		}
		return s.toString();
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

				final String strValue2 = "" + this.m_GroupName;
				XMLParser.writeLine(1, out, "GroupName", strValue2, true);

				final String strValue3 = "" + this.m_Description;
				XMLParser.writeLine(1, out, "Description", strValue3, true);

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
			strBuf.append("<StateGroup>");
			strBuf.append(this.toXML());
			strBuf.append("</StateGroup>");
			strBuf.append("</SET>");

			bSaved = server.update(strBuf.toString());

			this.setDirty(!bSaved);
		}

		return bSaved;
	}

	public static final void sortByPrimaryKey(final List<StateGroup> list, final boolean ascending) {
		Collections.sort(list, new ComparatorPrimaryKey(ascending));
	}

	public static final void sortByServerReplicationVersion(final List<StateGroup> list, final boolean ascending) {
		Collections.sort(list, new ComparatorServerReplicationVersion(ascending));
	}

	public static final void sortByGroupName(final List<StateGroup> list, final boolean ascending) {
		Collections.sort(list, new ComparatorGroupName(ascending));
	}

	public static final void sortByDescription(final List<StateGroup> list, final boolean ascending) {
		Collections.sort(list, new ComparatorDescription(ascending));
	}

	public static final class ComparatorPrimaryKey implements Comparator<StateGroup> {
		private final boolean ascending;

		public ComparatorPrimaryKey(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateGroup obj1, final StateGroup obj2) {
			final StateGroup stategroup1;
			final StateGroup stategroup2;
			if (this.ascending) {
				stategroup1 = obj1;
				stategroup2 = obj2;
			} else {
				stategroup1 = obj2;
				stategroup2 = obj1;
			}

			final Long long1 = new Long(stategroup1.m_PrimaryKey);
			final Long long2 = new Long(stategroup2.m_PrimaryKey);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorServerReplicationVersion implements Comparator<StateGroup> {
		private final boolean ascending;

		public ComparatorServerReplicationVersion(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateGroup obj1, final StateGroup obj2) {
			final StateGroup stategroup1;
			final StateGroup stategroup2;
			if (this.ascending) {
				stategroup1 = obj1;
				stategroup2 = obj2;
			} else {
				stategroup1 = obj2;
				stategroup2 = obj1;
			}

			final Long long1 = new Long(stategroup1.m_ServerReplicationVersion);
			final Long long2 = new Long(stategroup2.m_ServerReplicationVersion);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorGroupName implements Comparator<StateGroup> {
		private final boolean ascending;

		public ComparatorGroupName(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateGroup obj1, final StateGroup obj2) {
			final StateGroup stategroup1;
			final StateGroup stategroup2;
			if (this.ascending) {
				stategroup1 = obj1;
				stategroup2 = obj2;
			} else {
				stategroup1 = obj2;
				stategroup2 = obj1;
			}

			final String string1 = stategroup1.m_GroupName.toLowerCase();
			final String string2 = stategroup2.m_GroupName.toLowerCase();

			return string1.compareTo(string2);
		}
	}

	public static final class ComparatorDescription implements Comparator<StateGroup> {
		private final boolean ascending;

		public ComparatorDescription(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateGroup obj1, final StateGroup obj2) {
			final StateGroup stategroup1;
			final StateGroup stategroup2;
			if (this.ascending) {
				stategroup1 = obj1;
				stategroup2 = obj2;
			} else {
				stategroup1 = obj2;
				stategroup2 = obj1;
			}

			final String string1 = stategroup1.m_Description.toLowerCase();
			final String string2 = stategroup2.m_Description.toLowerCase();

			return string1.compareTo(string2);
		}
	}

	/*
	 * export this object to a database export fragment
	 */
	public final boolean export(final CommunicationModel dbExport) {
		boolean bExported = false;
		if (dbExport != null) {
			bExported = dbExport.addStateGroup(this);
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
			final List<Observer> list0 = ServerFactory.create().referencesObserverByStateGroup(this.getPrimaryKey());
			for (Observer element : list0) {
				if (recursive) {
					element.contextExport(dbExport, recursive);
				} else {
					element.export(dbExport);
				}
			}
			final List<StateGroupLink> list1 = ServerFactory.create()
					.referencesStateGroupLinkBySource(this.getPrimaryKey());
			for (StateGroupLink element : list1) {
				if (recursive) {
					element.contextExport(dbExport, recursive);
				} else {
					element.export(dbExport);
				}
			}
			final List<StateGroupLink> list2 = ServerFactory.create()
					.referencesStateGroupLinkByDestination(this.getPrimaryKey());
			for (StateGroupLink element : list2) {
				if (recursive) {
					element.contextExport(dbExport, recursive);
				} else {
					element.export(dbExport);
				}
			}
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
		XMLParser.writeLine(1, out, "StateGroup", null, true);

		XMLParser.writeLine(2, out, "PrimaryKey", Long.toString(this.m_PrimaryKey), true);
		XMLParser.writeLine(2, out, "ServerReplicationVersion", Long.toString(this.m_ServerReplicationVersion), true);
		XMLParser.writeLine(2, out, "GroupName", this.m_GroupName, true);
		XMLParser.writeLine(2, out, "Description", this.m_Description, true);

		XMLParser.writeLine(1, out, "StateGroup", null, false);

		return true;
	}

}
