package de.boetzmeyer.observerengine.example;

import de.boetzmeyer.observerengine.IObserverEngineAdmin;
import de.boetzmeyer.observerengine.ObserverEngine;

public class Example {
	private static final String MODEL_DIR = "/Users/timbotzmeyer/Documents/CommunicationModel/";
	
	private static final String SPEED_CAR_1 = "S-543";
	private static final String SPEED_CAR_2 = "S-875";
	private static final String SPEED_CAR_3 = "S-12455";
	private static final String SPEED_CAR_4 = "S-0";
	
	public static void main(String[] args) {
		
		// initialize the engine
		final IObserverEngineAdmin observerEngine = ObserverEngine.init(MODEL_DIR);
		
		// start the engine
		observerEngine.start();
		
		// register some state observers
		observerEngine.addStateChangeListener(SPEED_CAR_1, new CarSpeedObserver(1));
		observerEngine.addStateChangeListener(SPEED_CAR_2, new CarSpeedObserver(2));
		observerEngine.addStateChangeListener(SPEED_CAR_3, new CarSpeedObserver(3));
		observerEngine.addStateChangeListener(SPEED_CAR_4, new CarSpeedObserver(4));
		
		// modify a state
		observerEngine.update(SPEED_CAR_1, "60");
		
		// stop the engine
		observerEngine.stop();
	}

}
