package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DevstudioConfigTest {

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

	private File target;

	@Before
	public void createTargetDir() throws IOException {
		target = tempFolder.newFolder();
	}

	@Test
	public void testCreatingInstallationFileForJBDS7() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_7_1_1_GA);
		assertInstallationFile("/test-install-7.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileForJBDS8() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_8_1_0_GA);
		assertInstallationFile("/test-install-8.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileForJBDS9() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_9_1_0_GA);
		assertInstallationFile("/test-install-9.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileForJBDS9WithIU() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_9_1_0_GA);
		config.addFeature("com.jboss.devstudio.integration-stack.fuse.feature.feature.group");
		assertInstallationFile("/test-install-9-iu.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileForJBDS10() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_10_1_0_GA);
		assertInstallationFile("/test-install-10.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileForJBDS10WithEAP() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_10_1_0_GA_EAP);
		assertInstallationFile("/test-install-10-eap.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileForJBDS10WithIU() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_10_1_0_GA);
		config.addFeature("com.jboss.devstudio.integration-stack.fuse.feature.feature.group");
		assertInstallationFile("/test-install-10-iu.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileForJBDSIS10WithFuse() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDSIS_10_0_0_CR1_RT);
		config.addRuntime("devstudio-is/runtime/jboss-fuse-karaf-6.3.0.redhat-187.zip");
		assertInstallationFile("/test-install-10-fuse.xml", config.toFile(target));
	}

	private static DevstudioConfig devstudioConfig(String installerName) {
		DevstudioConfig config = DevstudioConfig.createFromInstallerName(installerName);
		config.setTarget("INSTALL_PATH");
		config.setJre("JRE_LOCATION");
		return config;
	}

	private static void assertInstallationFile(String expected, File actual) throws Exception {
		assertInstallationFile(expected, actual.getAbsolutePath());
	}

	private static void assertInstallationFile(String expected, String actual) throws Exception {
		Reader reader = new InputStreamReader(DevstudioTest.class.getResourceAsStream(expected));
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		Diff diff = XMLUnit.compareXML(reader, new FileReader(actual));

		if (diff.identical()) {
			return;
		}

		Assert.assertEquals(IOUtils.toString(DevstudioTest.class.getResourceAsStream(expected), "UTF-8"),
				FileUtils.readFileToString(new File(actual)));
	}

}
