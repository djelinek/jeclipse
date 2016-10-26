package org.apodhrad.jeclipse.maven.plugin;

import java.io.File;

import org.junit.Test;

public class InstallerTest extends BetterAbstractMojoTestCase {

	@Test
	public void testinstallJBDS2Test() throws Exception {
		File pom = getTestFile("src/test/resources/install-jbdsis-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
	}
}
