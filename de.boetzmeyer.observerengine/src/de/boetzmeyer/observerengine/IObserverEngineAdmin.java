package de.boetzmeyer.observerengine;

import java.util.Date;
import java.util.List;

public interface IObserverEngineAdmin extends IObserverEngine {

	void start();

	void stop();

	List<IState> getModifiedStates(final Date inLastQuery);
	
	List<IState> getStates();
	
	List<IModule> getModules();
	
	List<IObserver> getStateObservers(final IState inState);
	
	List<IObserver> getStateObservers(final IState inState, final IModule inModule);
	
	List<IObserver> getModuleObservers(final IModule inModule);

}
