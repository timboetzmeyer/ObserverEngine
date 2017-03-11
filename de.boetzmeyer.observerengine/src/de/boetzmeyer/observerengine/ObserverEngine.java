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
	private static IObserverEngineAdmin observerEngine;

	private ObserverEngine() {
	}

	/**
	 * Creates the only instance of the observer engine in the client
	 * application.
	 * 
	 * @param observerModelDir
	 *            the local directory where the observer model is stored on
	 *            hard disk
	 * @return a reference to the initialize observer engine
	 */
	public synchronized static IObserverEngineAdmin init(final String observerModelDir) {
		checkModelDir(observerModelDir);
		if (observerEngine == null) {
			observerEngine = new ObserverEngineImpl(observerModelDir, 5, 1000, 60);
		}
		return observerEngine;
	}

	private static void checkModelDir(final String observerModelDir) {
		if (observerModelDir == null) {
			throw new IllegalArgumentException("observerModelDir must not be null");
		}
	}

	public synchronized static IObserverEngine get() {
		if (observerEngine == null) {
			throw new IllegalStateException(
					"ObserverEngine.init(...) has to be called before invoking ObserverEngine.get()");
		}
		return observerEngine;
	}

}
