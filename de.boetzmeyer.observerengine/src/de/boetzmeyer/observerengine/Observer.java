package de.boetzmeyer.observerengine;

import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class Observer implements IObserver {
	private static final long DEFAULT_PRIMARYKEY = 0L;
	private static final long DEFAULT_SERVERREPLICATIONVERSION = 0L;
	private static final long DEFAULT_STATE = 0L;
	private static final long DEFAULT_STATEGROUP = 0L;
	private static final String DEFAULT_ACTIONCLASS = "";
	private static final String DEFAULT_DESCRIPTION = "";

	/**
	 * 
	 * the table attribute names
	 */
	public static final String[] ATTRIBUTE_NAMES = new String[] { "State", "StateGroup", "ActionClass", "Description" };

	/**
	 * 
	 * the table header column names
	 */
	public static final String[] TABLE_HEADER = new String[] { "State", "State Group", "Action Class", "Description" };

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
	private long m_StateGroup = DEFAULT_STATEGROUP;

	/**
	 * 
	 * 
	 */
	private String m_ActionClass = DEFAULT_ACTIONCLASS;

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
		if (this.getDescription().length() == 0) {
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
		if (this.getDescription().length() == 0) {
			s.append("  Description fehlt!");
		}
		return s.toString();
	}

	public final CommunicationModel getDB() {
		return this.m_db;
	}

	public final synchronized boolean sameContent(final IRecordable theOther) {
		if (!(theOther instanceof Observer)) {
			return false;
		}
		final Observer other = (Observer) theOther;
		if (null == other)
			return false;

		if (other.m_PrimaryKey != this.m_PrimaryKey)
			return false;

		if (other.m_ServerReplicationVersion != this.m_ServerReplicationVersion)
			return false;

		if (other.m_State != this.m_State)
			return false;

		if (other.m_StateGroup != this.m_StateGroup)
			return false;

		if (!other.m_ActionClass.equals(this.m_ActionClass))
			return false;

		if (!other.m_Description.equals(this.m_Description))
			return false;

		return true;
	}

	private void increaseServerReplicationVersion() {
		this.setServerReplicationVersion(1 + this.getServerReplicationVersion());
	}

	public final void overwrite(final Observer source) {
		this.overwrite(source, false);
	}

	public final void overwrite(final Observer source, final boolean bIncreaseVersion) {
		if (source != null) {
			if (bIncreaseVersion) {
				this.increaseServerReplicationVersion();
			}

			this.setState(source.getState(), true);
			this.setStateGroup(source.getStateGroup(), true);
			this.setActionClass(source.getActionClass(), true);
			this.setDescription(source.getDescription(), true);
		}
	}

	private Observer() {
		this.m_db = null;
	}

	public final Observer copy() {
		final Observer recordCopy = new Observer();

		synchronized (this) {
			recordCopy.m_PrimaryKey = this.m_PrimaryKey;
			recordCopy.m_ServerReplicationVersion = this.m_ServerReplicationVersion;
			recordCopy.m_State = this.m_State;
			recordCopy.m_StateGroup = this.m_StateGroup;
			recordCopy.m_ActionClass = this.m_ActionClass;
			recordCopy.m_Description = this.m_Description;
		}

		return recordCopy;
	}

	public static final Observer createByState(final long lID) {
		final Observer record = generate();

		if (record != null) {
			record.m_State = lID;
		}

		return record;
	}

	public static final Observer createByStateGroup(final long lID) {
		final Observer record = generate();

		if (record != null) {
			record.m_StateGroup = lID;
		}

		return record;
	}

	private Observer(final CommunicationModel db, final boolean bRecursive, final boolean bNewID) {
		this.m_db = db;

		if (bNewID) {
			this.m_PrimaryKey = IDGenerator.createPrimaryKey();
		}

		if (bRecursive) {
			final State refState = State.createNewTree(db);

			if (refState != null) {
				this.m_State = refState.getPrimaryKey();
			}

			final StateGroup refStateGroup = StateGroup.createNewTree(db);

			if (refStateGroup != null) {
				this.m_StateGroup = refStateGroup.getPrimaryKey();
			}

		}

		if (this.m_db != null) {
			this.m_db.addObserver(this);
		}
	}

	public static final Observer createNewTree(final CommunicationModel db) {
		return new Observer(db, true, true);
	}

	public static final Observer createNew(final CommunicationModel db) {
		return new Observer(db, false, true);
	}

	public static final Observer createNew(final CommunicationModel db, final boolean bNewID) {
		return new Observer(db, false, bNewID);
	}

	public static final Observer create(final CommunicationModel db, final boolean bRecursive) {
		return new Observer(db, bRecursive, true);
	}

	public static final Observer generate() {
		Observer record = new Observer();
		record.m_PrimaryKey = IDGenerator.createPrimaryKey();
		return record;
	}

	static final Observer generateWithoutKey() {
		return new Observer();
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

	public final synchronized long getStateGroup() {
		return this.m_StateGroup;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setStateGroup(long value) {
		return this.setStateGroup(value, false);
	}

	public final synchronized boolean setStateGroup(long value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_StateGroup = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final synchronized String getActionClass() {
		return this.m_ActionClass;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setActionClass(String value) {
		return this.setActionClass(value, false);
	}

	public final synchronized boolean setActionClass(String value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_ActionClass = value;
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

	public final State getStateRef() {
		State record = null;
		final ISource server = ServerFactory.create();
		if (server != null) {
			record = server.findByIDState(this.getState());
		}
		return record;
	}

	public final StateGroup getStateGroupRef() {
		StateGroup record = null;
		final ISource server = ServerFactory.create();
		if (server != null) {
			record = server.findByIDStateGroup(this.getStateGroup());
		}
		return record;
	}

	public final boolean equals(final Object obj) {
		if (obj instanceof Observer) {
			return ((Observer) obj).getPrimaryKey() == this.getPrimaryKey();
		}
		return false;
	}

	public final int hashCode() {
		return (int) (this.m_PrimaryKey % 1000);
	}

	public final String toString() {
		final ISource server = ServerFactory.create();

		final StringBuilder strBuilder = new StringBuilder();

		final State firstRecord = server.findByIDState(this.m_State);

		if (firstRecord != null) {
			strBuilder.append(firstRecord.toString());
		}

		strBuilder.append("  ");

		final StateGroup secondRecord = server.findByIDStateGroup(this.m_StateGroup);

		if (secondRecord != null) {
			strBuilder.append(secondRecord.toString());
		}

		return strBuilder.toString();
	}

	public static final Observer load(final Node node, final CommunicationModel db) {
		final Observer observer = new Observer();
		observer.m_db = db;
		if (node != null) {
			final NodeList listElementChildren = node.getChildNodes();
			final int nNodeCount = listElementChildren.getLength();
			for (int i = 0; i < nNodeCount; i++) {
				final Node nodeChild = listElementChildren.item(i);
				if (XMLParser.isElementNode(nodeChild)) {
					final String strNodeName = nodeChild.getNodeName();
					if (strNodeName.equals("PrimaryKey")) {
						observer.m_PrimaryKey = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("ServerReplicationVersion")) {
						observer.m_ServerReplicationVersion = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("State")) {
						observer.m_State = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("StateGroup")) {
						observer.m_StateGroup = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("ActionClass")) {
						observer.m_ActionClass = XMLParser.loadString(nodeChild);
						continue;
					}

					if (strNodeName.equals("Description")) {
						observer.m_Description = XMLParser.loadString(nodeChild);
						continue;
					}

				}
			}
		}

		return observer;
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

			strBuf.append("<StateGroup>");
			strBuf.append(Long.toString(this.m_StateGroup));
			strBuf.append("</StateGroup>");

			strBuf.append("<ActionClass>");
			strBuf.append(XMLParser.escapeSequence(this.m_ActionClass));
			strBuf.append("</ActionClass>");

			strBuf.append("<Description>");
			strBuf.append(XMLParser.escapeSequence(this.m_Description));
			strBuf.append("</Description>");

			strBuf.append("</RECORD>");
		}

		return strBuf.toString();
	}

	public final String differences(final IRecordable theOther) {
		if (!(theOther instanceof Observer)) {
			return "";
		}
		final Observer other = (Observer) theOther;
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
		if (this.m_StateGroup != other.m_StateGroup) {
			s.append("State Group:  ");
			final StateGroup refThis = this.getStateGroupRef();
			if (refThis != null) {
				s.append(refThis.toString());
			} else {
				s.append("---");
			}
			s.append(" --> ");
			final StateGroup refOther = other.getStateGroupRef();
			if (refOther != null) {
				s.append(refOther.toString());
			} else {
				s.append("---");
			}
			s.append("\r\n");
		}
		if (!this.m_ActionClass.equals(other.m_ActionClass)) {
			s.append("Action Class:  ");
			s.append(this.m_ActionClass);
			s.append(" --> ");
			s.append(other.m_ActionClass);
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

				final String strValue2 = "" + this.m_State;
				XMLParser.writeLine(1, out, "State", strValue2, true);

				final String strValue3 = "" + this.m_StateGroup;
				XMLParser.writeLine(1, out, "StateGroup", strValue3, true);

				final String strValue4 = "" + this.m_ActionClass;
				XMLParser.writeLine(1, out, "ActionClass", strValue4, true);

				final String strValue5 = "" + this.m_Description;
				XMLParser.writeLine(1, out, "Description", strValue5, true);

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
			strBuf.append("<Observer>");
			strBuf.append(this.toXML());
			strBuf.append("</Observer>");
			strBuf.append("</SET>");

			bSaved = server.update(strBuf.toString());

			this.setDirty(!bSaved);
		}

		return bSaved;
	}

	public static final void sortByPrimaryKey(final List<Observer> list, final boolean ascending) {
		Collections.sort(list, new ComparatorPrimaryKey(ascending));
	}

	public static final void sortByServerReplicationVersion(final List<Observer> list, final boolean ascending) {
		Collections.sort(list, new ComparatorServerReplicationVersion(ascending));
	}

	public static final void sortByState(final List<Observer> list, final boolean ascending) {
		Collections.sort(list, new ComparatorState(ascending));
	}

	public static final void sortByStateGroup(final List<Observer> list, final boolean ascending) {
		Collections.sort(list, new ComparatorStateGroup(ascending));
	}

	public static final void sortByActionClass(final List<Observer> list, final boolean ascending) {
		Collections.sort(list, new ComparatorActionClass(ascending));
	}

	public static final void sortByDescription(final List<Observer> list, final boolean ascending) {
		Collections.sort(list, new ComparatorDescription(ascending));
	}

	public static final class ComparatorPrimaryKey implements Comparator<Observer> {
		private final boolean ascending;

		public ComparatorPrimaryKey(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final Observer obj1, final Observer obj2) {
			final Observer observer1;
			final Observer observer2;
			if (this.ascending) {
				observer1 = obj1;
				observer2 = obj2;
			} else {
				observer1 = obj2;
				observer2 = obj1;
			}

			final Long long1 = new Long(observer1.m_PrimaryKey);
			final Long long2 = new Long(observer2.m_PrimaryKey);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorServerReplicationVersion implements Comparator<Observer> {
		private final boolean ascending;

		public ComparatorServerReplicationVersion(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final Observer obj1, final Observer obj2) {
			final Observer observer1;
			final Observer observer2;
			if (this.ascending) {
				observer1 = obj1;
				observer2 = obj2;
			} else {
				observer1 = obj2;
				observer2 = obj1;
			}

			final Long long1 = new Long(observer1.m_ServerReplicationVersion);
			final Long long2 = new Long(observer2.m_ServerReplicationVersion);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorState implements Comparator<Observer> {
		private final boolean ascending;

		public ComparatorState(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final Observer obj1, final Observer obj2) {
			final Observer observer1;
			final Observer observer2;
			if (this.ascending) {
				observer1 = obj1;
				observer2 = obj2;
			} else {
				observer1 = obj2;
				observer2 = obj1;
			}

			final State ref1 = observer1.getStateRef();
			final State ref2 = observer2.getStateRef();
			if ((ref1 != null) && (ref2 != null)) {
				return ref1.toString().compareTo(ref2.toString());
			}
			return 0;
		}
	}

	public static final class ComparatorStateGroup implements Comparator<Observer> {
		private final boolean ascending;

		public ComparatorStateGroup(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final Observer obj1, final Observer obj2) {
			final Observer observer1;
			final Observer observer2;
			if (this.ascending) {
				observer1 = obj1;
				observer2 = obj2;
			} else {
				observer1 = obj2;
				observer2 = obj1;
			}

			final StateGroup ref1 = observer1.getStateGroupRef();
			final StateGroup ref2 = observer2.getStateGroupRef();
			if ((ref1 != null) && (ref2 != null)) {
				return ref1.toString().compareTo(ref2.toString());
			}
			return 0;
		}
	}

	public static final class ComparatorActionClass implements Comparator<Observer> {
		private final boolean ascending;

		public ComparatorActionClass(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final Observer obj1, final Observer obj2) {
			final Observer observer1;
			final Observer observer2;
			if (this.ascending) {
				observer1 = obj1;
				observer2 = obj2;
			} else {
				observer1 = obj2;
				observer2 = obj1;
			}

			final String string1 = observer1.m_ActionClass.toLowerCase();
			final String string2 = observer2.m_ActionClass.toLowerCase();

			return string1.compareTo(string2);
		}
	}

	public static final class ComparatorDescription implements Comparator<Observer> {
		private final boolean ascending;

		public ComparatorDescription(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final Observer obj1, final Observer obj2) {
			final Observer observer1;
			final Observer observer2;
			if (this.ascending) {
				observer1 = obj1;
				observer2 = obj2;
			} else {
				observer1 = obj2;
				observer2 = obj1;
			}

			final String string1 = observer1.m_Description.toLowerCase();
			final String string2 = observer2.m_Description.toLowerCase();

			return string1.compareTo(string2);
		}
	}

	/*
	 * export this object to a database export fragment
	 */
	public final boolean export(final CommunicationModel dbExport) {
		boolean bExported = false;
		if (dbExport != null) {
			bExported = dbExport.addObserver(this);
			final State record2 = ServerFactory.create().findByIDState(this.m_State);
			if (record2 != null) {
				record2.export(dbExport);
			}
			final StateGroup record3 = ServerFactory.create().findByIDStateGroup(this.m_StateGroup);
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
			final List<ObserverLink> list0 = ServerFactory.create()
					.referencesObserverLinkBySource(this.getPrimaryKey());
			for (ObserverLink element : list0) {
				if (recursive) {
					element.contextExport(dbExport, recursive);
				} else {
					element.export(dbExport);
				}
			}
			final List<ObserverLink> list1 = ServerFactory.create()
					.referencesObserverLinkByDestination(this.getPrimaryKey());
			for (ObserverLink element : list1) {
				if (recursive) {
					element.contextExport(dbExport, recursive);
				} else {
					element.export(dbExport);
				}
			}
			final List<NotificationScope> list2 = ServerFactory.create()
					.referencesNotificationScopeByObserver(this.getPrimaryKey());
			for (NotificationScope element : list2) {
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
		XMLParser.writeLine(1, out, "Observer", null, true);

		XMLParser.writeLine(2, out, "PrimaryKey", Long.toString(this.m_PrimaryKey), true);
		XMLParser.writeLine(2, out, "ServerReplicationVersion", Long.toString(this.m_ServerReplicationVersion), true);
		XMLParser.writeLine(2, out, "State", Long.toString(this.m_State), true);
		XMLParser.writeLine(2, out, "StateGroup", Long.toString(this.m_StateGroup), true);
		XMLParser.writeLine(2, out, "ActionClass", this.m_ActionClass, true);
		XMLParser.writeLine(2, out, "Description", this.m_Description, true);

		XMLParser.writeLine(1, out, "Observer", null, false);

		final State state = this.getStateRef();
		if (state != null) {
			state.export(out);
		}

		final StateGroup stategroup = this.getStateGroupRef();
		if (stategroup != null) {
			stategroup.export(out);
		}

		return true;
	}

}
