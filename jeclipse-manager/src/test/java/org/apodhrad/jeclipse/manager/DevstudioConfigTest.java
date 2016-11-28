package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

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
	public static final String DEVSTUDIO_10_1_0_GA = "devstudio-10.1.0.GA-installer-standalone.jar";
	public static final String DEVSTUDIO_10_1_0_GA_EAP = "devstudio-10.1.0.GA-installer-eap.jar";
	public static final String DEVSTUDIO_10_2_0_AM2 = "devstudio-10.2.0.AM2-v20161014-1657-B6205-installer-standalone.jar";

	public static final String JBDSIS_9_0_3_GA = "devstudio-integration-stack-9.0.3.GA-standalone-installer.jar";
	public static final String DEVSTUDIOIS_10_0_0_GA = "devstudio-integration-stack-10.0.0.GA-standalone-installer.jar";
	public static final String DEVSTUDIOIS_10_0_0_GA_RT = "devstudio-integration-stack-10.0.0.GA-runtime-installer.jar";

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private File target;

	@Before
	public void createTargetDir() throws IOException {
		target = tempFolder.newFolder();
	}

	@Test
	public void testCreatingInstallationFileFromNameForJBDS7() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_7_1_1_GA);
		assertInstallationFile("/test-install-7.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromNameForJBDS8() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_8_1_0_GA);
		assertInstallationFile("/test-install-8.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromNameForJBDS9() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDS_9_1_0_GA);
		assertInstallationFile("/test-install-9.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromNameForJBDSIS9WithIU() throws Exception {
		DevstudioConfig config = devstudioConfig(JBDSIS_9_0_3_GA);
		config.addFeature("com.jboss.devstudio.integration-stack.fuse.feature.feature.group");
		assertInstallationFile("/test-install-9-iu.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromNameForDevstudio10() throws Exception {
		DevstudioConfig config = devstudioConfig(DEVSTUDIO_10_1_0_GA);
		assertInstallationFile("/test-install-10.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromNameForDevstudio10WithEAP() throws Exception {
		DevstudioConfig config = devstudioConfig(DEVSTUDIO_10_1_0_GA_EAP);
		assertInstallationFile("/test-install-10-eap.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromNameForDevstudioIS10WithIU() throws Exception {
		DevstudioConfig config = devstudioConfig(DEVSTUDIOIS_10_0_0_GA);
		config.addFeature("com.jboss.devstudio.integration-stack.fuse.feature.feature.group");
		assertInstallationFile("/test-install-10-iu.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromNameForDevstudioIS10WithFuse() throws Exception {
		DevstudioConfig config = devstudioConfig(DEVSTUDIOIS_10_0_0_GA_RT);
		config.addRuntime("devstudio-is/runtime/jboss-fuse-karaf-6.3.0.redhat-187.zip");
		assertInstallationFile("/test-install-10-fuse.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromInstallerForJBDS7() throws Exception {
		File installerJar = prepareDevstudioInstaller7(tempFolder.newFile("installer.jar"),
				"7.1.1.GA-v20140314-2145-B688");
		DevstudioConfig config = devstudioConfig(installerJar);
		assertInstallationFile("/test-install-7.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromInstallerForJBDS8() throws Exception {
		File installerJar = prepareDevstudioInstaller8(tempFolder.newFile("installer.jar"),
				"8.1.0.GA-v20150327-1349-B467");
		DevstudioConfig config = devstudioConfig(installerJar);
		assertInstallationFile("/test-install-8.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromInstallerForJBDS9() throws Exception {
		File installerJar = prepareDevstudioInstaller9(tempFolder.newFile("installer.jar"),
				"9.1.0.GA-v20160414-0124-B497");
		DevstudioConfig config = devstudioConfig(installerJar);
		assertInstallationFile("/test-install-9.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromInstallerForJBDSIS9WithIU() throws Exception {
		File installerJar = prepareDevstudioInstaller9(tempFolder.newFile("installer-is.jar"),
				"9.1.0.GA-v20160414-0124-B497");
		DevstudioConfig config = devstudioConfig(installerJar);
		config.addFeature("com.jboss.devstudio.integration-stack.fuse.feature.feature.group");
		assertInstallationFile("/test-install-9-iu.xml", config.toFile(target));
	}

	@Test
	public void testCreatingInstallationFileFromInstallerForDevstudioIS10WithFuseAndIU() throws Exception {
		File installerJar = prepareDevstudioInstaller10(tempFolder.newFile("installer-is.jar"),
				"10.1.0.GA-v20160902-1725-B43");
		DevstudioConfig config = devstudioConfig(installerJar);
		config.addFeature("com.jboss.devstudio.integration-stack.fuse.feature.feature.group");
		config.addRuntime("devstudio-is/runtime/jboss-fuse-karaf-6.3.0.redhat-187.zip");
		assertInstallationFile("/test-install-10-fuse-iu.xml", config.toFile(target));
	}

	private static DevstudioConfig devstudioConfig(String installerName) {
		DevstudioConfig config = DevstudioConfig.createFromInstallerName(installerName);
		config.setTarget("INSTALL_PATH");
		config.setJre("JRE_LOCATION");
		return config;
	}

	private static DevstudioConfig devstudioConfig(File installerJar) throws IOException {
		DevstudioConfig config = DevstudioConfig.createFromInstallerFile(installerJar);
		config.setTarget("INSTALL_PATH");
		config.setJre("JRE_LOCATION");
		return config;
	}

	private File prepareDevstudioInstaller7(File file, String fullVersion) throws IOException {
		JarOutputStream out = new JarOutputStream(new FileOutputStream(file));
		if (fullVersion != null) {
			out.putNextEntry(new ZipEntry("jbds/plugins/com.jboss.jbds.product_" + fullVersion + ".jar"));
		}
		out.flush();
		out.close();
		return file;
	}

	private File prepareDevstudioInstaller8(File file, String fullVersion) throws IOException {
		JarOutputStream out = new JarOutputStream(new FileOutputStream(file));
		if (fullVersion != null) {
			out.putNextEntry(new ZipEntry("jbds/plugins/com.jboss.devstudio.core_" + fullVersion + ".jar"));
		}
		out.flush();
		out.close();
		return file;
	}

	private File prepareDevstudioInstaller9(File file, String fullVersion) throws IOException {
		JarOutputStream out = new JarOutputStream(new FileOutputStream(file));
		if (fullVersion != null) {
			out.putNextEntry(new ZipEntry("jbds/plugins/com.jboss.devstudio.core_" + fullVersion + ".jar"));
		}
		out.putNextEntry(new ZipEntry("res/DevstudioFeaturesSpec.json"));
		out.write(
				"[{\"id\": \"com.jboss.devstudio.core.package\", \"path\": \"jbds\"}, {\"id\": \"org.testng.eclipse.feature.group\", \"path\": \"jbds\"}]"
						.getBytes());
		if (file.getName().contains("is")) {
			out.putNextEntry(new ZipEntry("res/AdditionalFeaturesSpec.json"));
			out.write(
					"[{\"id\": \"com.jboss.devstudio.integration-stack.fuse.feature.feature.group\", \"path\": \"jbdsis\"}]"
							.getBytes());
		}
		out.flush();
		out.close();
		return file;
	}

	private File prepareDevstudioInstaller10(File file, String fullVersion) throws IOException {
		JarOutputStream out = new JarOutputStream(new FileOutputStream(file));
		if (fullVersion != null) {
			out.putNextEntry(new ZipEntry("devstudio/plugins/com.jboss.devstudio.core_" + fullVersion + ".jar"));
		}
		out.putNextEntry(new ZipEntry("res/DevstudioFeaturesSpec.json"));
		out.write(
				"[{\"id\": \"com.jboss.devstudio.core.package\", \"path\": \"devstudio\"}, {\"id\": \"org.testng.eclipse.feature.group\", \"path\": \"devstudio\"}]"
						.getBytes());
		if (file.getName().contains("is")) {
			out.putNextEntry(new ZipEntry("res/AdditionalFeaturesSpec.json"));
			out.write(
					"[{\"id\": \"com.jboss.devstudio.integration-stack.fuse.feature.feature.group\", \"path\": \"seconddevstudi-is\"}]"
							.getBytes());
		}
		out.flush();
		out.close();
		return file;
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
