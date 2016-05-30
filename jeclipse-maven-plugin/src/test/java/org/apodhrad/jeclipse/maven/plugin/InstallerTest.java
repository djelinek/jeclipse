package org.apodhrad.jeclipse.maven.plugin;

import static org.junit.Assume.assumeTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class InstallerTest extends BetterAbstractMojoTestCase {

	public static final String ECLIPSE_LUNA_VERSION = "jee-luna-SR2";
	public static final String ECLIPSE_LUNA_PLUGIN = "org.eclipse.platform_4.4.2.v20150204-1700";
	
	public static final String ECLIPSE_MARS_VERSION = "jee-mars-2";
	public static final String ECLIPSE_MARS_PLUGIN = "org.eclipse.platform_4.5.2.v20160212-1500";

	public static final String JBDS_7_PLUGIN = "com.jboss.jbds.product_7.1.1.GA-v20140314-2145-B688.jar";
	public static final String JBDS_8_PLUGIN = "com.jboss.devstudio.core_8.1.0.GA-v20150327-1349-B467.jar";
	public static final String JBDS_9_PLUGIN = "com.jboss.devstudio.core_9.1.0.GA-v20160414-0124-B497.jar";

	private static String TARGET = systemProperty("project.build.directory");

	@Test
	public void testEclipseLunaInstallation() throws Exception {
		System.setProperty("eclipse.version", ECLIPSE_LUNA_VERSION);

		File target = execMaven("install-eclipse-test", "install");
		assertTrue(new File(target, "eclipse/plugins/" + ECLIPSE_LUNA_PLUGIN).exists());
	}

	@Test
	public void testEclipseMarsInstallation() throws Exception {
		System.setProperty("eclipse.version", "jee-mars-2");

		File target = execMaven("install-eclipse-test", "install");
		assertTrue(new File(target, "eclipse/plugins/" + ECLIPSE_MARS_PLUGIN).exists());
	}

	@Test
	public void testJBDS8Installation() throws Exception {
		String jbdsUrl = assumeProperty("jbds8.url");
		System.setProperty("jbds.url", jbdsUrl);

		File target = execMaven("install-jbds-test", "install");
		assertTrue(new File(target, "jbdevstudio/studio/plugins/" + JBDS_8_PLUGIN).exists());
	}
	
	@Test
	public void testJBDS9Installation() throws Exception {
		String jbdsUrl = assumeProperty("jbds9.url");
		System.setProperty("jbds.url", jbdsUrl);

		File target = execMaven("install-jbds-test", "install");
		assertTrue(new File(target, "jbdevstudio/studio/plugins/" + JBDS_9_PLUGIN).exists());
	}

	private File execMaven(String project, String goal) throws Exception {
		File pom = getTestFile(TARGET, "test-classes/" + project + "/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());
		File target = new File(pom.getParent(), "target");
		FileUtils.deleteQuietly(target);
		Installer installer = (Installer) lookupConfiguredMojo(goal, pom);
		assertNotNull(installer);
		installer.execute();
		return target;
	}

	private static String systemProperty(String key) {
		String value = System.getProperty(key);
		assertTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}

	private static String assumeProperty(String key) {
		String value = System.getProperty(key);
		assumeTrue("The system property '" + key + "' is not defined!", value != null && value.length() > 0);
		return value;
	}

}
