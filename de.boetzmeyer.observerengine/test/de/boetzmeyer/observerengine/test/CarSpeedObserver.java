package de.boetzmeyer.observerengine.test;

import de.boetzmeyer.observerengine.IStateChange;
import de.boetzmeyer.observerengine.IStateObserver;

public class CarSpeedObserver implements IStateObserver {
	private final int carID;

	public CarSpeedObserver(final int carID) {
		this.carID = carID;
	}

	@Override
	public void stateChanged(final IStateChange inStateChange) {
		// TODO Auto-generated method stub

	}

}
