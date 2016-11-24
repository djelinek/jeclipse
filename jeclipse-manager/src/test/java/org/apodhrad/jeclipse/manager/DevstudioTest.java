package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DevstudioTest {

	public static final String JBDS_7_1_1_GA = "jbdevstudio-product-universal-7.1.1.GA-v20140314-2145-B688.jar";
	public static final String JBDS_8_1_0_GA = "jboss-devstudio-8.1.0.GA-installer-standalone.jar";
	public static final String JBDS_9_1_0_GA = "jboss-devstudio-9.1.0.GA-installer-standalone.jar";
	public static final String JBDS_10_1_0_GA = "devstudio-10.1.0.GA-installer-standalone.jar";
	public static final String JBDS_10_1_0_GA_EAP = "devstudio-10.1.0.GA-installer-eap.jar";
	public static final String JBDS_10_2_0_AM2 = "devstudio-10.2.0.AM2-v20161014-1657-B6205-installer-standalone.jar";
	public static final String JBDSIS_10_0_0_CR1 = "devstudio-integration-stack-10.0.0.CR1-standalone-installer.jar";
	public static final String JBDSIS_10_0_0_CR1_RT = "devstudio-integration-stack-rt-10.0.0.CR1-standalone-installer.jar";

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testCreatingInstallationFileForJBDS7() throws Exception {
		File target = tempFolder.newFolder();
		File installerFile = tempFolder.newFile(JBDS_7_1_1_GA);
		String installationFile = Devstudio.createInstallationFile(target, installerFile, null);

		assertInstallationFile("/test-install-7.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS8() throws Exception {
		File target = tempFolder.newFolder();
		File installerFile = tempFolder.newFile(JBDS_8_1_0_GA);
		String installationFile = Devstudio.createInstallationFile(target, installerFile, null);

		assertInstallationFile("/test-install-8.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS9() throws Exception {
		File target = tempFolder.newFolder();
		File installerFile = tempFolder.newFile(JBDS_9_1_0_GA);
		String installationFile = Devstudio.createInstallationFile(target, installerFile, null);

		assertInstallationFile("/test-install-9.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS9WithIU() throws Exception {
		File target = tempFolder.newFolder();
		File installerFile = tempFolder.newFile(JBDS_9_1_0_GA);
		String installationFile = Devstudio.createInstallationFile(target, installerFile, null,
				"com.jboss.devstudio.integration-stack.fuse.feature.feature.group");

		assertInstallationFile("/test-install-9-iu.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS10() throws Exception {
		File target = tempFolder.newFolder();
		File installerFile = tempFolder.newFile(JBDS_10_1_0_GA);
		String installationFile = Devstudio.createInstallationFile(target, installerFile, null);

		assertInstallationFile("/test-install-10.xml", installationFile);
	}

	@Test
	public void testCreatingInstallationFileForJBDS10WithEAP() throws Exception {
		File target = tempFolder.newFolder();
		File installerFile = tempFolder.newFile(JBDS_10_1_0_GA_EAP);

		DevstudioConfig config = new DevstudioConfig();
		config.setTarget(target);
		config.setInstallerJarFile(installerFile);

		String installationFile = Devstudio.createInstallationFile(config);

		assertInstallationFile("/test-install-10-eap.xml", installationFile);
	}


	@Test
	public void testCreatingInstallationFileForJBDS10WithIU() throws Exception {
		File target = tempFolder.newFolder();
		File installerFile = tempFolder.newFile(JBDSIS_10_0_0_CR1);
		String installationFile = Devstudio.createInstallationFile(target, installerFile, null,
				"com.jboss.devstudio.integration-stack.fuse.feature.feature.group");

		assertInstallationFile("/test-install-10-iu.xml", installationFile);
	}
	
	@Test
	public void testCreatingInstallationFileForJBDSIS10WithFuse() throws Exception {
		File target = tempFolder.newFolder();
		File installerFile = tempFolder.newFile(JBDSIS_10_0_0_CR1_RT);

		DevstudioConfig config = new DevstudioConfig();
		config.setTarget(target);
		config.setInstallerJarFile(installerFile);
		config.addRuntime("devstudio-is/runtime/jboss-fuse-karaf-6.3.0.redhat-187.zip");

		String installationFile = Devstudio.createInstallationFile(config);

		assertInstallationFile("/test-install-10-fuse.xml", installationFile);
	}

	private static void assertInstallationFile(String expected, String actual) throws Exception {
		Reader reader = new InputStreamReader(DevstudioTest.class.getResourceAsStream(expected));
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

		Assert.assertEquals(IOUtils.toString(DevstudioTest.class.getResourceAsStream(expected), "UTF-8"),
				FileUtils.readFileToString(new File(actual)));
	}

}
