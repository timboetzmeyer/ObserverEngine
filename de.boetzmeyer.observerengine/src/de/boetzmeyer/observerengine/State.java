package de.boetzmeyer.observerengine;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class State implements Serializable, IRecordable, IState {
	private static final long DEFAULT_PRIMARYKEY = 0L;
	private static final long DEFAULT_SERVERREPLICATIONVERSION = 0L;
	private static final String DEFAULT_STATENAME = "";
	private static final String DEFAULT_DEFAULTVALUE = "";
	private static final long DEFAULT_STATETYPE = 0L;

	/**
	 * 
	 * the table attribute names
	 */
	public static final String[] ATTRIBUTE_NAMES = new String[] { "StateName", "DefaultValue", "StateType" };

	/**
	 * 
	 * the table header column names
	 */
	public static final String[] TABLE_HEADER = new String[] { "State Name", "Default Value", "Type" };

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
	private String m_StateName = DEFAULT_STATENAME;

	/**
	 * 
	 * 
	 */
	private String m_DefaultValue = DEFAULT_DEFAULTVALUE;

	/**
	 * 
	 * 
	 */
	private long m_StateType = DEFAULT_STATETYPE;

	public synchronized final boolean isDirty() {
		return this.m_bDirty;
	}

	public synchronized final void setDirty(final boolean bDirty) {
		this.m_bDirty = bDirty;
	}

	public synchronized final boolean isValid() {
		if (this.getStateName().length() == 0) {
			return false;
		}
		if (this.getStateType() == 0L) {
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
		if (this.getStateName().length() == 0) {
			s.append("  State Name fehlt!");
		}
		if (this.getStateType() == 0L) {
			s.append("  Type fehlt!");
		}
		return s.toString();
	}

	public final CommunicationModel getDB() {
		return this.m_db;
	}

	public final synchronized boolean sameContent(final IRecordable theOther) {
		if (!(theOther instanceof State)) {
			return false;
		}
		final State other = (State) theOther;
		if (null == other)
			return false;

		if (other.m_PrimaryKey != this.m_PrimaryKey)
			return false;

		if (other.m_ServerReplicationVersion != this.m_ServerReplicationVersion)
			return false;

		if (!other.m_StateName.equals(this.m_StateName))
			return false;

		if (!other.m_DefaultValue.equals(this.m_DefaultValue))
			return false;

		if (other.m_StateType != this.m_StateType)
			return false;

		return true;
	}

	private void increaseServerReplicationVersion() {
		this.setServerReplicationVersion(1 + this.getServerReplicationVersion());
	}

	public final void overwrite(final State source) {
		this.overwrite(source, false);
	}

	public final void overwrite(final State source, final boolean bIncreaseVersion) {
		if (source != null) {
			if (bIncreaseVersion) {
				this.increaseServerReplicationVersion();
			}

			this.setStateName(source.getStateName(), true);
			this.setDefaultValue(source.getDefaultValue(), true);
			this.setStateType(source.getStateType(), true);
		}
	}

	private State() {
		this.m_db = null;
	}

	public final State copy() {
		final State recordCopy = new State();

		synchronized (this) {
			recordCopy.m_PrimaryKey = this.m_PrimaryKey;
			recordCopy.m_ServerReplicationVersion = this.m_ServerReplicationVersion;
			recordCopy.m_StateName = this.m_StateName;
			recordCopy.m_DefaultValue = this.m_DefaultValue;
			recordCopy.m_StateType = this.m_StateType;
		}

		return recordCopy;
	}

	public static final State createByStateType(final long lID) {
		final State record = generate();

		if (record != null) {
			record.m_StateType = lID;
		}

		return record;
	}

	private State(final CommunicationModel db, final boolean bRecursive, final boolean bNewID) {
		this.m_db = db;

		if (bNewID) {
			this.m_PrimaryKey = IDGenerator.createPrimaryKey();
		}

		if (bRecursive) {
			final StateType refStateType = StateType.createNewTree(db);

			if (refStateType != null) {
				this.m_StateType = refStateType.getPrimaryKey();
			}

		}

		if (this.m_db != null) {
			this.m_db.addState(this);
		}
	}

	public static final State createNewTree(final CommunicationModel db) {
		return new State(db, true, true);
	}

	public static final State createNew(final CommunicationModel db) {
		return new State(db, false, true);
	}

	public static final State createNew(final CommunicationModel db, final boolean bNewID) {
		return new State(db, false, bNewID);
	}

	public static final State create(final CommunicationModel db, final boolean bRecursive) {
		return new State(db, bRecursive, true);
	}

	public static final State generate() {
		State record = new State();
		record.m_PrimaryKey = IDGenerator.createPrimaryKey();
		return record;
	}

	static final State generateWithoutKey() {
		return new State();
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

	public final synchronized String getStateName() {
		return this.m_StateName;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setStateName(String value) {
		return this.setStateName(value, false);
	}

	public final synchronized boolean setStateName(String value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_StateName = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final synchronized String getDefaultValue() {
		return this.m_DefaultValue;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setDefaultValue(String value) {
		return this.setDefaultValue(value, false);
	}

	public final synchronized boolean setDefaultValue(String value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_DefaultValue = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final synchronized long getStateType() {
		return this.m_StateType;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setStateType(long value) {
		return this.setStateType(value, false);
	}

	public final synchronized boolean setStateType(long value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_StateType = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final StateType getStateTypeRef() {
		StateType record = null;
		final ISource server = SourceLocator.create();
		if (server != null) {
			record = server.findByIDStateType(this.getStateType());
		}
		return record;
	}

	public final boolean equals(final Object obj) {
		if (obj instanceof State) {
			return ((State) obj).getPrimaryKey() == this.getPrimaryKey();
		}
		return false;
	}

	public final int hashCode() {
		return (int) (this.m_PrimaryKey % 1000);
	}

	public final String toString() {
		final ISource server = SourceLocator.create();

		final StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("" + this.m_StateName);

		strBuilder.append(" : ");

		final StateType secondRecord = server.findByIDStateType(this.m_StateType);

		if (secondRecord != null) {
			strBuilder.append(secondRecord.toString());
		}

		return strBuilder.toString();
	}

	public static final State load(final Node node, final CommunicationModel db) {
		final State state = new State();
		state.m_db = db;
		if (node != null) {
			final NodeList listElementChildren = node.getChildNodes();
			final int nNodeCount = listElementChildren.getLength();
			for (int i = 0; i < nNodeCount; i++) {
				final Node nodeChild = listElementChildren.item(i);
				if (XMLParser.isElementNode(nodeChild)) {
					final String strNodeName = nodeChild.getNodeName();
					if (strNodeName.equals("PrimaryKey")) {
						state.m_PrimaryKey = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("ServerReplicationVersion")) {
						state.m_ServerReplicationVersion = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("StateName")) {
						state.m_StateName = XMLParser.loadString(nodeChild);
						continue;
					}

					if (strNodeName.equals("DefaultValue")) {
						state.m_DefaultValue = XMLParser.loadString(nodeChild);
						continue;
					}

					if (strNodeName.equals("StateType")) {
						state.m_StateType = XMLParser.loadLong(nodeChild);
						continue;
					}

				}
			}
		}

		return state;
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

			strBuf.append("<StateName>");
			strBuf.append(XMLParser.escapeSequence(this.m_StateName));
			strBuf.append("</StateName>");

			strBuf.append("<DefaultValue>");
			strBuf.append(XMLParser.escapeSequence(this.m_DefaultValue));
			strBuf.append("</DefaultValue>");

			strBuf.append("<StateType>");
			strBuf.append(Long.toString(this.m_StateType));
			strBuf.append("</StateType>");

			strBuf.append("</RECORD>");
		}

		return strBuf.toString();
	}

	public final String differences(final IRecordable theOther) {
		if (!(theOther instanceof State)) {
			return "";
		}
		final State other = (State) theOther;
		final StringBuilder s = new StringBuilder();
		if (!this.m_StateName.equals(other.m_StateName)) {
			s.append("State Name:  ");
			s.append(this.m_StateName);
			s.append(" --> ");
			s.append(other.m_StateName);
			s.append("\r\n");
		}
		if (!this.m_DefaultValue.equals(other.m_DefaultValue)) {
			s.append("Default Value:  ");
			s.append(this.m_DefaultValue);
			s.append(" --> ");
			s.append(other.m_DefaultValue);
			s.append("\r\n");
		}
		if (this.m_StateType != other.m_StateType) {
			s.append("Type:  ");
			final StateType refThis = this.getStateTypeRef();
			if (refThis != null) {
				s.append(refThis.toString());
			} else {
				s.append("---");
			}
			s.append(" --> ");
			final StateType refOther = other.getStateTypeRef();
			if (refOther != null) {
				s.append(refOther.toString());
			} else {
				s.append("---");
			}
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

				final String strValue2 = "" + this.m_StateName;
				XMLParser.writeLine(1, out, "StateName", strValue2, true);

				final String strValue3 = "" + this.m_DefaultValue;
				XMLParser.writeLine(1, out, "DefaultValue", strValue3, true);

				final String strValue4 = "" + this.m_StateType;
				XMLParser.writeLine(1, out, "StateType", strValue4, true);

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
			strBuf.append("<State>");
			strBuf.append(this.toXML());
			strBuf.append("</State>");
			strBuf.append("</SET>");

			bSaved = server.update(strBuf.toString());

			this.setDirty(!bSaved);
		}

		return bSaved;
	}

	public static final void sortByPrimaryKey(final List<State> list, final boolean ascending) {
		Collections.sort(list, new ComparatorPrimaryKey(ascending));
	}

	public static final void sortByServerReplicationVersion(final List<State> list, final boolean ascending) {
		Collections.sort(list, new ComparatorServerReplicationVersion(ascending));
	}

	public static final void sortByStateName(final List<State> list, final boolean ascending) {
		Collections.sort(list, new ComparatorStateName(ascending));
	}

	public static final void sortByDefaultValue(final List<State> list, final boolean ascending) {
		Collections.sort(list, new ComparatorDefaultValue(ascending));
	}

	public static final void sortByStateType(final List<State> list, final boolean ascending) {
		Collections.sort(list, new ComparatorStateType(ascending));
	}

	public static final class ComparatorPrimaryKey implements Comparator<State> {
		private final boolean ascending;

		public ComparatorPrimaryKey(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final State obj1, final State obj2) {
			final State state1;
			final State state2;
			if (this.ascending) {
				state1 = obj1;
				state2 = obj2;
			} else {
				state1 = obj2;
				state2 = obj1;
			}

			final Long long1 = new Long(state1.m_PrimaryKey);
			final Long long2 = new Long(state2.m_PrimaryKey);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorServerReplicationVersion implements Comparator<State> {
		private final boolean ascending;

		public ComparatorServerReplicationVersion(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final State obj1, final State obj2) {
			final State state1;
			final State state2;
			if (this.ascending) {
				state1 = obj1;
				state2 = obj2;
			} else {
				state1 = obj2;
				state2 = obj1;
			}

			final Long long1 = new Long(state1.m_ServerReplicationVersion);
			final Long long2 = new Long(state2.m_ServerReplicationVersion);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorStateName implements Comparator<State> {
		private final boolean ascending;

		public ComparatorStateName(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final State obj1, final State obj2) {
			final State state1;
			final State state2;
			if (this.ascending) {
				state1 = obj1;
				state2 = obj2;
			} else {
				state1 = obj2;
				state2 = obj1;
			}

			final String string1 = state1.m_StateName.toLowerCase();
			final String string2 = state2.m_StateName.toLowerCase();

			return string1.compareTo(string2);
		}
	}

	public static final class ComparatorDefaultValue implements Comparator<State> {
		private final boolean ascending;

		public ComparatorDefaultValue(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final State obj1, final State obj2) {
			final State state1;
			final State state2;
			if (this.ascending) {
				state1 = obj1;
				state2 = obj2;
			} else {
				state1 = obj2;
				state2 = obj1;
			}

			final String string1 = state1.m_DefaultValue.toLowerCase();
			final String string2 = state2.m_DefaultValue.toLowerCase();

			return string1.compareTo(string2);
		}
	}

	public static final class ComparatorStateType implements Comparator<State> {
		private final boolean ascending;

		public ComparatorStateType(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final State obj1, final State obj2) {
			final State state1;
			final State state2;
			if (this.ascending) {
				state1 = obj1;
				state2 = obj2;
			} else {
				state1 = obj2;
				state2 = obj1;
			}

			final StateType ref1 = state1.getStateTypeRef();
			final StateType ref2 = state2.getStateTypeRef();
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
			bExported = dbExport.addState(this);
			final StateType record4 = SourceLocator.create().findByIDStateType(this.m_StateType);
			if (record4 != null) {
				record4.export(dbExport);
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
			final List<Observer> list0 = SourceLocator.create().referencesObserverByState(this.getPrimaryKey());
			for (Observer element : list0) {
				if (recursive) {
					element.contextExport(dbExport, recursive);
				} else {
					element.export(dbExport);
				}
			}
			final List<StateChange> list1 = SourceLocator.create().referencesStateChangeByState(this.getPrimaryKey());
			for (StateChange element : list1) {
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
		XMLParser.writeLine(1, out, "State", null, true);

		XMLParser.writeLine(2, out, "PrimaryKey", Long.toString(this.m_PrimaryKey), true);
		XMLParser.writeLine(2, out, "ServerReplicationVersion", Long.toString(this.m_ServerReplicationVersion), true);
		XMLParser.writeLine(2, out, "StateName", this.m_StateName, true);
		XMLParser.writeLine(2, out, "DefaultValue", this.m_DefaultValue, true);
		XMLParser.writeLine(2, out, "StateType", Long.toString(this.m_StateType), true);

		XMLParser.writeLine(1, out, "State", null, false);

		final StateType statetype = this.getStateTypeRef();
		if (statetype != null) {
			statetype.export(out);
		}

		return true;
	}

}
