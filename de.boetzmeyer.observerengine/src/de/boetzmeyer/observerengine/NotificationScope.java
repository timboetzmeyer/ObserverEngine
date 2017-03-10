package de.boetzmeyer.observerengine;

import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class NotificationScope implements INotificationScope {
	private static final long DEFAULT_PRIMARYKEY = 0L;
	private static final long DEFAULT_SERVERREPLICATIONVERSION = 0L;
	private static final long DEFAULT_OBSERVER = 0L;
	private static final long DEFAULT_MODULE = 0L;

	/**
	 * 
	 * the table attribute names
	 */
	public static final String[] ATTRIBUTE_NAMES = new String[] { "Observer", "Module" };

	/**
	 * 
	 * the table header column names
	 */
	public static final String[] TABLE_HEADER = new String[] { "Observer", "Module" };

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
	private long m_Observer = DEFAULT_OBSERVER;

	/**
	 * 
	 * 
	 */
	private long m_Module = DEFAULT_MODULE;

	public synchronized final boolean isDirty() {
		return this.m_bDirty;
	}

	public synchronized final void setDirty(final boolean bDirty) {
		this.m_bDirty = bDirty;
	}

	public synchronized final boolean isValid() {
		if (this.getObserver() == 0L) {
			return false;
		}
		if (this.getModule() == 0L) {
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
		if (this.getObserver() == 0L) {
			s.append("  Observer fehlt!");
		}
		if (this.getModule() == 0L) {
			s.append("  Module fehlt!");
		}
		return s.toString();
	}

	public final CommunicationModel getDB() {
		return this.m_db;
	}

	public final synchronized boolean sameContent(final IRecordable theOther) {
		if (!(theOther instanceof NotificationScope)) {
			return false;
		}
		final NotificationScope other = (NotificationScope) theOther;
		if (null == other)
			return false;

		if (other.m_PrimaryKey != this.m_PrimaryKey)
			return false;

		if (other.m_ServerReplicationVersion != this.m_ServerReplicationVersion)
			return false;

		if (other.m_Observer != this.m_Observer)
			return false;

		if (other.m_Module != this.m_Module)
			return false;

		return true;
	}

	private void increaseServerReplicationVersion() {
		this.setServerReplicationVersion(1 + this.getServerReplicationVersion());
	}

	public final void overwrite(final NotificationScope source) {
		this.overwrite(source, false);
	}

	public final void overwrite(final NotificationScope source, final boolean bIncreaseVersion) {
		if (source != null) {
			if (bIncreaseVersion) {
				this.increaseServerReplicationVersion();
			}

			this.setObserver(source.getObserver(), true);
			this.setModule(source.getModule(), true);
		}
	}

	private NotificationScope() {
		this.m_db = null;
	}

	public final NotificationScope copy() {
		final NotificationScope recordCopy = new NotificationScope();

		synchronized (this) {
			recordCopy.m_PrimaryKey = this.m_PrimaryKey;
			recordCopy.m_ServerReplicationVersion = this.m_ServerReplicationVersion;
			recordCopy.m_Observer = this.m_Observer;
			recordCopy.m_Module = this.m_Module;
		}

		return recordCopy;
	}

	public static final NotificationScope createByObserver(final long lID) {
		final NotificationScope record = generate();

		if (record != null) {
			record.m_Observer = lID;
		}

		return record;
	}

	public static final NotificationScope createByModule(final long lID) {
		final NotificationScope record = generate();

		if (record != null) {
			record.m_Module = lID;
		}

		return record;
	}

	private NotificationScope(final CommunicationModel db, final boolean bRecursive, final boolean bNewID) {
		this.m_db = db;

		if (bNewID) {
			this.m_PrimaryKey = IDGenerator.createPrimaryKey();
		}

		if (bRecursive) {
			final Observer refObserver = Observer.createNewTree(db);

			if (refObserver != null) {
				this.m_Observer = refObserver.getPrimaryKey();
			}

			final Module refModule = Module.createNewTree(db);

			if (refModule != null) {
				this.m_Module = refModule.getPrimaryKey();
			}

		}

		if (this.m_db != null) {
			this.m_db.addNotificationScope(this);
		}
	}

	public static final NotificationScope createNewTree(final CommunicationModel db) {
		return new NotificationScope(db, true, true);
	}

	public static final NotificationScope createNew(final CommunicationModel db) {
		return new NotificationScope(db, false, true);
	}

	public static final NotificationScope createNew(final CommunicationModel db, final boolean bNewID) {
		return new NotificationScope(db, false, bNewID);
	}

	public static final NotificationScope create(final CommunicationModel db, final boolean bRecursive) {
		return new NotificationScope(db, bRecursive, true);
	}

	public static final NotificationScope generate() {
		NotificationScope record = new NotificationScope();
		record.m_PrimaryKey = IDGenerator.createPrimaryKey();
		return record;
	}

	static final NotificationScope generateWithoutKey() {
		return new NotificationScope();
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

	public final synchronized long getObserver() {
		return this.m_Observer;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setObserver(long value) {
		return this.setObserver(value, false);
	}

	public final synchronized boolean setObserver(long value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_Observer = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final synchronized long getModule() {
		return this.m_Module;
	}

	/**
	 * 
	 * 
	 */
	public final boolean setModule(long value) {
		return this.setModule(value, false);
	}

	public final synchronized boolean setModule(long value, final boolean bReloadFromServer) {
		boolean bSet = false;

		this.m_Module = value;
		bSet = true;

		if (!bReloadFromServer && bSet) {
			this.setDirty(true);
		}

		return bSet;
	}

	public final Observer getObserverRef() {
		Observer record = null;
		final ISource server = ServerFactory.create();
		if (server != null) {
			record = server.findByIDObserver(this.getObserver());
		}
		return record;
	}

	public final Module getModuleRef() {
		Module record = null;
		final ISource server = ServerFactory.create();
		if (server != null) {
			record = server.findByIDModule(this.getModule());
		}
		return record;
	}

	public final boolean equals(final Object obj) {
		if (obj instanceof NotificationScope) {
			return ((NotificationScope) obj).getPrimaryKey() == this.getPrimaryKey();
		}
		return false;
	}

	public final int hashCode() {
		return (int) (this.m_PrimaryKey % 1000);
	}

	public final String toString() {
		final ISource server = ServerFactory.create();

		final StringBuilder strBuilder = new StringBuilder();

		final Module firstRecord = server.findByIDModule(this.m_Module);

		if (firstRecord != null) {
			strBuilder.append(firstRecord.toString());
		}

		strBuilder.append(" : ");

		final Observer secondRecord = server.findByIDObserver(this.m_Observer);

		if (secondRecord != null) {
			strBuilder.append(secondRecord.toString());
		}

		return strBuilder.toString();
	}

	public static final NotificationScope load(final Node node, final CommunicationModel db) {
		final NotificationScope notificationscope = new NotificationScope();
		notificationscope.m_db = db;
		if (node != null) {
			final NodeList listElementChildren = node.getChildNodes();
			final int nNodeCount = listElementChildren.getLength();
			for (int i = 0; i < nNodeCount; i++) {
				final Node nodeChild = listElementChildren.item(i);
				if (XMLParser.isElementNode(nodeChild)) {
					final String strNodeName = nodeChild.getNodeName();
					if (strNodeName.equals("PrimaryKey")) {
						notificationscope.m_PrimaryKey = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("ServerReplicationVersion")) {
						notificationscope.m_ServerReplicationVersion = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("Observer")) {
						notificationscope.m_Observer = XMLParser.loadLong(nodeChild);
						continue;
					}

					if (strNodeName.equals("Module")) {
						notificationscope.m_Module = XMLParser.loadLong(nodeChild);
						continue;
					}

				}
			}
		}

		return notificationscope;
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

			strBuf.append("<Observer>");
			strBuf.append(Long.toString(this.m_Observer));
			strBuf.append("</Observer>");

			strBuf.append("<Module>");
			strBuf.append(Long.toString(this.m_Module));
			strBuf.append("</Module>");

			strBuf.append("</RECORD>");
		}

		return strBuf.toString();
	}

	public final String differences(final IRecordable theOther) {
		if (!(theOther instanceof NotificationScope)) {
			return "";
		}
		final NotificationScope other = (NotificationScope) theOther;
		final StringBuilder s = new StringBuilder();
		if (this.m_Observer != other.m_Observer) {
			s.append("Observer:  ");
			final Observer refThis = this.getObserverRef();
			if (refThis != null) {
				s.append(refThis.toString());
			} else {
				s.append("---");
			}
			s.append(" --> ");
			final Observer refOther = other.getObserverRef();
			if (refOther != null) {
				s.append(refOther.toString());
			} else {
				s.append("---");
			}
			s.append("\r\n");
		}
		if (this.m_Module != other.m_Module) {
			s.append("Module:  ");
			final Module refThis = this.getModuleRef();
			if (refThis != null) {
				s.append(refThis.toString());
			} else {
				s.append("---");
			}
			s.append(" --> ");
			final Module refOther = other.getModuleRef();
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

				final String strValue2 = "" + this.m_Observer;
				XMLParser.writeLine(1, out, "Observer", strValue2, true);

				final String strValue3 = "" + this.m_Module;
				XMLParser.writeLine(1, out, "Module", strValue3, true);

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
			strBuf.append("<NotificationScope>");
			strBuf.append(this.toXML());
			strBuf.append("</NotificationScope>");
			strBuf.append("</SET>");

			bSaved = server.update(strBuf.toString());

			this.setDirty(!bSaved);
		}

		return bSaved;
	}

	public static final void sortByPrimaryKey(final List<NotificationScope> list, final boolean ascending) {
		Collections.sort(list, new ComparatorPrimaryKey(ascending));
	}

	public static final void sortByServerReplicationVersion(final List<NotificationScope> list,
			final boolean ascending) {
		Collections.sort(list, new ComparatorServerReplicationVersion(ascending));
	}

	public static final void sortByObserver(final List<NotificationScope> list, final boolean ascending) {
		Collections.sort(list, new ComparatorObserver(ascending));
	}

	public static final void sortByModule(final List<NotificationScope> list, final boolean ascending) {
		Collections.sort(list, new ComparatorModule(ascending));
	}

	public static final class ComparatorPrimaryKey implements Comparator<NotificationScope> {
		private final boolean ascending;

		public ComparatorPrimaryKey(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final NotificationScope obj1, final NotificationScope obj2) {
			final NotificationScope notificationscope1;
			final NotificationScope notificationscope2;
			if (this.ascending) {
				notificationscope1 = obj1;
				notificationscope2 = obj2;
			} else {
				notificationscope1 = obj2;
				notificationscope2 = obj1;
			}

			final Long long1 = new Long(notificationscope1.m_PrimaryKey);
			final Long long2 = new Long(notificationscope2.m_PrimaryKey);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorServerReplicationVersion implements Comparator<NotificationScope> {
		private final boolean ascending;

		public ComparatorServerReplicationVersion(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final NotificationScope obj1, final NotificationScope obj2) {
			final NotificationScope notificationscope1;
			final NotificationScope notificationscope2;
			if (this.ascending) {
				notificationscope1 = obj1;
				notificationscope2 = obj2;
			} else {
				notificationscope1 = obj2;
				notificationscope2 = obj1;
			}

			final Long long1 = new Long(notificationscope1.m_ServerReplicationVersion);
			final Long long2 = new Long(notificationscope2.m_ServerReplicationVersion);

			return long1.compareTo(long2);
		}
	}

	public static final class ComparatorObserver implements Comparator<NotificationScope> {
		private final boolean ascending;

		public ComparatorObserver(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final NotificationScope obj1, final NotificationScope obj2) {
			final NotificationScope notificationscope1;
			final NotificationScope notificationscope2;
			if (this.ascending) {
				notificationscope1 = obj1;
				notificationscope2 = obj2;
			} else {
				notificationscope1 = obj2;
				notificationscope2 = obj1;
			}

			final Observer ref1 = notificationscope1.getObserverRef();
			final Observer ref2 = notificationscope2.getObserverRef();
			if ((ref1 != null) && (ref2 != null)) {
				return ref1.toString().compareTo(ref2.toString());
			}
			return 0;
		}
	}

	public static final class ComparatorModule implements Comparator<NotificationScope> {
		private final boolean ascending;

		public ComparatorModule(final boolean ascending) {
			this.ascending = ascending;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(final NotificationScope obj1, final NotificationScope obj2) {
			final NotificationScope notificationscope1;
			final NotificationScope notificationscope2;
			if (this.ascending) {
				notificationscope1 = obj1;
				notificationscope2 = obj2;
			} else {
				notificationscope1 = obj2;
				notificationscope2 = obj1;
			}

			final Module ref1 = notificationscope1.getModuleRef();
			final Module ref2 = notificationscope2.getModuleRef();
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
			bExported = dbExport.addNotificationScope(this);
			final Observer record2 = ServerFactory.create().findByIDObserver(this.m_Observer);
			if (record2 != null) {
				record2.export(dbExport);
			}
			final Module record3 = ServerFactory.create().findByIDModule(this.m_Module);
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
		XMLParser.writeLine(1, out, "NotificationScope", null, true);

		XMLParser.writeLine(2, out, "PrimaryKey", Long.toString(this.m_PrimaryKey), true);
		XMLParser.writeLine(2, out, "ServerReplicationVersion", Long.toString(this.m_ServerReplicationVersion), true);
		XMLParser.writeLine(2, out, "Observer", Long.toString(this.m_Observer), true);
		XMLParser.writeLine(2, out, "Module", Long.toString(this.m_Module), true);

		XMLParser.writeLine(1, out, "NotificationScope", null, false);

		final Observer observer = this.getObserverRef();
		if (observer != null) {
			observer.export(out);
		}

		final Module module = this.getModuleRef();
		if (module != null) {
			module.export(out);
		}

		return true;
	}

}
