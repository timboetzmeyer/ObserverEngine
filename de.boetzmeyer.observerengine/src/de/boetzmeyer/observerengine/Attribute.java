package de.boetzmeyer.observerengine;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class Attribute implements Serializable {

	private static final long serialVersionUID = "ATTRIBUTE".hashCode();

	// xml tags
	// ========
	public static final String ATTRIBUTE = "Attribute"; // UserManagement +
														// ModelVersion
	public static final String KEY = "Key"; // UserManagement + ModelVersion
	private static final String TABLE_NAME = "TableName"; // ModelVersion
	private static final String ATTRIBUTE_NAME = "AttributeName"; // ModelVersion
	private static final String ALIAS_NAME = "AliasName"; // ModelVersion
	private static final String TYPE = "Type"; // ModelVersion

	// type safe enums
	// ===============

	public static final Attribute OBSERVER_PRIMARYKEY = new Attribute("Observer", "PrimaryKey", "PrimaryKey", "long");
	public static final Attribute OBSERVER_SERVERREPLICATIONVERSION = new Attribute("Observer",
			"ServerReplicationVersion", "ServerReplicationVersion", "long");
	public static final Attribute OBSERVER_STATE = new Attribute("Observer", "State", "State", "long");
	public static final Attribute OBSERVER_STATEGROUP = new Attribute("Observer", "StateGroup", "State Group", "long");
	public static final Attribute OBSERVER_ACTIONCLASS = new Attribute("Observer", "ActionClass", "Action Class",
			"String");
	public static final Attribute OBSERVER_DESCRIPTION = new Attribute("Observer", "Description", "Description",
			"String");

	public static final Attribute OBSERVERLINK_PRIMARYKEY = new Attribute("ObserverLink", "PrimaryKey", "PrimaryKey",
			"long");
	public static final Attribute OBSERVERLINK_SERVERREPLICATIONVERSION = new Attribute("ObserverLink",
			"ServerReplicationVersion", "ServerReplicationVersion", "long");
	public static final Attribute OBSERVERLINK_SOURCE = new Attribute("ObserverLink", "Source", "Observer", "long");
	public static final Attribute OBSERVERLINK_DESTINATION = new Attribute("ObserverLink", "Destination", "Observed",
			"long");

	public static final Attribute STATE_PRIMARYKEY = new Attribute("State", "PrimaryKey", "PrimaryKey", "long");
	public static final Attribute STATE_SERVERREPLICATIONVERSION = new Attribute("State", "ServerReplicationVersion",
			"ServerReplicationVersion", "long");
	public static final Attribute STATE_STATENAME = new Attribute("State", "StateName", "State Name", "String");
	public static final Attribute STATE_DEFAULTVALUE = new Attribute("State", "DefaultValue", "Default Value",
			"String");
	public static final Attribute STATE_STATETYPE = new Attribute("State", "StateType", "Type", "long");

	public static final Attribute STATETYPE_PRIMARYKEY = new Attribute("StateType", "PrimaryKey", "PrimaryKey", "long");
	public static final Attribute STATETYPE_SERVERREPLICATIONVERSION = new Attribute("StateType",
			"ServerReplicationVersion", "ServerReplicationVersion", "long");
	public static final Attribute STATETYPE_TYPENAME = new Attribute("StateType", "TypeName", "Type Name", "String");
	public static final Attribute STATETYPE_DESCRIPTION = new Attribute("StateType", "Description", "Description",
			"String");

	public static final Attribute STATEGROUP_PRIMARYKEY = new Attribute("StateGroup", "PrimaryKey", "PrimaryKey",
			"long");
	public static final Attribute STATEGROUP_SERVERREPLICATIONVERSION = new Attribute("StateGroup",
			"ServerReplicationVersion", "ServerReplicationVersion", "long");
	public static final Attribute STATEGROUP_GROUPNAME = new Attribute("StateGroup", "GroupName", "Group Name",
			"String");
	public static final Attribute STATEGROUP_DESCRIPTION = new Attribute("StateGroup", "Description", "Description",
			"String");

	public static final Attribute STATECHANGE_PRIMARYKEY = new Attribute("StateChange", "PrimaryKey", "PrimaryKey",
			"long");
	public static final Attribute STATECHANGE_SERVERREPLICATIONVERSION = new Attribute("StateChange",
			"ServerReplicationVersion", "ServerReplicationVersion", "long");
	public static final Attribute STATECHANGE_STATE = new Attribute("StateChange", "State", "State", "long");
	public static final Attribute STATECHANGE_STATEVALUE = new Attribute("StateChange", "StateValue", "Value",
			"String");
	public static final Attribute STATECHANGE_CHANGETIME = new Attribute("StateChange", "ChangeTime", "Change Time",
			"Date");

	public static final Attribute STATEGROUPLINK_PRIMARYKEY = new Attribute("StateGroupLink", "PrimaryKey",
			"PrimaryKey", "long");
	public static final Attribute STATEGROUPLINK_SERVERREPLICATIONVERSION = new Attribute("StateGroupLink",
			"ServerReplicationVersion", "ServerReplicationVersion", "long");
	public static final Attribute STATEGROUPLINK_SOURCE = new Attribute("StateGroupLink", "Source", "State Group",
			"long");
	public static final Attribute STATEGROUPLINK_DESTINATION = new Attribute("StateGroupLink", "Destination",
			"Sub Group", "long");

	public static final Attribute NOTIFICATIONSCOPE_PRIMARYKEY = new Attribute("NotificationScope", "PrimaryKey",
			"PrimaryKey", "long");
	public static final Attribute NOTIFICATIONSCOPE_SERVERREPLICATIONVERSION = new Attribute("NotificationScope",
			"ServerReplicationVersion", "ServerReplicationVersion", "long");
	public static final Attribute NOTIFICATIONSCOPE_OBSERVER = new Attribute("NotificationScope", "Observer",
			"Observer", "long");
	public static final Attribute NOTIFICATIONSCOPE_MODULE = new Attribute("NotificationScope", "Module", "Module",
			"long");

	public static final Attribute MODULE_PRIMARYKEY = new Attribute("Module", "PrimaryKey", "PrimaryKey", "long");
	public static final Attribute MODULE_SERVERREPLICATIONVERSION = new Attribute("Module", "ServerReplicationVersion",
			"ServerReplicationVersion", "long");
	public static final Attribute MODULE_MODULENAME = new Attribute("Module", "ModuleName", "Module Name", "String");
	public static final Attribute MODULE_DESCRIPTION = new Attribute("Module", "Description", "Description", "String");

	private final String m_strTableName;
	private final String m_strAttributeName;
	private final String m_strAliasName;
	private final String m_strType;

	private Attribute(final String strTableName, final String strAttributeName, final String strAliasName,
			final String strType) {
		this.m_strTableName = strTableName;
		this.m_strAttributeName = strAttributeName;
		this.m_strAliasName = strAliasName;
		this.m_strType = strType.toUpperCase();
	}

	public final String toString() {
		return this.m_strAliasName;
	}

	public final boolean hasSameType(final Attribute attribute) {
		if (attribute != null) {
			return (this.m_strType.equalsIgnoreCase(attribute.getType()));
		}

		return false;
	}

	public final String getType() {
		return this.m_strType;
	}

	public final boolean isTypeString() {
		return this.m_strType.equalsIgnoreCase("String");
	}

	public final boolean isTypeDate() {
		return this.m_strType.equalsIgnoreCase("Date");
	}

	public final boolean isTypeInt() {
		return this.m_strType.equalsIgnoreCase("Int");
	}

	public final boolean isTypeLong() {
		return this.m_strType.equalsIgnoreCase("Long");
	}

	public final boolean isTypeFloat() {
		return this.m_strType.equalsIgnoreCase("float");
	}

	public final boolean isTypeDouble() {
		return this.m_strType.equalsIgnoreCase("double");
	}

	public final boolean isTypeBoolean() {
		return this.m_strType.equalsIgnoreCase("boolean");
	}

	public final String getTableName() {
		return this.m_strTableName;
	}

	public final String getAttributeName() {
		return this.m_strAttributeName;
	}

	public final String getAliasName() {
		return this.m_strAliasName;
	}

	public final boolean save(final OutputStreamWriter out) {
		boolean bSaved = false;

		if (out != null) {
			XMLParser.writeLine(0, out, ATTRIBUTE, null, true);

			XMLParser.writeLine(1, out, KEY, this.getKey(), true);
			XMLParser.writeLine(1, out, TABLE_NAME, this.getTableName(), true);
			XMLParser.writeLine(1, out, ATTRIBUTE_NAME, this.getAttributeName(), true);
			XMLParser.writeLine(1, out, ALIAS_NAME, this.getAliasName(), true);
			XMLParser.writeLine(1, out, TYPE, this.getType(), true);

			XMLParser.writeLine(0, out, ATTRIBUTE, null, false);

			bSaved = true;
		}

		return bSaved;
	}

	public static final Attribute load(final Node node) {
		Attribute attribute = null;

		String strTableName = "";
		String strAttributeName = "";
		String strAliasName = "";
		String strType = "";

		if (node != null) {
			final NodeList listElementChildren = node.getChildNodes();

			final int nNodeCount = listElementChildren.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node nodeChild = listElementChildren.item(i);

				if (XMLParser.isElementNode(nodeChild)) {
					final String strNodeName = nodeChild.getNodeName();

					if (strNodeName.equals(TABLE_NAME)) {
						strTableName = XMLParser.loadString(nodeChild);
					} else if (strNodeName.equals(ATTRIBUTE_NAME)) {
						strAttributeName = XMLParser.loadString(nodeChild);
					} else if (strNodeName.equals(ALIAS_NAME)) {
						strAliasName = XMLParser.loadString(nodeChild);
					} else if (strNodeName.equals(TYPE)) {
						strType = XMLParser.loadString(nodeChild);
					}
				}
			}

			attribute = new Attribute(strTableName, strAttributeName, strAliasName, strType);
		}

		return attribute;
	}

	public final String getKey() {
		final StringBuilder strBuilder = new StringBuilder(this.m_strTableName);

		strBuilder.append(".");
		strBuilder.append(this.m_strAttributeName);

		return strBuilder.toString();
	}

	public static final Attribute getByKey(final String strAttributeKey) {
		if (strAttributeKey != null) {
			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVER_PRIMARYKEY.getKey())) {
				return Attribute.OBSERVER_PRIMARYKEY;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVER_SERVERREPLICATIONVERSION.getKey())) {
				return Attribute.OBSERVER_SERVERREPLICATIONVERSION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVER_STATE.getKey())) {
				return Attribute.OBSERVER_STATE;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVER_STATEGROUP.getKey())) {
				return Attribute.OBSERVER_STATEGROUP;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVER_ACTIONCLASS.getKey())) {
				return Attribute.OBSERVER_ACTIONCLASS;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVER_DESCRIPTION.getKey())) {
				return Attribute.OBSERVER_DESCRIPTION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVERLINK_PRIMARYKEY.getKey())) {
				return Attribute.OBSERVERLINK_PRIMARYKEY;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVERLINK_SERVERREPLICATIONVERSION.getKey())) {
				return Attribute.OBSERVERLINK_SERVERREPLICATIONVERSION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVERLINK_SOURCE.getKey())) {
				return Attribute.OBSERVERLINK_SOURCE;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.OBSERVERLINK_DESTINATION.getKey())) {
				return Attribute.OBSERVERLINK_DESTINATION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATE_PRIMARYKEY.getKey())) {
				return Attribute.STATE_PRIMARYKEY;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATE_SERVERREPLICATIONVERSION.getKey())) {
				return Attribute.STATE_SERVERREPLICATIONVERSION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATE_STATENAME.getKey())) {
				return Attribute.STATE_STATENAME;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATE_DEFAULTVALUE.getKey())) {
				return Attribute.STATE_DEFAULTVALUE;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATE_STATETYPE.getKey())) {
				return Attribute.STATE_STATETYPE;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATETYPE_PRIMARYKEY.getKey())) {
				return Attribute.STATETYPE_PRIMARYKEY;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATETYPE_SERVERREPLICATIONVERSION.getKey())) {
				return Attribute.STATETYPE_SERVERREPLICATIONVERSION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATETYPE_TYPENAME.getKey())) {
				return Attribute.STATETYPE_TYPENAME;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATETYPE_DESCRIPTION.getKey())) {
				return Attribute.STATETYPE_DESCRIPTION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATEGROUP_PRIMARYKEY.getKey())) {
				return Attribute.STATEGROUP_PRIMARYKEY;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATEGROUP_SERVERREPLICATIONVERSION.getKey())) {
				return Attribute.STATEGROUP_SERVERREPLICATIONVERSION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATEGROUP_GROUPNAME.getKey())) {
				return Attribute.STATEGROUP_GROUPNAME;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATEGROUP_DESCRIPTION.getKey())) {
				return Attribute.STATEGROUP_DESCRIPTION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATECHANGE_PRIMARYKEY.getKey())) {
				return Attribute.STATECHANGE_PRIMARYKEY;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATECHANGE_SERVERREPLICATIONVERSION.getKey())) {
				return Attribute.STATECHANGE_SERVERREPLICATIONVERSION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATECHANGE_STATE.getKey())) {
				return Attribute.STATECHANGE_STATE;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATECHANGE_STATEVALUE.getKey())) {
				return Attribute.STATECHANGE_STATEVALUE;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATECHANGE_CHANGETIME.getKey())) {
				return Attribute.STATECHANGE_CHANGETIME;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATEGROUPLINK_PRIMARYKEY.getKey())) {
				return Attribute.STATEGROUPLINK_PRIMARYKEY;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATEGROUPLINK_SERVERREPLICATIONVERSION.getKey())) {
				return Attribute.STATEGROUPLINK_SERVERREPLICATIONVERSION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATEGROUPLINK_SOURCE.getKey())) {
				return Attribute.STATEGROUPLINK_SOURCE;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.STATEGROUPLINK_DESTINATION.getKey())) {
				return Attribute.STATEGROUPLINK_DESTINATION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.NOTIFICATIONSCOPE_PRIMARYKEY.getKey())) {
				return Attribute.NOTIFICATIONSCOPE_PRIMARYKEY;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.NOTIFICATIONSCOPE_SERVERREPLICATIONVERSION.getKey())) {
				return Attribute.NOTIFICATIONSCOPE_SERVERREPLICATIONVERSION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.NOTIFICATIONSCOPE_OBSERVER.getKey())) {
				return Attribute.NOTIFICATIONSCOPE_OBSERVER;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.NOTIFICATIONSCOPE_MODULE.getKey())) {
				return Attribute.NOTIFICATIONSCOPE_MODULE;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.MODULE_PRIMARYKEY.getKey())) {
				return Attribute.MODULE_PRIMARYKEY;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.MODULE_SERVERREPLICATIONVERSION.getKey())) {
				return Attribute.MODULE_SERVERREPLICATIONVERSION;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.MODULE_MODULENAME.getKey())) {
				return Attribute.MODULE_MODULENAME;
			}

			if (strAttributeKey.equalsIgnoreCase(Attribute.MODULE_DESCRIPTION.getKey())) {
				return Attribute.MODULE_DESCRIPTION;
			}

		}

		return null;
	}

	public static boolean equals(final Date date1, final Date date2) {
		final Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(date1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		final long value1 = calendar.getTime().getTime();
		calendar.clear();
		calendar.setTime(date2);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		final long value2 = calendar.getTime().getTime();
		return (value1 == value2);
	}

	public final boolean equals(final Object obj) {
		if (obj instanceof Attribute) {
			final String strKey1 = ((Attribute) obj).getKey();
			final String strKey2 = this.getKey();

			return strKey2.equalsIgnoreCase(strKey1);
		}

		return false;
	}

	public final int hashCode() {
		return this.getKey().toUpperCase().hashCode();
	}

}
