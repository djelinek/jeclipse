package org.apodhrad.jeclipse.maven.plugin;

import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apodhrad.jeclipse.manager.Bundle;
import org.junit.BeforeClass;
import org.junit.Test;

public class JBDSInstallerIT extends BetterAbstractMojoTestCase {

	@BeforeClass
	public static void checkSystemProperties() {
		assumeSystemProperty("jeclipse.test.jbds.url");
		assumeSystemProperty("jeclipse.test.jbdsis.url");
	}

	@Test
	public void testinstallJBDSTest() throws Exception {
		assumeSystemProperty("jeclipse.test.jbds.url");
		File pom = getTestFile("src/test/resources/install-jbds-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();
	}

	@Test
	public void testinstallJBDSISTest() throws Exception {
		assumeSystemProperty("jeclipse.test.jbdsis.url");
		File pom = getTestFile("src/test/resources/install-jbdsis-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();
	}

	static public String systemProperty(String key) {
		String value = System.getProperty(key);
		assertTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}

	private static String assumeSystemProperty(String key) {
		String value = System.getProperty(key);
		assumeTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}

	private static void assertContainsBundle(List<Bundle> bundles, String expectedName, String expectedVersion) {
		Bundle bundle = new Bundle(expectedName, expectedVersion);
		assertTrue("The list " + bundles + " doesn't contain bundle " + bundle, bundles.contains(bundle));
	}

	private static void assertContainsBundle(Bundle[] bundles, String expectedName, String expectedVersion) {
		assertContainsBundle(Arrays.asList(bundles), expectedName, expectedVersion);
	}

}
