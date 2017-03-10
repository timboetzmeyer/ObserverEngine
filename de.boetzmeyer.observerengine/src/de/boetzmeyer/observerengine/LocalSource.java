package de.boetzmeyer.observerengine;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.List;

import org.w3c.dom.Document;

final class LocalSource implements ISource, Runnable {
	/**
	 * the time gap that the save thread sleeps until it checks for
	 * modifications again
	 */
	private final long AUTO_SAVE_TIME = 10000;

	/**
	 * singleton
	 */
	private static LocalSource sm_server = null;

	/**
	 * the database class
	 */
	private CommunicationModel m_db = null;

	private LocalSource(final String strDatabaseDir) {
		final File dir = new File(strDatabaseDir);
		dir.mkdirs();

		this.m_db = CommunicationModel.create(strDatabaseDir);

		if (null == this.m_db) {
			throw new IllegalArgumentException("Unable to create database from directory [" + strDatabaseDir + "]");
		}

		new Thread(this, "CommunicationModel Auto Save Thread [" + AUTO_SAVE_TIME + " ms]").start();
	}

	public synchronized static final void reset() {
		sm_server = null;
	}

	public static final synchronized LocalSource create() {
		if (null == sm_server) {
			sm_server = new LocalSource(Settings.getLocaleDatabaseDir());
		}

		return sm_server;
	}

	public final void run() {
		while (true) {
			try {
				this.m_db.saveAll(false);

				Thread.sleep(AUTO_SAVE_TIME);
			} catch (final Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	public final String getServerName() {
		return "localhost";
	}

	public final int getPort() {
		return Settings.INVALID_PORT;
	}

	/**
	 * returns the data model of the server to check compatibility
	 */
	public final ModelVersion getModelVersion() {
		return this.m_db.getModelVersion();
	}

	/**
	 * checks, if the data model of the server is compatible to that of the
	 * client
	 */
	public final boolean isCompatible(final ModelVersion clientModel) {
		return true;
	}

	/**
	 * loads a database from the given directory
	 */
	public final boolean load(final String strDirPath) {
		return false;
	}

	public final String toString() {
		return "LocalServer";
	}

	public final boolean update(final String strXML) {
		boolean bUpdated = false;

		final File fileTemp = new File(
				Settings.getLocaleDatabaseDir() + File.separatorChar + System.currentTimeMillis() + ".xml");

		final OutputStreamWriter out = XMLParser.openStreamWriter(fileTemp.getAbsolutePath());

		if (out != null) {
			XMLParser.writeLine(0, out, "SET", strXML, true, false); // don't
																		// escape
		}

		XMLParser.close(out);

		final Document doc = XMLParser.createDOMTree(fileTemp);

		if (doc != null) {
			final List<Observer> listObserver = CommunicationModelParser.extractObserver(doc);
			for (int i = 0, nSizeObserver = listObserver.size(); i < nSizeObserver; ++i) {
				final Observer newRecord = listObserver.get(i);
				final Observer oldRecord = this.m_db.findByIDObserver(newRecord.getPrimaryKey());

				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					oldRecord.setDirty(true);
					this.m_db.setDirty(true);
				} else {
					this.m_db.addObserver(newRecord, false);
				}
			}

			final List<ObserverLink> listObserverLink = CommunicationModelParser.extractObserverLink(doc);
			for (int i = 0, nSizeObserverLink = listObserverLink.size(); i < nSizeObserverLink; ++i) {
				final ObserverLink newRecord = listObserverLink.get(i);
				final ObserverLink oldRecord = this.m_db.findByIDObserverLink(newRecord.getPrimaryKey());

				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					oldRecord.setDirty(true);
					this.m_db.setDirty(true);
				} else {
					this.m_db.addObserverLink(newRecord, false);
				}
			}

			final List<State> listState = CommunicationModelParser.extractState(doc);
			for (int i = 0, nSizeState = listState.size(); i < nSizeState; ++i) {
				final State newRecord = listState.get(i);
				final State oldRecord = this.m_db.findByIDState(newRecord.getPrimaryKey());

				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					oldRecord.setDirty(true);
					this.m_db.setDirty(true);
				} else {
					this.m_db.addState(newRecord, false);
				}
			}

			final List<StateType> listStateType = CommunicationModelParser.extractStateType(doc);
			for (int i = 0, nSizeStateType = listStateType.size(); i < nSizeStateType; ++i) {
				final StateType newRecord = listStateType.get(i);
				final StateType oldRecord = this.m_db.findByIDStateType(newRecord.getPrimaryKey());

				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					oldRecord.setDirty(true);
					this.m_db.setDirty(true);
				} else {
					this.m_db.addStateType(newRecord, false);
				}
			}

			final List<StateGroup> listStateGroup = CommunicationModelParser.extractStateGroup(doc);
			for (int i = 0, nSizeStateGroup = listStateGroup.size(); i < nSizeStateGroup; ++i) {
				final StateGroup newRecord = listStateGroup.get(i);
				final StateGroup oldRecord = this.m_db.findByIDStateGroup(newRecord.getPrimaryKey());

				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					oldRecord.setDirty(true);
					this.m_db.setDirty(true);
				} else {
					this.m_db.addStateGroup(newRecord, false);
				}
			}

			final List<StateChange> listStateChange = CommunicationModelParser.extractStateChange(doc);
			for (int i = 0, nSizeStateChange = listStateChange.size(); i < nSizeStateChange; ++i) {
				final StateChange newRecord = listStateChange.get(i);
				final StateChange oldRecord = this.m_db.findByIDStateChange(newRecord.getPrimaryKey());

				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					oldRecord.setDirty(true);
					this.m_db.setDirty(true);
				} else {
					this.m_db.addStateChange(newRecord, false);
				}
			}

			final List<StateGroupLink> listStateGroupLink = CommunicationModelParser.extractStateGroupLink(doc);
			for (int i = 0, nSizeStateGroupLink = listStateGroupLink.size(); i < nSizeStateGroupLink; ++i) {
				final StateGroupLink newRecord = listStateGroupLink.get(i);
				final StateGroupLink oldRecord = this.m_db.findByIDStateGroupLink(newRecord.getPrimaryKey());

				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					oldRecord.setDirty(true);
					this.m_db.setDirty(true);
				} else {
					this.m_db.addStateGroupLink(newRecord, false);
				}
			}

			final List<NotificationScope> listNotificationScope = CommunicationModelParser
					.extractNotificationScope(doc);
			for (int i = 0, nSizeNotificationScope = listNotificationScope.size(); i < nSizeNotificationScope; ++i) {
				final NotificationScope newRecord = listNotificationScope.get(i);
				final NotificationScope oldRecord = this.m_db.findByIDNotificationScope(newRecord.getPrimaryKey());

				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					oldRecord.setDirty(true);
					this.m_db.setDirty(true);
				} else {
					this.m_db.addNotificationScope(newRecord, false);
				}
			}

			final List<Module> listModule = CommunicationModelParser.extractModule(doc);
			for (int i = 0, nSizeModule = listModule.size(); i < nSizeModule; ++i) {
				final Module newRecord = listModule.get(i);
				final Module oldRecord = this.m_db.findByIDModule(newRecord.getPrimaryKey());

				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					oldRecord.setDirty(true);
					this.m_db.setDirty(true);
				} else {
					this.m_db.addModule(newRecord, false);
				}
			}

			bUpdated = true;
		}

		fileTemp.delete();

		return bUpdated;
	}

	/**
	 * get all records of type [Observer] from the server
	 */
	public final List<Observer> listObserver() {
		return this.m_db.listObserver();
	}

	/**
	 * get all records of type [Observer] from the server that match to all
	 * conditions in the list
	 */
	public final List<Observer> listObserver(final List<Condition> listConditions) {
		return this.m_db.listObserver(listConditions);
	}

	/**
	 * get all records of type [ObserverLink] from the server
	 */
	public final List<ObserverLink> listObserverLink() {
		return this.m_db.listObserverLink();
	}

	/**
	 * get all records of type [ObserverLink] from the server that match to all
	 * conditions in the list
	 */
	public final List<ObserverLink> listObserverLink(final List<Condition> listConditions) {
		return this.m_db.listObserverLink(listConditions);
	}

	/**
	 * get all records of type [State] from the server
	 */
	public final List<State> listState() {
		return this.m_db.listState();
	}

	/**
	 * get all records of type [State] from the server that match to all
	 * conditions in the list
	 */
	public final List<State> listState(final List<Condition> listConditions) {
		return this.m_db.listState(listConditions);
	}

	/**
	 * get all records of type [StateType] from the server
	 */
	public final List<StateType> listStateType() {
		return this.m_db.listStateType();
	}

	/**
	 * get all records of type [StateType] from the server that match to all
	 * conditions in the list
	 */
	public final List<StateType> listStateType(final List<Condition> listConditions) {
		return this.m_db.listStateType(listConditions);
	}

	/**
	 * get all records of type [StateGroup] from the server
	 */
	public final List<StateGroup> listStateGroup() {
		return this.m_db.listStateGroup();
	}

	/**
	 * get all records of type [StateGroup] from the server that match to all
	 * conditions in the list
	 */
	public final List<StateGroup> listStateGroup(final List<Condition> listConditions) {
		return this.m_db.listStateGroup(listConditions);
	}

	/**
	 * get all records of type [StateChange] from the server
	 */
	public final List<StateChange> listStateChange() {
		return this.m_db.listStateChange();
	}

	/**
	 * get all records of type [StateChange] from the server that match to all
	 * conditions in the list
	 */
	public final List<StateChange> listStateChange(final List<Condition> listConditions) {
		return this.m_db.listStateChange(listConditions);
	}

	/**
	 * get all records of type [StateGroupLink] from the server
	 */
	public final List<StateGroupLink> listStateGroupLink() {
		return this.m_db.listStateGroupLink();
	}

	/**
	 * get all records of type [StateGroupLink] from the server that match to
	 * all conditions in the list
	 */
	public final List<StateGroupLink> listStateGroupLink(final List<Condition> listConditions) {
		return this.m_db.listStateGroupLink(listConditions);
	}

	/**
	 * get all records of type [NotificationScope] from the server
	 */
	public final List<NotificationScope> listNotificationScope() {
		return this.m_db.listNotificationScope();
	}

	/**
	 * get all records of type [NotificationScope] from the server that match to
	 * all conditions in the list
	 */
	public final List<NotificationScope> listNotificationScope(final List<Condition> listConditions) {
		return this.m_db.listNotificationScope(listConditions);
	}

	/**
	 * get all records of type [Module] from the server
	 */
	public final List<Module> listModule() {
		return this.m_db.listModule();
	}

	/**
	 * get all records of type [Module] from the server that match to all
	 * conditions in the list
	 */
	public final List<Module> listModule(final List<Condition> listConditions) {
		return this.m_db.listModule(listConditions);
	}

	/**
	 * get the corresponding [Observer] record from the server
	 */
	public final Observer findByIDObserver(final long lPrimaryKey) {
		return this.m_db.findByIDObserver(lPrimaryKey);
	}

	/**
	 * tries to delete the record of type [Observer] from the server if it is no
	 * longer referenced by another record
	 */
	public final boolean deleteObserver(final long lPrimaryKey) {
		return this.m_db.deleteObserver(lPrimaryKey);
	}

	/**
	 * get the corresponding [ObserverLink] record from the server
	 */
	public final ObserverLink findByIDObserverLink(final long lPrimaryKey) {
		return this.m_db.findByIDObserverLink(lPrimaryKey);
	}

	/**
	 * tries to delete the record of type [ObserverLink] from the server if it
	 * is no longer referenced by another record
	 */
	public final boolean deleteObserverLink(final long lPrimaryKey) {
		return this.m_db.deleteObserverLink(lPrimaryKey);
	}

	/**
	 * get the corresponding [State] record from the server
	 */
	public final State findByIDState(final long lPrimaryKey) {
		return this.m_db.findByIDState(lPrimaryKey);
	}

	/**
	 * tries to delete the record of type [State] from the server if it is no
	 * longer referenced by another record
	 */
	public final boolean deleteState(final long lPrimaryKey) {
		return this.m_db.deleteState(lPrimaryKey);
	}

	/**
	 * get the corresponding [StateType] record from the server
	 */
	public final StateType findByIDStateType(final long lPrimaryKey) {
		return this.m_db.findByIDStateType(lPrimaryKey);
	}

	/**
	 * tries to delete the record of type [StateType] from the server if it is
	 * no longer referenced by another record
	 */
	public final boolean deleteStateType(final long lPrimaryKey) {
		return this.m_db.deleteStateType(lPrimaryKey);
	}

	/**
	 * get the corresponding [StateGroup] record from the server
	 */
	public final StateGroup findByIDStateGroup(final long lPrimaryKey) {
		return this.m_db.findByIDStateGroup(lPrimaryKey);
	}

	/**
	 * tries to delete the record of type [StateGroup] from the server if it is
	 * no longer referenced by another record
	 */
	public final boolean deleteStateGroup(final long lPrimaryKey) {
		return this.m_db.deleteStateGroup(lPrimaryKey);
	}

	/**
	 * get the corresponding [StateChange] record from the server
	 */
	public final StateChange findByIDStateChange(final long lPrimaryKey) {
		return this.m_db.findByIDStateChange(lPrimaryKey);
	}

	/**
	 * tries to delete the record of type [StateChange] from the server if it is
	 * no longer referenced by another record
	 */
	public final boolean deleteStateChange(final long lPrimaryKey) {
		return this.m_db.deleteStateChange(lPrimaryKey);
	}

	/**
	 * get the corresponding [StateGroupLink] record from the server
	 */
	public final StateGroupLink findByIDStateGroupLink(final long lPrimaryKey) {
		return this.m_db.findByIDStateGroupLink(lPrimaryKey);
	}

	/**
	 * tries to delete the record of type [StateGroupLink] from the server if it
	 * is no longer referenced by another record
	 */
	public final boolean deleteStateGroupLink(final long lPrimaryKey) {
		return this.m_db.deleteStateGroupLink(lPrimaryKey);
	}

	/**
	 * get the corresponding [NotificationScope] record from the server
	 */
	public final NotificationScope findByIDNotificationScope(final long lPrimaryKey) {
		return this.m_db.findByIDNotificationScope(lPrimaryKey);
	}

	/**
	 * tries to delete the record of type [NotificationScope] from the server if
	 * it is no longer referenced by another record
	 */
	public final boolean deleteNotificationScope(final long lPrimaryKey) {
		return this.m_db.deleteNotificationScope(lPrimaryKey);
	}

	/**
	 * get the corresponding [Module] record from the server
	 */
	public final Module findByIDModule(final long lPrimaryKey) {
		return this.m_db.findByIDModule(lPrimaryKey);
	}

	/**
	 * tries to delete the record of type [Module] from the server if it is no
	 * longer referenced by another record
	 */
	public final boolean deleteModule(final long lPrimaryKey) {
		return this.m_db.deleteModule(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [Observer] record from the
	 * server
	 */
	public final CommunicationModel exportObserver(final long lPrimaryKey) {
		return this.m_db.exportObserver(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [Observer] records in the
	 * list from the server
	 */
	public final CommunicationModel exportObserver(final List<Observer> list) {
		return this.m_db.exportObserver(list);
	}

	/**
	 * get all records that are referenced by this [ObserverLink] record from
	 * the server
	 */
	public final CommunicationModel exportObserverLink(final long lPrimaryKey) {
		return this.m_db.exportObserverLink(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [ObserverLink] records in the
	 * list from the server
	 */
	public final CommunicationModel exportObserverLink(final List<ObserverLink> list) {
		return this.m_db.exportObserverLink(list);
	}

	/**
	 * get all records that are referenced by this [State] record from the
	 * server
	 */
	public final CommunicationModel exportState(final long lPrimaryKey) {
		return this.m_db.exportState(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [State] records in the list
	 * from the server
	 */
	public final CommunicationModel exportState(final List<State> list) {
		return this.m_db.exportState(list);
	}

	/**
	 * get all records that are referenced by this [StateType] record from the
	 * server
	 */
	public final CommunicationModel exportStateType(final long lPrimaryKey) {
		return this.m_db.exportStateType(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [StateType] records in the
	 * list from the server
	 */
	public final CommunicationModel exportStateType(final List<StateType> list) {
		return this.m_db.exportStateType(list);
	}

	/**
	 * get all records that are referenced by this [StateGroup] record from the
	 * server
	 */
	public final CommunicationModel exportStateGroup(final long lPrimaryKey) {
		return this.m_db.exportStateGroup(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [StateGroup] records in the
	 * list from the server
	 */
	public final CommunicationModel exportStateGroup(final List<StateGroup> list) {
		return this.m_db.exportStateGroup(list);
	}

	/**
	 * get all records that are referenced by this [StateChange] record from the
	 * server
	 */
	public final CommunicationModel exportStateChange(final long lPrimaryKey) {
		return this.m_db.exportStateChange(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [StateChange] records in the
	 * list from the server
	 */
	public final CommunicationModel exportStateChange(final List<StateChange> list) {
		return this.m_db.exportStateChange(list);
	}

	/**
	 * get all records that are referenced by this [StateGroupLink] record from
	 * the server
	 */
	public final CommunicationModel exportStateGroupLink(final long lPrimaryKey) {
		return this.m_db.exportStateGroupLink(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [StateGroupLink] records in
	 * the list from the server
	 */
	public final CommunicationModel exportStateGroupLink(final List<StateGroupLink> list) {
		return this.m_db.exportStateGroupLink(list);
	}

	/**
	 * get all records that are referenced by this [NotificationScope] record
	 * from the server
	 */
	public final CommunicationModel exportNotificationScope(final long lPrimaryKey) {
		return this.m_db.exportNotificationScope(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [NotificationScope] records
	 * in the list from the server
	 */
	public final CommunicationModel exportNotificationScope(final List<NotificationScope> list) {
		return this.m_db.exportNotificationScope(list);
	}

	/**
	 * get all records that are referenced by this [Module] record from the
	 * server
	 */
	public final CommunicationModel exportModule(final long lPrimaryKey) {
		return this.m_db.exportModule(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [Module] records in the list
	 * from the server
	 */
	public final CommunicationModel exportModule(final List<Module> list) {
		return this.m_db.exportModule(list);
	}

	/**
	 * get all records that are referenced by this [Observer] record from the
	 * server
	 */
	public final CommunicationModel contextExportObserver(final long lPrimaryKey) {
		return this.m_db.contextExportObserver(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [Observer] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportObserver(final List<Observer> list) {
		return this.m_db.contextExportObserver(list);
	}

	/**
	 * get all records that are referenced by this [ObserverLink] record from
	 * the server
	 */
	public final CommunicationModel contextExportObserverLink(final long lPrimaryKey) {
		return this.m_db.contextExportObserverLink(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [ObserverLink] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportObserverLink(final List<ObserverLink> list) {
		return this.m_db.contextExportObserverLink(list);
	}

	/**
	 * get all records that are referenced by this [State] record from the
	 * server
	 */
	public final CommunicationModel contextExportState(final long lPrimaryKey) {
		return this.m_db.contextExportState(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [State] records in the list
	 * from the server
	 */
	public final CommunicationModel contextExportState(final List<State> list) {
		return this.m_db.contextExportState(list);
	}

	/**
	 * get all records that are referenced by this [StateType] record from the
	 * server
	 */
	public final CommunicationModel contextExportStateType(final long lPrimaryKey) {
		return this.m_db.contextExportStateType(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [StateType] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportStateType(final List<StateType> list) {
		return this.m_db.contextExportStateType(list);
	}

	/**
	 * get all records that are referenced by this [StateGroup] record from the
	 * server
	 */
	public final CommunicationModel contextExportStateGroup(final long lPrimaryKey) {
		return this.m_db.contextExportStateGroup(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [StateGroup] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportStateGroup(final List<StateGroup> list) {
		return this.m_db.contextExportStateGroup(list);
	}

	/**
	 * get all records that are referenced by this [StateChange] record from the
	 * server
	 */
	public final CommunicationModel contextExportStateChange(final long lPrimaryKey) {
		return this.m_db.contextExportStateChange(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [StateChange] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportStateChange(final List<StateChange> list) {
		return this.m_db.contextExportStateChange(list);
	}

	/**
	 * get all records that are referenced by this [StateGroupLink] record from
	 * the server
	 */
	public final CommunicationModel contextExportStateGroupLink(final long lPrimaryKey) {
		return this.m_db.contextExportStateGroupLink(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [StateGroupLink] records in
	 * the list from the server
	 */
	public final CommunicationModel contextExportStateGroupLink(final List<StateGroupLink> list) {
		return this.m_db.contextExportStateGroupLink(list);
	}

	/**
	 * get all records that are referenced by this [NotificationScope] record
	 * from the server
	 */
	public final CommunicationModel contextExportNotificationScope(final long lPrimaryKey) {
		return this.m_db.contextExportNotificationScope(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [NotificationScope] records
	 * in the list from the server
	 */
	public final CommunicationModel contextExportNotificationScope(final List<NotificationScope> list) {
		return this.m_db.contextExportNotificationScope(list);
	}

	/**
	 * get all records that are referenced by this [Module] record from the
	 * server
	 */
	public final CommunicationModel contextExportModule(final long lPrimaryKey) {
		return this.m_db.contextExportModule(lPrimaryKey);
	}

	/**
	 * get all records that are referenced by this [Module] records in the list
	 * from the server
	 */
	public final CommunicationModel contextExportModule(final List<Module> list) {
		return this.m_db.contextExportModule(list);
	}

	/**
	 * list all records in the [Observer] table, that use this primary key
	 */
	public final List<Observer> referencesObserverByState(final long State) {
		return this.m_db.referencesObserverByState(State);
	}

	/**
	 * list all records in the [Observer] table, that use this primary key
	 */
	public final List<Observer> referencesObserverByStateGroup(final long StateGroup) {
		return this.m_db.referencesObserverByStateGroup(StateGroup);
	}

	/**
	 * list all records in the [ObserverLink] table, that use this primary key
	 */
	public final List<ObserverLink> referencesObserverLinkBySource(final long Source) {
		return this.m_db.referencesObserverLinkBySource(Source);
	}

	/**
	 * list all records in the [ObserverLink] table, that use this primary key
	 */
	public final List<ObserverLink> referencesObserverLinkByDestination(final long Destination) {
		return this.m_db.referencesObserverLinkByDestination(Destination);
	}

	/**
	 * list all records in the [State] table, that use this primary key
	 */
	public final List<State> referencesStateByStateType(final long StateType) {
		return this.m_db.referencesStateByStateType(StateType);
	}

	/**
	 * list all records in the [StateChange] table, that use this primary key
	 */
	public final List<StateChange> referencesStateChangeByState(final long State) {
		return this.m_db.referencesStateChangeByState(State);
	}

	/**
	 * list all records in the [StateGroupLink] table, that use this primary key
	 */
	public final List<StateGroupLink> referencesStateGroupLinkBySource(final long Source) {
		return this.m_db.referencesStateGroupLinkBySource(Source);
	}

	/**
	 * list all records in the [StateGroupLink] table, that use this primary key
	 */
	public final List<StateGroupLink> referencesStateGroupLinkByDestination(final long Destination) {
		return this.m_db.referencesStateGroupLinkByDestination(Destination);
	}

	/**
	 * list all records in the [NotificationScope] table, that use this primary
	 * key
	 */
	public final List<NotificationScope> referencesNotificationScopeByObserver(final long Observer) {
		return this.m_db.referencesNotificationScopeByObserver(Observer);
	}

	/**
	 * list all records in the [NotificationScope] table, that use this primary
	 * key
	 */
	public final List<NotificationScope> referencesNotificationScopeByModule(final long Module) {
		return this.m_db.referencesNotificationScopeByModule(Module);
	}

}
