package org.apodhrad.jeclipse.manager.integration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.apodhrad.jeclipse.manager.DevstudioInstaller;
import org.junit.Assert;
import org.junit.Test;

public class DevstudioInstallerIT {

	@Test
	public void testGettingCoreVersion() throws Exception {
		File installerJar = new File("/home/apodhrad/.jdownload-cache/devstudio-10.1.0.GA-installer-standalone.jar");
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);
		Assert.assertEquals("10.1.0.GA", installer.getCoreVersion());
	}
}
