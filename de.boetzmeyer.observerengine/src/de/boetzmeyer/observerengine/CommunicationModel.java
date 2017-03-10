package de.boetzmeyer.observerengine;

import java.io.File;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

final class CommunicationModel {
	private static final String MODEL_VERSION_FILE = "_ModelVersion.xml";
	private static final String DB_EXTENSION = ".zip";
	private static final String DB_FILE_COPY_SEPARATOR = "__";
	private static final String DB_FILE_COPY = "CommunicationModel" + DB_FILE_COPY_SEPARATOR;

	private final String m_strDatabaseDir;
	private final ModelVersion m_modelVersion = ModelVersion.LOCAL_MODEL;

	public static final String OBSERVER = "Observer";
	public static final String OBSERVERLINK = "ObserverLink";
	public static final String STATE = "State";
	public static final String STATETYPE = "StateType";
	public static final String STATEGROUP = "StateGroup";
	public static final String STATECHANGE = "StateChange";
	public static final String STATEGROUPLINK = "StateGroupLink";
	public static final String NOTIFICATIONSCOPE = "NotificationScope";
	public static final String MODULE = "Module";
	private boolean m_bDirty = false;

	private boolean m_bDirtyObserver = false;
	private final Collection<Observer> m_setObserver = new HashSet<Observer>();
	private boolean m_bDirtyObserverLink = false;
	private final Collection<ObserverLink> m_setObserverLink = new HashSet<ObserverLink>();
	private boolean m_bDirtyState = false;
	private final Collection<State> m_setState = new HashSet<State>();
	private boolean m_bDirtyStateType = false;
	private final Collection<StateType> m_setStateType = new HashSet<StateType>();
	private boolean m_bDirtyStateGroup = false;
	private final Collection<StateGroup> m_setStateGroup = new HashSet<StateGroup>();
	private boolean m_bDirtyStateChange = false;
	private final Collection<StateChange> m_setStateChange = new HashSet<StateChange>();
	private boolean m_bDirtyStateGroupLink = false;
	private final Collection<StateGroupLink> m_setStateGroupLink = new HashSet<StateGroupLink>();
	private boolean m_bDirtyNotificationScope = false;
	private final Collection<NotificationScope> m_setNotificationScope = new HashSet<NotificationScope>();
	private boolean m_bDirtyModule = false;
	private final Collection<Module> m_setModule = new HashSet<Module>();

	private CommunicationModel() {
		this(null, false);
	}

	private CommunicationModel(final String strDatabaseDir, final boolean bPermanent) {
		this.m_strDatabaseDir = strDatabaseDir;
		if (this.hasFileSystemMirror()) {
			if (this.loadAll()) {
				if (bPermanent) {
					Runtime.getRuntime().addShutdownHook(new ThreadShutdownHook());
				}
			} else {
				throw new RuntimeException("Loading database failed");
			}
		}
	}

	public final ModelVersion getModelVersion() {
		return this.m_modelVersion;
	}

	public final String getDatabaseDir() {
		return this.m_strDatabaseDir;
	}

	public final String getFilePath() {
		return this.m_strDatabaseDir + File.separatorChar + "CommunicationModel.zip";
	}

	public final boolean isCompatible(final ModelVersion clientModel) {
		return this.m_modelVersion.isCompatibleWith(clientModel);
	}

	public boolean hasFileSystemMirror() {
		return (this.m_strDatabaseDir != null);
	}

	public final boolean isLocked() {
		return false;
	}

	public final boolean setLocked(final boolean bLocked) {
		return false;
	}

	public static final CommunicationModel createEmpty() {
		return new CommunicationModel(null, false);
	}

	public static final CommunicationModel create(final String strDatabaseDir) {
		return create(strDatabaseDir, true);
	}

	public static final CommunicationModel create(final String strDatabaseDir, final boolean bPermanent) {
		CommunicationModel db = null;
		try {
			db = new CommunicationModel(strDatabaseDir, bPermanent);
		} catch (final Throwable t) {
			throw new RuntimeException("LOAD  " + t.getMessage());
		}
		return db;
	}

	private ModelVersion loadModelVersion(final String strDatabaseDir) {
		final String strPathPath = strDatabaseDir + File.separatorChar + MODEL_VERSION_FILE;
		return ModelVersion.load(strPathPath);
	}

	public synchronized final void setDirty(final boolean bDirty) {
		this.m_bDirty = bDirty;
	}

	public final boolean isEmpty() {
		synchronized (this) {
			if (this.m_setObserver.size() > 0)
				return false;
			if (this.m_setObserverLink.size() > 0)
				return false;
			if (this.m_setState.size() > 0)
				return false;
			if (this.m_setStateType.size() > 0)
				return false;
			if (this.m_setStateGroup.size() > 0)
				return false;
			if (this.m_setStateChange.size() > 0)
				return false;
			if (this.m_setStateGroupLink.size() > 0)
				return false;
			if (this.m_setNotificationScope.size() > 0)
				return false;
			if (this.m_setModule.size() > 0)
				return false;
		}
		return true;
	}

	public final int size() {
		int nRecordCount = 0;
		synchronized (this) {
			nRecordCount += this.sizeObserver();
			nRecordCount += this.sizeObserverLink();
			nRecordCount += this.sizeState();
			nRecordCount += this.sizeStateType();
			nRecordCount += this.sizeStateGroup();
			nRecordCount += this.sizeStateChange();
			nRecordCount += this.sizeStateGroupLink();
			nRecordCount += this.sizeNotificationScope();
			nRecordCount += this.sizeModule();
		}
		return nRecordCount;
	}

	private final boolean isDirtyObserver() {
		boolean bDirty = this.m_bDirtyObserver;
		if (!bDirty) {
			final Collection<Observer> collObserver;
			synchronized (this.m_setObserver) {
				collObserver = new ArrayList<Observer>(this.m_setObserver);
			}
			final Iterator<Observer> it = collObserver.iterator();
			while (it.hasNext() && !bDirty) {
				final Observer nextRecord = it.next();
				bDirty = (nextRecord != null) && nextRecord.isDirty();
			}
		}
		return bDirty;
	}

	public final List<Observer> listModifiedObserver() {
		final List<Observer> listModified = new ArrayList<Observer>();
		final Collection<Observer> collObserver;
		synchronized (this.m_setObserver) {
			collObserver = new ArrayList<Observer>(this.m_setObserver);
		}
		final Iterator<Observer> it = collObserver.iterator();
		while (it.hasNext()) {
			final Observer nextRecord = it.next();
			if ((nextRecord != null) && nextRecord.isDirty()) {
				listModified.add(nextRecord);
			}
		}
		return listModified;
	}

	public final List<Observer> listObserver() {
		synchronized (this.m_setObserver) {
			return new ArrayList<Observer>(this.m_setObserver);
		}
	}

	public final List<Observer> listObserver(final List<Condition> listConditions) {
		synchronized (this.m_setObserver) {
			return new ArrayList<Observer>(this.m_setObserver);
		}
	}

	public final int sizeObserver() {
		synchronized (this.m_setObserver) {
			return this.m_setObserver.size();
		}
	}

	public final void clearObserver() {
		synchronized (this.m_setObserver) {
			this.m_setObserver.clear();
		}
		if (this.hasFileSystemMirror()) {
			System.out.println("DELETE ALL of table [Observer]");
		}
	}

	public final boolean addAllObserver(final Collection<Observer> coll) {
		return this.addAllObserver(coll, false);
	}

	public final boolean addAllObserver(final Collection<Observer> coll, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setObserver) {
			bAdded = this.m_setObserver.addAll(coll);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyObserver(true);
				this.setDirty(true);
				if (this.hasFileSystemMirror()) {
					final Iterator<Observer> it = coll.iterator();
					while (it.hasNext()) {
						final Observer record = it.next();
						if (record != null) {
							System.out.println("INSERT " + "Observer [" + record.toString() + "]");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean addObserver(final Observer record) {
		return this.addObserver(record, false);
	}

	public final boolean addObserver(final Observer record, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setObserver) {
			bAdded = this.m_setObserver.add(record);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyObserver(true);
				this.setDirty(true);
				if ((record != null) && this.hasFileSystemMirror()) {
					System.out.println("INSERT " + "Observer [" + record.toString() + "]");
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeAllObserver(final Collection<Observer> coll) {
		final boolean bRemoved;
		synchronized (this.m_setObserver) {
			bRemoved = this.m_setObserver.removeAll(coll);
		}
		if (bRemoved) {
			this.setDirtyObserver(true);
			this.setDirty(true);
			if (this.hasFileSystemMirror()) {
				final Iterator<Observer> it = coll.iterator();
				while (it.hasNext()) {
					final Observer record = it.next();
					if (record != null) {
						System.out.println("DELETE " + "Observer [" + record.toString() + "]");
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeObserver(final Observer record) {
		final boolean bRemoved;
		synchronized (this.m_setObserver) {
			bRemoved = this.m_setObserver.remove(record);
		}
		if (bRemoved) {
			this.setDirtyObserver(true);
			this.setDirty(true);
			if ((record != null) && this.hasFileSystemMirror()) {
				System.out.println("DELETE " + "Observer [" + record.toString() + "]");
			}
			return true;
		}
		return false;
	}

	private synchronized void setDirtyObserver(final boolean bDirty) {
		this.m_bDirtyObserver = bDirty;
	}

	private final boolean isDirtyObserverLink() {
		boolean bDirty = this.m_bDirtyObserverLink;
		if (!bDirty) {
			final Collection<ObserverLink> collObserverLink;
			synchronized (this.m_setObserverLink) {
				collObserverLink = new ArrayList<ObserverLink>(this.m_setObserverLink);
			}
			final Iterator<ObserverLink> it = collObserverLink.iterator();
			while (it.hasNext() && !bDirty) {
				final ObserverLink nextRecord = it.next();
				bDirty = (nextRecord != null) && nextRecord.isDirty();
			}
		}
		return bDirty;
	}

	public final List<ObserverLink> listModifiedObserverLink() {
		final List<ObserverLink> listModified = new ArrayList<ObserverLink>();
		final Collection<ObserverLink> collObserverLink;
		synchronized (this.m_setObserverLink) {
			collObserverLink = new ArrayList<ObserverLink>(this.m_setObserverLink);
		}
		final Iterator<ObserverLink> it = collObserverLink.iterator();
		while (it.hasNext()) {
			final ObserverLink nextRecord = it.next();
			if ((nextRecord != null) && nextRecord.isDirty()) {
				listModified.add(nextRecord);
			}
		}
		return listModified;
	}

	public final List<ObserverLink> listObserverLink() {
		synchronized (this.m_setObserverLink) {
			return new ArrayList<ObserverLink>(this.m_setObserverLink);
		}
	}

	public final List<ObserverLink> listObserverLink(final List<Condition> listConditions) {
		synchronized (this.m_setObserverLink) {
			return new ArrayList<ObserverLink>(this.m_setObserverLink);
		}
	}

	public final int sizeObserverLink() {
		synchronized (this.m_setObserverLink) {
			return this.m_setObserverLink.size();
		}
	}

	public final void clearObserverLink() {
		synchronized (this.m_setObserverLink) {
			this.m_setObserverLink.clear();
		}
		if (this.hasFileSystemMirror()) {
			System.out.println("DELETE ALL of table [ObserverLink]");
		}
	}

	public final boolean addAllObserverLink(final Collection<ObserverLink> coll) {
		return this.addAllObserverLink(coll, false);
	}

	public final boolean addAllObserverLink(final Collection<ObserverLink> coll, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setObserverLink) {
			bAdded = this.m_setObserverLink.addAll(coll);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyObserverLink(true);
				this.setDirty(true);
				if (this.hasFileSystemMirror()) {
					final Iterator<ObserverLink> it = coll.iterator();
					while (it.hasNext()) {
						final ObserverLink record = it.next();
						if (record != null) {
							System.out.println("INSERT " + "ObserverLink [" + record.toString() + "]");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean addObserverLink(final ObserverLink record) {
		return this.addObserverLink(record, false);
	}

	public final boolean addObserverLink(final ObserverLink record, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setObserverLink) {
			bAdded = this.m_setObserverLink.add(record);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyObserverLink(true);
				this.setDirty(true);
				if ((record != null) && this.hasFileSystemMirror()) {
					System.out.println("INSERT " + "ObserverLink [" + record.toString() + "]");
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeAllObserverLink(final Collection<ObserverLink> coll) {
		final boolean bRemoved;
		synchronized (this.m_setObserverLink) {
			bRemoved = this.m_setObserverLink.removeAll(coll);
		}
		if (bRemoved) {
			this.setDirtyObserverLink(true);
			this.setDirty(true);
			if (this.hasFileSystemMirror()) {
				final Iterator<ObserverLink> it = coll.iterator();
				while (it.hasNext()) {
					final ObserverLink record = it.next();
					if (record != null) {
						System.out.println("DELETE " + "ObserverLink [" + record.toString() + "]");
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeObserverLink(final ObserverLink record) {
		final boolean bRemoved;
		synchronized (this.m_setObserverLink) {
			bRemoved = this.m_setObserverLink.remove(record);
		}
		if (bRemoved) {
			this.setDirtyObserverLink(true);
			this.setDirty(true);
			if ((record != null) && this.hasFileSystemMirror()) {
				System.out.println("DELETE " + "ObserverLink [" + record.toString() + "]");
			}
			return true;
		}
		return false;
	}

	private synchronized void setDirtyObserverLink(final boolean bDirty) {
		this.m_bDirtyObserverLink = bDirty;
	}

	private final boolean isDirtyState() {
		boolean bDirty = this.m_bDirtyState;
		if (!bDirty) {
			final Collection<State> collState;
			synchronized (this.m_setState) {
				collState = new ArrayList<State>(this.m_setState);
			}
			final Iterator<State> it = collState.iterator();
			while (it.hasNext() && !bDirty) {
				final State nextRecord = it.next();
				bDirty = (nextRecord != null) && nextRecord.isDirty();
			}
		}
		return bDirty;
	}

	public final List<State> listModifiedState() {
		final List<State> listModified = new ArrayList<State>();
		final Collection<State> collState;
		synchronized (this.m_setState) {
			collState = new ArrayList<State>(this.m_setState);
		}
		final Iterator<State> it = collState.iterator();
		while (it.hasNext()) {
			final State nextRecord = it.next();
			if ((nextRecord != null) && nextRecord.isDirty()) {
				listModified.add(nextRecord);
			}
		}
		return listModified;
	}

	public final List<State> listState() {
		synchronized (this.m_setState) {
			return new ArrayList<State>(this.m_setState);
		}
	}

	public final List<State> listState(final List<Condition> listConditions) {
		synchronized (this.m_setState) {
			return new ArrayList<State>(this.m_setState);
		}
	}

	public final int sizeState() {
		synchronized (this.m_setState) {
			return this.m_setState.size();
		}
	}

	public final void clearState() {
		synchronized (this.m_setState) {
			this.m_setState.clear();
		}
		if (this.hasFileSystemMirror()) {
			System.out.println("DELETE ALL of table [State]");
		}
	}

	public final boolean addAllState(final Collection<State> coll) {
		return this.addAllState(coll, false);
	}

	public final boolean addAllState(final Collection<State> coll, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setState) {
			bAdded = this.m_setState.addAll(coll);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyState(true);
				this.setDirty(true);
				if (this.hasFileSystemMirror()) {
					final Iterator<State> it = coll.iterator();
					while (it.hasNext()) {
						final State record = it.next();
						if (record != null) {
							System.out.println("INSERT " + "State [" + record.toString() + "]");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean addState(final State record) {
		return this.addState(record, false);
	}

	public final boolean addState(final State record, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setState) {
			bAdded = this.m_setState.add(record);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyState(true);
				this.setDirty(true);
				if ((record != null) && this.hasFileSystemMirror()) {
					System.out.println("INSERT " + "State [" + record.toString() + "]");
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeAllState(final Collection<State> coll) {
		final boolean bRemoved;
		synchronized (this.m_setState) {
			bRemoved = this.m_setState.removeAll(coll);
		}
		if (bRemoved) {
			this.setDirtyState(true);
			this.setDirty(true);
			if (this.hasFileSystemMirror()) {
				final Iterator<State> it = coll.iterator();
				while (it.hasNext()) {
					final State record = it.next();
					if (record != null) {
						System.out.println("DELETE " + "State [" + record.toString() + "]");
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeState(final State record) {
		final boolean bRemoved;
		synchronized (this.m_setState) {
			bRemoved = this.m_setState.remove(record);
		}
		if (bRemoved) {
			this.setDirtyState(true);
			this.setDirty(true);
			if ((record != null) && this.hasFileSystemMirror()) {
				System.out.println("DELETE " + "State [" + record.toString() + "]");
			}
			return true;
		}
		return false;
	}

	private synchronized void setDirtyState(final boolean bDirty) {
		this.m_bDirtyState = bDirty;
	}

	private final boolean isDirtyStateType() {
		boolean bDirty = this.m_bDirtyStateType;
		if (!bDirty) {
			final Collection<StateType> collStateType;
			synchronized (this.m_setStateType) {
				collStateType = new ArrayList<StateType>(this.m_setStateType);
			}
			final Iterator<StateType> it = collStateType.iterator();
			while (it.hasNext() && !bDirty) {
				final StateType nextRecord = it.next();
				bDirty = (nextRecord != null) && nextRecord.isDirty();
			}
		}
		return bDirty;
	}

	public final List<StateType> listModifiedStateType() {
		final List<StateType> listModified = new ArrayList<StateType>();
		final Collection<StateType> collStateType;
		synchronized (this.m_setStateType) {
			collStateType = new ArrayList<StateType>(this.m_setStateType);
		}
		final Iterator<StateType> it = collStateType.iterator();
		while (it.hasNext()) {
			final StateType nextRecord = it.next();
			if ((nextRecord != null) && nextRecord.isDirty()) {
				listModified.add(nextRecord);
			}
		}
		return listModified;
	}

	public final List<StateType> listStateType() {
		synchronized (this.m_setStateType) {
			return new ArrayList<StateType>(this.m_setStateType);
		}
	}

	public final List<StateType> listStateType(final List<Condition> listConditions) {
		synchronized (this.m_setStateType) {
			return new ArrayList<StateType>(this.m_setStateType);
		}
	}

	public final int sizeStateType() {
		synchronized (this.m_setStateType) {
			return this.m_setStateType.size();
		}
	}

	public final void clearStateType() {
		synchronized (this.m_setStateType) {
			this.m_setStateType.clear();
		}
		if (this.hasFileSystemMirror()) {
			System.out.println("DELETE ALL of table [StateType]");
		}
	}

	public final boolean addAllStateType(final Collection<StateType> coll) {
		return this.addAllStateType(coll, false);
	}

	public final boolean addAllStateType(final Collection<StateType> coll, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setStateType) {
			bAdded = this.m_setStateType.addAll(coll);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyStateType(true);
				this.setDirty(true);
				if (this.hasFileSystemMirror()) {
					final Iterator<StateType> it = coll.iterator();
					while (it.hasNext()) {
						final StateType record = it.next();
						if (record != null) {
							System.out.println("INSERT " + "StateType [" + record.toString() + "]");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean addStateType(final StateType record) {
		return this.addStateType(record, false);
	}

	public final boolean addStateType(final StateType record, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setStateType) {
			bAdded = this.m_setStateType.add(record);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyStateType(true);
				this.setDirty(true);
				if ((record != null) && this.hasFileSystemMirror()) {
					System.out.println("INSERT " + "StateType [" + record.toString() + "]");
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeAllStateType(final Collection<StateType> coll) {
		final boolean bRemoved;
		synchronized (this.m_setStateType) {
			bRemoved = this.m_setStateType.removeAll(coll);
		}
		if (bRemoved) {
			this.setDirtyStateType(true);
			this.setDirty(true);
			if (this.hasFileSystemMirror()) {
				final Iterator<StateType> it = coll.iterator();
				while (it.hasNext()) {
					final StateType record = it.next();
					if (record != null) {
						System.out.println("DELETE " + "StateType [" + record.toString() + "]");
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeStateType(final StateType record) {
		final boolean bRemoved;
		synchronized (this.m_setStateType) {
			bRemoved = this.m_setStateType.remove(record);
		}
		if (bRemoved) {
			this.setDirtyStateType(true);
			this.setDirty(true);
			if ((record != null) && this.hasFileSystemMirror()) {
				System.out.println("DELETE " + "StateType [" + record.toString() + "]");
			}
			return true;
		}
		return false;
	}

	private synchronized void setDirtyStateType(final boolean bDirty) {
		this.m_bDirtyStateType = bDirty;
	}

	private final boolean isDirtyStateGroup() {
		boolean bDirty = this.m_bDirtyStateGroup;
		if (!bDirty) {
			final Collection<StateGroup> collStateGroup;
			synchronized (this.m_setStateGroup) {
				collStateGroup = new ArrayList<StateGroup>(this.m_setStateGroup);
			}
			final Iterator<StateGroup> it = collStateGroup.iterator();
			while (it.hasNext() && !bDirty) {
				final StateGroup nextRecord = it.next();
				bDirty = (nextRecord != null) && nextRecord.isDirty();
			}
		}
		return bDirty;
	}

	public final List<StateGroup> listModifiedStateGroup() {
		final List<StateGroup> listModified = new ArrayList<StateGroup>();
		final Collection<StateGroup> collStateGroup;
		synchronized (this.m_setStateGroup) {
			collStateGroup = new ArrayList<StateGroup>(this.m_setStateGroup);
		}
		final Iterator<StateGroup> it = collStateGroup.iterator();
		while (it.hasNext()) {
			final StateGroup nextRecord = it.next();
			if ((nextRecord != null) && nextRecord.isDirty()) {
				listModified.add(nextRecord);
			}
		}
		return listModified;
	}

	public final List<StateGroup> listStateGroup() {
		synchronized (this.m_setStateGroup) {
			return new ArrayList<StateGroup>(this.m_setStateGroup);
		}
	}

	public final List<StateGroup> listStateGroup(final List<Condition> listConditions) {
		synchronized (this.m_setStateGroup) {
			return new ArrayList<StateGroup>(this.m_setStateGroup);
		}
	}

	public final int sizeStateGroup() {
		synchronized (this.m_setStateGroup) {
			return this.m_setStateGroup.size();
		}
	}

	public final void clearStateGroup() {
		synchronized (this.m_setStateGroup) {
			this.m_setStateGroup.clear();
		}
		if (this.hasFileSystemMirror()) {
			System.out.println("DELETE ALL of table [StateGroup]");
		}
	}

	public final boolean addAllStateGroup(final Collection<StateGroup> coll) {
		return this.addAllStateGroup(coll, false);
	}

	public final boolean addAllStateGroup(final Collection<StateGroup> coll, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setStateGroup) {
			bAdded = this.m_setStateGroup.addAll(coll);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyStateGroup(true);
				this.setDirty(true);
				if (this.hasFileSystemMirror()) {
					final Iterator<StateGroup> it = coll.iterator();
					while (it.hasNext()) {
						final StateGroup record = it.next();
						if (record != null) {
							System.out.println("INSERT " + "StateGroup [" + record.toString() + "]");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean addStateGroup(final StateGroup record) {
		return this.addStateGroup(record, false);
	}

	public final boolean addStateGroup(final StateGroup record, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setStateGroup) {
			bAdded = this.m_setStateGroup.add(record);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyStateGroup(true);
				this.setDirty(true);
				if ((record != null) && this.hasFileSystemMirror()) {
					System.out.println("INSERT " + "StateGroup [" + record.toString() + "]");
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeAllStateGroup(final Collection<StateGroup> coll) {
		final boolean bRemoved;
		synchronized (this.m_setStateGroup) {
			bRemoved = this.m_setStateGroup.removeAll(coll);
		}
		if (bRemoved) {
			this.setDirtyStateGroup(true);
			this.setDirty(true);
			if (this.hasFileSystemMirror()) {
				final Iterator<StateGroup> it = coll.iterator();
				while (it.hasNext()) {
					final StateGroup record = it.next();
					if (record != null) {
						System.out.println("DELETE " + "StateGroup [" + record.toString() + "]");
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeStateGroup(final StateGroup record) {
		final boolean bRemoved;
		synchronized (this.m_setStateGroup) {
			bRemoved = this.m_setStateGroup.remove(record);
		}
		if (bRemoved) {
			this.setDirtyStateGroup(true);
			this.setDirty(true);
			if ((record != null) && this.hasFileSystemMirror()) {
				System.out.println("DELETE " + "StateGroup [" + record.toString() + "]");
			}
			return true;
		}
		return false;
	}

	private synchronized void setDirtyStateGroup(final boolean bDirty) {
		this.m_bDirtyStateGroup = bDirty;
	}

	private final boolean isDirtyStateChange() {
		boolean bDirty = this.m_bDirtyStateChange;
		if (!bDirty) {
			final Collection<StateChange> collStateChange;
			synchronized (this.m_setStateChange) {
				collStateChange = new ArrayList<StateChange>(this.m_setStateChange);
			}
			final Iterator<StateChange> it = collStateChange.iterator();
			while (it.hasNext() && !bDirty) {
				final StateChange nextRecord = it.next();
				bDirty = (nextRecord != null) && nextRecord.isDirty();
			}
		}
		return bDirty;
	}

	public final List<StateChange> listModifiedStateChange() {
		final List<StateChange> listModified = new ArrayList<StateChange>();
		final Collection<StateChange> collStateChange;
		synchronized (this.m_setStateChange) {
			collStateChange = new ArrayList<StateChange>(this.m_setStateChange);
		}
		final Iterator<StateChange> it = collStateChange.iterator();
		while (it.hasNext()) {
			final StateChange nextRecord = it.next();
			if ((nextRecord != null) && nextRecord.isDirty()) {
				listModified.add(nextRecord);
			}
		}
		return listModified;
	}

	public final List<StateChange> listStateChange() {
		synchronized (this.m_setStateChange) {
			return new ArrayList<StateChange>(this.m_setStateChange);
		}
	}

	public final List<StateChange> listStateChange(final List<Condition> listConditions) {
		synchronized (this.m_setStateChange) {
			return new ArrayList<StateChange>(this.m_setStateChange);
		}
	}

	public final int sizeStateChange() {
		synchronized (this.m_setStateChange) {
			return this.m_setStateChange.size();
		}
	}

	public final void clearStateChange() {
		synchronized (this.m_setStateChange) {
			this.m_setStateChange.clear();
		}
		if (this.hasFileSystemMirror()) {
			System.out.println("DELETE ALL of table [StateChange]");
		}
	}

	public final boolean addAllStateChange(final Collection<StateChange> coll) {
		return this.addAllStateChange(coll, false);
	}

	public final boolean addAllStateChange(final Collection<StateChange> coll, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setStateChange) {
			bAdded = this.m_setStateChange.addAll(coll);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyStateChange(true);
				this.setDirty(true);
				if (this.hasFileSystemMirror()) {
					final Iterator<StateChange> it = coll.iterator();
					while (it.hasNext()) {
						final StateChange record = it.next();
						if (record != null) {
							System.out.println("INSERT " + "StateChange [" + record.toString() + "]");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean addStateChange(final StateChange record) {
		return this.addStateChange(record, false);
	}

	public final boolean addStateChange(final StateChange record, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setStateChange) {
			bAdded = this.m_setStateChange.add(record);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyStateChange(true);
				this.setDirty(true);
				if ((record != null) && this.hasFileSystemMirror()) {
					System.out.println("INSERT " + "StateChange [" + record.toString() + "]");
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeAllStateChange(final Collection<StateChange> coll) {
		final boolean bRemoved;
		synchronized (this.m_setStateChange) {
			bRemoved = this.m_setStateChange.removeAll(coll);
		}
		if (bRemoved) {
			this.setDirtyStateChange(true);
			this.setDirty(true);
			if (this.hasFileSystemMirror()) {
				final Iterator<StateChange> it = coll.iterator();
				while (it.hasNext()) {
					final StateChange record = it.next();
					if (record != null) {
						System.out.println("DELETE " + "StateChange [" + record.toString() + "]");
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeStateChange(final StateChange record) {
		final boolean bRemoved;
		synchronized (this.m_setStateChange) {
			bRemoved = this.m_setStateChange.remove(record);
		}
		if (bRemoved) {
			this.setDirtyStateChange(true);
			this.setDirty(true);
			if ((record != null) && this.hasFileSystemMirror()) {
				System.out.println("DELETE " + "StateChange [" + record.toString() + "]");
			}
			return true;
		}
		return false;
	}

	private synchronized void setDirtyStateChange(final boolean bDirty) {
		this.m_bDirtyStateChange = bDirty;
	}

	private final boolean isDirtyStateGroupLink() {
		boolean bDirty = this.m_bDirtyStateGroupLink;
		if (!bDirty) {
			final Collection<StateGroupLink> collStateGroupLink;
			synchronized (this.m_setStateGroupLink) {
				collStateGroupLink = new ArrayList<StateGroupLink>(this.m_setStateGroupLink);
			}
			final Iterator<StateGroupLink> it = collStateGroupLink.iterator();
			while (it.hasNext() && !bDirty) {
				final StateGroupLink nextRecord = it.next();
				bDirty = (nextRecord != null) && nextRecord.isDirty();
			}
		}
		return bDirty;
	}

	public final List<StateGroupLink> listModifiedStateGroupLink() {
		final List<StateGroupLink> listModified = new ArrayList<StateGroupLink>();
		final Collection<StateGroupLink> collStateGroupLink;
		synchronized (this.m_setStateGroupLink) {
			collStateGroupLink = new ArrayList<StateGroupLink>(this.m_setStateGroupLink);
		}
		final Iterator<StateGroupLink> it = collStateGroupLink.iterator();
		while (it.hasNext()) {
			final StateGroupLink nextRecord = it.next();
			if ((nextRecord != null) && nextRecord.isDirty()) {
				listModified.add(nextRecord);
			}
		}
		return listModified;
	}

	public final List<StateGroupLink> listStateGroupLink() {
		synchronized (this.m_setStateGroupLink) {
			return new ArrayList<StateGroupLink>(this.m_setStateGroupLink);
		}
	}

	public final List<StateGroupLink> listStateGroupLink(final List<Condition> listConditions) {
		synchronized (this.m_setStateGroupLink) {
			return new ArrayList<StateGroupLink>(this.m_setStateGroupLink);
		}
	}

	public final int sizeStateGroupLink() {
		synchronized (this.m_setStateGroupLink) {
			return this.m_setStateGroupLink.size();
		}
	}

	public final void clearStateGroupLink() {
		synchronized (this.m_setStateGroupLink) {
			this.m_setStateGroupLink.clear();
		}
		if (this.hasFileSystemMirror()) {
			System.out.println("DELETE ALL of table [StateGroupLink]");
		}
	}

	public final boolean addAllStateGroupLink(final Collection<StateGroupLink> coll) {
		return this.addAllStateGroupLink(coll, false);
	}

	public final boolean addAllStateGroupLink(final Collection<StateGroupLink> coll, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setStateGroupLink) {
			bAdded = this.m_setStateGroupLink.addAll(coll);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyStateGroupLink(true);
				this.setDirty(true);
				if (this.hasFileSystemMirror()) {
					final Iterator<StateGroupLink> it = coll.iterator();
					while (it.hasNext()) {
						final StateGroupLink record = it.next();
						if (record != null) {
							System.out.println("INSERT " + "StateGroupLink [" + record.toString() + "]");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean addStateGroupLink(final StateGroupLink record) {
		return this.addStateGroupLink(record, false);
	}

	public final boolean addStateGroupLink(final StateGroupLink record, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setStateGroupLink) {
			bAdded = this.m_setStateGroupLink.add(record);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyStateGroupLink(true);
				this.setDirty(true);
				if ((record != null) && this.hasFileSystemMirror()) {
					System.out.println("INSERT " + "StateGroupLink [" + record.toString() + "]");
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeAllStateGroupLink(final Collection<StateGroupLink> coll) {
		final boolean bRemoved;
		synchronized (this.m_setStateGroupLink) {
			bRemoved = this.m_setStateGroupLink.removeAll(coll);
		}
		if (bRemoved) {
			this.setDirtyStateGroupLink(true);
			this.setDirty(true);
			if (this.hasFileSystemMirror()) {
				final Iterator<StateGroupLink> it = coll.iterator();
				while (it.hasNext()) {
					final StateGroupLink record = it.next();
					if (record != null) {
						System.out.println("DELETE " + "StateGroupLink [" + record.toString() + "]");
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeStateGroupLink(final StateGroupLink record) {
		final boolean bRemoved;
		synchronized (this.m_setStateGroupLink) {
			bRemoved = this.m_setStateGroupLink.remove(record);
		}
		if (bRemoved) {
			this.setDirtyStateGroupLink(true);
			this.setDirty(true);
			if ((record != null) && this.hasFileSystemMirror()) {
				System.out.println("DELETE " + "StateGroupLink [" + record.toString() + "]");
			}
			return true;
		}
		return false;
	}

	private synchronized void setDirtyStateGroupLink(final boolean bDirty) {
		this.m_bDirtyStateGroupLink = bDirty;
	}

	private final boolean isDirtyNotificationScope() {
		boolean bDirty = this.m_bDirtyNotificationScope;
		if (!bDirty) {
			final Collection<NotificationScope> collNotificationScope;
			synchronized (this.m_setNotificationScope) {
				collNotificationScope = new ArrayList<NotificationScope>(this.m_setNotificationScope);
			}
			final Iterator<NotificationScope> it = collNotificationScope.iterator();
			while (it.hasNext() && !bDirty) {
				final NotificationScope nextRecord = it.next();
				bDirty = (nextRecord != null) && nextRecord.isDirty();
			}
		}
		return bDirty;
	}

	public final List<NotificationScope> listModifiedNotificationScope() {
		final List<NotificationScope> listModified = new ArrayList<NotificationScope>();
		final Collection<NotificationScope> collNotificationScope;
		synchronized (this.m_setNotificationScope) {
			collNotificationScope = new ArrayList<NotificationScope>(this.m_setNotificationScope);
		}
		final Iterator<NotificationScope> it = collNotificationScope.iterator();
		while (it.hasNext()) {
			final NotificationScope nextRecord = it.next();
			if ((nextRecord != null) && nextRecord.isDirty()) {
				listModified.add(nextRecord);
			}
		}
		return listModified;
	}

	public final List<NotificationScope> listNotificationScope() {
		synchronized (this.m_setNotificationScope) {
			return new ArrayList<NotificationScope>(this.m_setNotificationScope);
		}
	}

	public final List<NotificationScope> listNotificationScope(final List<Condition> listConditions) {
		synchronized (this.m_setNotificationScope) {
			return new ArrayList<NotificationScope>(this.m_setNotificationScope);
		}
	}

	public final int sizeNotificationScope() {
		synchronized (this.m_setNotificationScope) {
			return this.m_setNotificationScope.size();
		}
	}

	public final void clearNotificationScope() {
		synchronized (this.m_setNotificationScope) {
			this.m_setNotificationScope.clear();
		}
		if (this.hasFileSystemMirror()) {
			System.out.println("DELETE ALL of table [NotificationScope]");
		}
	}

	public final boolean addAllNotificationScope(final Collection<NotificationScope> coll) {
		return this.addAllNotificationScope(coll, false);
	}

	public final boolean addAllNotificationScope(final Collection<NotificationScope> coll,
			final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setNotificationScope) {
			bAdded = this.m_setNotificationScope.addAll(coll);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyNotificationScope(true);
				this.setDirty(true);
				if (this.hasFileSystemMirror()) {
					final Iterator<NotificationScope> it = coll.iterator();
					while (it.hasNext()) {
						final NotificationScope record = it.next();
						if (record != null) {
							System.out.println("INSERT " + "NotificationScope [" + record.toString() + "]");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean addNotificationScope(final NotificationScope record) {
		return this.addNotificationScope(record, false);
	}

	public final boolean addNotificationScope(final NotificationScope record, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setNotificationScope) {
			bAdded = this.m_setNotificationScope.add(record);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyNotificationScope(true);
				this.setDirty(true);
				if ((record != null) && this.hasFileSystemMirror()) {
					System.out.println("INSERT " + "NotificationScope [" + record.toString() + "]");
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeAllNotificationScope(final Collection<NotificationScope> coll) {
		final boolean bRemoved;
		synchronized (this.m_setNotificationScope) {
			bRemoved = this.m_setNotificationScope.removeAll(coll);
		}
		if (bRemoved) {
			this.setDirtyNotificationScope(true);
			this.setDirty(true);
			if (this.hasFileSystemMirror()) {
				final Iterator<NotificationScope> it = coll.iterator();
				while (it.hasNext()) {
					final NotificationScope record = it.next();
					if (record != null) {
						System.out.println("DELETE " + "NotificationScope [" + record.toString() + "]");
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeNotificationScope(final NotificationScope record) {
		final boolean bRemoved;
		synchronized (this.m_setNotificationScope) {
			bRemoved = this.m_setNotificationScope.remove(record);
		}
		if (bRemoved) {
			this.setDirtyNotificationScope(true);
			this.setDirty(true);
			if ((record != null) && this.hasFileSystemMirror()) {
				System.out.println("DELETE " + "NotificationScope [" + record.toString() + "]");
			}
			return true;
		}
		return false;
	}

	private synchronized void setDirtyNotificationScope(final boolean bDirty) {
		this.m_bDirtyNotificationScope = bDirty;
	}

	private final boolean isDirtyModule() {
		boolean bDirty = this.m_bDirtyModule;
		if (!bDirty) {
			final Collection<Module> collModule;
			synchronized (this.m_setModule) {
				collModule = new ArrayList<Module>(this.m_setModule);
			}
			final Iterator<Module> it = collModule.iterator();
			while (it.hasNext() && !bDirty) {
				final Module nextRecord = it.next();
				bDirty = (nextRecord != null) && nextRecord.isDirty();
			}
		}
		return bDirty;
	}

	public final List<Module> listModifiedModule() {
		final List<Module> listModified = new ArrayList<Module>();
		final Collection<Module> collModule;
		synchronized (this.m_setModule) {
			collModule = new ArrayList<Module>(this.m_setModule);
		}
		final Iterator<Module> it = collModule.iterator();
		while (it.hasNext()) {
			final Module nextRecord = it.next();
			if ((nextRecord != null) && nextRecord.isDirty()) {
				listModified.add(nextRecord);
			}
		}
		return listModified;
	}

	public final List<Module> listModule() {
		synchronized (this.m_setModule) {
			return new ArrayList<Module>(this.m_setModule);
		}
	}

	public final List<Module> listModule(final List<Condition> listConditions) {
		synchronized (this.m_setModule) {
			return new ArrayList<Module>(this.m_setModule);
		}
	}

	public final int sizeModule() {
		synchronized (this.m_setModule) {
			return this.m_setModule.size();
		}
	}

	public final void clearModule() {
		synchronized (this.m_setModule) {
			this.m_setModule.clear();
		}
		if (this.hasFileSystemMirror()) {
			System.out.println("DELETE ALL of table [Module]");
		}
	}

	public final boolean addAllModule(final Collection<Module> coll) {
		return this.addAllModule(coll, false);
	}

	public final boolean addAllModule(final Collection<Module> coll, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setModule) {
			bAdded = this.m_setModule.addAll(coll);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyModule(true);
				this.setDirty(true);
				if (this.hasFileSystemMirror()) {
					final Iterator<Module> it = coll.iterator();
					while (it.hasNext()) {
						final Module record = it.next();
						if (record != null) {
							System.out.println("INSERT " + "Module [" + record.toString() + "]");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean addModule(final Module record) {
		return this.addModule(record, false);
	}

	public final boolean addModule(final Module record, final boolean bLoadProcedure) {
		final boolean bAdded;
		synchronized (this.m_setModule) {
			bAdded = this.m_setModule.add(record);
		}
		if (bAdded) {
			if (!bLoadProcedure) {
				this.setDirtyModule(true);
				this.setDirty(true);
				if ((record != null) && this.hasFileSystemMirror()) {
					System.out.println("INSERT " + "Module [" + record.toString() + "]");
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeAllModule(final Collection<Module> coll) {
		final boolean bRemoved;
		synchronized (this.m_setModule) {
			bRemoved = this.m_setModule.removeAll(coll);
		}
		if (bRemoved) {
			this.setDirtyModule(true);
			this.setDirty(true);
			if (this.hasFileSystemMirror()) {
				final Iterator<Module> it = coll.iterator();
				while (it.hasNext()) {
					final Module record = it.next();
					if (record != null) {
						System.out.println("DELETE " + "Module [" + record.toString() + "]");
					}
				}
			}
			return true;
		}
		return false;
	}

	public final boolean removeModule(final Module record) {
		final boolean bRemoved;
		synchronized (this.m_setModule) {
			bRemoved = this.m_setModule.remove(record);
		}
		if (bRemoved) {
			this.setDirtyModule(true);
			this.setDirty(true);
			if ((record != null) && this.hasFileSystemMirror()) {
				System.out.println("DELETE " + "Module [" + record.toString() + "]");
			}
			return true;
		}
		return false;
	}

	private synchronized void setDirtyModule(final boolean bDirty) {
		this.m_bDirtyModule = bDirty;
	}

	public final Observer findByIDObserver(final long lPrimaryKey) {
		Observer foundObserver = null;
		final Collection<Observer> collObserver;
		synchronized (this.m_setObserver) {
			collObserver = new ArrayList<Observer>(this.m_setObserver);
		}
		final Iterator<Observer> itObserver = collObserver.iterator();
		while (itObserver.hasNext() && (null == foundObserver)) {
			final Observer nextObserver = itObserver.next();
			if (nextObserver != null) {
				if (nextObserver != null) {
					if (nextObserver.getPrimaryKey() == lPrimaryKey) {
						foundObserver = nextObserver;
					}
				}
			}
		}
		return foundObserver;
	}

	public final boolean deleteObserver(final long lPrimaryKey) {
		boolean bDeleted = false;
		final Observer record = findByIDObserver(lPrimaryKey);
		if (record != null) {
			if (!this.isReferencedObserver(lPrimaryKey)) {
				bDeleted = this.removeObserver(record);
			}
		} else {
			bDeleted = true;
		}
		return bDeleted;
	}

	public final CommunicationModel exportObserver(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final Observer record = this.findByIDObserver(lPrimaryKey);
		if (record != null) {
			record.export(dbExport);
		}
		return dbExport;
	}

	public final CommunicationModel exportObserver(final List<Observer> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<Observer> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<Observer>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final Observer record = listCopy.get(i);
			if (record != null) {
				record.export(dbExport);
			}
		}
		return dbExport;
	}

	public final CommunicationModel contextExportObserver(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final Observer record = this.findByIDObserver(lPrimaryKey);
		if (record != null) {
			record.contextExport(dbExport, false);
		}
		return dbExport;
	}

	public final CommunicationModel contextExportObserver(final List<Observer> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<Observer> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<Observer>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final Observer record = listCopy.get(i);
			if (record != null) {
				record.contextExport(dbExport, false);
			}
		}
		return dbExport;
	}

	public final ObserverLink findByIDObserverLink(final long lPrimaryKey) {
		ObserverLink foundObserverLink = null;
		final Collection<ObserverLink> collObserverLink;
		synchronized (this.m_setObserverLink) {
			collObserverLink = new ArrayList<ObserverLink>(this.m_setObserverLink);
		}
		final Iterator<ObserverLink> itObserverLink = collObserverLink.iterator();
		while (itObserverLink.hasNext() && (null == foundObserverLink)) {
			final ObserverLink nextObserverLink = itObserverLink.next();
			if (nextObserverLink != null) {
				if (nextObserverLink != null) {
					if (nextObserverLink.getPrimaryKey() == lPrimaryKey) {
						foundObserverLink = nextObserverLink;
					}
				}
			}
		}
		return foundObserverLink;
	}

	public final boolean deleteObserverLink(final long lPrimaryKey) {
		boolean bDeleted = false;
		final ObserverLink record = findByIDObserverLink(lPrimaryKey);
		if (record != null) {
			if (!this.isReferencedObserverLink(lPrimaryKey)) {
				bDeleted = this.removeObserverLink(record);
			}
		} else {
			bDeleted = true;
		}
		return bDeleted;
	}

	public final CommunicationModel exportObserverLink(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final ObserverLink record = this.findByIDObserverLink(lPrimaryKey);
		if (record != null) {
			record.export(dbExport);
		}
		return dbExport;
	}

	public final CommunicationModel exportObserverLink(final List<ObserverLink> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<ObserverLink> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<ObserverLink>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final ObserverLink record = listCopy.get(i);
			if (record != null) {
				record.export(dbExport);
			}
		}
		return dbExport;
	}

	public final CommunicationModel contextExportObserverLink(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final ObserverLink record = this.findByIDObserverLink(lPrimaryKey);
		if (record != null) {
			record.contextExport(dbExport, false);
		}
		return dbExport;
	}

	public final CommunicationModel contextExportObserverLink(final List<ObserverLink> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<ObserverLink> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<ObserverLink>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final ObserverLink record = listCopy.get(i);
			if (record != null) {
				record.contextExport(dbExport, false);
			}
		}
		return dbExport;
	}

	public final State findByIDState(final long lPrimaryKey) {
		State foundState = null;
		final Collection<State> collState;
		synchronized (this.m_setState) {
			collState = new ArrayList<State>(this.m_setState);
		}
		final Iterator<State> itState = collState.iterator();
		while (itState.hasNext() && (null == foundState)) {
			final State nextState = itState.next();
			if (nextState != null) {
				if (nextState != null) {
					if (nextState.getPrimaryKey() == lPrimaryKey) {
						foundState = nextState;
					}
				}
			}
		}
		return foundState;
	}

	public final boolean deleteState(final long lPrimaryKey) {
		boolean bDeleted = false;
		final State record = findByIDState(lPrimaryKey);
		if (record != null) {
			if (!this.isReferencedState(lPrimaryKey)) {
				bDeleted = this.removeState(record);
			}
		} else {
			bDeleted = true;
		}
		return bDeleted;
	}

	public final CommunicationModel exportState(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final State record = this.findByIDState(lPrimaryKey);
		if (record != null) {
			record.export(dbExport);
		}
		return dbExport;
	}

	public final CommunicationModel exportState(final List<State> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<State> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<State>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final State record = listCopy.get(i);
			if (record != null) {
				record.export(dbExport);
			}
		}
		return dbExport;
	}

	public final CommunicationModel contextExportState(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final State record = this.findByIDState(lPrimaryKey);
		if (record != null) {
			record.contextExport(dbExport, false);
		}
		return dbExport;
	}

	public final CommunicationModel contextExportState(final List<State> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<State> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<State>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final State record = listCopy.get(i);
			if (record != null) {
				record.contextExport(dbExport, false);
			}
		}
		return dbExport;
	}

	public final StateType findByIDStateType(final long lPrimaryKey) {
		StateType foundStateType = null;
		final Collection<StateType> collStateType;
		synchronized (this.m_setStateType) {
			collStateType = new ArrayList<StateType>(this.m_setStateType);
		}
		final Iterator<StateType> itStateType = collStateType.iterator();
		while (itStateType.hasNext() && (null == foundStateType)) {
			final StateType nextStateType = itStateType.next();
			if (nextStateType != null) {
				if (nextStateType != null) {
					if (nextStateType.getPrimaryKey() == lPrimaryKey) {
						foundStateType = nextStateType;
					}
				}
			}
		}
		return foundStateType;
	}

	public final boolean deleteStateType(final long lPrimaryKey) {
		boolean bDeleted = false;
		final StateType record = findByIDStateType(lPrimaryKey);
		if (record != null) {
			if (!this.isReferencedStateType(lPrimaryKey)) {
				bDeleted = this.removeStateType(record);
			}
		} else {
			bDeleted = true;
		}
		return bDeleted;
	}

	public final CommunicationModel exportStateType(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateType record = this.findByIDStateType(lPrimaryKey);
		if (record != null) {
			record.export(dbExport);
		}
		return dbExport;
	}

	public final CommunicationModel exportStateType(final List<StateType> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<StateType> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<StateType>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final StateType record = listCopy.get(i);
			if (record != null) {
				record.export(dbExport);
			}
		}
		return dbExport;
	}

	public final CommunicationModel contextExportStateType(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateType record = this.findByIDStateType(lPrimaryKey);
		if (record != null) {
			record.contextExport(dbExport, false);
		}
		return dbExport;
	}

	public final CommunicationModel contextExportStateType(final List<StateType> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<StateType> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<StateType>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final StateType record = listCopy.get(i);
			if (record != null) {
				record.contextExport(dbExport, false);
			}
		}
		return dbExport;
	}

	public final StateGroup findByIDStateGroup(final long lPrimaryKey) {
		StateGroup foundStateGroup = null;
		final Collection<StateGroup> collStateGroup;
		synchronized (this.m_setStateGroup) {
			collStateGroup = new ArrayList<StateGroup>(this.m_setStateGroup);
		}
		final Iterator<StateGroup> itStateGroup = collStateGroup.iterator();
		while (itStateGroup.hasNext() && (null == foundStateGroup)) {
			final StateGroup nextStateGroup = itStateGroup.next();
			if (nextStateGroup != null) {
				if (nextStateGroup != null) {
					if (nextStateGroup.getPrimaryKey() == lPrimaryKey) {
						foundStateGroup = nextStateGroup;
					}
				}
			}
		}
		return foundStateGroup;
	}

	public final boolean deleteStateGroup(final long lPrimaryKey) {
		boolean bDeleted = false;
		final StateGroup record = findByIDStateGroup(lPrimaryKey);
		if (record != null) {
			if (!this.isReferencedStateGroup(lPrimaryKey)) {
				bDeleted = this.removeStateGroup(record);
			}
		} else {
			bDeleted = true;
		}
		return bDeleted;
	}

	public final CommunicationModel exportStateGroup(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateGroup record = this.findByIDStateGroup(lPrimaryKey);
		if (record != null) {
			record.export(dbExport);
		}
		return dbExport;
	}

	public final CommunicationModel exportStateGroup(final List<StateGroup> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<StateGroup> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<StateGroup>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final StateGroup record = listCopy.get(i);
			if (record != null) {
				record.export(dbExport);
			}
		}
		return dbExport;
	}

	public final CommunicationModel contextExportStateGroup(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateGroup record = this.findByIDStateGroup(lPrimaryKey);
		if (record != null) {
			record.contextExport(dbExport, false);
		}
		return dbExport;
	}

	public final CommunicationModel contextExportStateGroup(final List<StateGroup> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<StateGroup> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<StateGroup>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final StateGroup record = listCopy.get(i);
			if (record != null) {
				record.contextExport(dbExport, false);
			}
		}
		return dbExport;
	}

	public final StateChange findByIDStateChange(final long lPrimaryKey) {
		StateChange foundStateChange = null;
		final Collection<StateChange> collStateChange;
		synchronized (this.m_setStateChange) {
			collStateChange = new ArrayList<StateChange>(this.m_setStateChange);
		}
		final Iterator<StateChange> itStateChange = collStateChange.iterator();
		while (itStateChange.hasNext() && (null == foundStateChange)) {
			final StateChange nextStateChange = itStateChange.next();
			if (nextStateChange != null) {
				if (nextStateChange != null) {
					if (nextStateChange.getPrimaryKey() == lPrimaryKey) {
						foundStateChange = nextStateChange;
					}
				}
			}
		}
		return foundStateChange;
	}

	public final boolean deleteStateChange(final long lPrimaryKey) {
		boolean bDeleted = false;
		final StateChange record = findByIDStateChange(lPrimaryKey);
		if (record != null) {
			if (!this.isReferencedStateChange(lPrimaryKey)) {
				bDeleted = this.removeStateChange(record);
			}
		} else {
			bDeleted = true;
		}
		return bDeleted;
	}

	public final CommunicationModel exportStateChange(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateChange record = this.findByIDStateChange(lPrimaryKey);
		if (record != null) {
			record.export(dbExport);
		}
		return dbExport;
	}

	public final CommunicationModel exportStateChange(final List<StateChange> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<StateChange> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<StateChange>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final StateChange record = listCopy.get(i);
			if (record != null) {
				record.export(dbExport);
			}
		}
		return dbExport;
	}

	public final CommunicationModel contextExportStateChange(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateChange record = this.findByIDStateChange(lPrimaryKey);
		if (record != null) {
			record.contextExport(dbExport, false);
		}
		return dbExport;
	}

	public final CommunicationModel contextExportStateChange(final List<StateChange> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<StateChange> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<StateChange>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final StateChange record = listCopy.get(i);
			if (record != null) {
				record.contextExport(dbExport, false);
			}
		}
		return dbExport;
	}

	public final StateGroupLink findByIDStateGroupLink(final long lPrimaryKey) {
		StateGroupLink foundStateGroupLink = null;
		final Collection<StateGroupLink> collStateGroupLink;
		synchronized (this.m_setStateGroupLink) {
			collStateGroupLink = new ArrayList<StateGroupLink>(this.m_setStateGroupLink);
		}
		final Iterator<StateGroupLink> itStateGroupLink = collStateGroupLink.iterator();
		while (itStateGroupLink.hasNext() && (null == foundStateGroupLink)) {
			final StateGroupLink nextStateGroupLink = itStateGroupLink.next();
			if (nextStateGroupLink != null) {
				if (nextStateGroupLink != null) {
					if (nextStateGroupLink.getPrimaryKey() == lPrimaryKey) {
						foundStateGroupLink = nextStateGroupLink;
					}
				}
			}
		}
		return foundStateGroupLink;
	}

	public final boolean deleteStateGroupLink(final long lPrimaryKey) {
		boolean bDeleted = false;
		final StateGroupLink record = findByIDStateGroupLink(lPrimaryKey);
		if (record != null) {
			if (!this.isReferencedStateGroupLink(lPrimaryKey)) {
				bDeleted = this.removeStateGroupLink(record);
			}
		} else {
			bDeleted = true;
		}
		return bDeleted;
	}

	public final CommunicationModel exportStateGroupLink(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateGroupLink record = this.findByIDStateGroupLink(lPrimaryKey);
		if (record != null) {
			record.export(dbExport);
		}
		return dbExport;
	}

	public final CommunicationModel exportStateGroupLink(final List<StateGroupLink> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<StateGroupLink> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<StateGroupLink>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final StateGroupLink record = listCopy.get(i);
			if (record != null) {
				record.export(dbExport);
			}
		}
		return dbExport;
	}

	public final CommunicationModel contextExportStateGroupLink(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateGroupLink record = this.findByIDStateGroupLink(lPrimaryKey);
		if (record != null) {
			record.contextExport(dbExport, false);
		}
		return dbExport;
	}

	public final CommunicationModel contextExportStateGroupLink(final List<StateGroupLink> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<StateGroupLink> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<StateGroupLink>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final StateGroupLink record = listCopy.get(i);
			if (record != null) {
				record.contextExport(dbExport, false);
			}
		}
		return dbExport;
	}

	public final NotificationScope findByIDNotificationScope(final long lPrimaryKey) {
		NotificationScope foundNotificationScope = null;
		final Collection<NotificationScope> collNotificationScope;
		synchronized (this.m_setNotificationScope) {
			collNotificationScope = new ArrayList<NotificationScope>(this.m_setNotificationScope);
		}
		final Iterator<NotificationScope> itNotificationScope = collNotificationScope.iterator();
		while (itNotificationScope.hasNext() && (null == foundNotificationScope)) {
			final NotificationScope nextNotificationScope = itNotificationScope.next();
			if (nextNotificationScope != null) {
				if (nextNotificationScope != null) {
					if (nextNotificationScope.getPrimaryKey() == lPrimaryKey) {
						foundNotificationScope = nextNotificationScope;
					}
				}
			}
		}
		return foundNotificationScope;
	}

	public final boolean deleteNotificationScope(final long lPrimaryKey) {
		boolean bDeleted = false;
		final NotificationScope record = findByIDNotificationScope(lPrimaryKey);
		if (record != null) {
			if (!this.isReferencedNotificationScope(lPrimaryKey)) {
				bDeleted = this.removeNotificationScope(record);
			}
		} else {
			bDeleted = true;
		}
		return bDeleted;
	}

	public final CommunicationModel exportNotificationScope(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final NotificationScope record = this.findByIDNotificationScope(lPrimaryKey);
		if (record != null) {
			record.export(dbExport);
		}
		return dbExport;
	}

	public final CommunicationModel exportNotificationScope(final List<NotificationScope> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<NotificationScope> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<NotificationScope>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final NotificationScope record = listCopy.get(i);
			if (record != null) {
				record.export(dbExport);
			}
		}
		return dbExport;
	}

	public final CommunicationModel contextExportNotificationScope(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final NotificationScope record = this.findByIDNotificationScope(lPrimaryKey);
		if (record != null) {
			record.contextExport(dbExport, false);
		}
		return dbExport;
	}

	public final CommunicationModel contextExportNotificationScope(final List<NotificationScope> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<NotificationScope> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<NotificationScope>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final NotificationScope record = listCopy.get(i);
			if (record != null) {
				record.contextExport(dbExport, false);
			}
		}
		return dbExport;
	}

	public final Module findByIDModule(final long lPrimaryKey) {
		Module foundModule = null;
		final Collection<Module> collModule;
		synchronized (this.m_setModule) {
			collModule = new ArrayList<Module>(this.m_setModule);
		}
		final Iterator<Module> itModule = collModule.iterator();
		while (itModule.hasNext() && (null == foundModule)) {
			final Module nextModule = itModule.next();
			if (nextModule != null) {
				if (nextModule != null) {
					if (nextModule.getPrimaryKey() == lPrimaryKey) {
						foundModule = nextModule;
					}
				}
			}
		}
		return foundModule;
	}

	public final boolean deleteModule(final long lPrimaryKey) {
		boolean bDeleted = false;
		final Module record = findByIDModule(lPrimaryKey);
		if (record != null) {
			if (!this.isReferencedModule(lPrimaryKey)) {
				bDeleted = this.removeModule(record);
			}
		} else {
			bDeleted = true;
		}
		return bDeleted;
	}

	public final CommunicationModel exportModule(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final Module record = this.findByIDModule(lPrimaryKey);
		if (record != null) {
			record.export(dbExport);
		}
		return dbExport;
	}

	public final CommunicationModel exportModule(final List<Module> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<Module> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<Module>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final Module record = listCopy.get(i);
			if (record != null) {
				record.export(dbExport);
			}
		}
		return dbExport;
	}

	public final CommunicationModel contextExportModule(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final Module record = this.findByIDModule(lPrimaryKey);
		if (record != null) {
			record.contextExport(dbExport, false);
		}
		return dbExport;
	}

	public final CommunicationModel contextExportModule(final List<Module> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final List<Module> listCopy;
		synchronized (list) {
			listCopy = new ArrayList<Module>(list);
		}
		for (int i = 0, nSize = listCopy.size(); i < nSize; i++) {
			final Module record = listCopy.get(i);
			if (record != null) {
				record.contextExport(dbExport, false);
			}
		}
		return dbExport;
	}

	private File getPreviousDatabaseFile(final String strDatabaseDir) {
		File filePrevious = null;
		final File[] arrFiles = new File(strDatabaseDir).listFiles();
		final List<File> listDatabaseFiles = new ArrayList<File>();
		if (arrFiles != null) {
			for (int i = 0; i < arrFiles.length; i++) {
				if (arrFiles[i] != null) {
					final String strFileName = arrFiles[i].getName();
					if (strFileName != null) {
						if (strFileName.startsWith("CommunicationModel") && strFileName.endsWith(DB_EXTENSION)) {
							listDatabaseFiles.add(arrFiles[i]);
						}
					}
				}
			}
		}
		int nMaxCount = 0;
		for (int i = 0, nCount = listDatabaseFiles.size(); i < nCount; i++) {
			final File nextFile = listDatabaseFiles.get(i);
			final String strFileName = nextFile.getName();
			final int nLength = strFileName.length();
			if (nLength > nMaxCount) {
				nMaxCount = nLength;
				filePrevious = nextFile;
			}
		}
		return filePrevious;
	}

	public final boolean loadAll() {
		return this.loadAll(this.m_strDatabaseDir);
	}

	public final synchronized boolean loadAll(final String strDatabaseDir) {
		boolean bCompatibleData = true;
		if (this.hasFileSystemMirror()) {
			int nRecordCount = 0;
			final String strZipPath = strDatabaseDir + File.separatorChar + "CommunicationModel" + DB_EXTENSION;
			List<String> listFilePaths = FileSystem.unzipFile(strZipPath, strDatabaseDir);
			if (listFilePaths.size() == 0) {
				final File fileCorrupted = new File(strZipPath);
				fileCorrupted.delete();
				final File filePrevious = this.getPreviousDatabaseFile(strDatabaseDir);
				if (filePrevious != null) {
					filePrevious.renameTo(fileCorrupted);
					FileSystem.unzipFile(strZipPath, strDatabaseDir);
				}
			}
			long lTime = System.nanoTime();
			final ModelVersion modelData = this.loadModelVersion(strDatabaseDir);
			if (modelData != null) {
				bCompatibleData = modelData.isCompatibleWith(this.m_modelVersion);
			} else {
				System.out.println("LOAD  model version could not be loaded");
			}
			if (bCompatibleData) {
				final String strPathObserver = strDatabaseDir + File.separatorChar + "Observer.xml";
				nRecordCount += this.loadObserver(strPathObserver);
				System.out.println("LOAD [" + nRecordCount + "] of [Observer.xml] took "
						+ ((System.nanoTime() - lTime) / 1000000.0) + " ms");
				lTime = System.nanoTime();
				final String strPathObserverLink = strDatabaseDir + File.separatorChar + "ObserverLink.xml";
				nRecordCount += this.loadObserverLink(strPathObserverLink);
				System.out.println("LOAD [" + nRecordCount + "] of [ObserverLink.xml] took "
						+ ((System.nanoTime() - lTime) / 1000000.0) + " ms");
				lTime = System.nanoTime();
				final String strPathState = strDatabaseDir + File.separatorChar + "State.xml";
				nRecordCount += this.loadState(strPathState);
				System.out.println("LOAD [" + nRecordCount + "] of [State.xml] took "
						+ ((System.nanoTime() - lTime) / 1000000.0) + " ms");
				lTime = System.nanoTime();
				final String strPathStateType = strDatabaseDir + File.separatorChar + "StateType.xml";
				nRecordCount += this.loadStateType(strPathStateType);
				System.out.println("LOAD [" + nRecordCount + "] of [StateType.xml] took "
						+ ((System.nanoTime() - lTime) / 1000000.0) + " ms");
				lTime = System.nanoTime();
				final String strPathStateGroup = strDatabaseDir + File.separatorChar + "StateGroup.xml";
				nRecordCount += this.loadStateGroup(strPathStateGroup);
				System.out.println("LOAD [" + nRecordCount + "] of [StateGroup.xml] took "
						+ ((System.nanoTime() - lTime) / 1000000.0) + " ms");
				lTime = System.nanoTime();
				final String strPathStateChange = strDatabaseDir + File.separatorChar + "StateChange.xml";
				nRecordCount += this.loadStateChange(strPathStateChange);
				System.out.println("LOAD [" + nRecordCount + "] of [StateChange.xml] took "
						+ ((System.nanoTime() - lTime) / 1000000.0) + " ms");
				lTime = System.nanoTime();
				final String strPathStateGroupLink = strDatabaseDir + File.separatorChar + "StateGroupLink.xml";
				nRecordCount += this.loadStateGroupLink(strPathStateGroupLink);
				System.out.println("LOAD [" + nRecordCount + "] of [StateGroupLink.xml] took "
						+ ((System.nanoTime() - lTime) / 1000000.0) + " ms");
				lTime = System.nanoTime();
				final String strPathNotificationScope = strDatabaseDir + File.separatorChar + "NotificationScope.xml";
				nRecordCount += this.loadNotificationScope(strPathNotificationScope);
				System.out.println("LOAD [" + nRecordCount + "] of [NotificationScope.xml] took "
						+ ((System.nanoTime() - lTime) / 1000000.0) + " ms");
				lTime = System.nanoTime();
				final String strPathModule = strDatabaseDir + File.separatorChar + "Module.xml";
				nRecordCount += this.loadModule(strPathModule);
				System.out.println("LOAD [" + nRecordCount + "] of [Module.xml] took "
						+ ((System.nanoTime() - lTime) / 1000000.0) + " ms");
				lTime = System.nanoTime();
				this.deleteXMLFiles(strDatabaseDir);
				System.out.println("---> 9 tables loaded");
			} else {
				System.out
						.println("MODEL  database file is incompatible with the running software --> update software");
				bCompatibleData = false;
			}
		}
		return bCompatibleData;
	}

	public final boolean export(final String strExportDir) {
		return this.saveAll(strExportDir, true);
	}

	public final boolean saveAll(final boolean bEnforceSave) {
		return this.saveAll(this.m_strDatabaseDir, bEnforceSave);
	}

	public final synchronized boolean saveAll(final String strDatabaseDir, final boolean bEnforceSave) {
		boolean bSave = bEnforceSave || this.isDirty();
		final File fileDir = new File(strDatabaseDir);
		if (bSave && (this.hasFileSystemMirror() || fileDir.isDirectory())) {
			final File dirDB = new File(strDatabaseDir);
			dirDB.mkdirs();
			final String strPathObserver = strDatabaseDir + File.separatorChar + "Observer.xml";
			this.saveObserver(strPathObserver, bSave);
			final String strPathObserverLink = strDatabaseDir + File.separatorChar + "ObserverLink.xml";
			this.saveObserverLink(strPathObserverLink, bSave);
			final String strPathState = strDatabaseDir + File.separatorChar + "State.xml";
			this.saveState(strPathState, bSave);
			final String strPathStateType = strDatabaseDir + File.separatorChar + "StateType.xml";
			this.saveStateType(strPathStateType, bSave);
			final String strPathStateGroup = strDatabaseDir + File.separatorChar + "StateGroup.xml";
			this.saveStateGroup(strPathStateGroup, bSave);
			final String strPathStateChange = strDatabaseDir + File.separatorChar + "StateChange.xml";
			this.saveStateChange(strPathStateChange, bSave);
			final String strPathStateGroupLink = strDatabaseDir + File.separatorChar + "StateGroupLink.xml";
			this.saveStateGroupLink(strPathStateGroupLink, bSave);
			final String strPathNotificationScope = strDatabaseDir + File.separatorChar + "NotificationScope.xml";
			this.saveNotificationScope(strPathNotificationScope, bSave);
			final String strPathModule = strDatabaseDir + File.separatorChar + "Module.xml";
			this.saveModule(strPathModule, bSave);
			final String strPathModelVersion = strDatabaseDir + File.separatorChar + MODEL_VERSION_FILE;
			this.saveModelVersion(strPathModelVersion, bSave);
			this.renameOldZip(strDatabaseDir);
			bSave = (null != this.zip(strDatabaseDir,
					strDatabaseDir + File.separatorChar + "CommunicationModel" + DB_EXTENSION));
			if (bSave) {
				this.organizeZips(strDatabaseDir);
				this.deleteXMLFiles(strDatabaseDir);
				this.setDirty(false);
			}
		}
		return bSave;
	}

	private void organizeZips(final String strDatabaseDir) {
		final File fileZipDir = new File(strDatabaseDir);
		final List<File> listZipFiles = new ArrayList<File>();
		File fileLatest = null;
		final File[] arrFiles = fileZipDir.listFiles();
		if (arrFiles != null) {
			for (int i = 0; i < arrFiles.length; i++) {
				if (this.isDatabaseZip(arrFiles[i])) {
					listZipFiles.add(arrFiles[i]);
				}
			}
			final int nSize = listZipFiles.size();
			if (nSize > 0) {
				fileLatest = listZipFiles.get(0);
				for (int i = 1; i < nSize; i++) {
					final File fileNext = listZipFiles.get(i);
					if (this.isLatestFile(fileNext, fileLatest)) {
						fileLatest = fileNext;
					}
				}
			}
		}
		for (int i = 0, nSize = listZipFiles.size(); i < nSize; i++) {
			final File fileToDelete = listZipFiles.get(i);
			if (fileToDelete != fileLatest) {
				if (!fileToDelete.delete()) {
					System.out.println(
							"Organize old DB files --> [" + fileToDelete.getAbsolutePath() + "] was not deleted");
				}
			}
		}
	}

	private boolean isDatabaseZip(final File file) {
		if ((file != null) && file.isFile()) {
			final String strFileName = file.getName();
			if (strFileName.toUpperCase().startsWith(DB_FILE_COPY.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	private boolean isLatestFile(final File file, final File fileLatest) {
		boolean bFileLatest = false;
		if ((file != null) && (fileLatest != null)) {
			final String strFileName = file.getName();
			final String strFileLatestName = fileLatest.getName();
			final int nBeginDateFile = strFileName.lastIndexOf(DB_FILE_COPY_SEPARATOR);
			final int nBeginDateFileLatest = strFileLatestName.lastIndexOf(DB_FILE_COPY_SEPARATOR);
			final int nEndDateFile = strFileName.indexOf(DB_EXTENSION);
			final int nEndDateFileLatest = strFileLatestName.indexOf(DB_EXTENSION);
			final int nSepLength = DB_FILE_COPY_SEPARATOR.length();
			final boolean bSyntaxCheck = (nBeginDateFile >= 0) && (nBeginDateFileLatest >= 0) && (nEndDateFile >= 0)
					&& (nEndDateFileLatest >= 0) && (nEndDateFileLatest >= (nBeginDateFileLatest + nSepLength))
					&& (nEndDateFile >= (nBeginDateFile + nSepLength));
			if (bSyntaxCheck) {
				try {
					final String strTimeFile = strFileName.substring(nBeginDateFile + nSepLength, nEndDateFile);
					final String strTimeFileLatest = strFileLatestName.substring(nBeginDateFileLatest + nSepLength,
							nEndDateFileLatest);
					bFileLatest = (new Long(strTimeFile).longValue() > new Long(strTimeFileLatest).longValue());
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
		return bFileLatest;
	}

	private void renameOldZip(final String strDatabaseDir) {
		final Date dateNow = new Date();
		final String strDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN).format(dateNow);
		final String strDateInMillis = Long.toString(dateNow.getTime());
		final String strBackupFileName = DB_FILE_COPY + strDate + DB_FILE_COPY_SEPARATOR + strDateInMillis
				+ DB_EXTENSION;
		final File fileZip = new File(strDatabaseDir + File.separatorChar + "CommunicationModel" + DB_EXTENSION);
		final File fileZipBackup = new File(strDatabaseDir + File.separatorChar + strBackupFileName);
		if (fileZip.exists() && fileZip.isFile()) {
			if (!fileZip.renameTo(fileZipBackup)) {
				System.out.println("Rename old DB files --> [" + fileZip.getAbsolutePath() + "] was not deleted");
			}
		}
	}

	private void deleteXMLFiles(final String strDatabaseDir) {
		final File fileObserver = new File(strDatabaseDir + File.separatorChar + "Observer.xml");
		final File fileObserverLink = new File(strDatabaseDir + File.separatorChar + "ObserverLink.xml");
		final File fileState = new File(strDatabaseDir + File.separatorChar + "State.xml");
		final File fileStateType = new File(strDatabaseDir + File.separatorChar + "StateType.xml");
		final File fileStateGroup = new File(strDatabaseDir + File.separatorChar + "StateGroup.xml");
		final File fileStateChange = new File(strDatabaseDir + File.separatorChar + "StateChange.xml");
		final File fileStateGroupLink = new File(strDatabaseDir + File.separatorChar + "StateGroupLink.xml");
		final File fileNotificationScope = new File(strDatabaseDir + File.separatorChar + "NotificationScope.xml");
		final File fileModule = new File(strDatabaseDir + File.separatorChar + "Module.xml");
		final File fileModelVersion = new File(strDatabaseDir + File.separatorChar + MODEL_VERSION_FILE);
		if (!fileObserver.delete()) {
			System.out
					.println("Delete of old DB-XML file --> [" + fileObserver.getAbsolutePath() + "] was not deleted");
		}
		if (!fileObserverLink.delete()) {
			System.out.println(
					"Delete of old DB-XML file --> [" + fileObserverLink.getAbsolutePath() + "] was not deleted");
		}
		if (!fileState.delete()) {
			System.out.println("Delete of old DB-XML file --> [" + fileState.getAbsolutePath() + "] was not deleted");
		}
		if (!fileStateType.delete()) {
			System.out
					.println("Delete of old DB-XML file --> [" + fileStateType.getAbsolutePath() + "] was not deleted");
		}
		if (!fileStateGroup.delete()) {
			System.out.println(
					"Delete of old DB-XML file --> [" + fileStateGroup.getAbsolutePath() + "] was not deleted");
		}
		if (!fileStateChange.delete()) {
			System.out.println(
					"Delete of old DB-XML file --> [" + fileStateChange.getAbsolutePath() + "] was not deleted");
		}
		if (!fileStateGroupLink.delete()) {
			System.out.println(
					"Delete of old DB-XML file --> [" + fileStateGroupLink.getAbsolutePath() + "] was not deleted");
		}
		if (!fileNotificationScope.delete()) {
			System.out.println(
					"Delete of old DB-XML file --> [" + fileNotificationScope.getAbsolutePath() + "] was not deleted");
		}
		if (!fileModule.delete()) {
			System.out.println("Delete of old DB-XML file --> [" + fileModule.getAbsolutePath() + "] was not deleted");
		}
		if (!fileModelVersion.delete()) {
			System.out.println(
					"Delete of old DB-XML file --> [" + fileModelVersion.getAbsolutePath() + "] was not deleted");
		}
	}

	private final int loadObserver(final String strFilePath) {
		final Document doc = XMLParser.createDOMTree(strFilePath);
		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("RECORD");
			for (int i = 0, nSize = nodeList.getLength(); i < nSize; ++i) {
				final Observer record = Observer.load(nodeList.item(i), this);
				if (record != null) {
					this.addObserver(record, true);
				}
			}
		} else {
			System.out.println("Could not parse DB-XML file --> [" + strFilePath + "] was not deleted");
		}
		return this.sizeObserver();
	}

	private boolean saveObserver(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave || this.isDirty() || this.isDirtyObserver()) {
			final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);
			if (out != null) {
				final Collection<Observer> collObserver;
				synchronized (this.m_setObserver) {
					collObserver = new ArrayList<Observer>(this.m_setObserver);
				}
				final Iterator<Observer> it = collObserver.iterator();
				XMLParser.writeLine(0, out, "Observer", null, true);
				while (it.hasNext()) {
					final Observer nextRecord = it.next();
					if (nextRecord != null) {
						nextRecord.save(out);
						nextRecord.setDirty(false);
					}
				}
				XMLParser.writeLine(0, out, "Observer", null, false);
				bSaved = true;
				this.m_bDirtyObserver = false;
			} else {
				System.out.println("Could not write DB-XML file --> [" + strFilePath + "] ");
			}
			XMLParser.close(out);
		}
		return bSaved;
	}

	private final int loadObserverLink(final String strFilePath) {
		final Document doc = XMLParser.createDOMTree(strFilePath);
		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("RECORD");
			for (int i = 0, nSize = nodeList.getLength(); i < nSize; ++i) {
				final ObserverLink record = ObserverLink.load(nodeList.item(i), this);
				if (record != null) {
					this.addObserverLink(record, true);
				}
			}
		} else {
			System.out.println("Could not parse DB-XML file --> [" + strFilePath + "] was not deleted");
		}
		return this.sizeObserverLink();
	}

	private boolean saveObserverLink(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave || this.isDirty() || this.isDirtyObserverLink()) {
			final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);
			if (out != null) {
				final Collection<ObserverLink> collObserverLink;
				synchronized (this.m_setObserverLink) {
					collObserverLink = new ArrayList<ObserverLink>(this.m_setObserverLink);
				}
				final Iterator<ObserverLink> it = collObserverLink.iterator();
				XMLParser.writeLine(0, out, "ObserverLink", null, true);
				while (it.hasNext()) {
					final ObserverLink nextRecord = it.next();
					if (nextRecord != null) {
						nextRecord.save(out);
						nextRecord.setDirty(false);
					}
				}
				XMLParser.writeLine(0, out, "ObserverLink", null, false);
				bSaved = true;
				this.m_bDirtyObserverLink = false;
			} else {
				System.out.println("Could not write DB-XML file --> [" + strFilePath + "] ");
			}
			XMLParser.close(out);
		}
		return bSaved;
	}

	private final int loadState(final String strFilePath) {
		final Document doc = XMLParser.createDOMTree(strFilePath);
		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("RECORD");
			for (int i = 0, nSize = nodeList.getLength(); i < nSize; ++i) {
				final State record = State.load(nodeList.item(i), this);
				if (record != null) {
					this.addState(record, true);
				}
			}
		} else {
			System.out.println("Could not parse DB-XML file --> [" + strFilePath + "] was not deleted");
		}
		return this.sizeState();
	}

	private boolean saveState(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave || this.isDirty() || this.isDirtyState()) {
			final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);
			if (out != null) {
				final Collection<State> collState;
				synchronized (this.m_setState) {
					collState = new ArrayList<State>(this.m_setState);
				}
				final Iterator<State> it = collState.iterator();
				XMLParser.writeLine(0, out, "State", null, true);
				while (it.hasNext()) {
					final State nextRecord = it.next();
					if (nextRecord != null) {
						nextRecord.save(out);
						nextRecord.setDirty(false);
					}
				}
				XMLParser.writeLine(0, out, "State", null, false);
				bSaved = true;
				this.m_bDirtyState = false;
			} else {
				System.out.println("Could not write DB-XML file --> [" + strFilePath + "] ");
			}
			XMLParser.close(out);
		}
		return bSaved;
	}

	private final int loadStateType(final String strFilePath) {
		final Document doc = XMLParser.createDOMTree(strFilePath);
		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("RECORD");
			for (int i = 0, nSize = nodeList.getLength(); i < nSize; ++i) {
				final StateType record = StateType.load(nodeList.item(i), this);
				if (record != null) {
					this.addStateType(record, true);
				}
			}
		} else {
			System.out.println("Could not parse DB-XML file --> [" + strFilePath + "] was not deleted");
		}
		return this.sizeStateType();
	}

	private boolean saveStateType(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave || this.isDirty() || this.isDirtyStateType()) {
			final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);
			if (out != null) {
				final Collection<StateType> collStateType;
				synchronized (this.m_setStateType) {
					collStateType = new ArrayList<StateType>(this.m_setStateType);
				}
				final Iterator<StateType> it = collStateType.iterator();
				XMLParser.writeLine(0, out, "StateType", null, true);
				while (it.hasNext()) {
					final StateType nextRecord = it.next();
					if (nextRecord != null) {
						nextRecord.save(out);
						nextRecord.setDirty(false);
					}
				}
				XMLParser.writeLine(0, out, "StateType", null, false);
				bSaved = true;
				this.m_bDirtyStateType = false;
			} else {
				System.out.println("Could not write DB-XML file --> [" + strFilePath + "] ");
			}
			XMLParser.close(out);
		}
		return bSaved;
	}

	private final int loadStateGroup(final String strFilePath) {
		final Document doc = XMLParser.createDOMTree(strFilePath);
		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("RECORD");
			for (int i = 0, nSize = nodeList.getLength(); i < nSize; ++i) {
				final StateGroup record = StateGroup.load(nodeList.item(i), this);
				if (record != null) {
					this.addStateGroup(record, true);
				}
			}
		} else {
			System.out.println("Could not parse DB-XML file --> [" + strFilePath + "] was not deleted");
		}
		return this.sizeStateGroup();
	}

	private boolean saveStateGroup(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave || this.isDirty() || this.isDirtyStateGroup()) {
			final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);
			if (out != null) {
				final Collection<StateGroup> collStateGroup;
				synchronized (this.m_setStateGroup) {
					collStateGroup = new ArrayList<StateGroup>(this.m_setStateGroup);
				}
				final Iterator<StateGroup> it = collStateGroup.iterator();
				XMLParser.writeLine(0, out, "StateGroup", null, true);
				while (it.hasNext()) {
					final StateGroup nextRecord = it.next();
					if (nextRecord != null) {
						nextRecord.save(out);
						nextRecord.setDirty(false);
					}
				}
				XMLParser.writeLine(0, out, "StateGroup", null, false);
				bSaved = true;
				this.m_bDirtyStateGroup = false;
			} else {
				System.out.println("Could not write DB-XML file --> [" + strFilePath + "] ");
			}
			XMLParser.close(out);
		}
		return bSaved;
	}

	private final int loadStateChange(final String strFilePath) {
		final Document doc = XMLParser.createDOMTree(strFilePath);
		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("RECORD");
			for (int i = 0, nSize = nodeList.getLength(); i < nSize; ++i) {
				final StateChange record = StateChange.load(nodeList.item(i), this);
				if (record != null) {
					this.addStateChange(record, true);
				}
			}
		} else {
			System.out.println("Could not parse DB-XML file --> [" + strFilePath + "] was not deleted");
		}
		return this.sizeStateChange();
	}

	private boolean saveStateChange(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave || this.isDirty() || this.isDirtyStateChange()) {
			final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);
			if (out != null) {
				final Collection<StateChange> collStateChange;
				synchronized (this.m_setStateChange) {
					collStateChange = new ArrayList<StateChange>(this.m_setStateChange);
				}
				final Iterator<StateChange> it = collStateChange.iterator();
				XMLParser.writeLine(0, out, "StateChange", null, true);
				while (it.hasNext()) {
					final StateChange nextRecord = it.next();
					if (nextRecord != null) {
						nextRecord.save(out);
						nextRecord.setDirty(false);
					}
				}
				XMLParser.writeLine(0, out, "StateChange", null, false);
				bSaved = true;
				this.m_bDirtyStateChange = false;
			} else {
				System.out.println("Could not write DB-XML file --> [" + strFilePath + "] ");
			}
			XMLParser.close(out);
		}
		return bSaved;
	}

	private final int loadStateGroupLink(final String strFilePath) {
		final Document doc = XMLParser.createDOMTree(strFilePath);
		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("RECORD");
			for (int i = 0, nSize = nodeList.getLength(); i < nSize; ++i) {
				final StateGroupLink record = StateGroupLink.load(nodeList.item(i), this);
				if (record != null) {
					this.addStateGroupLink(record, true);
				}
			}
		} else {
			System.out.println("Could not parse DB-XML file --> [" + strFilePath + "] was not deleted");
		}
		return this.sizeStateGroupLink();
	}

	private boolean saveStateGroupLink(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave || this.isDirty() || this.isDirtyStateGroupLink()) {
			final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);
			if (out != null) {
				final Collection<StateGroupLink> collStateGroupLink;
				synchronized (this.m_setStateGroupLink) {
					collStateGroupLink = new ArrayList<StateGroupLink>(this.m_setStateGroupLink);
				}
				final Iterator<StateGroupLink> it = collStateGroupLink.iterator();
				XMLParser.writeLine(0, out, "StateGroupLink", null, true);
				while (it.hasNext()) {
					final StateGroupLink nextRecord = it.next();
					if (nextRecord != null) {
						nextRecord.save(out);
						nextRecord.setDirty(false);
					}
				}
				XMLParser.writeLine(0, out, "StateGroupLink", null, false);
				bSaved = true;
				this.m_bDirtyStateGroupLink = false;
			} else {
				System.out.println("Could not write DB-XML file --> [" + strFilePath + "] ");
			}
			XMLParser.close(out);
		}
		return bSaved;
	}

	private final int loadNotificationScope(final String strFilePath) {
		final Document doc = XMLParser.createDOMTree(strFilePath);
		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("RECORD");
			for (int i = 0, nSize = nodeList.getLength(); i < nSize; ++i) {
				final NotificationScope record = NotificationScope.load(nodeList.item(i), this);
				if (record != null) {
					this.addNotificationScope(record, true);
				}
			}
		} else {
			System.out.println("Could not parse DB-XML file --> [" + strFilePath + "] was not deleted");
		}
		return this.sizeNotificationScope();
	}

	private boolean saveNotificationScope(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave || this.isDirty() || this.isDirtyNotificationScope()) {
			final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);
			if (out != null) {
				final Collection<NotificationScope> collNotificationScope;
				synchronized (this.m_setNotificationScope) {
					collNotificationScope = new ArrayList<NotificationScope>(this.m_setNotificationScope);
				}
				final Iterator<NotificationScope> it = collNotificationScope.iterator();
				XMLParser.writeLine(0, out, "NotificationScope", null, true);
				while (it.hasNext()) {
					final NotificationScope nextRecord = it.next();
					if (nextRecord != null) {
						nextRecord.save(out);
						nextRecord.setDirty(false);
					}
				}
				XMLParser.writeLine(0, out, "NotificationScope", null, false);
				bSaved = true;
				this.m_bDirtyNotificationScope = false;
			} else {
				System.out.println("Could not write DB-XML file --> [" + strFilePath + "] ");
			}
			XMLParser.close(out);
		}
		return bSaved;
	}

	private final int loadModule(final String strFilePath) {
		final Document doc = XMLParser.createDOMTree(strFilePath);
		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("RECORD");
			for (int i = 0, nSize = nodeList.getLength(); i < nSize; ++i) {
				final Module record = Module.load(nodeList.item(i), this);
				if (record != null) {
					this.addModule(record, true);
				}
			}
		} else {
			System.out.println("Could not parse DB-XML file --> [" + strFilePath + "] was not deleted");
		}
		return this.sizeModule();
	}

	private boolean saveModule(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave || this.isDirty() || this.isDirtyModule()) {
			final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);
			if (out != null) {
				final Collection<Module> collModule;
				synchronized (this.m_setModule) {
					collModule = new ArrayList<Module>(this.m_setModule);
				}
				final Iterator<Module> it = collModule.iterator();
				XMLParser.writeLine(0, out, "Module", null, true);
				while (it.hasNext()) {
					final Module nextRecord = it.next();
					if (nextRecord != null) {
						nextRecord.save(out);
						nextRecord.setDirty(false);
					}
				}
				XMLParser.writeLine(0, out, "Module", null, false);
				bSaved = true;
				this.m_bDirtyModule = false;
			} else {
				System.out.println("Could not write DB-XML file --> [" + strFilePath + "] ");
			}
			XMLParser.close(out);
		}
		return bSaved;
	}

	private boolean saveModelVersion(final String strFilePath, final boolean bEnforceSave) {
		boolean bSaved = false;
		if (bEnforceSave) {
			bSaved = this.m_modelVersion.save(strFilePath);
			if (!bSaved) {
				System.out.println("Could not write ModelVersion-XML file --> [" + strFilePath + "] ");
			}
		}
		return bSaved;
	}

	public final boolean save() {
		boolean bSaved = false;
		ISource server = ServerFactory.create();
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

			final Collection<Observer> collObserver;
			final Collection<ObserverLink> collObserverLink;
			final Collection<State> collState;
			final Collection<StateType> collStateType;
			final Collection<StateGroup> collStateGroup;
			final Collection<StateChange> collStateChange;
			final Collection<StateGroupLink> collStateGroupLink;
			final Collection<NotificationScope> collNotificationScope;
			final Collection<Module> collModule;
			synchronized (this) {
				collObserver = new ArrayList<Observer>(this.m_setObserver);
				collObserverLink = new ArrayList<ObserverLink>(this.m_setObserverLink);
				collState = new ArrayList<State>(this.m_setState);
				collStateType = new ArrayList<StateType>(this.m_setStateType);
				collStateGroup = new ArrayList<StateGroup>(this.m_setStateGroup);
				collStateChange = new ArrayList<StateChange>(this.m_setStateChange);
				collStateGroupLink = new ArrayList<StateGroupLink>(this.m_setStateGroupLink);
				collNotificationScope = new ArrayList<NotificationScope>(this.m_setNotificationScope);
				collModule = new ArrayList<Module>(this.m_setModule);
			}

			// save [Observer]
			// =============================
			strBuf.append("<Observer>");
			final Iterator<Observer> itObserver = collObserver.iterator();
			while (itObserver.hasNext()) {
				Observer nextObserver = itObserver.next();
				if ((nextObserver != null) && (nextObserver.isDirty())) {
					strBuf.append(nextObserver.toXML());
				}
			}
			strBuf.append("</Observer>");
			// save [ObserverLink]
			// =============================
			strBuf.append("<ObserverLink>");
			final Iterator<ObserverLink> itObserverLink = collObserverLink.iterator();
			while (itObserverLink.hasNext()) {
				ObserverLink nextObserverLink = itObserverLink.next();
				if ((nextObserverLink != null) && (nextObserverLink.isDirty())) {
					strBuf.append(nextObserverLink.toXML());
				}
			}
			strBuf.append("</ObserverLink>");
			// save [State]
			// =============================
			strBuf.append("<State>");
			final Iterator<State> itState = collState.iterator();
			while (itState.hasNext()) {
				State nextState = itState.next();
				if ((nextState != null) && (nextState.isDirty())) {
					strBuf.append(nextState.toXML());
				}
			}
			strBuf.append("</State>");
			// save [StateType]
			// =============================
			strBuf.append("<StateType>");
			final Iterator<StateType> itStateType = collStateType.iterator();
			while (itStateType.hasNext()) {
				StateType nextStateType = itStateType.next();
				if ((nextStateType != null) && (nextStateType.isDirty())) {
					strBuf.append(nextStateType.toXML());
				}
			}
			strBuf.append("</StateType>");
			// save [StateGroup]
			// =============================
			strBuf.append("<StateGroup>");
			final Iterator<StateGroup> itStateGroup = collStateGroup.iterator();
			while (itStateGroup.hasNext()) {
				StateGroup nextStateGroup = itStateGroup.next();
				if ((nextStateGroup != null) && (nextStateGroup.isDirty())) {
					strBuf.append(nextStateGroup.toXML());
				}
			}
			strBuf.append("</StateGroup>");
			// save [StateChange]
			// =============================
			strBuf.append("<StateChange>");
			final Iterator<StateChange> itStateChange = collStateChange.iterator();
			while (itStateChange.hasNext()) {
				StateChange nextStateChange = itStateChange.next();
				if ((nextStateChange != null) && (nextStateChange.isDirty())) {
					strBuf.append(nextStateChange.toXML());
				}
			}
			strBuf.append("</StateChange>");
			// save [StateGroupLink]
			// =============================
			strBuf.append("<StateGroupLink>");
			final Iterator<StateGroupLink> itStateGroupLink = collStateGroupLink.iterator();
			while (itStateGroupLink.hasNext()) {
				StateGroupLink nextStateGroupLink = itStateGroupLink.next();
				if ((nextStateGroupLink != null) && (nextStateGroupLink.isDirty())) {
					strBuf.append(nextStateGroupLink.toXML());
				}
			}
			strBuf.append("</StateGroupLink>");
			// save [NotificationScope]
			// =============================
			strBuf.append("<NotificationScope>");
			final Iterator<NotificationScope> itNotificationScope = collNotificationScope.iterator();
			while (itNotificationScope.hasNext()) {
				NotificationScope nextNotificationScope = itNotificationScope.next();
				if ((nextNotificationScope != null) && (nextNotificationScope.isDirty())) {
					strBuf.append(nextNotificationScope.toXML());
				}
			}
			strBuf.append("</NotificationScope>");
			// save [Module]
			// =============================
			strBuf.append("<Module>");
			final Iterator<Module> itModule = collModule.iterator();
			while (itModule.hasNext()) {
				Module nextModule = itModule.next();
				if ((nextModule != null) && (nextModule.isDirty())) {
					strBuf.append(nextModule.toXML());
				}
			}
			strBuf.append("</Module>");

			strBuf.append("</SET>");
			bSaved = server.update(strBuf.toString());
		}
		return bSaved;
	}

	public final String toString() {
		final String NL = "\r\n";
		final StringBuffer strBuf = new StringBuffer();
		final String strSizeObserver = Integer.toString(this.sizeObserver());
		strBuf.append("(1) Observer [" + strSizeObserver + "]");
		strBuf.append(NL);
		final String strSizeObserverLink = Integer.toString(this.sizeObserverLink());
		strBuf.append("(2) ObserverLink [" + strSizeObserverLink + "]");
		strBuf.append(NL);
		final String strSizeState = Integer.toString(this.sizeState());
		strBuf.append("(3) State [" + strSizeState + "]");
		strBuf.append(NL);
		final String strSizeStateType = Integer.toString(this.sizeStateType());
		strBuf.append("(4) StateType [" + strSizeStateType + "]");
		strBuf.append(NL);
		final String strSizeStateGroup = Integer.toString(this.sizeStateGroup());
		strBuf.append("(5) StateGroup [" + strSizeStateGroup + "]");
		strBuf.append(NL);
		final String strSizeStateChange = Integer.toString(this.sizeStateChange());
		strBuf.append("(6) StateChange [" + strSizeStateChange + "]");
		strBuf.append(NL);
		final String strSizeStateGroupLink = Integer.toString(this.sizeStateGroupLink());
		strBuf.append("(7) StateGroupLink [" + strSizeStateGroupLink + "]");
		strBuf.append(NL);
		final String strSizeNotificationScope = Integer.toString(this.sizeNotificationScope());
		strBuf.append("(8) NotificationScope [" + strSizeNotificationScope + "]");
		strBuf.append(NL);
		final String strSizeModule = Integer.toString(this.sizeModule());
		strBuf.append("(9) Module [" + strSizeModule + "]");
		strBuf.append(NL);
		return strBuf.toString();
	}

	private boolean isReferencedObserver(final long lPrimaryKey) {
		if (0 < this.countObserverLinkBySource(lPrimaryKey)) {
			return true;
		}
		if (0 < this.countObserverLinkByDestination(lPrimaryKey)) {
			return true;
		}
		if (0 < this.countNotificationScopeByObserver(lPrimaryKey)) {
			return true;
		}
		return false;
	}

	public final List<Observer> referencesObserverByState(final long State) {
		final List<Observer> list = new ArrayList<Observer>();
		final Collection<Observer> collObserver;
		synchronized (this.m_setObserver) {
			collObserver = new ArrayList<Observer>(this.m_setObserver);
		}
		final Iterator<Observer> it = collObserver.iterator();
		while (it.hasNext()) {
			final Observer nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getState() == State)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countObserverByState(final long lPrimaryKey) {
		return this.referencesObserverByState(lPrimaryKey).size();
	}

	public final List<Observer> referencesObserverByStateGroup(final long StateGroup) {
		final List<Observer> list = new ArrayList<Observer>();
		final Collection<Observer> collObserver;
		synchronized (this.m_setObserver) {
			collObserver = new ArrayList<Observer>(this.m_setObserver);
		}
		final Iterator<Observer> it = collObserver.iterator();
		while (it.hasNext()) {
			final Observer nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getStateGroup() == StateGroup)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countObserverByStateGroup(final long lPrimaryKey) {
		return this.referencesObserverByStateGroup(lPrimaryKey).size();
	}

	private boolean isReferencedObserverLink(final long lPrimaryKey) {
		return false;
	}

	public final List<ObserverLink> referencesObserverLinkBySource(final long Source) {
		final List<ObserverLink> list = new ArrayList<ObserverLink>();
		final Collection<ObserverLink> collObserverLink;
		synchronized (this.m_setObserverLink) {
			collObserverLink = new ArrayList<ObserverLink>(this.m_setObserverLink);
		}
		final Iterator<ObserverLink> it = collObserverLink.iterator();
		while (it.hasNext()) {
			final ObserverLink nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getSource() == Source)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countObserverLinkBySource(final long lPrimaryKey) {
		return this.referencesObserverLinkBySource(lPrimaryKey).size();
	}

	public final List<ObserverLink> referencesObserverLinkByDestination(final long Destination) {
		final List<ObserverLink> list = new ArrayList<ObserverLink>();
		final Collection<ObserverLink> collObserverLink;
		synchronized (this.m_setObserverLink) {
			collObserverLink = new ArrayList<ObserverLink>(this.m_setObserverLink);
		}
		final Iterator<ObserverLink> it = collObserverLink.iterator();
		while (it.hasNext()) {
			final ObserverLink nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getDestination() == Destination)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countObserverLinkByDestination(final long lPrimaryKey) {
		return this.referencesObserverLinkByDestination(lPrimaryKey).size();
	}

	private boolean isReferencedState(final long lPrimaryKey) {
		if (0 < this.countObserverByState(lPrimaryKey)) {
			return true;
		}
		if (0 < this.countStateChangeByState(lPrimaryKey)) {
			return true;
		}
		return false;
	}

	public final List<State> referencesStateByStateType(final long StateType) {
		final List<State> list = new ArrayList<State>();
		final Collection<State> collState;
		synchronized (this.m_setState) {
			collState = new ArrayList<State>(this.m_setState);
		}
		final Iterator<State> it = collState.iterator();
		while (it.hasNext()) {
			final State nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getStateType() == StateType)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countStateByStateType(final long lPrimaryKey) {
		return this.referencesStateByStateType(lPrimaryKey).size();
	}

	private boolean isReferencedStateType(final long lPrimaryKey) {
		if (0 < this.countStateByStateType(lPrimaryKey)) {
			return true;
		}
		return false;
	}

	private boolean isReferencedStateGroup(final long lPrimaryKey) {
		if (0 < this.countObserverByStateGroup(lPrimaryKey)) {
			return true;
		}
		if (0 < this.countStateGroupLinkBySource(lPrimaryKey)) {
			return true;
		}
		if (0 < this.countStateGroupLinkByDestination(lPrimaryKey)) {
			return true;
		}
		return false;
	}

	private boolean isReferencedStateChange(final long lPrimaryKey) {
		return false;
	}

	public final List<StateChange> referencesStateChangeByState(final long State) {
		final List<StateChange> list = new ArrayList<StateChange>();
		final Collection<StateChange> collStateChange;
		synchronized (this.m_setStateChange) {
			collStateChange = new ArrayList<StateChange>(this.m_setStateChange);
		}
		final Iterator<StateChange> it = collStateChange.iterator();
		while (it.hasNext()) {
			final StateChange nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getState() == State)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countStateChangeByState(final long lPrimaryKey) {
		return this.referencesStateChangeByState(lPrimaryKey).size();
	}

	private boolean isReferencedStateGroupLink(final long lPrimaryKey) {
		return false;
	}

	public final List<StateGroupLink> referencesStateGroupLinkBySource(final long Source) {
		final List<StateGroupLink> list = new ArrayList<StateGroupLink>();
		final Collection<StateGroupLink> collStateGroupLink;
		synchronized (this.m_setStateGroupLink) {
			collStateGroupLink = new ArrayList<StateGroupLink>(this.m_setStateGroupLink);
		}
		final Iterator<StateGroupLink> it = collStateGroupLink.iterator();
		while (it.hasNext()) {
			final StateGroupLink nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getSource() == Source)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countStateGroupLinkBySource(final long lPrimaryKey) {
		return this.referencesStateGroupLinkBySource(lPrimaryKey).size();
	}

	public final List<StateGroupLink> referencesStateGroupLinkByDestination(final long Destination) {
		final List<StateGroupLink> list = new ArrayList<StateGroupLink>();
		final Collection<StateGroupLink> collStateGroupLink;
		synchronized (this.m_setStateGroupLink) {
			collStateGroupLink = new ArrayList<StateGroupLink>(this.m_setStateGroupLink);
		}
		final Iterator<StateGroupLink> it = collStateGroupLink.iterator();
		while (it.hasNext()) {
			final StateGroupLink nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getDestination() == Destination)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countStateGroupLinkByDestination(final long lPrimaryKey) {
		return this.referencesStateGroupLinkByDestination(lPrimaryKey).size();
	}

	private boolean isReferencedNotificationScope(final long lPrimaryKey) {
		return false;
	}

	public final List<NotificationScope> referencesNotificationScopeByObserver(final long Observer) {
		final List<NotificationScope> list = new ArrayList<NotificationScope>();
		final Collection<NotificationScope> collNotificationScope;
		synchronized (this.m_setNotificationScope) {
			collNotificationScope = new ArrayList<NotificationScope>(this.m_setNotificationScope);
		}
		final Iterator<NotificationScope> it = collNotificationScope.iterator();
		while (it.hasNext()) {
			final NotificationScope nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getObserver() == Observer)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countNotificationScopeByObserver(final long lPrimaryKey) {
		return this.referencesNotificationScopeByObserver(lPrimaryKey).size();
	}

	public final List<NotificationScope> referencesNotificationScopeByModule(final long Module) {
		final List<NotificationScope> list = new ArrayList<NotificationScope>();
		final Collection<NotificationScope> collNotificationScope;
		synchronized (this.m_setNotificationScope) {
			collNotificationScope = new ArrayList<NotificationScope>(this.m_setNotificationScope);
		}
		final Iterator<NotificationScope> it = collNotificationScope.iterator();
		while (it.hasNext()) {
			final NotificationScope nextRecord = it.next();
			if ((nextRecord != null) && (nextRecord.getModule() == Module)) {
				list.add(nextRecord);
			}
		}
		return list;
	}

	private int countNotificationScopeByModule(final long lPrimaryKey) {
		return this.referencesNotificationScopeByModule(lPrimaryKey).size();
	}

	private boolean isReferencedModule(final long lPrimaryKey) {
		if (0 < this.countNotificationScopeByModule(lPrimaryKey)) {
			return true;
		}
		return false;
	}

	public final File zip(final String strDatabaseDir, final String strZipFilePath) {
		final String strZipPath = strDatabaseDir + File.separatorChar + "CommunicationModel" + DB_EXTENSION;
		final String[] XML_FILES = new String[] { strDatabaseDir + File.separatorChar + "Observer.xml",
				strDatabaseDir + File.separatorChar + "ObserverLink.xml",
				strDatabaseDir + File.separatorChar + "State.xml",
				strDatabaseDir + File.separatorChar + "StateType.xml",
				strDatabaseDir + File.separatorChar + "StateGroup.xml",
				strDatabaseDir + File.separatorChar + "StateChange.xml",
				strDatabaseDir + File.separatorChar + "StateGroupLink.xml",
				strDatabaseDir + File.separatorChar + "NotificationScope.xml",
				strDatabaseDir + File.separatorChar + "Module.xml",
				strDatabaseDir + File.separatorChar + MODEL_VERSION_FILE };

		FileSystem.zipFiles(strZipPath, XML_FILES);
		final File fileZip = new File(strZipPath);
		if (fileZip.exists()) {
			return fileZip;
		}
		return null;
	}

	public synchronized final boolean isDirty() {
		boolean bDirty = this.m_bDirty;
		if (!bDirty) {
			bDirty = this.isDirtyObserver() || this.isDirtyObserverLink() || this.isDirtyState()
					|| this.isDirtyStateType() || this.isDirtyStateGroup() || this.isDirtyStateChange()
					|| this.isDirtyStateGroupLink() || this.isDirtyNotificationScope() || this.isDirtyModule();
		}
		return bDirty;
	}

	public synchronized final void setAllDirty(final boolean bDirty) {
		final Iterator<Observer> itObserver = this.m_setObserver.iterator();
		while (itObserver.hasNext()) {
			final Observer nextRecord = itObserver.next();
			if (nextRecord != null) {
				nextRecord.setDirty(bDirty);
			}
		}
		final Iterator<ObserverLink> itObserverLink = this.m_setObserverLink.iterator();
		while (itObserverLink.hasNext()) {
			final ObserverLink nextRecord = itObserverLink.next();
			if (nextRecord != null) {
				nextRecord.setDirty(bDirty);
			}
		}
		final Iterator<State> itState = this.m_setState.iterator();
		while (itState.hasNext()) {
			final State nextRecord = itState.next();
			if (nextRecord != null) {
				nextRecord.setDirty(bDirty);
			}
		}
		final Iterator<StateType> itStateType = this.m_setStateType.iterator();
		while (itStateType.hasNext()) {
			final StateType nextRecord = itStateType.next();
			if (nextRecord != null) {
				nextRecord.setDirty(bDirty);
			}
		}
		final Iterator<StateGroup> itStateGroup = this.m_setStateGroup.iterator();
		while (itStateGroup.hasNext()) {
			final StateGroup nextRecord = itStateGroup.next();
			if (nextRecord != null) {
				nextRecord.setDirty(bDirty);
			}
		}
		final Iterator<StateChange> itStateChange = this.m_setStateChange.iterator();
		while (itStateChange.hasNext()) {
			final StateChange nextRecord = itStateChange.next();
			if (nextRecord != null) {
				nextRecord.setDirty(bDirty);
			}
		}
		final Iterator<StateGroupLink> itStateGroupLink = this.m_setStateGroupLink.iterator();
		while (itStateGroupLink.hasNext()) {
			final StateGroupLink nextRecord = itStateGroupLink.next();
			if (nextRecord != null) {
				nextRecord.setDirty(bDirty);
			}
		}
		final Iterator<NotificationScope> itNotificationScope = this.m_setNotificationScope.iterator();
		while (itNotificationScope.hasNext()) {
			final NotificationScope nextRecord = itNotificationScope.next();
			if (nextRecord != null) {
				nextRecord.setDirty(bDirty);
			}
		}
		final Iterator<Module> itModule = this.m_setModule.iterator();
		while (itModule.hasNext()) {
			final Module nextRecord = itModule.next();
			if (nextRecord != null) {
				nextRecord.setDirty(bDirty);
			}
		}
	}

	public final void add(final CommunicationModel dbFragment) {
		if (dbFragment != null) {
			this.addAllObserver(dbFragment.listObserver());
			this.addAllObserverLink(dbFragment.listObserverLink());
			this.addAllState(dbFragment.listState());
			this.addAllStateType(dbFragment.listStateType());
			this.addAllStateGroup(dbFragment.listStateGroup());
			this.addAllStateChange(dbFragment.listStateChange());
			this.addAllStateGroupLink(dbFragment.listStateGroupLink());
			this.addAllNotificationScope(dbFragment.listNotificationScope());
			this.addAllModule(dbFragment.listModule());
		}
	}

	private final class ThreadShutdownHook extends Thread {
		public ThreadShutdownHook() {
			super("Database - ShutdownHook");
		}

		public final void run() {
			CommunicationModel.this.saveAll(false);
		}
	}
}
