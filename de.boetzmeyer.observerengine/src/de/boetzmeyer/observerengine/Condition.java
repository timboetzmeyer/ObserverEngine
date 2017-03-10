package de.boetzmeyer.observerengine;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

final class Condition {

	private final Operation m_operation;
	private final Attribute m_attribute;
	private final String m_strValue;

	public static final Condition createInteger(final Operation op, final Attribute attr, final int nValue) {
		if ((op != null) && (attr != null)) {
			return new Condition(op, attr, Integer.toString(nValue));
		}

		return null;
	}

	public static final Condition createLong(final Operation op, final Attribute attr, final long lValue) {
		if ((op != null) && (attr != null)) {
			return new Condition(op, attr, Long.toString(lValue));
		}

		return null;
	}

	public static final Condition createFloat(final Operation op, final Attribute attr, final float fValue) {
		if ((op != null) && (attr != null)) {
			return new Condition(op, attr, Float.toString(fValue));
		}

		return null;
	}

	public static final Condition createDouble(final Operation op, final Attribute attr, final double dValue) {
		if ((op != null) && (attr != null)) {
			return new Condition(op, attr, Double.toString(dValue));
		}

		return null;
	}

	public static final Condition createBoolean(final Operation op, final Attribute attr, final boolean bValue) {
		if ((op != null) && (attr != null)) {
			return new Condition(op, attr, Boolean.toString(bValue));
		}

		return null;
	}

	public static final Condition createString(final Operation op, final Attribute attr, final String strValue) {
		if ((op != null) && (attr != null) && (strValue != null)) {
			return new Condition(op, attr, strValue);
		}

		return null;
	}

	public static final Condition createDate(final Operation op, final Attribute attr, final Date date) {
		if ((op != null) && (attr != null) && (date != null)) {
			return new Condition(op, attr, Long.toString(date.getTime()));
		}

		return null;
	}

	private Condition(final Operation op, final Attribute attr, final String strValue) {
		this.m_operation = op;
		this.m_attribute = attr;
		this.m_strValue = strValue;
	}

	public final Operation getOperation() {
		return this.m_operation;
	}

	public final Attribute getAttribute() {
		return this.m_attribute;
	}

	public final String getTableName() {
		return this.m_attribute.getTableName();
	}

	public final String getValue() {
		return this.m_strValue;
	}

	public final String toString() {
		final StringBuffer strBuf = new StringBuffer();

		strBuf.append("[");
		strBuf.append(this.m_attribute.toString());
		strBuf.append("] ");
		strBuf.append(this.m_operation.toString());
		strBuf.append(" ");
		strBuf.append(this.m_strValue);

		return strBuf.toString();
	}

	public final String toXML() {
		final StringBuffer strBuf = new StringBuffer();

		strBuf.append("<Condition>");
		strBuf.append("  <Operation>");
		strBuf.append(Integer.toString(this.m_operation.getID()));
		strBuf.append("  </Operation>");
		strBuf.append("  <Table>");
		strBuf.append(this.m_attribute.getTableName());
		strBuf.append("  </Table>");
		strBuf.append("  <Value>");
		strBuf.append("  <Attribute>");
		strBuf.append(this.m_attribute.getAttributeName());
		strBuf.append("  </Attribute>");
		strBuf.append("  <Value>");
		strBuf.append(this.m_strValue);
		strBuf.append("  </Value>");
		strBuf.append("</Condition>");

		return strBuf.toString();
	}

	static final String toXML(final Collection<Condition> coll, final String strTableName) {
		final StringBuffer strBuf = new StringBuffer();

		strBuf.append("<Conditions>");

		strBuf.append("<Table>");
		strBuf.append(strTableName);
		strBuf.append("</Table>");

		if (coll != null) {
			final Iterator<Condition> it = coll.iterator();

			while (it.hasNext()) {
				final Condition nextCondition = it.next();

				if (nextCondition != null) {
					strBuf.append(nextCondition.toXML());
				}
			}
		}

		strBuf.append("</Conditions>");

		return strBuf.toString();
	}

}
