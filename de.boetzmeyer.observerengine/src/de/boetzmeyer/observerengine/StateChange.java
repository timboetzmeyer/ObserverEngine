package de.boetzmeyer.observerengine;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class StateChange implements Serializable, IRecordable, IStateChange {
	private static final long DEFAULT_PRIMARYKEY = 0L;
	private static final long DEFAULT_SERVERREPLICATIONVERSION = 0L;
	private static final long DEFAULT_STATE = 0L;
	private static final String DEFAULT_STATEVALUE = "";
	private static final Date DEFAULT_CHANGETIME = new Date();

	/**
	 * 
	 * the table attribute names
	 */
	public static final String[] ATTRIBUTE_NAMES = new String[] { "State", "StateValue", "ChangeTime" };

	/**
	 * 
	 * the table header column names
	 */
	public static final String[] TABLE_HEADER = new String[] { "State", "Value", "Change Time" };

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
	private long m_State = DEFAULT_STATE;

	/**
	 * 
	 * 
	 */
	private String m_StateValue = DEFAULT_STATEVALUE;

	/**
	 * 
	 * 
	 */
	private Date m_ChangeTime = DEFAULT_CHANGETIME;

	public synchronized final boolean isDirty() {
		return this.m_bDirty;
	}

	public synchronized final void setDirty(final boolean bDirty) {
		this.m_bDirty = bDirty;
	}

	public synchronized final boolean isValid() {
		if (this.getState() == 0L) {
			return false;
		}
		if (this.getStateValue().length() == 0) {
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
		if (this.getState() == 0L) {
			s.append("  State fehlt!");
		}
		if (this.getStateValue().length() == 0) {
			s.append("  Value fehlt!");
		}
		return s.toString();
	}

	public final CommunicationModel getDB() {
		return this.m_db;
	}

	public final synchronized boolean sameContent(final IRecordable theOther) {
		if (!(theOther instanceof StateChange)) {
			return false;
		}
		final StateChange other = (StateChange) theOther;
		if (null == other)
			return false;

		if (other.m_PrimaryKey != this.m_PrimaryKey)
			return false;

		if (other.m_ServerReplicationVersion != this.m_ServerReplicationVersion)
			return false;

		if (other.m_State != this.m_State)
			return false;

		if (!other.m_StateValue.equals(this.m_StateValue))
			return false;

		if (!Attribute.equals(other.m_ChangeTime, this.m_ChangeTime))
			return false;

		return true;
	}

	private void increaseServerReplicationVersion() {
		this.setServerReplicationVersion(1 + this.getServerReplicationVersion());
	}

	public final void overwrite(final StateChange source) {
		this.overwrite(source, false);
	}

	public final void overwrite(final StateChange source, final boolean bIncreaseVersion) {
		if (source != null) {
			if (bIncreaseVersion) {
				this.increaseServerReplicationVersion();
			}

			this.setState(source.getState(), true);
			this.setStateValue(source.getStateValue(), true);
			this.setChangeTime(source.getChangeTime(), true);
		}
	}

	private StateChange() {
		this.m_db = null;

		this.m_ChangeTime = new Date();
	}

	public final StateChange copy() {
		final StateChange recordCopy = new StateChange();

		synchronized (this) {
			recordCopy.m_PrimaryKey = this.m_PrimaryKey;
			recordCopy.m_ServerReplicationVersion = this.m_ServerReplicationVersion;
			recordCopy.m_State = this.m_State;
			recordCopy.m_StateValue = this.m_StateValue;
			recordCopy.m_ChangeTime = new Date(this.m_ChangeTime.getTime());
		}

		return recordCopy;
	}

	public static final StateChange createByState(final long lID) {
		final StateChange record = generate();

		if (record != null) {
			record.m_State = lID;
		}

		return record;
	}

	private StateChange(final CommunicationModel db, final boolean bRecursive, final boolean bNewID) {
		this.m_db = db;

		if (bNewID) {
			this.m_PrimaryKey = IDGenerator.createPrimaryKey();
		}

		if (bRecursive) {
			final State refState = State.createNewTree(db);

			if (refState != null) {
				this.m_State = refState.getPrimaryKey();
			}

		}

		if (this.m_db != null) {
			this.m_db.addStateChange(this);
		}
	}

	public static final StateChange createNewTree(final CommunicationModel db) {
		return new StateChange(db, true, true);
	}

	public static final StateChange createNew(final CommunicationModel db) {
		return new StateChange(db, false, true);
	}

	public static final StateChange createNew(final CommunicationModel db, final boolean bNewID) {
		return new StateChange(db, false, bNewID);
	}

	public static final StateChange create(final CommunicationModel db, final boolean bRecursive) {
		return new StateChange(db, bRecursive, true);
	}

	public static final StateChange generate() {
		StateChange record = new StateChange();
		record.m_PrimaryKey = IDGenerator.createPrimaryKey();
		return record;
	}

	static final StateChange generateWithoutKey() {
		return new StateChange();
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

	public final synchronized long getState() {
		return this.m_State;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setState(long value) {
		return this.setState(value, false);
	}

	public final synchronized boolean setState(long value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_State = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final synchronized String getStateValue() {
		return this.m_StateValue;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setStateValue(String value) {
		return this.setStateValue(value, false);
	}

	public final synchronized boolean setStateValue(String value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_StateValue = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final synchronized Date getChangeTime() {
		return new Date(this.m_ChangeTime.getTime());
	}

	/**
	 * 
	 * 
	 */
	public final boolean setChangeTime(Date value) {
		return this.setChangeTime(value, false);
	}

	public final synchronized boolean setChangeTime(Date value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_ChangeTime = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final State getStateRef() {
		State record = null;
		final ISource server = SourceLocator.create();
		if (server != null) {
			record = server.findByIDState(this.getState());
		}
		return record;
	}

	public final boolean equals(final Object obj) {
		if (obj instanceof StateChange) {
			return ((StateChange) obj).getPrimaryKey() == this.getPrimaryKey();
		}
		return false;
	}

	public final int hashCode() {
		return (int) (this.m_PrimaryKey % 1000);
	}

	public final String toString() {
		return "" + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(this.m_ChangeTime) + "   "
				+ this.m_StateValue;
	}

	public static final StateChange load(final Node node, final CommunicationModel db) {
		final StateChange statechange = new StateChange();
		statechange.m_db = db;
		if (node != null) {
			final NodeList listElementChildren = node.getChildNodes();
			final int nNodeCount = listElementChildren.getLength();
			for (int i = 0; i < nNodeCount; i++) {
				final Node nodeChild = listElementChildren.item(i);
				if (XMLParser.isElementNode(nodeChild)) {
					final String strNodeName = nodeChild.getNodeName();
					if (strNodeName.equals("PrimaryKey")) {
						statechange.m_PrimaryKey = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("ServerReplicationVersion")) {
						statechange.m_ServerReplicationVersion = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("State")) {
						statechange.m_State = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("StateValue")) {
						statechange.m_StateValue = XMLParser.loadString(nodeChild);
						continue;
					}

					if (strNodeName.equals("ChangeTime")) {
						statechange.m_ChangeTime = XMLParser.loadDate(nodeChild);
						continue;
					}

				}
			}
		}

		return statechange;
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

			strBuf.append("<State>");
			strBuf.append(Long.toString(this.m_State));
			strBuf.append("</State>");

			strBuf.append("<StateValue>");
			strBuf.append(XMLParser.escapeSequence(this.m_StateValue));
			strBuf.append("</StateValue>");

			strBuf.append("<ChangeTime>");
			strBuf.append(Long.toString(this.m_ChangeTime.getTime()));
			strBuf.append("</ChangeTime>");

			strBuf.append("</RECORD>");
		}

		return strBuf.toString();
	}

	public final String differences(final IRecordable theOther) {
		if (!(theOther instanceof StateChange)) {
			return "";
		}
		final StateChange other = (StateChange) theOther;
		final StringBuilder s = new StringBuilder();
		if (this.m_State != other.m_State) {
			s.append("State:  ");
			final State refThis = this.getStateRef();
			if (refThis != null) {
				s.append(refThis.toString());
			} else {
				s.append("---");
			}
			s.append(" --> ");
			final State refOther = other.getStateRef();
			if (refOther != null) {
				s.append(refOther.toString());
			} else {
				s.append("---");
			}
			s.append("\r\n");
		}
		if (!this.m_StateValue.equals(other.m_StateValue)) {
			s.append("Value:  ");
			s.append(this.m_StateValue);
			s.append(" --> ");
			s.append(other.m_StateValue);
			s.append("\r\n");
		}
		if (this.m_ChangeTime.equals(other.m_ChangeTime)) {
			s.append("Change Time:  ");
			s.append(DateFormat.getDateInstance(DateFormat.SHORT).format(this.m_ChangeTime));
			s.append(" --> ");
			s.append(DateFormat.getDateInstance(DateFormat.SHORT).format(other.m_ChangeTime));
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

				final String strValue2 = "" + this.m_State;
				XMLParser.writeLine(1, out, "State", strValue2, true);

				final String strValue3 = "" + this.m_StateValue;
				XMLParser.writeLine(1, out, "StateValue", strValue3, true);

				final String strValue4 = "" + this.m_ChangeTime.getTime();
				XMLParser.writeLine(1, out, "ChangeTime", strValue4, true);

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
			strBuf.append("<StateChange>");
			strBuf.append(this.toXML());
			strBuf.append("</StateChange>");
			strBuf.append("</SET>");

			bSaved = server.update(strBuf.toString());

			this.setDirty(!bSaved);
		}

		return bSaved;
	}

	public static final void sortByPrimaryKey(final List<StateChange> list, final boolean ascending) {
		Collections.sort(list, new ComparatorPrimaryKey(ascending));
	}

	public static final void sortByServerReplicationVersion(final List<StateChange> list, final boolean ascending) {
		Collections.sort(list, new ComparatorServerReplicationVersion(ascending));
	}

	public static final void sortByState(final List<StateChange> list, final boolean ascending) {
		Collections.sort(list, new ComparatorState(ascending));
	}

	public static final void sortByStateValue(final List<StateChange> list, final boolean ascending) {
		Collections.sort(list, new ComparatorStateValue(ascending));
	}

	public static final void sortByChangeTime(final List<StateChange> list, final boolean ascending) {
		Collections.sort(list, new ComparatorChangeTime(ascending));
	}

	public static final class ComparatorPrimaryKey implements Comparator<StateChange> {
		private final boolean ascending;

		public ComparatorPrimaryKey(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateChange obj1, final StateChange obj2) {
			final StateChange statechange1;
			final StateChange statechange2;
			if (this.ascending) {
				statechange1 = obj1;
				statechange2 = obj2;
			} else {
				statechange1 = obj2;
				statechange2 = obj1;
			}

			final Long long1 = new Long(statechange1.m_PrimaryKey);
			final Long long2 = new Long(statechange2.m_PrimaryKey);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorServerReplicationVersion implements Comparator<StateChange> {
		private final boolean ascending;

		public ComparatorServerReplicationVersion(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateChange obj1, final StateChange obj2) {
			final StateChange statechange1;
			final StateChange statechange2;
			if (this.ascending) {
				statechange1 = obj1;
				statechange2 = obj2;
			} else {
				statechange1 = obj2;
				statechange2 = obj1;
			}

			final Long long1 = new Long(statechange1.m_ServerReplicationVersion);
			final Long long2 = new Long(statechange2.m_ServerReplicationVersion);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorState implements Comparator<StateChange> {
		private final boolean ascending;

		public ComparatorState(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateChange obj1, final StateChange obj2) {
			final StateChange statechange1;
			final StateChange statechange2;
			if (this.ascending) {
				statechange1 = obj1;
				statechange2 = obj2;
			} else {
				statechange1 = obj2;
				statechange2 = obj1;
			}

			final State ref1 = statechange1.getStateRef();
			final State ref2 = statechange2.getStateRef();
			if ((ref1 != null) && (ref2 != null)) {
				return ref1.toString().compareTo(ref2.toString());
			}
			return 0;
		}
	}

	public static final class ComparatorStateValue implements Comparator<StateChange> {
		private final boolean ascending;

		public ComparatorStateValue(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateChange obj1, final StateChange obj2) {
			final StateChange statechange1;
			final StateChange statechange2;
			if (this.ascending) {
				statechange1 = obj1;
				statechange2 = obj2;
			} else {
				statechange1 = obj2;
				statechange2 = obj1;
			}

			final String string1 = statechange1.m_StateValue.toLowerCase();
			final String string2 = statechange2.m_StateValue.toLowerCase();

			return string1.compareTo(string2);
		}
	}

	public static final class ComparatorChangeTime implements Comparator<StateChange> {
		private final boolean ascending;

		public ComparatorChangeTime(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final StateChange obj1, final StateChange obj2) {
			final StateChange statechange1;
			final StateChange statechange2;
			if (this.ascending) {
				statechange1 = obj1;
				statechange2 = obj2;
			} else {
				statechange1 = obj2;
				statechange2 = obj1;
			}

			final Date date1 = statechange1.m_ChangeTime;
			final Date date2 = statechange2.m_ChangeTime;

			return date1.compareTo(date2);
		}
	}

	/*
	 * export this object to a database export fragment
	 */
	public final boolean export(final CommunicationModel dbExport) {
		boolean bExported = false;
		if (dbExport != null) {
			bExported = dbExport.addStateChange(this);
			final State record2 = SourceLocator.create().findByIDState(this.m_State);
			if (record2 != null) {
				record2.export(dbExport);
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
		XMLParser.writeLine(1, out, "StateChange", null, true);

		XMLParser.writeLine(2, out, "PrimaryKey", Long.toString(this.m_PrimaryKey), true);
		XMLParser.writeLine(2, out, "ServerReplicationVersion", Long.toString(this.m_ServerReplicationVersion), true);
		XMLParser.writeLine(2, out, "State", Long.toString(this.m_State), true);
		XMLParser.writeLine(2, out, "StateValue", this.m_StateValue, true);
		XMLParser.writeLine(2, out, "ChangeTime", Long.toString(this.m_ChangeTime.getTime()), true);

		XMLParser.writeLine(1, out, "StateChange", null, false);

		final State state = this.getStateRef();
		if (state != null) {
			state.export(out);
		}

		return true;
	}

}
