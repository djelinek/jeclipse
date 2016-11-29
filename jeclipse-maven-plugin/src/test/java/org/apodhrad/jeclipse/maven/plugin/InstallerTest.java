package org.apodhrad.jeclipse.maven.plugin;

import static org.apache.commons.io.FileUtils.ONE_KB;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apodhrad.jdownload.manager.hash.Hash;
import org.apodhrad.jdownload.manager.hash.SHA256Hash;
import org.apodhrad.jeclipse.manager.util.XMLUtils;
import org.apodhrad.jeclipse.maven.plugin.fake.DevstudioInstall;
import org.apodhrad.jeclipse.maven.plugin.fake.EclipseLauncher;
import org.apodhrad.jeclipse.maven.plugin.fake.JarBuilder;
import org.junit.Test;

public class InstallerTest extends BetterAbstractMojoTestCase {

	// @Test
	public void tesGettingHashSha256() throws Exception {
		File pom = getTestFile("src/test/resources/junit/devstudio-sha256.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		assertEquals(new SHA256Hash("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e").toString(),
				installer.getDevstudioHash().toString());
	}

	@Test
	public void testInstallingDevstudioFake() throws Exception {
		File installerFile = new File("/home/apodhrad/Temp/nstaller-fake.jar");
		JarBuilder jarBuilder = new JarBuilder();
		jarBuilder.setMainClass(DevstudioInstall.class);
		jarBuilder.addClass(XMLUtils.class);
		jarBuilder.addClass(JarBuilder.class);
		jarBuilder.addClass(EclipseLauncher.class);
		jarBuilder.addResource("devstudio/plugins/com.jboss.devstudio.core_10.1.0.GA-v20160902-1725-B43", "");
		jarBuilder.build(installerFile);

		System.setProperty("devstudio.url", "file:///" + installerFile.getAbsolutePath());
		System.setProperty("devstudio.md5", md5sum(installerFile));

		File pom = getTestFile("src/test/resources/junit/devstudio-fake.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();
	}

	public static String md5sum(File file) throws Exception {
		byte[] BUFFER = new byte[4 * (int) ONE_KB];
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			int read;
			while ((read = is.read(BUFFER)) > 0) {
				messageDigest.update(BUFFER, 0, read);
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		byte[] hash = messageDigest.digest();
		return Hash.convertToHex(hash);
	}

}
