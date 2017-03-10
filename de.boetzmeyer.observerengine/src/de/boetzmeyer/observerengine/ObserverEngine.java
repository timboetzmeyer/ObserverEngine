package de.boetzmeyer.observerengine;

public final class ObserverEngine {
	private static IObserverEngineAdmin observerEngine;
	
	private ObserverEngine() {
	}

	public synchronized static IObserverEngineAdmin init() {
		if (observerEngine == null) {
			observerEngine = new ObserverEngineImpl(5, 1000, 60);
		}
		return observerEngine;
	}
	
	public synchronized static IObserverEngine get() {
		if (observerEngine == null) {
			throw new IllegalStateException("ObserverEngine.init(...) has to be called before invoking ObserverEngine.get()");
		}
		return observerEngine;
	}

}
