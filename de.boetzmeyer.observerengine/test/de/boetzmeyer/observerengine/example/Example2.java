package de.boetzmeyer.observerengine.example;

import java.io.File;

import de.boetzmeyer.observerengine.IObserverEngineAdmin;
import de.boetzmeyer.observerengine.ObserverEngine;

public class Example2 {
	
	public static void main(String[] args) {
		
		// defines the directory for the observer model file 'CommunicationModel.zip', that has to be located in this directory
		final String userDir = System.getProperty("user.home");
		final String modelDir = String.format("%s%s%s", userDir, Character.toString(File.separatorChar), "exampleObserverEngine");
		
		// initialize the engine
		final IObserverEngineAdmin observerEngine = ObserverEngine.init(modelDir);
		
		// get all states known to the engine
		System.out.println(observerEngine.getStates());
		
		// get all modules known to the engine
		System.out.println(observerEngine.getModules());
		
		// get all observers known to the engine
		System.out.println(observerEngine.getObservers());

	}

}
