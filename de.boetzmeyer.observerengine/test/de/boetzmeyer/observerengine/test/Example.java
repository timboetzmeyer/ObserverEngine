package de.boetzmeyer.observerengine.test;

import java.io.File;

import de.boetzmeyer.observerengine.IObserverEngineAdmin;
import de.boetzmeyer.observerengine.ObserverEngine;

public class Example {
	private static final String SPEED_CAR_1 = "car1.speed";
	private static final String SPEED_CAR_2 = "car2.speed";
	private static final String SPEED_CAR_3 = "car3.speed";
	private static final String SPEED_CAR_4 = "car4s.speed";
	
	public static void main(String[] args) {
		final String userDir = System.getProperty("user.dir");
		final String modelDir = String.format("%s%s%s", userDir, Character.toString(File.separatorChar) + "exampleObserverEngine");
		final IObserverEngineAdmin observerEngine = ObserverEngine.init(modelDir);
		observerEngine.addStateChangeListener(SPEED_CAR_1, new CarSpeedObserver(1));
		observerEngine.addStateChangeListener(SPEED_CAR_2, new CarSpeedObserver(2));
		observerEngine.addStateChangeListener(SPEED_CAR_3, new CarSpeedObserver(3));
		observerEngine.addStateChangeListener(SPEED_CAR_4, new CarSpeedObserver(4));
	}

}
