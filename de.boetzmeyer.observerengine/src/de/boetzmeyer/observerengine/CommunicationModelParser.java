package de.boetzmeyer.observerengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class CommunicationModelParser {

	private CommunicationModelParser() {
	}

	public static final List<Observer> extractObserver(final Document doc) {
		final Collection<Observer> coll = new HashSet<Observer>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("Observer");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NodeList nodeListRecords = node.getChildNodes();

					final int nRecordCount = nodeListRecords.getLength();

					for (int j = 0; j < nRecordCount; j++) {
						final Node nodeRecord = nodeListRecords.item(j);

						if (XMLParser.isElementNode(nodeRecord)) {
							if (nodeRecord.getNodeName().equals("RECORD")) {
								final Observer nextObserver = Observer.load(nodeListRecords.item(j), null);

								if (nextObserver != null) {
									coll.add(nextObserver);
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<Observer>(coll);
	}

	public static final List<ObserverLink> extractObserverLink(final Document doc) {
		final Collection<ObserverLink> coll = new HashSet<ObserverLink>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("ObserverLink");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NodeList nodeListRecords = node.getChildNodes();

					final int nRecordCount = nodeListRecords.getLength();

					for (int j = 0; j < nRecordCount; j++) {
						final Node nodeRecord = nodeListRecords.item(j);

						if (XMLParser.isElementNode(nodeRecord)) {
							if (nodeRecord.getNodeName().equals("RECORD")) {
								final ObserverLink nextObserverLink = ObserverLink.load(nodeListRecords.item(j), null);

								if (nextObserverLink != null) {
									coll.add(nextObserverLink);
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<ObserverLink>(coll);
	}

	public static final List<State> extractState(final Document doc) {
		final Collection<State> coll = new HashSet<State>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("State");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NodeList nodeListRecords = node.getChildNodes();

					final int nRecordCount = nodeListRecords.getLength();

					for (int j = 0; j < nRecordCount; j++) {
						final Node nodeRecord = nodeListRecords.item(j);

						if (XMLParser.isElementNode(nodeRecord)) {
							if (nodeRecord.getNodeName().equals("RECORD")) {
								final State nextState = State.load(nodeListRecords.item(j), null);

								if (nextState != null) {
									coll.add(nextState);
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<State>(coll);
	}

	public static final List<StateType> extractStateType(final Document doc) {
		final Collection<StateType> coll = new HashSet<StateType>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("StateType");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NodeList nodeListRecords = node.getChildNodes();

					final int nRecordCount = nodeListRecords.getLength();

					for (int j = 0; j < nRecordCount; j++) {
						final Node nodeRecord = nodeListRecords.item(j);

						if (XMLParser.isElementNode(nodeRecord)) {
							if (nodeRecord.getNodeName().equals("RECORD")) {
								final StateType nextStateType = StateType.load(nodeListRecords.item(j), null);

								if (nextStateType != null) {
									coll.add(nextStateType);
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<StateType>(coll);
	}

	public static final List<StateGroup> extractStateGroup(final Document doc) {
		final Collection<StateGroup> coll = new HashSet<StateGroup>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("StateGroup");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NodeList nodeListRecords = node.getChildNodes();

					final int nRecordCount = nodeListRecords.getLength();

					for (int j = 0; j < nRecordCount; j++) {
						final Node nodeRecord = nodeListRecords.item(j);

						if (XMLParser.isElementNode(nodeRecord)) {
							if (nodeRecord.getNodeName().equals("RECORD")) {
								final StateGroup nextStateGroup = StateGroup.load(nodeListRecords.item(j), null);

								if (nextStateGroup != null) {
									coll.add(nextStateGroup);
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<StateGroup>(coll);
	}

	public static final List<StateChange> extractStateChange(final Document doc) {
		final Collection<StateChange> coll = new HashSet<StateChange>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("StateChange");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NodeList nodeListRecords = node.getChildNodes();

					final int nRecordCount = nodeListRecords.getLength();

					for (int j = 0; j < nRecordCount; j++) {
						final Node nodeRecord = nodeListRecords.item(j);

						if (XMLParser.isElementNode(nodeRecord)) {
							if (nodeRecord.getNodeName().equals("RECORD")) {
								final StateChange nextStateChange = StateChange.load(nodeListRecords.item(j), null);

								if (nextStateChange != null) {
									coll.add(nextStateChange);
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<StateChange>(coll);
	}

	public static final List<StateGroupLink> extractStateGroupLink(final Document doc) {
		final Collection<StateGroupLink> coll = new HashSet<StateGroupLink>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("StateGroupLink");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NodeList nodeListRecords = node.getChildNodes();

					final int nRecordCount = nodeListRecords.getLength();

					for (int j = 0; j < nRecordCount; j++) {
						final Node nodeRecord = nodeListRecords.item(j);

						if (XMLParser.isElementNode(nodeRecord)) {
							if (nodeRecord.getNodeName().equals("RECORD")) {
								final StateGroupLink nextStateGroupLink = StateGroupLink.load(nodeListRecords.item(j),
										null);

								if (nextStateGroupLink != null) {
									coll.add(nextStateGroupLink);
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<StateGroupLink>(coll);
	}

	public static final List<NotificationScope> extractNotificationScope(final Document doc) {
		final Collection<NotificationScope> coll = new HashSet<NotificationScope>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("NotificationScope");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NodeList nodeListRecords = node.getChildNodes();

					final int nRecordCount = nodeListRecords.getLength();

					for (int j = 0; j < nRecordCount; j++) {
						final Node nodeRecord = nodeListRecords.item(j);

						if (XMLParser.isElementNode(nodeRecord)) {
							if (nodeRecord.getNodeName().equals("RECORD")) {
								final NotificationScope nextNotificationScope = NotificationScope
										.load(nodeListRecords.item(j), null);

								if (nextNotificationScope != null) {
									coll.add(nextNotificationScope);
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<NotificationScope>(coll);
	}

	public static final List<Module> extractModule(final Document doc) {
		final Collection<Module> coll = new HashSet<Module>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("Module");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NodeList nodeListRecords = node.getChildNodes();

					final int nRecordCount = nodeListRecords.getLength();

					for (int j = 0; j < nRecordCount; j++) {
						final Node nodeRecord = nodeListRecords.item(j);

						if (XMLParser.isElementNode(nodeRecord)) {
							if (nodeRecord.getNodeName().equals("RECORD")) {
								final Module nextModule = Module.load(nodeListRecords.item(j), null);

								if (nextModule != null) {
									coll.add(nextModule);
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<Module>(coll);
	}

	public static final List<Observer> importObserver(final Document doc) {
		final Collection<Observer> coll = new HashSet<Observer>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("Observer");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final Observer nextObserver = Observer.load(node, null);

					if (nextObserver != null) {
						coll.add(nextObserver);
					}
				}
			}
		}

		return new ArrayList<Observer>(coll);
	}

	public static final List<ObserverLink> importObserverLink(final Document doc) {
		final Collection<ObserverLink> coll = new HashSet<ObserverLink>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("ObserverLink");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final ObserverLink nextObserverLink = ObserverLink.load(node, null);

					if (nextObserverLink != null) {
						coll.add(nextObserverLink);
					}
				}
			}
		}

		return new ArrayList<ObserverLink>(coll);
	}

	public static final List<State> importState(final Document doc) {
		final Collection<State> coll = new HashSet<State>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("State");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final State nextState = State.load(node, null);

					if (nextState != null) {
						coll.add(nextState);
					}
				}
			}
		}

		return new ArrayList<State>(coll);
	}

	public static final List<StateType> importStateType(final Document doc) {
		final Collection<StateType> coll = new HashSet<StateType>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("StateType");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final StateType nextStateType = StateType.load(node, null);

					if (nextStateType != null) {
						coll.add(nextStateType);
					}
				}
			}
		}

		return new ArrayList<StateType>(coll);
	}

	public static final List<StateGroup> importStateGroup(final Document doc) {
		final Collection<StateGroup> coll = new HashSet<StateGroup>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("StateGroup");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final StateGroup nextStateGroup = StateGroup.load(node, null);

					if (nextStateGroup != null) {
						coll.add(nextStateGroup);
					}
				}
			}
		}

		return new ArrayList<StateGroup>(coll);
	}

	public static final List<StateChange> importStateChange(final Document doc) {
		final Collection<StateChange> coll = new HashSet<StateChange>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("StateChange");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final StateChange nextStateChange = StateChange.load(node, null);

					if (nextStateChange != null) {
						coll.add(nextStateChange);
					}
				}
			}
		}

		return new ArrayList<StateChange>(coll);
	}

	public static final List<StateGroupLink> importStateGroupLink(final Document doc) {
		final Collection<StateGroupLink> coll = new HashSet<StateGroupLink>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("StateGroupLink");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final StateGroupLink nextStateGroupLink = StateGroupLink.load(node, null);

					if (nextStateGroupLink != null) {
						coll.add(nextStateGroupLink);
					}
				}
			}
		}

		return new ArrayList<StateGroupLink>(coll);
	}

	public static final List<NotificationScope> importNotificationScope(final Document doc) {
		final Collection<NotificationScope> coll = new HashSet<NotificationScope>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("NotificationScope");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final NotificationScope nextNotificationScope = NotificationScope.load(node, null);

					if (nextNotificationScope != null) {
						coll.add(nextNotificationScope);
					}
				}
			}
		}

		return new ArrayList<NotificationScope>(coll);
	}

	public static final List<Module> importModule(final Document doc) {
		final Collection<Module> coll = new HashSet<Module>();

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("Module");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					final Module nextModule = Module.load(node, null);

					if (nextModule != null) {
						coll.add(nextModule);
					}
				}
			}
		}

		return new ArrayList<Module>(coll);
	}

	public static final int extractCallbackPort(final Document doc) {
		int nCallbackPort = Settings.INVALID_PORT;

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("CallbackPort");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					nCallbackPort = XMLParser.loadInteger(node);
				}
			}
		}

		return nCallbackPort;
	}

	public static final String extractClientName(final Document doc) {
		String strClientName = Settings.LOCALHOST;

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("ClientName");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					strClientName = XMLParser.loadString(node);
				}
			}
		}

		return strClientName;
	}

	public static final String extractUserName(final Document doc) {
		String strUserName = "";

		if (doc != null) {
			final NodeList nodeList = doc.getElementsByTagName("UserName");

			final int nNodeCount = nodeList.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node node = nodeList.item(i);

				if (XMLParser.isElementNode(node)) {
					strUserName = XMLParser.loadString(node);
				}
			}
		}

		return strUserName;
	}

}
