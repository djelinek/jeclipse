package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DevstudioInstallerTest {

	public static final String DEVSTUDIO_VERSION = "10.1.0.GA-v20160902-1725-B43";

	@Rule
	public TemporaryFolder target = new TemporaryFolder();

	private File installerJar;

	@Test
	public void testGettingCoreVersion() throws Exception {
		installerJar = prepareDevstudioInstaller(target.newFile("installer.jar"), DEVSTUDIO_VERSION);
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);
		assertEquals("10.1.0.GA", installer.getCoreVersion());
	}

	@Test
	public void testGettingCoreFullVersion() throws Exception {
		installerJar = prepareDevstudioInstaller(target.newFile("installer.jar"), DEVSTUDIO_VERSION);
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);
		assertEquals("10.1.0.GA-v20160902-1725-B43", installer.getCoreFullVersion());
	}

	@Test
	public void testGettingCoreVersionWithoutCorePlugin() throws Exception {
		installerJar = prepareDevstudioInstaller(target.newFile("installer.jar"), null);
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);
		try {
			installer.getCoreVersion();
			Assert.fail("NoSuchElementException was expected");
		} catch (NoSuchElementException e) {
		}
	}

	@Test
	public void testGettingCoreFeatures() throws Exception {
		installerJar = prepareDevstudioInstaller(target.newFile("installer.jar"), DEVSTUDIO_VERSION);
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);
		assertEquals(Arrays.asList(new String[] { ("hello.feature.group") }), installer.getCoreFeatures());
	}

	@Test
	public void testGettingAdditionalFeatures() throws Exception {
		installerJar = prepareDevstudioInstaller(target.newFile("installer.jar"), DEVSTUDIO_VERSION);
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);
		assertEquals(Arrays.asList(new String[] { ("hello.source.feature.group") }), installer.getAdditionalFeatures());
	}

	@Test
	public void testGettingCoreFeatureProduct() throws Exception {
		installerJar = prepareDevstudioInstaller(target.newFile("installer.jar"), DEVSTUDIO_VERSION);
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);
		assertEquals("firstProduct", installer.getFeatureProduct("hello.feature.group"));
	}

	@Test
	public void testGettingAdditionalFeatureProduct() throws Exception {
		installerJar = prepareDevstudioInstaller(target.newFile("installer.jar"), DEVSTUDIO_VERSION);
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);
		assertEquals("secondProduct", installer.getFeatureProduct("hello.source.feature.group"));
	}

	private File prepareDevstudioInstaller(File file, String fullVersion) throws IOException {
		JarOutputStream out = new JarOutputStream(new FileOutputStream(file));
		if (fullVersion != null) {
			out.putNextEntry(new ZipEntry("devstudio/plugins/com.jboss.devstudio.core_" + fullVersion + ".jar"));
		}
		out.putNextEntry(new ZipEntry("res/DevstudioFeaturesSpec.json"));
		out.write("[{\"id\": \"hello.feature.group\", \"path\": \"firstProduct\"}]".getBytes());
		out.putNextEntry(new ZipEntry("res/AdditionalFeaturesSpec.json"));
		out.write("[{\"id\": \"hello.source.feature.group\", \"path\": \"secondProduct\"}]".getBytes());
		out.flush();
		out.close();
		return file;
	}
}
