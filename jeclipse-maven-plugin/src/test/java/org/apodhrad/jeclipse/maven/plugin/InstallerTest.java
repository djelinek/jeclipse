package org.apodhrad.jeclipse.maven.plugin;

import static org.apache.commons.io.FileUtils.ONE_KB;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apodhrad.jdownload.manager.hash.Hash;
import org.apodhrad.jeclipse.manager.Devstudio;
import org.apodhrad.jeclipse.manager.util.XMLUtils;
import org.apodhrad.jeclipse.maven.plugin.fake.DevstudioInstall;
import org.apodhrad.jeclipse.maven.plugin.fake.EclipseLauncher;
import org.apodhrad.jeclipse.maven.plugin.fake.JarBuilder;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * 
 * @author apodhrad
 *
 */
public class InstallerTest extends BetterAbstractMojoTestCase {

	private TemporaryFolder tempFolder = new TemporaryFolder();

	private File installerFile;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tempFolder.create();
		prepareDevstudioFakeInstaller();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		tempFolder.delete();
	}

	@Test
	public void testInstallingDevstudioFake() throws Exception {
		System.setProperty("devstudio.url", "file:///" + installerFile.getAbsolutePath());
		System.setProperty("devstudio.md5", md5sum(installerFile));

		File pomFile = prepareMavenProject("/junit/devstudio-fake.xml");
		executeMavenProject(pomFile);

		Devstudio devstudio = new Devstudio(new File(pomFile.getParentFile(), "target"));
		assertEquals("10.1.0.GA-v20160902-1725-B43", devstudio.getCoreVersion());

	}

	@Test
	public void testInstallingDevstudioFakeWithWrongHash() throws Exception {
		System.setProperty("devstudio.url", "file:///" + installerFile.getAbsolutePath());
		System.setProperty("devstudio.md5", "512d933bd5ed2bad991c2a7e5134e6d5");

		File pomFile = prepareMavenProject("/junit/devstudio-fake.xml");
		try {
			executeMavenProject(pomFile);
			fail("MojoExecutionException was expected");
		} catch (MojoExecutionException e) {
			// ok
		}

	}

	private void executeMavenProject(File pomFile) throws Exception {
		Installer installer = (Installer) lookupConfiguredMojo("install", pomFile);
		installer.execute();
	}

	private void prepareDevstudioFakeInstaller() throws Exception {
		installerFile = new File(tempFolder.getRoot(), "fake-installer.jar");

		JarBuilder jarBuilder = new JarBuilder();
		jarBuilder.setMainClass(DevstudioInstall.class);
		jarBuilder.addClass(XMLUtils.class);
		jarBuilder.addClass(JarBuilder.class);
		jarBuilder.addClass(EclipseLauncher.class);
		jarBuilder.addResource("devstudio/plugins/com.jboss.devstudio.core_10.1.0.GA-v20160902-1725-B43", "");
		jarBuilder.build(installerFile);
	}

	private File prepareMavenProject(String resource) throws IOException {
		URL url = InstallerTest.class.getResource(resource);
		File pomFile = new File(tempFolder.getRoot(), "pom.xml");
		FileUtils.copyURLToFile(url, pomFile);
		return pomFile;
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
