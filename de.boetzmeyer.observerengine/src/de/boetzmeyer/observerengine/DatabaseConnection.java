package de.boetzmeyer.observerengine;

/**
 * @author timbotzmeyer <br>
 *         <br>
 *         An object, that contains all necessary information to connect the
 *         client application with an observer model that exists in a relational
 *         database.
 */
public final class DatabaseConnection {
	private final String serverName;
	private final int port;
	private final String driverClass;
	private final String driverProtocol;
	private final String user;
	private final String password;

	public DatabaseConnection(final String serverName, final int port, final String driverClass,
			final String driverProtocol, final String user, final String password) {
		this.serverName = serverName;
		this.port = port;
		this.driverClass = driverClass;
		this.driverProtocol = driverProtocol;
		this.user = user;
		this.password = password;
	}

	public String getServerName() {
		return serverName;
	}

	public int getPort() {
		return port;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getDriverProtocol() {
		return driverProtocol;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
