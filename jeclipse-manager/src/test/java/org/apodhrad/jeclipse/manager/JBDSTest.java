package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JBDSTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Test
	public void testCreatingInstallationFileForJBDS7() throws Exception {
		File target = tempFolder.newFolder();
		String installationFile = JBDS.createInstallationFile(target, "7.0.0.GA", null);

		assertInstallationFile("/test-install-7.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS8() throws Exception {
		File target = tempFolder.newFolder();
		String installationFile = JBDS.createInstallationFile(target, "8.0.0.GA", null);

		assertInstallationFile("/test-install-8.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS9() throws Exception {
		File target = tempFolder.newFolder();
		String installationFile = JBDS.createInstallationFile(target, "9.0.0.GA", null);

		assertInstallationFile("/test-install-9.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS9WithIU() throws Exception {
		File target = tempFolder.newFolder();
		String installationFile = JBDS.createInstallationFile(target, "9.0.0.GA", null,
				"com.jboss.devstudio.integration-stack.fuse.feature.feature.group");

		assertInstallationFile("/test-install-9-iu.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS10() throws Exception {
		File target = tempFolder.newFolder();
		String installationFile = JBDS.createInstallationFile(target, "10.0.0.GA", null);

		assertInstallationFile("/test-install-10.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS10WithIU() throws Exception {
		File target = tempFolder.newFolder();
		String installationFile = JBDS.createInstallationFile(target, "10.0.0.GA", null,
				"com.jboss.devstudio.integration-stack.fuse.feature.feature.group");

		assertInstallationFile("/test-install-10-iu.xml", installationFile);
	}

	private static void assertInstallationFile(String expected, String actual) throws Exception {
		Reader reader = new InputStreamReader(JBDSTest.class.getResourceAsStream(expected));
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		Diff diff = XMLUnit.compareXML(reader, new FileReader(actual));
		DetailedDiff detailedDiff = new DetailedDiff(diff);

		List<?> allDifferences = detailedDiff.getAllDifferences();

		if (allDifferences.size() == 2
				&& allDifferences.get(0).toString().startsWith("Expected text value 'INSTALL_PATH'")
				&& allDifferences.get(1).toString().startsWith("Expected text value 'JRE_LOCATION'")) {
			return;
		}

		Assert.fail(detailedDiff.toString());
	}
}
