package de.boetzmeyer.observerengine;

import java.io.File;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;

final class SQLSource implements ISource {

	private static SQLSource sm_server = null;

	private final String m_strServerName;
	private final int m_nPort;
	private final String m_strDriverClass;
	private final String m_strDriverProtocol;
	private final String m_strUser;
	private final String m_strPassword;

	private final SQLDatabase m_db;

	private SQLSource(final String strServerName, final int nPort, final String strDriverClass,
			final String strDriverProtocol, final String strUser, final String strPassword) {
		this.m_strServerName = strServerName;
		this.m_nPort = nPort;
		this.m_strDriverClass = strDriverClass;
		this.m_strDriverProtocol = strDriverProtocol;
		this.m_strUser = strUser;
		this.m_strPassword = strPassword;

		this.m_db = new SQLDatabase();
	}

	public static final synchronized SQLSource create() {
		if (null == sm_server) {
			sm_server = new SQLSource(Settings.getServerName(), Settings.getPort(), Settings.getDriverClass(),
					Settings.getDriverProtocol(), Settings.getUserName(), Settings.getPassword());
		}

		return sm_server;
	}

	public final String getServerName() {
		return this.m_strServerName;
	}

	public final String getUser() {
		return this.m_strUser;
	}

	public final String getPassword() {
		return this.m_strPassword;
	}

	public final int getPort() {
		return this.m_nPort;
	}

	/**
	 * returns the data model of the server to check compatibility
	 */
	public final ModelVersion getModelVersion() {
		return ModelVersion.LOCAL_MODEL;
	}

	/**
	 * checks, if the data model of the server is compatible to that of the
	 * client
	 */
	public final boolean isCompatible(final ModelVersion clientModel) {
		return true;
	}

	public final String getDriverClass() {
		return this.m_strDriverClass;
	}

	public final String getDriverProtocol() {
		return this.m_strDriverProtocol;
	}

	public final String getConnectionString() {
		final StringBuffer strBuf = new StringBuffer();

		strBuf.append(this.getDriverProtocol());
		strBuf.append("://");
		strBuf.append(this.getServerName());
		strBuf.append(":");
		strBuf.append(Integer.toString(this.getPort()));
		strBuf.append("/");
		strBuf.append("CommunicationModel");

		// strBuf.append( this.getDriverProtocol() );
		// strBuf.append( ":" );
		// strBuf.append( this.getServerName() );
		// strBuf.append( ":" );
		// strBuf.append( Integer.toString( this.getPort() ) );
		// strBuf.append( ":" );
		// strBuf.append( "CommunicationModel" );

		return strBuf.toString();
	}

	/**
	 * loads a database from the given directory
	 */
	public final boolean load(final String strDirPath) {
		return false;
	}

	public final String toString() {
		return this.m_strServerName + " [" + this.m_nPort + "]";
	}

	public final boolean update(final String strXML) {
		boolean bUpdated = true;

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
			final List<Module> listModule = CommunicationModelParser.extractModule(doc);
			for (int i = 0, nSizeModule = listModule.size(); i < nSizeModule; ++i) {
				final Module newRecord = listModule.get(i);
				final Module oldRecord = SQLSource.this.findByIDModule(newRecord.getPrimaryKey());

				final String strSQL;
				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					strSQL = " UPDATE Module " + " SET " + " ServerReplicationVersion = '"
							+ oldRecord.getServerReplicationVersion() + "'," + " ModuleName = '"
							+ oldRecord.getModuleName() + "'," + " Description = '" + oldRecord.getDescription() + "'"
							+ " WHERE (PrimaryKey = '" + oldRecord.getPrimaryKey() + "')";
				} else {
					strSQL = " INSERT INTO Module (" + " PrimaryKey," + " ServerReplicationVersion," + " ModuleName,"
							+ " Description" + ")" + " VALUES (" + " '" + newRecord.getPrimaryKey() + "'," + " '"
							+ newRecord.getServerReplicationVersion() + "'," + " '" + newRecord.getModuleName() + "',"
							+ " '" + newRecord.getDescription() + "'" + ")";
				}
				bUpdated = (this.m_db.executeUpdate(strSQL) > 0);
			}

			final List<StateGroup> listStateGroup = CommunicationModelParser.extractStateGroup(doc);
			for (int i = 0, nSizeStateGroup = listStateGroup.size(); i < nSizeStateGroup; ++i) {
				final StateGroup newRecord = listStateGroup.get(i);
				final StateGroup oldRecord = SQLSource.this.findByIDStateGroup(newRecord.getPrimaryKey());

				final String strSQL;
				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					strSQL = " UPDATE StateGroup " + " SET " + " ServerReplicationVersion = '"
							+ oldRecord.getServerReplicationVersion() + "'," + " GroupName = '"
							+ oldRecord.getGroupName() + "'," + " Description = '" + oldRecord.getDescription() + "'"
							+ " WHERE (PrimaryKey = '" + oldRecord.getPrimaryKey() + "')";
				} else {
					strSQL = " INSERT INTO StateGroup (" + " PrimaryKey," + " ServerReplicationVersion," + " GroupName,"
							+ " Description" + ")" + " VALUES (" + " '" + newRecord.getPrimaryKey() + "'," + " '"
							+ newRecord.getServerReplicationVersion() + "'," + " '" + newRecord.getGroupName() + "',"
							+ " '" + newRecord.getDescription() + "'" + ")";
				}
				bUpdated = (this.m_db.executeUpdate(strSQL) > 0);
			}

			final List<StateType> listStateType = CommunicationModelParser.extractStateType(doc);
			for (int i = 0, nSizeStateType = listStateType.size(); i < nSizeStateType; ++i) {
				final StateType newRecord = listStateType.get(i);
				final StateType oldRecord = SQLSource.this.findByIDStateType(newRecord.getPrimaryKey());

				final String strSQL;
				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					strSQL = " UPDATE StateType " + " SET " + " ServerReplicationVersion = '"
							+ oldRecord.getServerReplicationVersion() + "'," + " TypeName = '" + oldRecord.getTypeName()
							+ "'," + " Description = '" + oldRecord.getDescription() + "'" + " WHERE (PrimaryKey = '"
							+ oldRecord.getPrimaryKey() + "')";
				} else {
					strSQL = " INSERT INTO StateType (" + " PrimaryKey," + " ServerReplicationVersion," + " TypeName,"
							+ " Description" + ")" + " VALUES (" + " '" + newRecord.getPrimaryKey() + "'," + " '"
							+ newRecord.getServerReplicationVersion() + "'," + " '" + newRecord.getTypeName() + "',"
							+ " '" + newRecord.getDescription() + "'" + ")";
				}
				bUpdated = (this.m_db.executeUpdate(strSQL) > 0);
			}

			final List<StateGroupLink> listStateGroupLink = CommunicationModelParser.extractStateGroupLink(doc);
			for (int i = 0, nSizeStateGroupLink = listStateGroupLink.size(); i < nSizeStateGroupLink; ++i) {
				final StateGroupLink newRecord = listStateGroupLink.get(i);
				final StateGroupLink oldRecord = SQLSource.this.findByIDStateGroupLink(newRecord.getPrimaryKey());

				final String strSQL;
				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					strSQL = " UPDATE StateGroupLink " + " SET " + " ServerReplicationVersion = '"
							+ oldRecord.getServerReplicationVersion() + "'," + " Source = '" + oldRecord.getSource()
							+ "'," + " Destination = '" + oldRecord.getDestination() + "'" + " WHERE (PrimaryKey = '"
							+ oldRecord.getPrimaryKey() + "')";
				} else {
					strSQL = " INSERT INTO StateGroupLink (" + " PrimaryKey," + " ServerReplicationVersion,"
							+ " Source," + " Destination" + ")" + " VALUES (" + " '" + newRecord.getPrimaryKey() + "',"
							+ " '" + newRecord.getServerReplicationVersion() + "'," + " '" + newRecord.getSource()
							+ "'," + " '" + newRecord.getDestination() + "'" + ")";
				}
				bUpdated = (this.m_db.executeUpdate(strSQL) > 0);
			}

			final List<State> listState = CommunicationModelParser.extractState(doc);
			for (int i = 0, nSizeState = listState.size(); i < nSizeState; ++i) {
				final State newRecord = listState.get(i);
				final State oldRecord = SQLSource.this.findByIDState(newRecord.getPrimaryKey());

				final String strSQL;
				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					strSQL = " UPDATE State " + " SET " + " ServerReplicationVersion = '"
							+ oldRecord.getServerReplicationVersion() + "'," + " StateName = '"
							+ oldRecord.getStateName() + "'," + " DefaultValue = '" + oldRecord.getDefaultValue() + "',"
							+ " StateType = '" + oldRecord.getStateType() + "'" + " WHERE (PrimaryKey = '"
							+ oldRecord.getPrimaryKey() + "')";
				} else {
					strSQL = " INSERT INTO State (" + " PrimaryKey," + " ServerReplicationVersion," + " StateName,"
							+ " DefaultValue," + " StateType" + ")" + " VALUES (" + " '" + newRecord.getPrimaryKey()
							+ "'," + " '" + newRecord.getServerReplicationVersion() + "'," + " '"
							+ newRecord.getStateName() + "'," + " '" + newRecord.getDefaultValue() + "'," + " '"
							+ newRecord.getStateType() + "'" + ")";
				}
				bUpdated = (this.m_db.executeUpdate(strSQL) > 0);
			}

			final List<StateChange> listStateChange = CommunicationModelParser.extractStateChange(doc);
			for (int i = 0, nSizeStateChange = listStateChange.size(); i < nSizeStateChange; ++i) {
				final StateChange newRecord = listStateChange.get(i);
				final StateChange oldRecord = SQLSource.this.findByIDStateChange(newRecord.getPrimaryKey());

				final String strSQL;
				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					strSQL = " UPDATE StateChange " + " SET " + " ServerReplicationVersion = '"
							+ oldRecord.getServerReplicationVersion() + "'," + " State = '" + oldRecord.getState()
							+ "'," + " StateValue = '" + oldRecord.getStateValue() + "'," + " ChangeTime = '"
							+ oldRecord.getChangeTime().getTime() + "'" + " WHERE (PrimaryKey = '"
							+ oldRecord.getPrimaryKey() + "')";
				} else {
					strSQL = " INSERT INTO StateChange (" + " PrimaryKey," + " ServerReplicationVersion," + " State,"
							+ " StateValue," + " ChangeTime" + ")" + " VALUES (" + " '" + newRecord.getPrimaryKey()
							+ "'," + " '" + newRecord.getServerReplicationVersion() + "'," + " '" + newRecord.getState()
							+ "'," + " '" + newRecord.getStateValue() + "'," + " '"
							+ newRecord.getChangeTime().getTime() + "'" + ")";
				}
				bUpdated = (this.m_db.executeUpdate(strSQL) > 0);
			}

			final List<Observer> listObserver = CommunicationModelParser.extractObserver(doc);
			for (int i = 0, nSizeObserver = listObserver.size(); i < nSizeObserver; ++i) {
				final Observer newRecord = listObserver.get(i);
				final Observer oldRecord = SQLSource.this.findByIDObserver(newRecord.getPrimaryKey());

				final String strSQL;
				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					strSQL = " UPDATE Observer " + " SET " + " ServerReplicationVersion = '"
							+ oldRecord.getServerReplicationVersion() + "'," + " State = '" + oldRecord.getState()
							+ "'," + " StateGroup = '" + oldRecord.getStateGroup() + "'," + " ActionClass = '"
							+ oldRecord.getActionClass() + "'," + " Description = '" + oldRecord.getDescription() + "'"
							+ " WHERE (PrimaryKey = '" + oldRecord.getPrimaryKey() + "')";
				} else {
					strSQL = " INSERT INTO Observer (" + " PrimaryKey," + " ServerReplicationVersion," + " State,"
							+ " StateGroup," + " ActionClass," + " Description" + ")" + " VALUES (" + " '"
							+ newRecord.getPrimaryKey() + "'," + " '" + newRecord.getServerReplicationVersion() + "',"
							+ " '" + newRecord.getState() + "'," + " '" + newRecord.getStateGroup() + "'," + " '"
							+ newRecord.getActionClass() + "'," + " '" + newRecord.getDescription() + "'" + ")";
				}
				bUpdated = (this.m_db.executeUpdate(strSQL) > 0);
			}

			final List<NotificationScope> listNotificationScope = CommunicationModelParser
					.extractNotificationScope(doc);
			for (int i = 0, nSizeNotificationScope = listNotificationScope.size(); i < nSizeNotificationScope; ++i) {
				final NotificationScope newRecord = listNotificationScope.get(i);
				final NotificationScope oldRecord = SQLSource.this.findByIDNotificationScope(newRecord.getPrimaryKey());

				final String strSQL;
				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					strSQL = " UPDATE NotificationScope " + " SET " + " ServerReplicationVersion = '"
							+ oldRecord.getServerReplicationVersion() + "'," + " Observer = '" + oldRecord.getObserver()
							+ "'," + " Module = '" + oldRecord.getModule() + "'" + " WHERE (PrimaryKey = '"
							+ oldRecord.getPrimaryKey() + "')";
				} else {
					strSQL = " INSERT INTO NotificationScope (" + " PrimaryKey," + " ServerReplicationVersion,"
							+ " Observer," + " Module" + ")" + " VALUES (" + " '" + newRecord.getPrimaryKey() + "',"
							+ " '" + newRecord.getServerReplicationVersion() + "'," + " '" + newRecord.getObserver()
							+ "'," + " '" + newRecord.getModule() + "'" + ")";
				}
				bUpdated = (this.m_db.executeUpdate(strSQL) > 0);
			}

			final List<ObserverLink> listObserverLink = CommunicationModelParser.extractObserverLink(doc);
			for (int i = 0, nSizeObserverLink = listObserverLink.size(); i < nSizeObserverLink; ++i) {
				final ObserverLink newRecord = listObserverLink.get(i);
				final ObserverLink oldRecord = SQLSource.this.findByIDObserverLink(newRecord.getPrimaryKey());

				final String strSQL;
				if (oldRecord != null) {
					oldRecord.overwrite(newRecord, true);
					strSQL = " UPDATE ObserverLink " + " SET " + " ServerReplicationVersion = '"
							+ oldRecord.getServerReplicationVersion() + "'," + " Source = '" + oldRecord.getSource()
							+ "'," + " Destination = '" + oldRecord.getDestination() + "'" + " WHERE (PrimaryKey = '"
							+ oldRecord.getPrimaryKey() + "')";
				} else {
					strSQL = " INSERT INTO ObserverLink (" + " PrimaryKey," + " ServerReplicationVersion," + " Source,"
							+ " Destination" + ")" + " VALUES (" + " '" + newRecord.getPrimaryKey() + "'," + " '"
							+ newRecord.getServerReplicationVersion() + "'," + " '" + newRecord.getSource() + "',"
							+ " '" + newRecord.getDestination() + "'" + ")";
				}
				bUpdated = (this.m_db.executeUpdate(strSQL) > 0);
			}

			bUpdated = true;
		}

		fileTemp.delete();

		return bUpdated;
	}

	/**
	 * get all records of type [Module] from the server
	 */
	public final List<Module> listModule() {
		List<Module> list = new ArrayList<Module>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       ModuleName,");
			strBuf.append("       Description");
			strBuf.append(" FROM Module LIMIT 1000 ");

			final String strSQL = strBuf.toString();

			list = this.listModule(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [Module] from the server that match to all
	 * conditions in the list
	 */
	public final List<Module> listModule(final List<Condition> listConditions) {
		List<Module> list = new ArrayList<Module>();
		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       ModuleName,");
			strBuf.append("       Description");
			this.appendFromClause(strBuf, "Module", listConditions);
			this.appendWhereClause(strBuf, listConditions);

			final String strSQL = strBuf.toString();

			list = this.listModule(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [StateGroup] from the server
	 */
	public final List<StateGroup> listStateGroup() {
		List<StateGroup> list = new ArrayList<StateGroup>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       GroupName,");
			strBuf.append("       Description");
			strBuf.append(" FROM StateGroup LIMIT 1000 ");

			final String strSQL = strBuf.toString();

			list = this.listStateGroup(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [StateGroup] from the server that match to all
	 * conditions in the list
	 */
	public final List<StateGroup> listStateGroup(final List<Condition> listConditions) {
		List<StateGroup> list = new ArrayList<StateGroup>();
		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       GroupName,");
			strBuf.append("       Description");
			this.appendFromClause(strBuf, "StateGroup", listConditions);
			this.appendWhereClause(strBuf, listConditions);

			final String strSQL = strBuf.toString();

			list = this.listStateGroup(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [StateType] from the server
	 */
	public final List<StateType> listStateType() {
		List<StateType> list = new ArrayList<StateType>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       TypeName,");
			strBuf.append("       Description");
			strBuf.append(" FROM StateType LIMIT 1000 ");

			final String strSQL = strBuf.toString();

			list = this.listStateType(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [StateType] from the server that match to all
	 * conditions in the list
	 */
	public final List<StateType> listStateType(final List<Condition> listConditions) {
		List<StateType> list = new ArrayList<StateType>();
		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       TypeName,");
			strBuf.append("       Description");
			this.appendFromClause(strBuf, "StateType", listConditions);
			this.appendWhereClause(strBuf, listConditions);

			final String strSQL = strBuf.toString();

			list = this.listStateType(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [StateGroupLink] from the server
	 */
	public final List<StateGroupLink> listStateGroupLink() {
		List<StateGroupLink> list = new ArrayList<StateGroupLink>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       Source,");
			strBuf.append("       Destination");
			strBuf.append(" FROM StateGroupLink LIMIT 1000 ");

			final String strSQL = strBuf.toString();

			list = this.listStateGroupLink(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [StateGroupLink] from the server that match to
	 * all conditions in the list
	 */
	public final List<StateGroupLink> listStateGroupLink(final List<Condition> listConditions) {
		List<StateGroupLink> list = new ArrayList<StateGroupLink>();
		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       Source,");
			strBuf.append("       Destination");
			this.appendFromClause(strBuf, "StateGroupLink", listConditions);
			this.appendWhereClause(strBuf, listConditions);

			final String strSQL = strBuf.toString();

			list = this.listStateGroupLink(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [State] from the server
	 */
	public final List<State> listState() {
		List<State> list = new ArrayList<State>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       StateName,");
			strBuf.append("       DefaultValue,");
			strBuf.append("       StateType");
			strBuf.append(" FROM State LIMIT 1000 ");

			final String strSQL = strBuf.toString();

			list = this.listState(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [State] from the server that match to all
	 * conditions in the list
	 */
	public final List<State> listState(final List<Condition> listConditions) {
		List<State> list = new ArrayList<State>();
		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       StateName,");
			strBuf.append("       DefaultValue,");
			strBuf.append("       StateType");
			this.appendFromClause(strBuf, "State", listConditions);
			this.appendWhereClause(strBuf, listConditions);

			final String strSQL = strBuf.toString();

			list = this.listState(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [StateChange] from the server
	 */
	public final List<StateChange> listStateChange() {
		List<StateChange> list = new ArrayList<StateChange>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       State,");
			strBuf.append("       StateValue,");
			strBuf.append("       ChangeTime");
			strBuf.append(" FROM StateChange LIMIT 1000 ");

			final String strSQL = strBuf.toString();

			list = this.listStateChange(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [StateChange] from the server that match to all
	 * conditions in the list
	 */
	public final List<StateChange> listStateChange(final List<Condition> listConditions) {
		List<StateChange> list = new ArrayList<StateChange>();
		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       State,");
			strBuf.append("       StateValue,");
			strBuf.append("       ChangeTime");
			this.appendFromClause(strBuf, "StateChange", listConditions);
			this.appendWhereClause(strBuf, listConditions);

			final String strSQL = strBuf.toString();

			list = this.listStateChange(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [Observer] from the server
	 */
	public final List<Observer> listObserver() {
		List<Observer> list = new ArrayList<Observer>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       State,");
			strBuf.append("       StateGroup,");
			strBuf.append("       ActionClass,");
			strBuf.append("       Description");
			strBuf.append(" FROM Observer LIMIT 1000 ");

			final String strSQL = strBuf.toString();

			list = this.listObserver(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [Observer] from the server that match to all
	 * conditions in the list
	 */
	public final List<Observer> listObserver(final List<Condition> listConditions) {
		List<Observer> list = new ArrayList<Observer>();
		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       State,");
			strBuf.append("       StateGroup,");
			strBuf.append("       ActionClass,");
			strBuf.append("       Description");
			this.appendFromClause(strBuf, "Observer", listConditions);
			this.appendWhereClause(strBuf, listConditions);

			final String strSQL = strBuf.toString();

			list = this.listObserver(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [NotificationScope] from the server
	 */
	public final List<NotificationScope> listNotificationScope() {
		List<NotificationScope> list = new ArrayList<NotificationScope>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       Observer,");
			strBuf.append("       Module");
			strBuf.append(" FROM NotificationScope LIMIT 1000 ");

			final String strSQL = strBuf.toString();

			list = this.listNotificationScope(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [NotificationScope] from the server that match to
	 * all conditions in the list
	 */
	public final List<NotificationScope> listNotificationScope(final List<Condition> listConditions) {
		List<NotificationScope> list = new ArrayList<NotificationScope>();
		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       Observer,");
			strBuf.append("       Module");
			this.appendFromClause(strBuf, "NotificationScope", listConditions);
			this.appendWhereClause(strBuf, listConditions);

			final String strSQL = strBuf.toString();

			list = this.listNotificationScope(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [ObserverLink] from the server
	 */
	public final List<ObserverLink> listObserverLink() {
		List<ObserverLink> list = new ArrayList<ObserverLink>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       Source,");
			strBuf.append("       Destination");
			strBuf.append(" FROM ObserverLink LIMIT 1000 ");

			final String strSQL = strBuf.toString();

			list = this.listObserverLink(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * get all records of type [ObserverLink] from the server that match to all
	 * conditions in the list
	 */
	public final List<ObserverLink> listObserverLink(final List<Condition> listConditions) {
		List<ObserverLink> list = new ArrayList<ObserverLink>();
		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       Source,");
			strBuf.append("       Destination");
			this.appendFromClause(strBuf, "ObserverLink", listConditions);
			this.appendWhereClause(strBuf, listConditions);

			final String strSQL = strBuf.toString();

			list = this.listObserverLink(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	private void appendFromClause(final StringBuffer strBuf, final String resultTable,
			final List<Condition> listConditions) {
		final Set<String> tableNames = new HashSet<String>();
		tableNames.add(resultTable);
		for (int i = 0, conditionCount = listConditions.size(); i < conditionCount; i++) {
			final Condition c = listConditions.get(i);
			final Attribute a = c.getAttribute();
			tableNames.add(a.getTableName());
		}
		final List<String> tables = new ArrayList<String>(tableNames);
		strBuf.append(" FROM ");
		for (int i = 0, tableCount = tables.size(); i < tableCount; i++) {
			strBuf.append(tables.get(i));
			if (i < (tableCount - 1)) {
				strBuf.append(", ");
			}
		}
		strBuf.append(" LIMIT 1000 ");
	}

	private void appendWhereClause(final StringBuffer strBuf, final List<Condition> listConditions) {
		final int conditionCount = listConditions.size();
		if (conditionCount > 0) {
			strBuf.append(" WHERE ");
			for (int i = 0; i < conditionCount; i++) {
				final Condition c = listConditions.get(i);
				final Attribute a = c.getAttribute();
				final String attName = a.getTableName() + "." + a.getAttributeName();
				final Operation o = c.getOperation();
				final String v = c.getValue();
				if (i == 0) {
					strBuf.append(" (" + attName + o.getPresentation() + "'" + v + "')");
				} else {
					strBuf.append(" AND (" + attName + o.getPresentation() + "'" + v + "')");
				}
			}
		}
	}

	/**
	 * get the corresponding [Module] record from the server
	 */
	public final Module findByIDModule(final long lPrimaryKey) {
		Module record = null;

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       ModuleName,");
			strBuf.append("       Description");
			strBuf.append(" FROM Module");
			strBuf.append(" WHERE PrimaryKey = " + Long.toString(lPrimaryKey));

			final String strSQL = strBuf.toString();

			final List<Module> list = this.listModule(this.m_db.executeQuery(strSQL));

			if (list.size() == 1) {
				record = list.get(0);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return record;
	}

	/**
	 * tries to delete the record of type [Module] from the server if it is no
	 * longer referenced by another record
	 */
	public final boolean deleteModule(final long lPrimaryKey) {
		boolean bDeleted = false;

		final Module record = findByIDModule(lPrimaryKey);

		if (record != null) {
			if (!this.isReferencedModule(lPrimaryKey)) {
				final String strSQL = "DELETE FROM Module" + " WHERE PrimaryKey = " + Long.toString(lPrimaryKey);

				bDeleted = (0 < this.m_db.executeUpdate(strSQL));
			}
		} else {
			bDeleted = true;
		}

		return bDeleted;
	}

	/**
	 * get the corresponding [StateGroup] record from the server
	 */
	public final StateGroup findByIDStateGroup(final long lPrimaryKey) {
		StateGroup record = null;

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       GroupName,");
			strBuf.append("       Description");
			strBuf.append(" FROM StateGroup");
			strBuf.append(" WHERE PrimaryKey = " + Long.toString(lPrimaryKey));

			final String strSQL = strBuf.toString();

			final List<StateGroup> list = this.listStateGroup(this.m_db.executeQuery(strSQL));

			if (list.size() == 1) {
				record = list.get(0);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return record;
	}

	/**
	 * tries to delete the record of type [StateGroup] from the server if it is
	 * no longer referenced by another record
	 */
	public final boolean deleteStateGroup(final long lPrimaryKey) {
		boolean bDeleted = false;

		final StateGroup record = findByIDStateGroup(lPrimaryKey);

		if (record != null) {
			if (!this.isReferencedStateGroup(lPrimaryKey)) {
				final String strSQL = "DELETE FROM StateGroup" + " WHERE PrimaryKey = " + Long.toString(lPrimaryKey);

				bDeleted = (0 < this.m_db.executeUpdate(strSQL));
			}
		} else {
			bDeleted = true;
		}

		return bDeleted;
	}

	/**
	 * get the corresponding [StateType] record from the server
	 */
	public final StateType findByIDStateType(final long lPrimaryKey) {
		StateType record = null;

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       TypeName,");
			strBuf.append("       Description");
			strBuf.append(" FROM StateType");
			strBuf.append(" WHERE PrimaryKey = " + Long.toString(lPrimaryKey));

			final String strSQL = strBuf.toString();

			final List<StateType> list = this.listStateType(this.m_db.executeQuery(strSQL));

			if (list.size() == 1) {
				record = list.get(0);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return record;
	}

	/**
	 * tries to delete the record of type [StateType] from the server if it is
	 * no longer referenced by another record
	 */
	public final boolean deleteStateType(final long lPrimaryKey) {
		boolean bDeleted = false;

		final StateType record = findByIDStateType(lPrimaryKey);

		if (record != null) {
			if (!this.isReferencedStateType(lPrimaryKey)) {
				final String strSQL = "DELETE FROM StateType" + " WHERE PrimaryKey = " + Long.toString(lPrimaryKey);

				bDeleted = (0 < this.m_db.executeUpdate(strSQL));
			}
		} else {
			bDeleted = true;
		}

		return bDeleted;
	}

	/**
	 * get the corresponding [StateGroupLink] record from the server
	 */
	public final StateGroupLink findByIDStateGroupLink(final long lPrimaryKey) {
		StateGroupLink record = null;

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       Source,");
			strBuf.append("       Destination");
			strBuf.append(" FROM StateGroupLink");
			strBuf.append(" WHERE PrimaryKey = " + Long.toString(lPrimaryKey));

			final String strSQL = strBuf.toString();

			final List<StateGroupLink> list = this.listStateGroupLink(this.m_db.executeQuery(strSQL));

			if (list.size() == 1) {
				record = list.get(0);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return record;
	}

	/**
	 * tries to delete the record of type [StateGroupLink] from the server if it
	 * is no longer referenced by another record
	 */
	public final boolean deleteStateGroupLink(final long lPrimaryKey) {
		boolean bDeleted = false;

		final StateGroupLink record = findByIDStateGroupLink(lPrimaryKey);

		if (record != null) {
			if (!this.isReferencedStateGroupLink(lPrimaryKey)) {
				final String strSQL = "DELETE FROM StateGroupLink" + " WHERE PrimaryKey = "
						+ Long.toString(lPrimaryKey);

				bDeleted = (0 < this.m_db.executeUpdate(strSQL));
			}
		} else {
			bDeleted = true;
		}

		return bDeleted;
	}

	/**
	 * get the corresponding [State] record from the server
	 */
	public final State findByIDState(final long lPrimaryKey) {
		State record = null;

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       StateName,");
			strBuf.append("       DefaultValue,");
			strBuf.append("       StateType");
			strBuf.append(" FROM State");
			strBuf.append(" WHERE PrimaryKey = " + Long.toString(lPrimaryKey));

			final String strSQL = strBuf.toString();

			final List<State> list = this.listState(this.m_db.executeQuery(strSQL));

			if (list.size() == 1) {
				record = list.get(0);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return record;
	}

	/**
	 * tries to delete the record of type [State] from the server if it is no
	 * longer referenced by another record
	 */
	public final boolean deleteState(final long lPrimaryKey) {
		boolean bDeleted = false;

		final State record = findByIDState(lPrimaryKey);

		if (record != null) {
			if (!this.isReferencedState(lPrimaryKey)) {
				final String strSQL = "DELETE FROM State" + " WHERE PrimaryKey = " + Long.toString(lPrimaryKey);

				bDeleted = (0 < this.m_db.executeUpdate(strSQL));
			}
		} else {
			bDeleted = true;
		}

		return bDeleted;
	}

	/**
	 * get the corresponding [StateChange] record from the server
	 */
	public final StateChange findByIDStateChange(final long lPrimaryKey) {
		StateChange record = null;

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       State,");
			strBuf.append("       StateValue,");
			strBuf.append("       ChangeTime");
			strBuf.append(" FROM StateChange");
			strBuf.append(" WHERE PrimaryKey = " + Long.toString(lPrimaryKey));

			final String strSQL = strBuf.toString();

			final List<StateChange> list = this.listStateChange(this.m_db.executeQuery(strSQL));

			if (list.size() == 1) {
				record = list.get(0);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return record;
	}

	/**
	 * tries to delete the record of type [StateChange] from the server if it is
	 * no longer referenced by another record
	 */
	public final boolean deleteStateChange(final long lPrimaryKey) {
		boolean bDeleted = false;

		final StateChange record = findByIDStateChange(lPrimaryKey);

		if (record != null) {
			if (!this.isReferencedStateChange(lPrimaryKey)) {
				final String strSQL = "DELETE FROM StateChange" + " WHERE PrimaryKey = " + Long.toString(lPrimaryKey);

				bDeleted = (0 < this.m_db.executeUpdate(strSQL));
			}
		} else {
			bDeleted = true;
		}

		return bDeleted;
	}

	/**
	 * get the corresponding [Observer] record from the server
	 */
	public final Observer findByIDObserver(final long lPrimaryKey) {
		Observer record = null;

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       State,");
			strBuf.append("       StateGroup,");
			strBuf.append("       ActionClass,");
			strBuf.append("       Description");
			strBuf.append(" FROM Observer");
			strBuf.append(" WHERE PrimaryKey = " + Long.toString(lPrimaryKey));

			final String strSQL = strBuf.toString();

			final List<Observer> list = this.listObserver(this.m_db.executeQuery(strSQL));

			if (list.size() == 1) {
				record = list.get(0);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return record;
	}

	/**
	 * tries to delete the record of type [Observer] from the server if it is no
	 * longer referenced by another record
	 */
	public final boolean deleteObserver(final long lPrimaryKey) {
		boolean bDeleted = false;

		final Observer record = findByIDObserver(lPrimaryKey);

		if (record != null) {
			if (!this.isReferencedObserver(lPrimaryKey)) {
				final String strSQL = "DELETE FROM Observer" + " WHERE PrimaryKey = " + Long.toString(lPrimaryKey);

				bDeleted = (0 < this.m_db.executeUpdate(strSQL));
			}
		} else {
			bDeleted = true;
		}

		return bDeleted;
	}

	/**
	 * get the corresponding [NotificationScope] record from the server
	 */
	public final NotificationScope findByIDNotificationScope(final long lPrimaryKey) {
		NotificationScope record = null;

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       Observer,");
			strBuf.append("       Module");
			strBuf.append(" FROM NotificationScope");
			strBuf.append(" WHERE PrimaryKey = " + Long.toString(lPrimaryKey));

			final String strSQL = strBuf.toString();

			final List<NotificationScope> list = this.listNotificationScope(this.m_db.executeQuery(strSQL));

			if (list.size() == 1) {
				record = list.get(0);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return record;
	}

	/**
	 * tries to delete the record of type [NotificationScope] from the server if
	 * it is no longer referenced by another record
	 */
	public final boolean deleteNotificationScope(final long lPrimaryKey) {
		boolean bDeleted = false;

		final NotificationScope record = findByIDNotificationScope(lPrimaryKey);

		if (record != null) {
			if (!this.isReferencedNotificationScope(lPrimaryKey)) {
				final String strSQL = "DELETE FROM NotificationScope" + " WHERE PrimaryKey = "
						+ Long.toString(lPrimaryKey);

				bDeleted = (0 < this.m_db.executeUpdate(strSQL));
			}
		} else {
			bDeleted = true;
		}

		return bDeleted;
	}

	/**
	 * get the corresponding [ObserverLink] record from the server
	 */
	public final ObserverLink findByIDObserverLink(final long lPrimaryKey) {
		ObserverLink record = null;

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT PrimaryKey,");
			strBuf.append("       ServerReplicationVersion,");
			strBuf.append("       Source,");
			strBuf.append("       Destination");
			strBuf.append(" FROM ObserverLink");
			strBuf.append(" WHERE PrimaryKey = " + Long.toString(lPrimaryKey));

			final String strSQL = strBuf.toString();

			final List<ObserverLink> list = this.listObserverLink(this.m_db.executeQuery(strSQL));

			if (list.size() == 1) {
				record = list.get(0);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return record;
	}

	/**
	 * tries to delete the record of type [ObserverLink] from the server if it
	 * is no longer referenced by another record
	 */
	public final boolean deleteObserverLink(final long lPrimaryKey) {
		boolean bDeleted = false;

		final ObserverLink record = findByIDObserverLink(lPrimaryKey);

		if (record != null) {
			if (!this.isReferencedObserverLink(lPrimaryKey)) {
				final String strSQL = "DELETE FROM ObserverLink" + " WHERE PrimaryKey = " + Long.toString(lPrimaryKey);

				bDeleted = (0 < this.m_db.executeUpdate(strSQL));
			}
		} else {
			bDeleted = true;
		}

		return bDeleted;
	}

	/**
	 * get all records that are referenced by this [Module] record from the
	 * server
	 */
	public final CommunicationModel exportModule(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final Module record = this.findByIDModule(lPrimaryKey);
		if (record != null) {
			dbExport.addModule(record);
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [Module] records in the list
	 * from the server
	 */
	public final CommunicationModel exportModule(final List<Module> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		for (int i = 0, nSize = list.size(); i < nSize; i++) {
			final Module record = list.get(i);
			if (record != null) {
				dbExport.add(this.exportModule(record.getPrimaryKey()));
			}
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [StateGroup] record from the
	 * server
	 */
	public final CommunicationModel exportStateGroup(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateGroup record = this.findByIDStateGroup(lPrimaryKey);
		if (record != null) {
			dbExport.addStateGroup(record);
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [StateGroup] records in the
	 * list from the server
	 */
	public final CommunicationModel exportStateGroup(final List<StateGroup> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		for (int i = 0, nSize = list.size(); i < nSize; i++) {
			final StateGroup record = list.get(i);
			if (record != null) {
				dbExport.add(this.exportStateGroup(record.getPrimaryKey()));
			}
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [StateType] record from the
	 * server
	 */
	public final CommunicationModel exportStateType(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateType record = this.findByIDStateType(lPrimaryKey);
		if (record != null) {
			dbExport.addStateType(record);
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [StateType] records in the
	 * list from the server
	 */
	public final CommunicationModel exportStateType(final List<StateType> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		for (int i = 0, nSize = list.size(); i < nSize; i++) {
			final StateType record = list.get(i);
			if (record != null) {
				dbExport.add(this.exportStateType(record.getPrimaryKey()));
			}
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [StateGroupLink] record from
	 * the server
	 */
	public final CommunicationModel exportStateGroupLink(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateGroupLink record = this.findByIDStateGroupLink(lPrimaryKey);
		if (record != null) {
			dbExport.addStateGroupLink(record);

			dbExport.add(this.exportStateGroup(record.getSource()));

			dbExport.add(this.exportStateGroup(record.getDestination()));
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [StateGroupLink] records in
	 * the list from the server
	 */
	public final CommunicationModel exportStateGroupLink(final List<StateGroupLink> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		for (int i = 0, nSize = list.size(); i < nSize; i++) {
			final StateGroupLink record = list.get(i);
			if (record != null) {
				dbExport.add(this.exportStateGroupLink(record.getPrimaryKey()));
			}
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [State] record from the
	 * server
	 */
	public final CommunicationModel exportState(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final State record = this.findByIDState(lPrimaryKey);
		if (record != null) {
			dbExport.addState(record);

			dbExport.add(this.exportStateType(record.getStateType()));
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [State] records in the list
	 * from the server
	 */
	public final CommunicationModel exportState(final List<State> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		for (int i = 0, nSize = list.size(); i < nSize; i++) {
			final State record = list.get(i);
			if (record != null) {
				dbExport.add(this.exportState(record.getPrimaryKey()));
			}
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [StateChange] record from the
	 * server
	 */
	public final CommunicationModel exportStateChange(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final StateChange record = this.findByIDStateChange(lPrimaryKey);
		if (record != null) {
			dbExport.addStateChange(record);

			dbExport.add(this.exportState(record.getState()));
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [StateChange] records in the
	 * list from the server
	 */
	public final CommunicationModel exportStateChange(final List<StateChange> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		for (int i = 0, nSize = list.size(); i < nSize; i++) {
			final StateChange record = list.get(i);
			if (record != null) {
				dbExport.add(this.exportStateChange(record.getPrimaryKey()));
			}
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [Observer] record from the
	 * server
	 */
	public final CommunicationModel exportObserver(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final Observer record = this.findByIDObserver(lPrimaryKey);
		if (record != null) {
			dbExport.addObserver(record);

			dbExport.add(this.exportState(record.getState()));

			dbExport.add(this.exportStateGroup(record.getStateGroup()));
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [Observer] records in the
	 * list from the server
	 */
	public final CommunicationModel exportObserver(final List<Observer> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		for (int i = 0, nSize = list.size(); i < nSize; i++) {
			final Observer record = list.get(i);
			if (record != null) {
				dbExport.add(this.exportObserver(record.getPrimaryKey()));
			}
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [NotificationScope] record
	 * from the server
	 */
	public final CommunicationModel exportNotificationScope(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final NotificationScope record = this.findByIDNotificationScope(lPrimaryKey);
		if (record != null) {
			dbExport.addNotificationScope(record);

			dbExport.add(this.exportObserver(record.getObserver()));

			dbExport.add(this.exportModule(record.getModule()));
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [NotificationScope] records
	 * in the list from the server
	 */
	public final CommunicationModel exportNotificationScope(final List<NotificationScope> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		for (int i = 0, nSize = list.size(); i < nSize; i++) {
			final NotificationScope record = list.get(i);
			if (record != null) {
				dbExport.add(this.exportNotificationScope(record.getPrimaryKey()));
			}
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [ObserverLink] record from
	 * the server
	 */
	public final CommunicationModel exportObserverLink(final long lPrimaryKey) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		final ObserverLink record = this.findByIDObserverLink(lPrimaryKey);
		if (record != null) {
			dbExport.addObserverLink(record);

			dbExport.add(this.exportObserver(record.getSource()));

			dbExport.add(this.exportObserver(record.getDestination()));
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [ObserverLink] records in the
	 * list from the server
	 */
	public final CommunicationModel exportObserverLink(final List<ObserverLink> list) {
		final CommunicationModel dbExport = CommunicationModel.createEmpty();
		for (int i = 0, nSize = list.size(); i < nSize; i++) {
			final ObserverLink record = list.get(i);
			if (record != null) {
				dbExport.add(this.exportObserverLink(record.getPrimaryKey()));
			}
		}
		return dbExport;
	}

	/**
	 * get all records that are referenced by this [Module] record from the
	 * server
	 */
	public final CommunicationModel contextExportModule(final long lPrimaryKey) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		final Module record = this.findByIDModule(lPrimaryKey);
		if (record != null) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [Module] records in the list
	 * from the server
	 */
	public final CommunicationModel contextExportModule(final List<Module> list) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		for (Module record : list) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [StateGroup] record from the
	 * server
	 */
	public final CommunicationModel contextExportStateGroup(final long lPrimaryKey) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		final StateGroup record = this.findByIDStateGroup(lPrimaryKey);
		if (record != null) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [StateGroup] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportStateGroup(final List<StateGroup> list) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		for (StateGroup record : list) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [StateType] record from the
	 * server
	 */
	public final CommunicationModel contextExportStateType(final long lPrimaryKey) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		final StateType record = this.findByIDStateType(lPrimaryKey);
		if (record != null) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [StateType] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportStateType(final List<StateType> list) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		for (StateType record : list) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [StateGroupLink] record from
	 * the server
	 */
	public final CommunicationModel contextExportStateGroupLink(final long lPrimaryKey) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		final StateGroupLink record = this.findByIDStateGroupLink(lPrimaryKey);
		if (record != null) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [StateGroupLink] records in
	 * the list from the server
	 */
	public final CommunicationModel contextExportStateGroupLink(final List<StateGroupLink> list) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		for (StateGroupLink record : list) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [State] record from the
	 * server
	 */
	public final CommunicationModel contextExportState(final long lPrimaryKey) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		final State record = this.findByIDState(lPrimaryKey);
		if (record != null) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [State] records in the list
	 * from the server
	 */
	public final CommunicationModel contextExportState(final List<State> list) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		for (State record : list) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [StateChange] record from the
	 * server
	 */
	public final CommunicationModel contextExportStateChange(final long lPrimaryKey) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		final StateChange record = this.findByIDStateChange(lPrimaryKey);
		if (record != null) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [StateChange] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportStateChange(final List<StateChange> list) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		for (StateChange record : list) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [Observer] record from the
	 * server
	 */
	public final CommunicationModel contextExportObserver(final long lPrimaryKey) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		final Observer record = this.findByIDObserver(lPrimaryKey);
		if (record != null) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [Observer] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportObserver(final List<Observer> list) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		for (Observer record : list) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [NotificationScope] record
	 * from the server
	 */
	public final CommunicationModel contextExportNotificationScope(final long lPrimaryKey) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		final NotificationScope record = this.findByIDNotificationScope(lPrimaryKey);
		if (record != null) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [NotificationScope] records
	 * in the list from the server
	 */
	public final CommunicationModel contextExportNotificationScope(final List<NotificationScope> list) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		for (NotificationScope record : list) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [ObserverLink] record from
	 * the server
	 */
	public final CommunicationModel contextExportObserverLink(final long lPrimaryKey) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		final ObserverLink record = this.findByIDObserverLink(lPrimaryKey);
		if (record != null) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * get all records that are referenced by this [ObserverLink] records in the
	 * list from the server
	 */
	public final CommunicationModel contextExportObserverLink(final List<ObserverLink> list) {
		final CommunicationModel db = CommunicationModel.createEmpty();
		for (ObserverLink record : list) {
			record.contextExport(db, false);
		}
		return db;
	}

	/**
	 * checks, if this [Module] record is still in use
	 */
	private boolean isReferencedModule(final long lPrimaryKey) {
		if (0 < this.countNotificationScopeByModule(lPrimaryKey)) {
			return true;
		}

		return false;
	}

	/**
	 * checks, if this [StateGroup] record is still in use
	 */
	private boolean isReferencedStateGroup(final long lPrimaryKey) {
		if (0 < this.countStateGroupLinkBySource(lPrimaryKey)) {
			return true;
		}

		if (0 < this.countStateGroupLinkByDestination(lPrimaryKey)) {
			return true;
		}

		if (0 < this.countObserverByStateGroup(lPrimaryKey)) {
			return true;
		}

		return false;
	}

	/**
	 * checks, if this [StateType] record is still in use
	 */
	private boolean isReferencedStateType(final long lPrimaryKey) {
		if (0 < this.countStateByStateType(lPrimaryKey)) {
			return true;
		}

		return false;
	}

	/**
	 * checks, if this [StateGroupLink] record is still in use
	 */
	private boolean isReferencedStateGroupLink(final long lPrimaryKey) {
		return false;
	}

	/**
	 * checks, if this [State] record is still in use
	 */
	private boolean isReferencedState(final long lPrimaryKey) {
		if (0 < this.countStateChangeByState(lPrimaryKey)) {
			return true;
		}

		if (0 < this.countObserverByState(lPrimaryKey)) {
			return true;
		}

		return false;
	}

	/**
	 * checks, if this [StateChange] record is still in use
	 */
	private boolean isReferencedStateChange(final long lPrimaryKey) {
		return false;
	}

	/**
	 * checks, if this [Observer] record is still in use
	 */
	private boolean isReferencedObserver(final long lPrimaryKey) {
		if (0 < this.countNotificationScopeByObserver(lPrimaryKey)) {
			return true;
		}

		if (0 < this.countObserverLinkBySource(lPrimaryKey)) {
			return true;
		}

		if (0 < this.countObserverLinkByDestination(lPrimaryKey)) {
			return true;
		}

		return false;
	}

	/**
	 * checks, if this [NotificationScope] record is still in use
	 */
	private boolean isReferencedNotificationScope(final long lPrimaryKey) {
		return false;
	}

	/**
	 * checks, if this [ObserverLink] record is still in use
	 */
	private boolean isReferencedObserverLink(final long lPrimaryKey) {
		return false;
	}

	/**
	 * list all records in the [StateGroupLink] table, that use this primary key
	 */
	public final List<StateGroupLink> referencesStateGroupLinkBySource(final long Source) {
		List<StateGroupLink> list = new ArrayList<StateGroupLink>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT StateGroupLink.PrimaryKey,");
			strBuf.append("       StateGroupLink.ServerReplicationVersion,");
			strBuf.append("       StateGroupLink.Source,");
			strBuf.append("       StateGroupLink.Destination");
			strBuf.append(" FROM StateGroupLink");
			strBuf.append(" LEFT JOIN StateGroup");
			strBuf.append(" ON StateGroupLink.Source = StateGroup.PrimaryKey");
			strBuf.append(" WHERE StateGroupLink.Source = '" + Source + "'");

			final String strSQL = strBuf.toString();

			list = this.listStateGroupLink(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [StateGroupLink] table, that use this primary
	 * key
	 */
	private int countStateGroupLinkBySource(final long lPrimaryKey) {
		return this.referencesStateGroupLinkBySource(lPrimaryKey).size();
	}

	/**
	 * list all records in the [StateGroupLink] table, that use this primary key
	 */
	public final List<StateGroupLink> referencesStateGroupLinkByDestination(final long Destination) {
		List<StateGroupLink> list = new ArrayList<StateGroupLink>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT StateGroupLink.PrimaryKey,");
			strBuf.append("       StateGroupLink.ServerReplicationVersion,");
			strBuf.append("       StateGroupLink.Source,");
			strBuf.append("       StateGroupLink.Destination");
			strBuf.append(" FROM StateGroupLink");
			strBuf.append(" LEFT JOIN StateGroup");
			strBuf.append(" ON StateGroupLink.Destination = StateGroup.PrimaryKey");
			strBuf.append(" WHERE StateGroupLink.Destination = '" + Destination + "'");

			final String strSQL = strBuf.toString();

			list = this.listStateGroupLink(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [StateGroupLink] table, that use this primary
	 * key
	 */
	private int countStateGroupLinkByDestination(final long lPrimaryKey) {
		return this.referencesStateGroupLinkByDestination(lPrimaryKey).size();
	}

	/**
	 * list all records in the [State] table, that use this primary key
	 */
	public final List<State> referencesStateByStateType(final long StateType) {
		List<State> list = new ArrayList<State>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT State.PrimaryKey,");
			strBuf.append("       State.ServerReplicationVersion,");
			strBuf.append("       State.StateName,");
			strBuf.append("       State.DefaultValue,");
			strBuf.append("       State.StateType");
			strBuf.append(" FROM State");
			strBuf.append(" LEFT JOIN StateType");
			strBuf.append(" ON State.StateType = StateType.PrimaryKey");
			strBuf.append(" WHERE State.StateType = '" + StateType + "'");

			final String strSQL = strBuf.toString();

			list = this.listState(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [State] table, that use this primary key
	 */
	private int countStateByStateType(final long lPrimaryKey) {
		return this.referencesStateByStateType(lPrimaryKey).size();
	}

	/**
	 * list all records in the [StateChange] table, that use this primary key
	 */
	public final List<StateChange> referencesStateChangeByState(final long State) {
		List<StateChange> list = new ArrayList<StateChange>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT StateChange.PrimaryKey,");
			strBuf.append("       StateChange.ServerReplicationVersion,");
			strBuf.append("       StateChange.State,");
			strBuf.append("       StateChange.StateValue,");
			strBuf.append("       StateChange.ChangeTime");
			strBuf.append(" FROM StateChange");
			strBuf.append(" LEFT JOIN State");
			strBuf.append(" ON StateChange.State = State.PrimaryKey");
			strBuf.append(" WHERE StateChange.State = '" + State + "'");

			final String strSQL = strBuf.toString();

			list = this.listStateChange(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [StateChange] table, that use this primary key
	 */
	private int countStateChangeByState(final long lPrimaryKey) {
		return this.referencesStateChangeByState(lPrimaryKey).size();
	}

	/**
	 * list all records in the [Observer] table, that use this primary key
	 */
	public final List<Observer> referencesObserverByState(final long State) {
		List<Observer> list = new ArrayList<Observer>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT Observer.PrimaryKey,");
			strBuf.append("       Observer.ServerReplicationVersion,");
			strBuf.append("       Observer.State,");
			strBuf.append("       Observer.StateGroup,");
			strBuf.append("       Observer.ActionClass,");
			strBuf.append("       Observer.Description");
			strBuf.append(" FROM Observer");
			strBuf.append(" LEFT JOIN State");
			strBuf.append(" ON Observer.State = State.PrimaryKey");
			strBuf.append(" WHERE Observer.State = '" + State + "'");

			final String strSQL = strBuf.toString();

			list = this.listObserver(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [Observer] table, that use this primary key
	 */
	private int countObserverByState(final long lPrimaryKey) {
		return this.referencesObserverByState(lPrimaryKey).size();
	}

	/**
	 * list all records in the [Observer] table, that use this primary key
	 */
	public final List<Observer> referencesObserverByStateGroup(final long StateGroup) {
		List<Observer> list = new ArrayList<Observer>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT Observer.PrimaryKey,");
			strBuf.append("       Observer.ServerReplicationVersion,");
			strBuf.append("       Observer.State,");
			strBuf.append("       Observer.StateGroup,");
			strBuf.append("       Observer.ActionClass,");
			strBuf.append("       Observer.Description");
			strBuf.append(" FROM Observer");
			strBuf.append(" LEFT JOIN StateGroup");
			strBuf.append(" ON Observer.StateGroup = StateGroup.PrimaryKey");
			strBuf.append(" WHERE Observer.StateGroup = '" + StateGroup + "'");

			final String strSQL = strBuf.toString();

			list = this.listObserver(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [Observer] table, that use this primary key
	 */
	private int countObserverByStateGroup(final long lPrimaryKey) {
		return this.referencesObserverByStateGroup(lPrimaryKey).size();
	}

	/**
	 * list all records in the [NotificationScope] table, that use this primary
	 * key
	 */
	public final List<NotificationScope> referencesNotificationScopeByObserver(final long Observer) {
		List<NotificationScope> list = new ArrayList<NotificationScope>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT NotificationScope.PrimaryKey,");
			strBuf.append("       NotificationScope.ServerReplicationVersion,");
			strBuf.append("       NotificationScope.Observer,");
			strBuf.append("       NotificationScope.Module");
			strBuf.append(" FROM NotificationScope");
			strBuf.append(" LEFT JOIN Observer");
			strBuf.append(" ON NotificationScope.Observer = Observer.PrimaryKey");
			strBuf.append(" WHERE NotificationScope.Observer = '" + Observer + "'");

			final String strSQL = strBuf.toString();

			list = this.listNotificationScope(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [NotificationScope] table, that use this primary
	 * key
	 */
	private int countNotificationScopeByObserver(final long lPrimaryKey) {
		return this.referencesNotificationScopeByObserver(lPrimaryKey).size();
	}

	/**
	 * list all records in the [NotificationScope] table, that use this primary
	 * key
	 */
	public final List<NotificationScope> referencesNotificationScopeByModule(final long Module) {
		List<NotificationScope> list = new ArrayList<NotificationScope>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT NotificationScope.PrimaryKey,");
			strBuf.append("       NotificationScope.ServerReplicationVersion,");
			strBuf.append("       NotificationScope.Observer,");
			strBuf.append("       NotificationScope.Module");
			strBuf.append(" FROM NotificationScope");
			strBuf.append(" LEFT JOIN Module");
			strBuf.append(" ON NotificationScope.Module = Module.PrimaryKey");
			strBuf.append(" WHERE NotificationScope.Module = '" + Module + "'");

			final String strSQL = strBuf.toString();

			list = this.listNotificationScope(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [NotificationScope] table, that use this primary
	 * key
	 */
	private int countNotificationScopeByModule(final long lPrimaryKey) {
		return this.referencesNotificationScopeByModule(lPrimaryKey).size();
	}

	/**
	 * list all records in the [ObserverLink] table, that use this primary key
	 */
	public final List<ObserverLink> referencesObserverLinkBySource(final long Source) {
		List<ObserverLink> list = new ArrayList<ObserverLink>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT ObserverLink.PrimaryKey,");
			strBuf.append("       ObserverLink.ServerReplicationVersion,");
			strBuf.append("       ObserverLink.Source,");
			strBuf.append("       ObserverLink.Destination");
			strBuf.append(" FROM ObserverLink");
			strBuf.append(" LEFT JOIN Observer");
			strBuf.append(" ON ObserverLink.Source = Observer.PrimaryKey");
			strBuf.append(" WHERE ObserverLink.Source = '" + Source + "'");

			final String strSQL = strBuf.toString();

			list = this.listObserverLink(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [ObserverLink] table, that use this primary key
	 */
	private int countObserverLinkBySource(final long lPrimaryKey) {
		return this.referencesObserverLinkBySource(lPrimaryKey).size();
	}

	/**
	 * list all records in the [ObserverLink] table, that use this primary key
	 */
	public final List<ObserverLink> referencesObserverLinkByDestination(final long Destination) {
		List<ObserverLink> list = new ArrayList<ObserverLink>();

		try {
			final StringBuffer strBuf = new StringBuffer();

			strBuf.append("SELECT ObserverLink.PrimaryKey,");
			strBuf.append("       ObserverLink.ServerReplicationVersion,");
			strBuf.append("       ObserverLink.Source,");
			strBuf.append("       ObserverLink.Destination");
			strBuf.append(" FROM ObserverLink");
			strBuf.append(" LEFT JOIN Observer");
			strBuf.append(" ON ObserverLink.Destination = Observer.PrimaryKey");
			strBuf.append(" WHERE ObserverLink.Destination = '" + Destination + "'");

			final String strSQL = strBuf.toString();

			list = this.listObserverLink(this.m_db.executeQuery(strSQL));
		} catch (final Throwable t) {
			throw new RuntimeException(t.getMessage());
		}

		return list;
	}

	/**
	 * count all records in the [ObserverLink] table, that use this primary key
	 */
	private int countObserverLinkByDestination(final long lPrimaryKey) {
		return this.referencesObserverLinkByDestination(lPrimaryKey).size();
	}

	/**
	 * get all records that are referenced by this [Module] record from the
	 * server
	 */
	private List<Module> listModule(final ResultSet rs) {
		final List<Module> list = new ArrayList<Module>();

		try {
			while (rs.next()) {
				final Module record = this.nextModule(rs);

				if (record != null) {
					list.add(record);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	private Module nextModule(final ResultSet rs) {
		Module record = null;

		try {
			record = Module.generateWithoutKey();

			record.setPrimaryKey(rs.getLong(1));
			record.setServerReplicationVersion(rs.getLong(2));
			record.setModuleName(rs.getString(3));
			record.setDescription(rs.getString(4));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return record;
	}

	/**
	 * get all records that are referenced by this [StateGroup] record from the
	 * server
	 */
	private List<StateGroup> listStateGroup(final ResultSet rs) {
		final List<StateGroup> list = new ArrayList<StateGroup>();

		try {
			while (rs.next()) {
				final StateGroup record = this.nextStateGroup(rs);

				if (record != null) {
					list.add(record);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	private StateGroup nextStateGroup(final ResultSet rs) {
		StateGroup record = null;

		try {
			record = StateGroup.generateWithoutKey();

			record.setPrimaryKey(rs.getLong(1));
			record.setServerReplicationVersion(rs.getLong(2));
			record.setGroupName(rs.getString(3));
			record.setDescription(rs.getString(4));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return record;
	}

	/**
	 * get all records that are referenced by this [StateType] record from the
	 * server
	 */
	private List<StateType> listStateType(final ResultSet rs) {
		final List<StateType> list = new ArrayList<StateType>();

		try {
			while (rs.next()) {
				final StateType record = this.nextStateType(rs);

				if (record != null) {
					list.add(record);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	private StateType nextStateType(final ResultSet rs) {
		StateType record = null;

		try {
			record = StateType.generateWithoutKey();

			record.setPrimaryKey(rs.getLong(1));
			record.setServerReplicationVersion(rs.getLong(2));
			record.setTypeName(rs.getString(3));
			record.setDescription(rs.getString(4));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return record;
	}

	/**
	 * get all records that are referenced by this [StateGroupLink] record from
	 * the server
	 */
	private List<StateGroupLink> listStateGroupLink(final ResultSet rs) {
		final List<StateGroupLink> list = new ArrayList<StateGroupLink>();

		try {
			while (rs.next()) {
				final StateGroupLink record = this.nextStateGroupLink(rs);

				if (record != null) {
					list.add(record);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	private StateGroupLink nextStateGroupLink(final ResultSet rs) {
		StateGroupLink record = null;

		try {
			record = StateGroupLink.generateWithoutKey();

			record.setPrimaryKey(rs.getLong(1));
			record.setServerReplicationVersion(rs.getLong(2));
			record.setSource(rs.getLong(3));
			record.setDestination(rs.getLong(4));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return record;
	}

	/**
	 * get all records that are referenced by this [State] record from the
	 * server
	 */
	private List<State> listState(final ResultSet rs) {
		final List<State> list = new ArrayList<State>();

		try {
			while (rs.next()) {
				final State record = this.nextState(rs);

				if (record != null) {
					list.add(record);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	private State nextState(final ResultSet rs) {
		State record = null;

		try {
			record = State.generateWithoutKey();

			record.setPrimaryKey(rs.getLong(1));
			record.setServerReplicationVersion(rs.getLong(2));
			record.setStateName(rs.getString(3));
			record.setDefaultValue(rs.getString(4));
			record.setStateType(rs.getLong(5));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return record;
	}

	/**
	 * get all records that are referenced by this [StateChange] record from the
	 * server
	 */
	private List<StateChange> listStateChange(final ResultSet rs) {
		final List<StateChange> list = new ArrayList<StateChange>();

		try {
			while (rs.next()) {
				final StateChange record = this.nextStateChange(rs);

				if (record != null) {
					list.add(record);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	private StateChange nextStateChange(final ResultSet rs) {
		StateChange record = null;

		try {
			record = StateChange.generateWithoutKey();

			record.setPrimaryKey(rs.getLong(1));
			record.setServerReplicationVersion(rs.getLong(2));
			record.setState(rs.getLong(3));
			record.setStateValue(rs.getString(4));
			record.setChangeTime(new Date(rs.getLong(5)));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return record;
	}

	/**
	 * get all records that are referenced by this [Observer] record from the
	 * server
	 */
	private List<Observer> listObserver(final ResultSet rs) {
		final List<Observer> list = new ArrayList<Observer>();

		try {
			while (rs.next()) {
				final Observer record = this.nextObserver(rs);

				if (record != null) {
					list.add(record);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	private Observer nextObserver(final ResultSet rs) {
		Observer record = null;

		try {
			record = Observer.generateWithoutKey();

			record.setPrimaryKey(rs.getLong(1));
			record.setServerReplicationVersion(rs.getLong(2));
			record.setState(rs.getLong(3));
			record.setStateGroup(rs.getLong(4));
			record.setActionClass(rs.getString(5));
			record.setDescription(rs.getString(6));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return record;
	}

	/**
	 * get all records that are referenced by this [NotificationScope] record
	 * from the server
	 */
	private List<NotificationScope> listNotificationScope(final ResultSet rs) {
		final List<NotificationScope> list = new ArrayList<NotificationScope>();

		try {
			while (rs.next()) {
				final NotificationScope record = this.nextNotificationScope(rs);

				if (record != null) {
					list.add(record);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	private NotificationScope nextNotificationScope(final ResultSet rs) {
		NotificationScope record = null;

		try {
			record = NotificationScope.generateWithoutKey();

			record.setPrimaryKey(rs.getLong(1));
			record.setServerReplicationVersion(rs.getLong(2));
			record.setObserver(rs.getLong(3));
			record.setModule(rs.getLong(4));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return record;
	}

	/**
	 * get all records that are referenced by this [ObserverLink] record from
	 * the server
	 */
	private List<ObserverLink> listObserverLink(final ResultSet rs) {
		final List<ObserverLink> list = new ArrayList<ObserverLink>();

		try {
			while (rs.next()) {
				final ObserverLink record = this.nextObserverLink(rs);

				if (record != null) {
					list.add(record);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	private ObserverLink nextObserverLink(final ResultSet rs) {
		ObserverLink record = null;

		try {
			record = ObserverLink.generateWithoutKey();

			record.setPrimaryKey(rs.getLong(1));
			record.setServerReplicationVersion(rs.getLong(2));
			record.setSource(rs.getLong(3));
			record.setDestination(rs.getLong(4));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return record;
	}

	public final boolean createSchema() {
		int nTableCreated = 0;

		boolean bSuccessful = true;

		// ##########################################################
		final StringBuilder strBuilderModule = new StringBuilder();

		strBuilderModule.append("CREATE TABLE Module");
		strBuilderModule.append("   ( PrimaryKey BIGINT,");
		strBuilderModule.append("     ServerReplicationVersion BIGINT,");
		strBuilderModule.append("     ModuleName CHAR(255),");
		strBuilderModule.append("     Description TEXT )");

		nTableCreated = this.m_db.executeUpdate(strBuilderModule.toString());

		if (nTableCreated < 0) {
			throw new RuntimeException("SQL TABLE CREATION failed");
		}

		// ##########################################################
		final StringBuilder strBuilderStateGroup = new StringBuilder();

		strBuilderStateGroup.append("CREATE TABLE StateGroup");
		strBuilderStateGroup.append("   ( PrimaryKey BIGINT,");
		strBuilderStateGroup.append("     ServerReplicationVersion BIGINT,");
		strBuilderStateGroup.append("     GroupName CHAR(255),");
		strBuilderStateGroup.append("     Description TEXT )");

		nTableCreated = this.m_db.executeUpdate(strBuilderStateGroup.toString());

		if (nTableCreated < 0) {
			throw new RuntimeException("SQL TABLE CREATION failed");
		}

		// ##########################################################
		final StringBuilder strBuilderStateType = new StringBuilder();

		strBuilderStateType.append("CREATE TABLE StateType");
		strBuilderStateType.append("   ( PrimaryKey BIGINT,");
		strBuilderStateType.append("     ServerReplicationVersion BIGINT,");
		strBuilderStateType.append("     TypeName CHAR(255),");
		strBuilderStateType.append("     Description TEXT )");

		nTableCreated = this.m_db.executeUpdate(strBuilderStateType.toString());

		if (nTableCreated < 0) {
			throw new RuntimeException("SQL TABLE CREATION failed");
		}

		// ##########################################################
		final StringBuilder strBuilderStateGroupLink = new StringBuilder();

		strBuilderStateGroupLink.append("CREATE TABLE StateGroupLink");
		strBuilderStateGroupLink.append("   ( PrimaryKey BIGINT,");
		strBuilderStateGroupLink.append("     ServerReplicationVersion BIGINT,");
		strBuilderStateGroupLink.append("     Source BIGINT,");
		strBuilderStateGroupLink.append("     Destination BIGINT )");

		nTableCreated = this.m_db.executeUpdate(strBuilderStateGroupLink.toString());

		if (nTableCreated < 0) {
			throw new RuntimeException("SQL TABLE CREATION failed");
		}

		// ##########################################################
		final StringBuilder strBuilderState = new StringBuilder();

		strBuilderState.append("CREATE TABLE State");
		strBuilderState.append("   ( PrimaryKey BIGINT,");
		strBuilderState.append("     ServerReplicationVersion BIGINT,");
		strBuilderState.append("     StateName CHAR(255),");
		strBuilderState.append("     DefaultValue CHAR(255),");
		strBuilderState.append("     StateType BIGINT )");

		nTableCreated = this.m_db.executeUpdate(strBuilderState.toString());

		if (nTableCreated < 0) {
			throw new RuntimeException("SQL TABLE CREATION failed");
		}

		// ##########################################################
		final StringBuilder strBuilderStateChange = new StringBuilder();

		strBuilderStateChange.append("CREATE TABLE StateChange");
		strBuilderStateChange.append("   ( PrimaryKey BIGINT,");
		strBuilderStateChange.append("     ServerReplicationVersion BIGINT,");
		strBuilderStateChange.append("     State BIGINT,");
		strBuilderStateChange.append("     StateValue CHAR(255),");
		strBuilderStateChange.append("     ChangeTime BIGINT )");

		nTableCreated = this.m_db.executeUpdate(strBuilderStateChange.toString());

		if (nTableCreated < 0) {
			throw new RuntimeException("SQL TABLE CREATION failed");
		}

		// ##########################################################
		final StringBuilder strBuilderObserver = new StringBuilder();

		strBuilderObserver.append("CREATE TABLE Observer");
		strBuilderObserver.append("   ( PrimaryKey BIGINT,");
		strBuilderObserver.append("     ServerReplicationVersion BIGINT,");
		strBuilderObserver.append("     State BIGINT,");
		strBuilderObserver.append("     StateGroup BIGINT,");
		strBuilderObserver.append("     ActionClass CHAR(255),");
		strBuilderObserver.append("     Description TEXT )");

		nTableCreated = this.m_db.executeUpdate(strBuilderObserver.toString());

		if (nTableCreated < 0) {
			throw new RuntimeException("SQL TABLE CREATION failed");
		}

		// ##########################################################
		final StringBuilder strBuilderNotificationScope = new StringBuilder();

		strBuilderNotificationScope.append("CREATE TABLE NotificationScope");
		strBuilderNotificationScope.append("   ( PrimaryKey BIGINT,");
		strBuilderNotificationScope.append("     ServerReplicationVersion BIGINT,");
		strBuilderNotificationScope.append("     Observer BIGINT,");
		strBuilderNotificationScope.append("     Module BIGINT )");

		nTableCreated = this.m_db.executeUpdate(strBuilderNotificationScope.toString());

		if (nTableCreated < 0) {
			throw new RuntimeException("SQL TABLE CREATION failed");
		}

		// ##########################################################
		final StringBuilder strBuilderObserverLink = new StringBuilder();

		strBuilderObserverLink.append("CREATE TABLE ObserverLink");
		strBuilderObserverLink.append("   ( PrimaryKey BIGINT,");
		strBuilderObserverLink.append("     ServerReplicationVersion BIGINT,");
		strBuilderObserverLink.append("     Source BIGINT,");
		strBuilderObserverLink.append("     Destination BIGINT )");

		nTableCreated = this.m_db.executeUpdate(strBuilderObserverLink.toString());

		if (nTableCreated < 0) {
			throw new RuntimeException("SQL TABLE CREATION failed");
		}
		return bSuccessful;
	}

	private final class SQLDatabase {

		private Connection m_dbConnection = null;

		private SQLDatabase() {
		}

		private boolean isConnected() {
			boolean bConnected = false;

			if (this.m_dbConnection != null) {
				try {
					bConnected = !this.m_dbConnection.isClosed();
				} catch (final Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			}

			return bConnected;
		}

		private Connection getConnection() {
			if (!this.isConnected()) {
				try {
					Class.forName(SQLSource.this.getDriverClass());

					this.m_dbConnection = DriverManager.getConnection(SQLSource.this.getConnectionString(),
							SQLSource.this.getUser(), SQLSource.this.getPassword());
				} catch (final Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			}

			return this.m_dbConnection;
		}

		private Statement createStatement() {
			Statement statement = null;

			final Connection dbConnection = this.getConnection();

			try {
				statement = dbConnection.createStatement();
			} catch (final Exception e) {
				throw new RuntimeException(e.getMessage());
			}

			return statement;
		}

		public final ResultSet executeQuery(final String strSQL) {
			ResultSet rs = null;

			final Statement statement = this.createStatement();

			if (statement != null) {
				try {
					rs = statement.executeQuery(strSQL);
				} catch (final Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			}

			return rs;
		}

		public final int executeUpdate(final String strSQL) {
			int nCount = 0;

			final Statement statement = this.createStatement();

			if (statement != null) {
				try {
					nCount = statement.executeUpdate(strSQL);
				} catch (final Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			}

			return nCount;
		}
	}

}
