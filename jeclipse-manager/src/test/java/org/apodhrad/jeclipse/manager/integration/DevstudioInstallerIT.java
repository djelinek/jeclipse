package org.apodhrad.jeclipse.manager.integration;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apodhrad.jeclipse.manager.DevstudioInstaller;
import org.junit.Test;

public class DevstudioInstallerIT {

	@Test
	public void testGettingCoreVersion() throws Exception {
		File installerJar = new File("/home/apodhrad/.jdownload-cache/devstudio-10.1.0.GA-installer-standalone.jar");
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);
		assertEquals("10.1.0.GA", installer.getCoreVersion());
	}
}
