package de.boetzmeyer.observerengine;

import java.io.File;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

final class Settings {

	private static final String MODEL_DIR = System.getProperty("user.home") + File.separatorChar + "CommunicationModel";
	public static final int INVALID_PORT = 0;
	public static final String LOCALHOST = "localhost";

	private static Locale sm_locale = Locale.getDefault();
	private static String sm_strServerName = LOCALHOST;
	private static String sm_strXMLExchangeDir = MODEL_DIR;
	private static int sm_nPort = 3306;
	private static int sm_nCallbackPort = INVALID_PORT;
	private static String password = "root";
	private static String userName = "root";

	private static String sm_strLocalDatabaseDir = MODEL_DIR;
	private static String sm_strDriverClass = "org.gjt.mm.mysql.Driver";
	private static String sm_strDriverProtocol = "jdbc:mysql";

	public static final String getLocaleDatabaseDir() {
		return sm_strLocalDatabaseDir;
	}

	public static final void setLocaleDatabaseDir(final String strDir) {
		if (strDir != null) {
			LocalSource.reset();
			sm_strLocalDatabaseDir = strDir;
		}
	}

	public static final String getDriverClass() {
		return sm_strDriverClass;
	}

	public static final String getDriverProtocol() {
		return sm_strDriverProtocol;
	}

	public static final void setDriverClass(final String inDriverClass) {
		sm_strDriverClass = inDriverClass;
	}

	public static final void setDriverProtocol(final String inDriverProtocol) {
		sm_strDriverProtocol = inDriverProtocol;
	}

	public static final String getServerName() {
		return sm_strServerName;
	}

	public static final int getPort() {
		return sm_nPort;
	}

	public static final void setPort(final int nPort) {
		sm_nPort = nPort;
	}

	public static final int getCallbackPort() {
		return sm_nCallbackPort;
	}

	public static final void setCallbackPort(final int nCallbackPort) {
		sm_nCallbackPort = nCallbackPort;
	}

	public static final String getXMLExchangeDir() {
		return sm_strXMLExchangeDir;
	}

	public static final void setServerName(final String strServerName) {
		if (strServerName != null) {
			sm_strServerName = strServerName;
		}
	}

	public static final void setXMLExchangeDir(final String strDir) {
		if (strDir != null) {
			if (new File(strDir).isDirectory()) {
				sm_strXMLExchangeDir = strDir;
			}
		}
	}

	public static final String getUserName() {
		return userName;
	}

	public static final void setUserName(final String inUserName) {
		userName = inUserName;
	}

	public static final String getPassword() {
		return password;
	}

	public static final void setPassword(final String inPassword) {
		password = inPassword;
	}

	public static final String getClientName() {
		String strClientName = LOCALHOST;

		try {
			strClientName = InetAddress.getLocalHost().getHostName();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return strClientName.toLowerCase();
	}

	public static final String getCurrencySymbol() {
		final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(getLocale());

		final Currency currency = numberFormat.getCurrency();

		return currency.getSymbol(getLocale());
	}

	public static final void setLocale(final Locale locale) {
		if (locale != null)
			sm_locale = locale;
	}

	public static final Locale getLocale() {
		return sm_locale;
	}

}
