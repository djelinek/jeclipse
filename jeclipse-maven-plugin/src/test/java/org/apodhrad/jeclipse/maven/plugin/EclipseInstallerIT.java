package org.apodhrad.jeclipse.maven.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apodhrad.jeclipse.manager.Bundle;
import org.apodhrad.jeclipse.manager.Eclipse;
import org.junit.Test;

public class EclipseInstallerIT extends BetterAbstractMojoTestCase {

	@Test
	public void testInstallEclipseJEELunaSR2Test() throws Exception {
		File pom = getTestFile("src/test/resources/install-eclipse-jee-luna-SR2-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();

		Eclipse eclipse = new Eclipse(new File(pom.getParentFile(), "target/eclipse"));
		assertContainsBundle(eclipse.getPlugins(), "org.eclipse.equinox.launcher", "1.3.0.v20140415-2008");
	}

	@Test
	public void testInstallEclipseJEEMars1Test() throws Exception {
		File pom = getTestFile("src/test/resources/install-eclipse-jee-mars-1-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();

		Eclipse eclipse = new Eclipse(new File(pom.getParentFile(), "target/eclipse"));
		assertContainsBundle(eclipse.getPlugins(), "org.eclipse.equinox.launcher", "1.3.100.v20150511-1540");
	}

	@Test
	public void testInstallEclipseJEENeon3Test() throws Exception {
		File pom = getTestFile("src/test/resources/install-eclipse-jee-neon-3-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();

		Eclipse eclipse = new Eclipse(new File(pom.getParentFile(), "target/eclipse"));
		assertContainsBundle(eclipse.getPlugins(), "org.eclipse.equinox.launcher", "1.3.201.v20161025-1711");
	}

	@Test
	public void testInstallEclipseJEEOxygen3aTest() throws Exception {
		File pom = getTestFile("src/test/resources/install-eclipse-jee-oxygen-3a-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();

		Eclipse eclipse = new Eclipse(new File(pom.getParentFile(), "target/eclipse"));
		assertContainsBundle(eclipse.getPlugins(), "org.eclipse.equinox.launcher", "1.4.0.v20161219-1356");
	}

	@Test
	public void testInstallEclipseJEEPhotonRTest() throws Exception {
		File pom = getTestFile("src/test/resources/install-eclipse-jee-photon-R-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();

		Eclipse eclipse = new Eclipse(new File(pom.getParentFile(), "target/eclipse"));
		assertContainsBundle(eclipse.getPlugins(), "org.eclipse.equinox.launcher", "1.5.0.v20180512-1130");
	}

	private static void assertContainsBundle(List<Bundle> bundles, String expectedName, String expectedVersion) {
		Bundle bundle = new Bundle(expectedName, expectedVersion);
		assertTrue("The list " + bundles + " doesn't contain bundle " + bundle, bundles.contains(bundle));
	}

	private static void assertContainsBundle(Bundle[] bundles, String expectedName, String expectedVersion) {
		assertContainsBundle(Arrays.asList(bundles), expectedName, expectedVersion);
	}

}
