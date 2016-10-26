package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author apodhrad
 *
 */
public class JBDSConfigRecord {

	private File file;
	private String installPath;
	private String installGroup;
	private String jreLocation;
	private List<String> installableUnits;
	private List<String> runtimes;

	protected JBDSConfigRecord() {
		installableUnits = new ArrayList<String>();
		runtimes = new ArrayList<String>();
	}

	public static JBDSConfigRecord load(File file)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		return load(new FileInputStream(file));
	}

	public static JBDSConfigRecord load(InputStream input)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		JBDSConfigRecord config = new JBDSConfigRecord();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(input);

		// install path
		NodeList node = doc.getElementsByTagName("installpath");
		config.setInstallPath(node.item(0).getTextContent());
		// install group
		node = doc.getElementsByTagName("installgroup");
		config.setInstallGroup(node.item(0).getTextContent());
		// jre location
		node = doc.getElementsByTagName("jrelocation");
		config.setJreLocation(node.item(0).getTextContent());
		// installable units
		node = doc.getElementsByTagName("ius");
		for (String iu : node.item(0).getTextContent().split(",")) {
			config.addInstallableUnit(iu);
		}

		return config;
	}

	public String getInstallPath() {
		return installPath;
	}

	public void setInstallPath(String installPath) {
		this.installPath = installPath;
	}

	public String getInstallGroup() {
		return installGroup;
	}

	public void setInstallGroup(String installGroup) {
		this.installGroup = installGroup;
	}

	public String getJreLocation() {
		return jreLocation;
	}

	public void setJreLocation(String jreLocation) {
		this.jreLocation = jreLocation;
	}

	public List<String> getInstallableUnits() {
		return installableUnits;
	}

	public void addInstallableUnit(String iu) {
		installableUnits.add(iu);
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return null;
	}
}
