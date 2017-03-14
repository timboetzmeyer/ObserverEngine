package de.boetzmeyer.observerengine;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class StateType implements Serializable, IRecordable, IStateType {
	private static final long DEFAULT_PRIMARYKEY = 0L;
	private static final long DEFAULT_SERVERREPLICATIONVERSION = 0L;
	private static final String DEFAULT_TYPENAME = "";
	private static final String DEFAULT_DESCRIPTION = "";

	/**
	 * 
	 * the table attribute names
	 */
	public static final String[] ATTRIBUTE_NAMES = new String[] { "TypeName", "Description" };

	/**
	 * 
	 * the table header column names
	 */
	public static final String[] TABLE_HEADER = new String[] { "Type Name", "Description" };

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
	private String m_TypeName = DEFAULT_TYPENAME;

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
		if (this.getTypeName().length() == 0) {
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
		if (this.getTypeName().length() == 0) {
			s.append("  Type Name fehlt!");
		}
		return s.toString();
	}

	public final CommunicationModel getDB() {
		return this.m_db;
	}

	public final synchronized boolean sameContent(final IRecordable theOther) {
		if (!(theOther instanceof StateType)) {
			return false;
		}
		final StateType other = (StateType) theOther;
		if (null == other)
			return false;

		if (other.m_PrimaryKey != this.m_PrimaryKey)
			return false;

		if (other.m_ServerReplicationVersion != this.m_ServerReplicationVersion)
			return false;

		if (!other.m_TypeName.equals(this.m_TypeName))
			return false;

		if (!other.m_Description.equals(this.m_Description))
			return false;

		return true;
	}

	private void increaseServerReplicationVersion() {
		this.setServerReplicationVersion(1 + this.getServerReplicationVersion());
	}

	public final void overwrite(final StateType source) {
		this.overwrite(source, false);
	}

	public final void overwrite(final StateType source, final boolean bIncreaseVersion) {
		if (source != null) {
			if (bIncreaseVersion) {
				this.increaseServerReplicationVersion();
			}

			this.setTypeName(source.getTypeName(), true);
			this.setDescription(source.getDescription(), true);
		}
	}

	private StateType() {
		this.m_db = null;
	}

	public final StateType copy() {
		final StateType recordCopy = new StateType();

		synchronized (this) {
			recordCopy.m_PrimaryKey = this.m_PrimaryKey;
			recordCopy.m_ServerReplicationVersion = this.m_ServerReplicationVersion;
			recordCopy.m_TypeName = this.m_TypeName;
			recordCopy.m_Description = this.m_Description;
		}

		return recordCopy;
	}

	private StateType(final CommunicationModel db, final boolean bRecursive, final boolean bNewID) {
		this.m_db = db;

		if (bNewID) {
			this.m_PrimaryKey = IDGenerator.createPrimaryKey();
		}

		if (this.m_db != null) {
			this.m_db.addStateType(this);
		}
	}

	public static final StateType createNewTree(final CommunicationModel db) {
		return new StateType(db, true, true);
	}

	public static final StateType createNew(final CommunicationModel db) {
		return new StateType(db, false, true);
	}

	public static final StateType createNew(final CommunicationModel db, final boolean bNewID) {
		return new StateType(db, false, bNewID);
	}

	public static final StateType create(final CommunicationModel db, final boolean bRecursive) {
		return new StateType(db, bRecursive, true);
	}

	public static final StateType generate() {
		StateType record = new StateType();
		record.m_PrimaryKey = IDGenerator.createPrimaryKey();
		return record;
	}

	static final StateType generateWithoutKey() {
		return new StateType();
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

	public final synchronized String getTypeName() {
		return this.m_TypeName;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setTypeName(String value) {
		return this.setTypeName(value, false);
	}

	public final synchronized boolean setTypeName(String value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_TypeName = value;
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
		if (obj instanceof StateType) {
			return ((StateType) obj).getPrimaryKey() == this.getPrimaryKey();
		}
		return false;
	}

	public final int hashCode() {
		return (int) (this.m_PrimaryKey % 1000);
	}

	public final String toString() {
		return "" + this.m_TypeName;
	}

	public static final StateType load(final Node node, final CommunicationModel db) {
		final StateType statetype = new StateType();
		statetype.m_db = db;
		if (node != null) {
			final NodeList listElementChildren = node.getChildNodes();
			final int nNodeCount = listElementChildren.getLength();
			for (int i = 0; i < nNodeCount; i++) {
				final Node nodeChild = listElementChildren.item(i);
				if (XMLParser.isElementNode(nodeChild)) {
					final String strNodeName = nodeChild.getNodeName();
					if (strNodeName.equals("PrimaryKey")) {
						statetype.m_PrimaryKey = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("ServerReplicationVersion")) {
						statetype.m_ServerReplicationVersion = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("TypeName")) {
						statetype.m_TypeName = XMLParser.loadString(nodeChild);
						continue;
					}

					if (strNodeName.equals("Description")) {
						statetype.m_Description = XMLParser.loadString(nodeChild);
						continue;
					}

				}
			}
		}

		return statetype;
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

			strBuf.append("<TypeName>");
			strBuf.append(XMLParser.escapeSequence(this.m_TypeName));
			strBuf.append("</TypeName>");

			strBuf.append("<Description>");
			strBuf.append(XMLParser.escapeSequence(this.m_Description));
			strBuf.append("</Description>");

			strBuf.append("</RECORD>");
		}

		return strBuf.toString();
	}

	public final String differences(final IRecordable theOther) {
		if (!(theOther instanceof StateType)) {
			return "";
		}
		final StateType other = (StateType) theOther;
		final StringBuilder s = new StringBuilder();
		if (!this.m_TypeName.equals(other.m_TypeName)) {
			s.append("Type Name:  ");
			s.append(this.m_TypeName);
			s.append(" --> ");
			s.append(other.m_TypeName);
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

				final String strValue2 = "" + this.m_TypeName;
				XMLParser.writeLine(1, out, "TypeName", strValue2, true);

				final String strValue3 = "" + this.m_Description;
				XMLParser.writeLine(1, out, "Description", strValue3, true);

				XMLParser.writeLine(0, out, "RECORD", null, false);
			}
		}

		return bSaved;
	}

	public final boolean save() {
		boolean bSaved = false;

		final ISource server = SourceLocator.create();

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
			strBuf.append("<StateType>");
			strBuf.append(this.toXML());
			strBuf.append("</StateType>");
			strBuf.append("</SET>");

			bSaved = server.update(strBuf.toString());

			this.setDirty(!bSaved);
		}

		return bSaved;
	}

	public static final void sortByPrimaryKey(final List<StateType> list, final boolean ascending) {
		Collections.sort(list, new ComparatorPrimaryKey(ascending));
	}

	public static final void sortByServerReplicationVersion(final List<StateType> list, final boolean ascending) {
		Collections.sort(list, new ComparatorServerReplicationVersion(ascending));
	}

	public static final void sortByTypeName(final List<StateType> list, final boolean ascending) {
		Collections.sort(list, new ComparatorTypeName(ascending));
	}

	public static final void sortByDescription(final List<StateType> list, final boolean ascending) {
		Collections.sort(list, new ComparatorDescription(ascending));
	}

	public static final class ComparatorPrimaryKey implements Comparator<StateType> {
		private final boolean ascending;

		public ComparatorPrimaryKey(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateType obj1, final StateType obj2) {
			final StateType statetype1;
			final StateType statetype2;
			if (this.ascending) {
				statetype1 = obj1;
				statetype2 = obj2;
			} else {
				statetype1 = obj2;
				statetype2 = obj1;
			}

			final Long long1 = new Long(statetype1.m_PrimaryKey);
			final Long long2 = new Long(statetype2.m_PrimaryKey);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorServerReplicationVersion implements Comparator<StateType> {
		private final boolean ascending;

		public ComparatorServerReplicationVersion(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateType obj1, final StateType obj2) {
			final StateType statetype1;
			final StateType statetype2;
			if (this.ascending) {
				statetype1 = obj1;
				statetype2 = obj2;
			} else {
				statetype1 = obj2;
				statetype2 = obj1;
			}

			final Long long1 = new Long(statetype1.m_ServerReplicationVersion);
			final Long long2 = new Long(statetype2.m_ServerReplicationVersion);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorTypeName implements Comparator<StateType> {
		private final boolean ascending;

		public ComparatorTypeName(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateType obj1, final StateType obj2) {
			final StateType statetype1;
			final StateType statetype2;
			if (this.ascending) {
				statetype1 = obj1;
				statetype2 = obj2;
			} else {
				statetype1 = obj2;
				statetype2 = obj1;
			}

			final String string1 = statetype1.m_TypeName.toLowerCase();
			final String string2 = statetype2.m_TypeName.toLowerCase();

			return string1.compareTo(string2);
		}
	}

	public static final class ComparatorDescription implements Comparator<StateType> {
		private final boolean ascending;

		public ComparatorDescription(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateType obj1, final StateType obj2) {
			final StateType statetype1;
			final StateType statetype2;
			if (this.ascending) {
				statetype1 = obj1;
				statetype2 = obj2;
			} else {
				statetype1 = obj2;
				statetype2 = obj1;
			}

			final String string1 = statetype1.m_Description.toLowerCase();
			final String string2 = statetype2.m_Description.toLowerCase();

			return string1.compareTo(string2);
		}
	}

	/*
	 * export this object to a database export fragment
	 */
	public final boolean export(final CommunicationModel dbExport) {
		boolean bExported = false;
		if (dbExport != null) {
			bExported = dbExport.addStateType(this);
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
			final List<State> list0 = SourceLocator.create().referencesStateByStateType(this.getPrimaryKey());
			for (State element : list0) {
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
		XMLParser.writeLine(1, out, "StateType", null, true);

		XMLParser.writeLine(2, out, "PrimaryKey", Long.toString(this.m_PrimaryKey), true);
		XMLParser.writeLine(2, out, "ServerReplicationVersion", Long.toString(this.m_ServerReplicationVersion), true);
		XMLParser.writeLine(2, out, "TypeName", this.m_TypeName, true);
		XMLParser.writeLine(2, out, "Description", this.m_Description, true);

		XMLParser.writeLine(1, out, "StateType", null, false);

		return true;
	}

}
