package de.boetzmeyer.observerengine;

final class Operation {

	// type safe enums
	// ===============
	public static final Operation EQUALS = new Operation(1, "=");
	public static final Operation GREATER = new Operation(2, ">");
	public static final Operation SMALLER = new Operation(4, "<");
	public static final Operation GREATER_OR_EQUAL = new Operation(8, ">=");
	public static final Operation SMALLER_OR_EQUAL = new Operation(16, "<=");

	private final int m_nID;
	private final String m_strPresentation;

	private Operation(final int nID, final String strPresentation) {
		this.m_nID = nID;
		this.m_strPresentation = strPresentation;
	}

	public final int getID() {
		return this.m_nID;
	}

	public final String getPresentation() {
		return this.m_strPresentation;
	}

	public final String toString() {
		return this.m_strPresentation;
	}

}
