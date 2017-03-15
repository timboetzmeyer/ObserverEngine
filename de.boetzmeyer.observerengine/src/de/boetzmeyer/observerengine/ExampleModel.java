package de.boetzmeyer.observerengine;

import java.util.List;

final class ExampleModel {
	private static final String MODEL_DIR = "/Users/timbotzmeyer/Documents/CommunicationModel/";
	private static final int STATE_COUNT = 1200;
	private static final int OBSERVER_PER_STATE = 5;
	
	private ExampleModel() {
	}

	public static void main(String[] args) {
		final CommunicationModel model = CommunicationModel.createEmpty();
		
		final Module moduleA = Module.generate();
		moduleA.setModuleName("Module A");
		moduleA.setDescription("Definition of Module A");
		model.addModule(moduleA);
		
		final Module moduleB = Module.generate();
		moduleB.setModuleName("Module B");
		moduleB.setDescription("Definition of Module B");
		model.addModule(moduleB);
		
		final Module moduleC = Module.generate();
		moduleC.setModuleName("Module C");
		moduleC.setDescription("Definition of Module C");
		model.addModule(moduleC);
		
		final StateType stateType = StateType.generate();
		stateType.setTypeName("Double");
		model.addStateType(stateType);
		
		for (int i = 0; i < STATE_COUNT; i++) {
			final State state = State.generate();
			state.setStateName(String.format("S-%s", Integer.toString(i)));
			state.setDefaultValue(Double.toString(Math.random()));
			state.setStateType(stateType.getPrimaryKey());
			model.addState(state);
			
			for (int j = 0; j < OBSERVER_PER_STATE; j++) {
				final Observer observer = Observer.generate();
				observer.setActionClass(MyStateObserver.class.getName());
				observer.setState(state.getPrimaryKey());
				model.addObserver(observer);
			}
		}

		final List<Observer> observers = model.listObserver();
		int size = observers.size() - (int)(observers.size() * 0.2);
		
		for (int i = 0; i < size; i++) {
			final NotificationScope scope = NotificationScope.generate();
			if (i % 5 == 0) {
				scope.setModule(moduleA.getPrimaryKey());
			} else if (i % 8 == 0) {
				scope.setModule(moduleB.getPrimaryKey());
			} else{
				scope.setModule(moduleC.getPrimaryKey());
			}
			scope.setObserver(observers.get(i).getPrimaryKey());
			model.addNotificationScope(scope);
		}
		
		model.saveAll(MODEL_DIR, true);
	}
	
	private static final class MyStateObserver implements IStateObserver {

		@Override
		public void stateChanged(final IStateChange inStateChange) {
			System.out.println(inStateChange.toString());
		}
		
	}
}
