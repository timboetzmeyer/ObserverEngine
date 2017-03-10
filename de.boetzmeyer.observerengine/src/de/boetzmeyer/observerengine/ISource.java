package de.boetzmeyer.observerengine;

import java.util.List;

interface ISource {

	/**
	 * the computer name of the server, this client will connect to
	 */
	String getServerName();

	/**
	 * the server port, this client will connect to
	 */
	int getPort();

	/**
	 * return the data model of the server to check compatibility
	 */
	ModelVersion getModelVersion();

	/**
	 * checks, if the data model of the server is compatible to that of the
	 * client
	 */
	boolean isCompatible(final ModelVersion clientModel);

	/**
	 * update the server with one or more changed record sets
	 */
	boolean update(final String strXML);

	/**
	 * loads a database from the given directory
	 */
	boolean load(final String strDirPath);

	/**
	 * get all records of type [Observer] from the server
	 */
	List<Observer> listObserver();

	/**
	 * get all records of type [ObserverLink] from the server
	 */
	List<ObserverLink> listObserverLink();

	/**
	 * get all records of type [State] from the server
	 */
	List<State> listState();

	/**
	 * get all records of type [StateType] from the server
	 */
	List<StateType> listStateType();

	/**
	 * get all records of type [StateGroup] from the server
	 */
	List<StateGroup> listStateGroup();

	/**
	 * get all records of type [StateChange] from the server
	 */
	List<StateChange> listStateChange();

	/**
	 * get all records of type [StateGroupLink] from the server
	 */
	List<StateGroupLink> listStateGroupLink();

	/**
	 * get all records of type [NotificationScope] from the server
	 */
	List<NotificationScope> listNotificationScope();

	/**
	 * get all records of type [Module] from the server
	 */
	List<Module> listModule();

	/**
	 * get all records of type [Observer] from the server that match to all
	 * conditions in the list
	 */
	List<Observer> listObserver(final List<Condition> listConditions);

	/**
	 * get all records of type [ObserverLink] from the server that match to all
	 * conditions in the list
	 */
	List<ObserverLink> listObserverLink(final List<Condition> listConditions);

	/**
	 * get all records of type [State] from the server that match to all
	 * conditions in the list
	 */
	List<State> listState(final List<Condition> listConditions);

	/**
	 * get all records of type [StateType] from the server that match to all
	 * conditions in the list
	 */
	List<StateType> listStateType(final List<Condition> listConditions);

	/**
	 * get all records of type [StateGroup] from the server that match to all
	 * conditions in the list
	 */
	List<StateGroup> listStateGroup(final List<Condition> listConditions);

	/**
	 * get all records of type [StateChange] from the server that match to all
	 * conditions in the list
	 */
	List<StateChange> listStateChange(final List<Condition> listConditions);

	/**
	 * get all records of type [StateGroupLink] from the server that match to
	 * all conditions in the list
	 */
	List<StateGroupLink> listStateGroupLink(final List<Condition> listConditions);

	/**
	 * get all records of type [NotificationScope] from the server that match to
	 * all conditions in the list
	 */
	List<NotificationScope> listNotificationScope(final List<Condition> listConditions);

	/**
	 * get all records of type [Module] from the server that match to all
	 * conditions in the list
	 */
	List<Module> listModule(final List<Condition> listConditions);

	/**
	 * get the corresponding [Observer] record from the server
	 */
	Observer findByIDObserver(final long lObserverID);

	/**
	 * tries to delete the record of type [Observer] from the server if it is no
	 * longer referenced by another record
	 */
	boolean deleteObserver(final long lObserverID);

	/**
	 * get the corresponding [ObserverLink] record from the server
	 */
	ObserverLink findByIDObserverLink(final long lObserverLinkID);

	/**
	 * tries to delete the record of type [ObserverLink] from the server if it
	 * is no longer referenced by another record
	 */
	boolean deleteObserverLink(final long lObserverLinkID);

	/**
	 * get the corresponding [State] record from the server
	 */
	State findByIDState(final long lStateID);

	/**
	 * tries to delete the record of type [State] from the server if it is no
	 * longer referenced by another record
	 */
	boolean deleteState(final long lStateID);

	/**
	 * get the corresponding [StateType] record from the server
	 */
	StateType findByIDStateType(final long lStateTypeID);

	/**
	 * tries to delete the record of type [StateType] from the server if it is
	 * no longer referenced by another record
	 */
	boolean deleteStateType(final long lStateTypeID);

	/**
	 * get the corresponding [StateGroup] record from the server
	 */
	StateGroup findByIDStateGroup(final long lStateGroupID);

	/**
	 * tries to delete the record of type [StateGroup] from the server if it is
	 * no longer referenced by another record
	 */
	boolean deleteStateGroup(final long lStateGroupID);

	/**
	 * get the corresponding [StateChange] record from the server
	 */
	StateChange findByIDStateChange(final long lStateChangeID);

	/**
	 * tries to delete the record of type [StateChange] from the server if it is
	 * no longer referenced by another record
	 */
	boolean deleteStateChange(final long lStateChangeID);

	/**
	 * get the corresponding [StateGroupLink] record from the server
	 */
	StateGroupLink findByIDStateGroupLink(final long lStateGroupLinkID);

	/**
	 * tries to delete the record of type [StateGroupLink] from the server if it
	 * is no longer referenced by another record
	 */
	boolean deleteStateGroupLink(final long lStateGroupLinkID);

	/**
	 * get the corresponding [NotificationScope] record from the server
	 */
	NotificationScope findByIDNotificationScope(final long lNotificationScopeID);

	/**
	 * tries to delete the record of type [NotificationScope] from the server if
	 * it is no longer referenced by another record
	 */
	boolean deleteNotificationScope(final long lNotificationScopeID);

	/**
	 * get the corresponding [Module] record from the server
	 */
	Module findByIDModule(final long lModuleID);

	/**
	 * tries to delete the record of type [Module] from the server if it is no
	 * longer referenced by another record
	 */
	boolean deleteModule(final long lModuleID);

	/**
	 * get all records that are referenced by this [Observer] record from the
	 * server
	 */
	CommunicationModel exportObserver(final long lObserverID);

	/**
	 * get all records that are referenced by this [Observer] records in the
	 * list from the server
	 */
	CommunicationModel exportObserver(final List<Observer> list);

	/**
	 * get all records that are referenced by this [ObserverLink] record from
	 * the server
	 */
	CommunicationModel exportObserverLink(final long lObserverLinkID);

	/**
	 * get all records that are referenced by this [ObserverLink] records in the
	 * list from the server
	 */
	CommunicationModel exportObserverLink(final List<ObserverLink> list);

	/**
	 * get all records that are referenced by this [State] record from the
	 * server
	 */
	CommunicationModel exportState(final long lStateID);

	/**
	 * get all records that are referenced by this [State] records in the list
	 * from the server
	 */
	CommunicationModel exportState(final List<State> list);

	/**
	 * get all records that are referenced by this [StateType] record from the
	 * server
	 */
	CommunicationModel exportStateType(final long lStateTypeID);

	/**
	 * get all records that are referenced by this [StateType] records in the
	 * list from the server
	 */
	CommunicationModel exportStateType(final List<StateType> list);

	/**
	 * get all records that are referenced by this [StateGroup] record from the
	 * server
	 */
	CommunicationModel exportStateGroup(final long lStateGroupID);

	/**
	 * get all records that are referenced by this [StateGroup] records in the
	 * list from the server
	 */
	CommunicationModel exportStateGroup(final List<StateGroup> list);

	/**
	 * get all records that are referenced by this [StateChange] record from the
	 * server
	 */
	CommunicationModel exportStateChange(final long lStateChangeID);

	/**
	 * get all records that are referenced by this [StateChange] records in the
	 * list from the server
	 */
	CommunicationModel exportStateChange(final List<StateChange> list);

	/**
	 * get all records that are referenced by this [StateGroupLink] record from
	 * the server
	 */
	CommunicationModel exportStateGroupLink(final long lStateGroupLinkID);

	/**
	 * get all records that are referenced by this [StateGroupLink] records in
	 * the list from the server
	 */
	CommunicationModel exportStateGroupLink(final List<StateGroupLink> list);

	/**
	 * get all records that are referenced by this [NotificationScope] record
	 * from the server
	 */
	CommunicationModel exportNotificationScope(final long lNotificationScopeID);

	/**
	 * get all records that are referenced by this [NotificationScope] records
	 * in the list from the server
	 */
	CommunicationModel exportNotificationScope(final List<NotificationScope> list);

	/**
	 * get all records that are referenced by this [Module] record from the
	 * server
	 */
	CommunicationModel exportModule(final long lModuleID);

	/**
	 * get all records that are referenced by this [Module] records in the list
	 * from the server
	 */
	CommunicationModel exportModule(final List<Module> list);

	CommunicationModel contextExportObserver(final long lObserverID);

	CommunicationModel contextExportObserver(final List<Observer> list);

	CommunicationModel contextExportObserverLink(final long lObserverLinkID);

	CommunicationModel contextExportObserverLink(final List<ObserverLink> list);

	CommunicationModel contextExportState(final long lStateID);

	CommunicationModel contextExportState(final List<State> list);

	CommunicationModel contextExportStateType(final long lStateTypeID);

	CommunicationModel contextExportStateType(final List<StateType> list);

	CommunicationModel contextExportStateGroup(final long lStateGroupID);

	CommunicationModel contextExportStateGroup(final List<StateGroup> list);

	CommunicationModel contextExportStateChange(final long lStateChangeID);

	CommunicationModel contextExportStateChange(final List<StateChange> list);

	CommunicationModel contextExportStateGroupLink(final long lStateGroupLinkID);

	CommunicationModel contextExportStateGroupLink(final List<StateGroupLink> list);

	CommunicationModel contextExportNotificationScope(final long lNotificationScopeID);

	CommunicationModel contextExportNotificationScope(final List<NotificationScope> list);

	CommunicationModel contextExportModule(final long lModuleID);

	CommunicationModel contextExportModule(final List<Module> list);

	/**
	 * list all records in the [Observer] table, that use this primary key
	 */
	List<Observer> referencesObserverByState(final long State);

	/**
	 * list all records in the [Observer] table, that use this primary key
	 */
	List<Observer> referencesObserverByStateGroup(final long StateGroup);

	/**
	 * list all records in the [ObserverLink] table, that use this primary key
	 */
	List<ObserverLink> referencesObserverLinkBySource(final long Source);

	/**
	 * list all records in the [ObserverLink] table, that use this primary key
	 */
	List<ObserverLink> referencesObserverLinkByDestination(final long Destination);

	/**
	 * list all records in the [State] table, that use this primary key
	 */
	List<State> referencesStateByStateType(final long StateType);

	/**
	 * list all records in the [StateChange] table, that use this primary key
	 */
	List<StateChange> referencesStateChangeByState(final long State);

	/**
	 * list all records in the [StateGroupLink] table, that use this primary key
	 */
	List<StateGroupLink> referencesStateGroupLinkBySource(final long Source);

	/**
	 * list all records in the [StateGroupLink] table, that use this primary key
	 */
	List<StateGroupLink> referencesStateGroupLinkByDestination(final long Destination);

	/**
	 * list all records in the [NotificationScope] table, that use this primary
	 * key
	 */
	List<NotificationScope> referencesNotificationScopeByObserver(final long Observer);

	/**
	 * list all records in the [NotificationScope] table, that use this primary
	 * key
	 */
	List<NotificationScope> referencesNotificationScopeByModule(final long Module);

}
