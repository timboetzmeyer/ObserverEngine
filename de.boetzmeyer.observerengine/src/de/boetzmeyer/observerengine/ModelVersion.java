package de.boetzmeyer.observerengine;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class ModelVersion implements Serializable {

	private static final long serialVersionUID = "MODELVERSION".hashCode();

	// XML tags
	// ========
	private static final String MODEL_VERSION = "ModelVersion";
	private static final String BUILD_NO = "BuildNo";
	private static final String RELEASE_VERSION = "ReleaseVersion";
	private static final String BUILD_DATE = "BuildDate";

	// the server model
	// ================
	public static final ModelVersion LOCAL_MODEL = new ModelVersion();

	// members
	// =======
	private final String m_strBuildNo;
	private final String m_strReleaseVersion;
	private final String m_strBuildDate;
	private final List<Attribute> m_listModelAttributes = new ArrayList<Attribute>();

	private ModelVersion(final String strBuildNo, final String strReleaseVersion, final String strBuildDate,
			final List<Attribute> listModelAttributes) {
		this.m_strBuildNo = strBuildNo;
		this.m_strReleaseVersion = strReleaseVersion;
		this.m_strBuildDate = strBuildDate;
		this.m_listModelAttributes.addAll(listModelAttributes);
	}

	private ModelVersion() {
		this.m_strBuildNo = "6";
		this.m_strReleaseVersion = "2.0.0";
		this.m_strBuildDate = "08.03.17 19:31";

		this.m_listModelAttributes.add(Attribute.OBSERVER_PRIMARYKEY);
		this.m_listModelAttributes.add(Attribute.OBSERVER_SERVERREPLICATIONVERSION);
		this.m_listModelAttributes.add(Attribute.OBSERVER_STATE);
		this.m_listModelAttributes.add(Attribute.OBSERVER_STATEGROUP);
		this.m_listModelAttributes.add(Attribute.OBSERVER_ACTIONCLASS);
		this.m_listModelAttributes.add(Attribute.OBSERVER_DESCRIPTION);
		this.m_listModelAttributes.add(Attribute.OBSERVERLINK_PRIMARYKEY);
		this.m_listModelAttributes.add(Attribute.OBSERVERLINK_SERVERREPLICATIONVERSION);
		this.m_listModelAttributes.add(Attribute.OBSERVERLINK_SOURCE);
		this.m_listModelAttributes.add(Attribute.OBSERVERLINK_DESTINATION);
		this.m_listModelAttributes.add(Attribute.STATE_PRIMARYKEY);
		this.m_listModelAttributes.add(Attribute.STATE_SERVERREPLICATIONVERSION);
		this.m_listModelAttributes.add(Attribute.STATE_STATENAME);
		this.m_listModelAttributes.add(Attribute.STATE_DEFAULTVALUE);
		this.m_listModelAttributes.add(Attribute.STATE_STATETYPE);
		this.m_listModelAttributes.add(Attribute.STATETYPE_PRIMARYKEY);
		this.m_listModelAttributes.add(Attribute.STATETYPE_SERVERREPLICATIONVERSION);
		this.m_listModelAttributes.add(Attribute.STATETYPE_TYPENAME);
		this.m_listModelAttributes.add(Attribute.STATETYPE_DESCRIPTION);
		this.m_listModelAttributes.add(Attribute.STATEGROUP_PRIMARYKEY);
		this.m_listModelAttributes.add(Attribute.STATEGROUP_SERVERREPLICATIONVERSION);
		this.m_listModelAttributes.add(Attribute.STATEGROUP_GROUPNAME);
		this.m_listModelAttributes.add(Attribute.STATEGROUP_DESCRIPTION);
		this.m_listModelAttributes.add(Attribute.STATECHANGE_PRIMARYKEY);
		this.m_listModelAttributes.add(Attribute.STATECHANGE_SERVERREPLICATIONVERSION);
		this.m_listModelAttributes.add(Attribute.STATECHANGE_STATE);
		this.m_listModelAttributes.add(Attribute.STATECHANGE_STATEVALUE);
		this.m_listModelAttributes.add(Attribute.STATECHANGE_CHANGETIME);
		this.m_listModelAttributes.add(Attribute.STATEGROUPLINK_PRIMARYKEY);
		this.m_listModelAttributes.add(Attribute.STATEGROUPLINK_SERVERREPLICATIONVERSION);
		this.m_listModelAttributes.add(Attribute.STATEGROUPLINK_SOURCE);
		this.m_listModelAttributes.add(Attribute.STATEGROUPLINK_DESTINATION);
		this.m_listModelAttributes.add(Attribute.NOTIFICATIONSCOPE_PRIMARYKEY);
		this.m_listModelAttributes.add(Attribute.NOTIFICATIONSCOPE_SERVERREPLICATIONVERSION);
		this.m_listModelAttributes.add(Attribute.NOTIFICATIONSCOPE_OBSERVER);
		this.m_listModelAttributes.add(Attribute.NOTIFICATIONSCOPE_MODULE);
		this.m_listModelAttributes.add(Attribute.MODULE_PRIMARYKEY);
		this.m_listModelAttributes.add(Attribute.MODULE_SERVERREPLICATIONVERSION);
		this.m_listModelAttributes.add(Attribute.MODULE_MODULENAME);
		this.m_listModelAttributes.add(Attribute.MODULE_DESCRIPTION);
	}

	public final String getBuildNo() {
		return this.m_strBuildNo;
	}

	public final String getBuildDate() {
		return this.m_strBuildDate;
	}

	public final String getReleaseVersion() {
		return this.m_strReleaseVersion;
	}

	public final boolean isCompatibleWith(final ModelVersion destinationModel) {
		return ModelVersion.isCompatible(this, destinationModel);
	}

	public static final boolean isCompatible(final ModelVersion sourceModel, final ModelVersion destinationModel) {
		boolean bCompatible = false;

		if ((sourceModel != null) && (destinationModel != null)) {
			final Iterator<Attribute> it = sourceModel.m_listModelAttributes.iterator();

			bCompatible = true;

			int i = 1;

			while (it.hasNext()) {
				final Attribute attributeSource = it.next();

				bCompatible = destinationModel.existsCompatibleAttribute(attributeSource);

				if (bCompatible) {
					System.out.println(
							i + " COMPATIBLE  [" + attributeSource.getKey() + " - " + attributeSource.getType() + "]");
				} else {
					System.out.println(i + " INCOMPATIBLE  [" + attributeSource.getKey() + " - "
							+ attributeSource.getType() + "]");
				}

				i++;
			}
		}

		return bCompatible;
	}

	private boolean canConvertWithoutLoss(final Attribute sourceAttribute, final Attribute destinationAttribute) {
		boolean bConvertible = false;

		if ((destinationAttribute != null) && (sourceAttribute != null)) {
			if (destinationAttribute.isTypeLong()) {
				bConvertible = sourceAttribute.isTypeLong() || sourceAttribute.isTypeDate()
						|| sourceAttribute.isTypeInt();
			} else if (destinationAttribute.isTypeInt()) {
				bConvertible = sourceAttribute.isTypeInt();
			} else if (destinationAttribute.isTypeBoolean()) {
				bConvertible = sourceAttribute.isTypeBoolean();
			} else if (destinationAttribute.isTypeDouble()) {
				bConvertible = sourceAttribute.isTypeDouble() || sourceAttribute.isTypeLong()
						|| sourceAttribute.isTypeFloat() || sourceAttribute.isTypeInt();
			} else if (destinationAttribute.isTypeFloat()) {
				bConvertible = sourceAttribute.isTypeFloat() || sourceAttribute.isTypeInt();
			} else if (destinationAttribute.isTypeString()) {
				bConvertible = sourceAttribute.isTypeString() || sourceAttribute.isTypeDouble()
						|| sourceAttribute.isTypeLong() || sourceAttribute.isTypeFloat() || sourceAttribute.isTypeDate()
						|| sourceAttribute.isTypeBoolean() || sourceAttribute.isTypeInt();
			} else if (destinationAttribute.isTypeDate()) {
				bConvertible = sourceAttribute.isTypeDate() || sourceAttribute.isTypeLong()
						|| sourceAttribute.isTypeInt();
			}
		}

		return bConvertible;
	}

	public final boolean existsCompatibleAttribute(final Attribute attribute) {
		boolean bExists = false;

		if (attribute != null) {
			Attribute foundAttribute = null;

			for (int i = 0, nSize = this.m_listModelAttributes.size(); (i < nSize) && (null == foundAttribute); i++) {
				final Attribute nextAttribute = this.m_listModelAttributes.get(i);

				if (attribute.equals(nextAttribute)) {
					foundAttribute = nextAttribute;
				}
			}

			if (foundAttribute != null) {
				bExists = foundAttribute.hasSameType(attribute);

				if (!bExists) {
					bExists = this.canConvertWithoutLoss(attribute, foundAttribute);

					if (!bExists) {
						System.out.println("Attribute [" + attribute.getKey()
								+ "] can not be converted --> destination model with type [" + foundAttribute.getType()
								+ "] instead of type [" + attribute.getType() + "]");
					}
				}
			} else {
				System.out.println("Attribute [" + attribute.getKey() + "] does not exist in the destination model");
			}
		}

		return bExists;
	}

	public final String toString() {
		return this.m_strBuildNo;
	}

	public final boolean equals(final Object obj) {
		if (obj instanceof ModelVersion) {
			return this.m_strBuildNo.equalsIgnoreCase(((ModelVersion) obj).m_strBuildNo);
		}

		return false;
	}

	public final int hashCode() {
		return this.m_strBuildNo.toUpperCase().hashCode();
	}

	/**
	 * loads the model version of the database
	 */
	public static final ModelVersion load(final String strFilePath) {
		ModelVersion modelVersion = null;

		final Document doc = XMLParser.createDOMTree(strFilePath);

		if (doc != null) {
			// load model version
			// ==================
			final NodeList nodeListModelVersion = doc.getElementsByTagName(MODEL_VERSION);

			for (int i = 0, nSize = nodeListModelVersion.getLength(); (i < nSize) && (null == modelVersion); ++i) {
				modelVersion = ModelVersion.load(nodeListModelVersion.item(i));
			}
		}

		return modelVersion;
	}

	public static final ModelVersion load(final Node node) {
		ModelVersion modelVersion = null;

		String strBuildNo = "";
		String strReleaseVersion = "";
		String strBuildDate = "";
		final List<Attribute> listModelAttributes = new ArrayList<Attribute>();

		if (node != null) {
			final NodeList listElementChildren = node.getChildNodes();

			final int nNodeCount = listElementChildren.getLength();

			for (int i = 0; i < nNodeCount; i++) {
				final Node nodeChild = listElementChildren.item(i);

				if (XMLParser.isElementNode(nodeChild)) {
					final String strNodeName = nodeChild.getNodeName();

					if (strNodeName.equals(BUILD_NO)) {
						strBuildNo = XMLParser.loadString(nodeChild);
					} else if (strNodeName.equals(RELEASE_VERSION)) {
						strReleaseVersion = XMLParser.loadString(nodeChild);
					} else if (strNodeName.equals(BUILD_DATE)) {
						strBuildDate = XMLParser.loadString(nodeChild);
					} else if (strNodeName.equals(Attribute.ATTRIBUTE)) {
						final Attribute attribute = Attribute.load(nodeChild);

						if (attribute != null) {
							listModelAttributes.add(attribute);
						}
					}
				}
			}

			modelVersion = new ModelVersion(strBuildNo, strReleaseVersion, strBuildDate, listModelAttributes);
		}

		return modelVersion;
	}

	/**
	 * saves the data model description to a file
	 */
	public final boolean save(final String strFilePath) {
		boolean bSaved = false;

		final OutputStreamWriter out = XMLParser.openStreamWriter(strFilePath);

		bSaved = this.save(out);

		XMLParser.close(out);

		return bSaved;
	}

	public final boolean save(final OutputStreamWriter out) {
		boolean bSaved = false;

		if (out != null) {
			XMLParser.writeLine(0, out, MODEL_VERSION, null, true);

			XMLParser.writeLine(1, out, BUILD_NO, this.m_strBuildNo, true);

			XMLParser.writeLine(1, out, RELEASE_VERSION, this.m_strReleaseVersion, true);

			XMLParser.writeLine(1, out, BUILD_DATE, this.m_strBuildDate, true);

			final Iterator<Attribute> it = this.m_listModelAttributes.iterator();

			while (it.hasNext()) {
				final Attribute nextAttribute = it.next();

				nextAttribute.save(out);
			}

			XMLParser.writeLine(0, out, MODEL_VERSION, null, false);

			bSaved = true;
		}

		return bSaved;
	}

}
