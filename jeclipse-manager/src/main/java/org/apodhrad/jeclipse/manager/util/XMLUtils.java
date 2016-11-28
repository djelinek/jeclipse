package org.apodhrad.jeclipse.manager.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author apodhrad
 *
 */
public class XMLUtils {

	public static String getTextByTagName(File xmlFile, String tagName)
			throws IOException, ParserConfigurationException, SAXException {
		return getTextByTagName(xmlFile, tagName, 0);
	}

	public static String getTextByTagName(File xmlFile, String tagName, int index)
			throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);

		NodeList nodeList = doc.getElementsByTagName(tagName);
		return nodeList.item(index).getTextContent();
	}
}
