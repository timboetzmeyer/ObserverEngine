package de.boetzmeyer.observerengine;

final class Table {

	// type safe enums
	// ===============
	public static final Table OBSERVER = new Table("Observer", "Observer");
	public static final Table OBSERVERLINK = new Table("ObserverLink", "Observer Link");
	public static final Table STATE = new Table("State", "State");
	public static final Table STATETYPE = new Table("StateType", "State Type");
	public static final Table STATEGROUP = new Table("StateGroup", "State Group");
	public static final Table STATECHANGE = new Table("StateChange", "State Change");
	public static final Table STATEGROUPLINK = new Table("StateGroupLink", "State Model");
	public static final Table NOTIFICATIONSCOPE = new Table("NotificationScope", "NotificationScope");
	public static final Table MODULE = new Table("Module", "Module");

	private final String m_strTableName;
	private final String m_strAliasName;

	private Table(final String strTableName, final String strAliasName) {
		this.m_strTableName = strTableName;
		this.m_strAliasName = strAliasName;
	}

	public final String toString() {
		return this.m_strAliasName;
	}

	public final String getTableName() {
		return this.m_strTableName;
	}

	public final String getAliasName() {
		return this.m_strAliasName;
	}

	public static final Table getByName(final String strTableName) {
		if (Table.OBSERVER.getAliasName().equalsIgnoreCase(strTableName))
			return Table.OBSERVER;

		if (Table.OBSERVERLINK.getAliasName().equalsIgnoreCase(strTableName))
			return Table.OBSERVERLINK;

		if (Table.STATE.getAliasName().equalsIgnoreCase(strTableName))
			return Table.STATE;

		if (Table.STATETYPE.getAliasName().equalsIgnoreCase(strTableName))
			return Table.STATETYPE;

		if (Table.STATEGROUP.getAliasName().equalsIgnoreCase(strTableName))
			return Table.STATEGROUP;

		if (Table.STATECHANGE.getAliasName().equalsIgnoreCase(strTableName))
			return Table.STATECHANGE;

		if (Table.STATEGROUPLINK.getAliasName().equalsIgnoreCase(strTableName))
			return Table.STATEGROUPLINK;

		if (Table.NOTIFICATIONSCOPE.getAliasName().equalsIgnoreCase(strTableName))
			return Table.NOTIFICATIONSCOPE;

		if (Table.MODULE.getAliasName().equalsIgnoreCase(strTableName))
			return Table.MODULE;

		return null;
	}

}
