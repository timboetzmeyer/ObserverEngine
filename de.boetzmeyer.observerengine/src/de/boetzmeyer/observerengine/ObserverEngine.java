package de.boetzmeyer.observerengine;

/**
 * @author Tim
 * 
 *         The entry point to work with the observer engine is this class.
 *         Before the engine is ready to work, the 'init' method has to be
 *         called by the using client application. After being initialized in
 *         the right way, the engine is able to handle client calls, that invoke
 *         the 'get' method. Without having been called 'init' correct, the
 *         'get' will throw an exception on each invocation.
 *
 */
public final class ObserverEngine {

	private static final int DEFAULT_HISTORY_CLEANUP_AFTER_X_OBSERVATION_CYCLES = 60;

	private static final int DEFAULT_SLEEP_TIME_BETWEEN_TWO_OBSERVATION_CYCLES_IN_MILLISECONDS = 1000;

	private static final int DEFAULT_MAX_HISTORY_ENTRIES = 5;

	private static IObserverEngineAdmin observerEngine;

	private ObserverEngine() {
	}

	/**
	 * Creates the only instance of the observer engine in the client
	 * application.
	 * 
	 * @param databaseConnection
	 *            the connection information to an observer model that exists in
	 *            a relational database
	 * @return a reference to the initialized observer engine
	 */
	public synchronized static IObserverEngineAdmin init(final DatabaseConnection databaseConnection) {
		checkDatabaseConnection(databaseConnection);
		if (observerEngine == null) {
			final IObserverEngineAdmin newEngine = new ObserverEngineImpl(databaseConnection,
					DEFAULT_MAX_HISTORY_ENTRIES, DEFAULT_SLEEP_TIME_BETWEEN_TWO_OBSERVATION_CYCLES_IN_MILLISECONDS,
					DEFAULT_HISTORY_CLEANUP_AFTER_X_OBSERVATION_CYCLES);

			// checkModelContent(databaseConnection, newEngine);
			observerEngine = newEngine;
		}
		return observerEngine;
	}

	/**
	 * Creates the only instance of the observer engine in the client
	 * application.
	 * 
	 * @param observerModelDir
	 *            the local directory where the observer model is stored on hard
	 *            disk
	 * @return a reference to the initialize observer engine
	 */
	public synchronized static IObserverEngineAdmin init(final String observerModelDir) {
		checkModelDir(observerModelDir);
		if (observerEngine == null) {
			final IObserverEngineAdmin newEngine = new ObserverEngineImpl(observerModelDir, DEFAULT_MAX_HISTORY_ENTRIES,
					DEFAULT_SLEEP_TIME_BETWEEN_TWO_OBSERVATION_CYCLES_IN_MILLISECONDS,
					DEFAULT_HISTORY_CLEANUP_AFTER_X_OBSERVATION_CYCLES);

			checkModelContent(observerModelDir, newEngine);
			observerEngine = newEngine;
		}
		return observerEngine;
	}

	private static void checkModelContent(final String observerModelDir, final IObserverEngineAdmin newEngine) {
		if (newEngine.getStates().size() == 0) {
			throw new IllegalArgumentException(
					String.format("No states for observation in model in directory '%s'", observerModelDir));
		}
		if (newEngine.getObservers().size() == 0) {
			throw new IllegalArgumentException(
					String.format("No observers in model in directory '%s'", observerModelDir));
		}
	}

	private static void checkModelDir(final String observerModelDir) {
		if (observerModelDir == null) {
			throw new IllegalArgumentException("observerModelDir must not be null");
		}
	}

	private static void checkDatabaseConnection(final DatabaseConnection databaseConnection) {
		if (databaseConnection == null) {
			throw new IllegalArgumentException("databaseConnection must not be null");
		}
	}

	/**
	 * Get a reference to the observer engine, if it is already initialized.
	 * Otherwise an IllegalStateException is thrown
	 * 
	 * @return a reference to the observer engine, if it is already initialized
	 */
	public synchronized static IObserverEngine get() {
		if (observerEngine == null) {
			throw new IllegalStateException(
					"ObserverEngine.init(...) has to be called before invoking ObserverEngine.get()");
		}
		return observerEngine;
	}

}
