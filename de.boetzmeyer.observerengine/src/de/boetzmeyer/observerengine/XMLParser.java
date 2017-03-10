package de.boetzmeyer.observerengine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class XMLParser {

	private static final String ARRAY_SEPARATOR = ";";

	private XMLParser() {
	}

	public static final Document createDOMTree(final String strFilePath) {
		if (strFilePath != null) {
			return createDOMTree(new File(strFilePath));
		}

		return null;
	}

	public static final Document createDOMTree(File fileXML) {
		Document docXML = null;

		if (fileXML.exists() && fileXML.isFile()) {
			try {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

				if (docBuilderFactory != null) {
					docBuilderFactory.setIgnoringComments(true);
					docBuilderFactory.setValidating(false);
					DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
					docXML = docBuilder.parse(fileXML);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return docXML;
	}

	public static final boolean isDocumentNode(final Node node) {
		if (node != null) {
			return (node.getNodeType() == Node.DOCUMENT_NODE);
		}

		return false;
	}

	public static final boolean isElementNode(final Node node) {
		if (node != null) {
			return (node.getNodeType() == Node.ELEMENT_NODE);
		}

		return false;
	}

	public static final boolean isTextNode(final Node node) {
		if (node != null) {
			return (node.getNodeType() == Node.TEXT_NODE);
		}

		return false;
	}

	public static final boolean isAttributeNode(final Node node) {
		if (node != null) {
			return (node.getNodeType() == Node.ATTRIBUTE_NODE);
		}

		return false;
	}

	public static final String loadString(final Node nodeElement) {
		String strValue = "";

		final NodeList listElementChildren = nodeElement.getChildNodes();

		final int nNodeCount = listElementChildren.getLength();

		for (int i = 0; i < nNodeCount; i++) {
			final Node nodeValue = listElementChildren.item(i);

			if (isTextNode(nodeValue)) {
				strValue = nodeValue.getNodeValue();
				strValue = strValue.trim();
			}

			break;
		}

		if (null == strValue) {
			strValue = "";
		}

		return strValue;
	}

	public static final int loadInteger(final Node nodeElement) {
		int value;

		final String strNode = loadString(nodeElement);

		try {
			value = Integer.parseInt(strNode);
		} catch (Exception e) {
			value = -1;
		}

		return value;
	}

	public static final long loadLong(final Node nodeElement) {
		long value;

		final String strNode = loadString(nodeElement);

		try {
			value = Long.parseLong(strNode);
		} catch (Exception e) {
			value = -1;
		}

		return value;
	}

	public static final float loadFloat(final Node nodeElement) {
		float value;

		final String strNode = loadString(nodeElement);

		try {
			value = Float.parseFloat(strNode);
		} catch (Exception e) {
			value = -1;
		}

		return value;
	}

	public static final double loadDouble(final Node nodeElement) {
		double value;

		final String strNode = loadString(nodeElement);

		try {
			value = Double.parseDouble(strNode);
		} catch (Exception e) {
			value = -1;
		}

		return value;
	}

	public static final boolean loadBoolean(final Node nodeElement) {
		boolean value;

		final String strNode = loadString(nodeElement);

		try {
			value = Boolean.parseBoolean(strNode);
		} catch (Exception e) {
			value = false;
		}

		return value;
	}

	public static final Date loadDate(final Node nodeElement) {
		return new Date(loadLong(nodeElement));
	}

	public static final List<Integer> tokenizeInt(final String strContent) {
		final List<Integer> list = new ArrayList<Integer>();

		final List<String> listStrings = tokenize(strContent, ARRAY_SEPARATOR);

		for (int i = 0, nSize = listStrings.size(); i < nSize; i++) {
			list.add(Integer.parseInt(listStrings.get(i)));
		}

		return list;
	}

	public static final List<Long> tokenizeLong(final String strContent) {
		final List<Long> list = new ArrayList<Long>();

		final List<String> listStrings = tokenize(strContent, ARRAY_SEPARATOR);

		for (int i = 0, nSize = listStrings.size(); i < nSize; i++) {
			list.add(Long.parseLong(listStrings.get(i)));
		}

		return list;
	}

	public static final List<Float> tokenizeFloat(final String strContent) {
		final List<Float> list = new ArrayList<Float>();

		final List<String> listStrings = tokenize(strContent, ARRAY_SEPARATOR);

		for (int i = 0, nSize = listStrings.size(); i < nSize; i++) {
			list.add(Float.parseFloat(listStrings.get(i)));
		}

		return list;
	}

	public static final List<Double> tokenizeDouble(final String strContent) {
		final List<Double> list = new ArrayList<Double>();

		final List<String> listStrings = tokenize(strContent, ARRAY_SEPARATOR);

		for (int i = 0, nSize = listStrings.size(); i < nSize; i++) {
			list.add(Double.parseDouble(listStrings.get(i)));
		}

		return list;
	}

	private static final List<String> tokenize(final String strContent, final String strToken) {
		final List<String> list = new ArrayList<String>();

		final StringTokenizer tokenizer = new StringTokenizer(strContent, strToken);

		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken());
		}

		return list;
	}

	public static final List<Long> loadArrayLong(final Node nodeElement) {
		final List<Long> list = new ArrayList<Long>();

		final List<String> listStrings = tokenize(loadString(nodeElement), ARRAY_SEPARATOR);

		for (int i = 0, nSize = listStrings.size(); i < nSize; i++) {
			try {
				list.add(Long.parseLong(listStrings.get(i)));
			} catch (final Exception e) {
				System.out.println(listStrings.get(i) + " is not a valid element of an long-array");
			}
		}

		return list;
	}

	public static final List<Integer> loadArrayInt(final Node nodeElement) {
		final List<Integer> list = new ArrayList<Integer>();

		final List<String> listStrings = tokenize(loadString(nodeElement), ARRAY_SEPARATOR);

		for (int i = 0, nSize = listStrings.size(); i < nSize; i++) {
			try {
				list.add(Integer.parseInt(listStrings.get(i)));
			} catch (final Exception e) {
				System.out.println(listStrings.get(i) + " is not a valid element of an int-array");
			}
		}

		return list;
	}

	public static final List<Float> loadArrayFloat(final Node nodeElement) {
		final List<Float> list = new ArrayList<Float>();

		final List<String> listStrings = tokenize(loadString(nodeElement), ARRAY_SEPARATOR);

		for (int i = 0, nSize = listStrings.size(); i < nSize; i++) {
			try {
				list.add(Float.parseFloat(listStrings.get(i)));
			} catch (final Exception e) {
				System.out.println(listStrings.get(i) + " is not a valid element of an float-array");
			}
		}

		return list;
	}

	public static final List<Double> loadArrayDouble(final Node nodeElement) {
		final List<Double> list = new ArrayList<Double>();

		final List<String> listStrings = tokenize(loadString(nodeElement), ARRAY_SEPARATOR);

		for (int i = 0, nSize = listStrings.size(); i < nSize; i++) {
			try {
				list.add(Double.parseDouble(listStrings.get(i)));
			} catch (final Exception e) {
				System.out.println(listStrings.get(i) + " is not a valid element of an double-array");
			}
		}

		return list;
	}

	public static final String listToString(final List list) {
		final StringBuilder strBuilder = new StringBuilder();

		if (list != null) {
			for (int i = 0, nSize = list.size(); i < nSize; i++) {
				strBuilder.append(list.get(i).toString());

				if (i < (nSize - 1)) {
					strBuilder.append(ARRAY_SEPARATOR);
				}
			}
		}

		return strBuilder.toString();
	}

	public static final boolean writeLine(final int nSemanticLevel, final OutputStreamWriter outStream,
			final String strElementName, String strElementValue, final boolean isElementBegin) {
		return writeLine(nSemanticLevel, outStream, strElementName, strElementValue, isElementBegin, true);
	}

	public static final boolean writeLine(final int nSemanticLevel, final OutputStreamWriter outStream,
			final String strElementName, String strElementValue, final boolean isElementBegin,
			final boolean bEscapeSequence) {
		boolean bLineWritten = false;

		if (bEscapeSequence) {
			strElementValue = escapeSequence(strElementValue);
		}

		final StringBuffer strBuf = new StringBuffer();

		if (outStream != null) {
			if (isElementBegin) {
				strBuf.append("<");
				strBuf.append(strElementName);
				strBuf.append(">");
			} else {
				strBuf.append("</");
				strBuf.append(strElementName);
				strBuf.append(">");
			}
		}

		if (strElementValue != null) {
			strBuf.append(strElementValue);
			strBuf.append("</");
			strBuf.append(strElementName);
			strBuf.append(">");
		}

		try {
			outStream.write(strBuf.toString());
			outStream.write("\r\n");
			bLineWritten = true;
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return bLineWritten;
	}

	public static final String escapeSequence(final String strContent) {
		if (strContent != null) {
			final StringBuffer strBufEscapeSequence = new StringBuffer();

			int nLength = strContent.length();

			for (int i = 0; i < nLength; i++) {
				char character = strContent.charAt(i);

				if ('"' == character) {
					strBufEscapeSequence.append("&#34;");
				} else if ('&' == character) {
					strBufEscapeSequence.append("&#38;");
				} else if ('<' == character) {
					strBufEscapeSequence.append("&#60;");
				} else if ('>' == character) {
					strBufEscapeSequence.append("&#62;");
				}
				// else if ( ' ' == character )
				// {
				// strBufEscapeSequence.append( "&#160;" );
				// }
				else {
					strBufEscapeSequence.append(character);
				}
			}

			return strBufEscapeSequence.toString();
		}

		return null;
	}

	public static final OutputStreamWriter openStreamWriter(String strFilePath) {
		final String XML_VERSION_1_0 = "1.0";
		final String UTF_8 = "UTF-8";

		OutputStreamWriter out = null;

		if ((strFilePath != null) && (strFilePath.length() > 0)) {
			final File fileXML = new File(strFilePath);

			OutputStream bout = null;

			try {
				bout = new BufferedOutputStream(new FileOutputStream(fileXML));

				final Charset charset = Charset.forName(UTF_8);

				out = new OutputStreamWriter(bout, charset);

				XMLParser.writeXMLProlog(XML_VERSION_1_0, UTF_8, out);
			} catch (final Exception e) {
				fileXML.delete();
				e.printStackTrace();
			}
		}

		return out;
	}

	public static final void writeXMLProlog(final String strXMLVersion, final String strEncoding,
			final OutputStreamWriter out) throws Exception {
		if (out != null) {
			out.write("<?xml version=\"");
			out.write(strXMLVersion);
			out.write("\" ");
			out.write("encoding=\"");
			out.write(strEncoding);
			out.write("\"?>\r\n");
		}
	}

	public static final void close(final OutputStreamWriter out) {
		if (out != null) {
			try {
				out.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}
