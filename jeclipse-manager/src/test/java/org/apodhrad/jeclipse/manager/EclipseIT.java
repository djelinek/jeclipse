package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author apodhrad
 *
 */
public class EclipseIT {

	public static final String ECLIPSE_VERSION = "jee-mars-2";
	public static final String ECLIPSE_LAUNCHER = "org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar";
	public static final String REDDEER_VERSION = "1.0.1.Final";
	public static final String REDDEER = "http://download.jboss.org/jbosstools/updates/stable/mars/core/reddeer/1.0.1/";
	public static final String REDDEER_ZIP = "https://github.com/jboss-reddeer/reddeer/releases/download/v1.0.1/org.jboss.reddeer.site-1.0.1.Final.zip";

	private static String targetPath;
	private static File targetFile;
	private static String eclipsePath;
	private static File eclipseFile;

	@Before
	public void prepareEclipse() throws IOException {
		targetPath = System.getProperty("project.build.directory", "target");
		assertNotNull("Set system property project.build.directory", targetPath);
		targetFile = new File(targetPath);
		assertTrue("'" + targetFile.getAbsolutePath() + "' must exists", targetFile.exists());

		FileUtils.deleteQuietly(new File(targetFile, "eclipse"));
		Eclipse eclipse = Eclipse.installEclipse(targetFile, ECLIPSE_VERSION);
		assertNotNull(eclipse);
		assertEquals(ECLIPSE_LAUNCHER, eclipse.getLauncher().getName());

		eclipseFile = new File(targetFile, "eclipse");
		assertTrue("'" + targetFile.getAbsolutePath() + "' must exists", eclipseFile.exists());
		eclipsePath = eclipseFile.getAbsolutePath();
	}

	@Test
	public void eclipseInstanceWithEclipsePathTest() throws Exception {
		Eclipse eclipse = new Eclipse(eclipsePath);
		File launcher = eclipse.getLauncher();
		assertTrue("'" + launcher.getAbsolutePath() + "' is not a launcher",
				launcher.getName().startsWith("org.eclipse.equinox.launcher_"));
	}

	@Test
	public void eclipseInstanceWithEclipseFileTest() throws Exception {
		Eclipse eclipse = new Eclipse(eclipseFile);
		File launcher = eclipse.getLauncher();
		assertTrue("'" + launcher.getAbsolutePath() + "' is not a launcher",
				launcher.getName().startsWith("org.eclipse.equinox.launcher_"));
	}

	@Test
	public void eclipseInstanceWithTargetPathTest() throws Exception {
		Eclipse eclipse = new Eclipse(targetPath);
		File launcher = eclipse.getLauncher();
		assertTrue("'" + launcher.getAbsolutePath() + "' is not a launcher",
				launcher.getName().startsWith("org.eclipse.equinox.launcher_"));
	}

	@Test
	public void eclipseInstanceWithTargetFileTest() throws Exception {
		Eclipse eclipse = new Eclipse(targetPath);
		File launcher = eclipse.getLauncher();
		assertTrue("'" + launcher.getAbsolutePath() + "' is not a launcher",
				launcher.getName().startsWith("org.eclipse.equinox.launcher_"));
	}

	@Test
	public void eclipseInstanceWithLauncherFileTest() throws Exception {
		File launcherFile = new File(eclipseFile, "plugins/" + ECLIPSE_LAUNCHER);
		Eclipse eclipse = new Eclipse(launcherFile);
		File launcher = eclipse.getLauncher();
		assertTrue("'" + launcher.getAbsolutePath() + "' is not a launcher",
				launcher.getName().startsWith("org.eclipse.equinox.launcher_"));
	}

	@Test(expected = EclipseException.class)
	public void eclipseInstanceWithWrongDirTest() throws Exception {
		new Eclipse(new File(targetPath, "classes"));
	}

	@Test
	public void managingUpdateSitesTest() {
		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addUpdateSite("update-site-1");
		eclipse.addUpdateSite("update-site-2");
		assertEquals(2, eclipse.getUpdateSites().size());
		assertTrue(eclipse.getUpdateSites().contains("update-site-1"));
		assertTrue(eclipse.getUpdateSites().contains("update-site-2"));
		try {
			eclipse.getUpdateSites().add("update-site-3");
		} catch (UnsupportedOperationException e) {
			// ok
		}
	}

	@Test
	public void listFeaturesTest() {
		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addUpdateSite(REDDEER);
		List<Bundle> features = eclipse.listFeatures();
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature", REDDEER_VERSION);
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature.source", REDDEER_VERSION);
	}

	@Test
	public void installFeaturesTest() {
		boolean found = false;
		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addUpdateSite(REDDEER);
		eclipse.installFeatures("org.jboss.reddeer.rcp.feature.feature.group");
		Bundle[] features = eclipse.getFeatures();
		for (Bundle feature : features) {
			if (feature.getName().equals("org.jboss.reddeer.rcp.feature")
					&& feature.getVersion().equals(REDDEER_VERSION)) {
				assertEquals("org.jboss.reddeer.rcp.feature_" + REDDEER_VERSION, feature.getFullName());
				assertEquals("org.jboss.reddeer.rcp.feature_" + REDDEER_VERSION, feature.toString());
				found = true;
				break;
			}
		}
		if (!found) {
			Assert.fail("Cannot find 'org.jboss.reddeer.rcp.feature_" + REDDEER_VERSION + "'");
		}
		Bundle[] plugins = eclipse.getPlugins();
		for (Bundle plugin : plugins) {
			if (plugin.getName().equals("org.jboss.reddeer.swt") && plugin.getVersion().equals(REDDEER_VERSION)) {
				assertEquals("org.jboss.reddeer.swt_" + REDDEER_VERSION, plugin.getFullName());
				assertEquals("org.jboss.reddeer.swt_" + REDDEER_VERSION, plugin.toString());
				found = true;
				break;
			}
		}
		if (!found) {
			Assert.fail("Cannot find 'org.jboss.reddeer.swt_0.7.0'");
		}
	}

	@Test
	public void installAllFeaturesTest() throws Exception {
		File zipFile = new JDownloadManager().download(REDDEER_ZIP, targetFile);

		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addUpdateSite("http://download.eclipse.org/releases/mars/");
		eclipse.installAllFeaturesFromUpdateSite("jar:file:" + zipFile.getAbsolutePath() + "!/");
		Bundle[] features = eclipse.getFeatures();
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature", REDDEER_VERSION);
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature.source", REDDEER_VERSION);
		assertContainsBundle(features, "org.jboss.reddeer.swt.feature", REDDEER_VERSION);
		assertContainsBundle(features, "org.jboss.reddeer.swt.feature.source", REDDEER_VERSION);
		assertContainsBundle(features, "org.jboss.reddeer.graphiti.feature", REDDEER_VERSION);
		assertContainsBundle(features, "org.jboss.reddeer.graphiti.feature.source", REDDEER_VERSION);
	}

	@Test
	public void installAllFeaturesWithIgnoraceTest() throws Exception {
		File zipFile = new JDownloadManager().download(REDDEER_ZIP, targetFile);

		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addUpdateSite("http://download.eclipse.org/releases/mars/");
		eclipse.ignoreFeature("org.jboss.reddeer.graphiti.*");
		eclipse.installAllFeaturesFromUpdateSite("jar:file:" + zipFile.getAbsolutePath() + "!/");
		Bundle[] features = eclipse.getFeatures();
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature", REDDEER_VERSION);
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature.source", REDDEER_VERSION);
		assertContainsBundle(features, "org.jboss.reddeer.swt.feature", REDDEER_VERSION);
		assertContainsBundle(features, "org.jboss.reddeer.swt.feature.source", REDDEER_VERSION);
		assertNotContainsBundle(features, "org.jboss.reddeer.graphiti.feature", REDDEER_VERSION);
		assertNotContainsBundle(features, "org.jboss.reddeer.graphiti.feature.source", REDDEER_VERSION);
	}

	@Test
	public void mirrorRepositoryTest() throws Exception {
		File mirror = new File(targetFile, "reddeer-mirror");

		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.mirrorRepository(REDDEER, mirror);

		assertTrue(new File(mirror, "artifacts.jar").exists());
		assertTrue(new File(mirror, "content.jar").exists());
		assertTrue(new File(mirror, "plugins").exists());
		assertTrue(new File(mirror, "features").exists());
		assertTrue(new File(new File(mirror, "features"), "org.jboss.reddeer.rcp.feature_" + REDDEER_VERSION + ".jar")
				.exists());
	}

	@Test
	public void eclipseAddProgramArgumentsTest() throws Exception {
		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addProgramArgument("-data", "tmp");
		File iniFile = eclipse.getIniFile();
		List<String> lines = FileUtils.readLines(iniFile);
		boolean isVMArgs = false;
		boolean foundData = false;
		int i = 0;
		for (String line : lines) {
			i++;
			if (line.equals("-vmargs")) {
				isVMArgs = true;
			}
			if (line.equals("-data")) {
				if (!isVMArgs) {
					foundData = true;
				} else {
					Assert.fail("-data must be before -vmargs!");
				}
				break;
			}
		}
		assertTrue("Cannot find -data", foundData);
		assertEquals("-data must be be followed by 'tmp'!", "tmp", lines.get(i));
	}

	@Test
	public void eclipseAddVMArgumentsTest() throws Exception {
		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addVMArgument("-Dfoo1=foo1", "-Dfoo2=foo2");
		File iniFile = eclipse.getIniFile();
		List<String> lines = FileUtils.readLines(iniFile);
		boolean isVMArgs = false;
		boolean foundFoo1 = false;
		boolean foundFoo2 = false;
		for (String line : lines) {
			if (line.equals("-vmargs")) {
				isVMArgs = true;
			}
			if (line.equals("-Dfoo1=foo1")) {
				if (isVMArgs) {
					foundFoo1 = true;
				} else {
					Assert.fail("-Dfoo1=foo1 must be after -vmargs!");
				}
			}
			if (line.equals("-Dfoo2=foo2")) {
				if (isVMArgs) {
					foundFoo2 = true;
				} else {
					Assert.fail("-Dfoo2=foo2 must be after -vmargs!");
				}
			}
		}
		assertTrue("Not all VM arguments were added!", foundFoo1 && foundFoo2);
	}

	@Test
	public void md5HashTableTest() {
		for (String value : Eclipse.ECLIPSE_MD5.values()) {
			assertEquals(32, value.length());
		}
	}

	private static void assertContainsBundle(List<Bundle> bundles, String expectedName, String expectedVersion) {
		Bundle bundle = new Bundle(expectedName, expectedVersion);
		assertTrue("The list " + bundles + " doesn't contain bundle" + bundle, bundles.contains(bundle));
	}

	private static void assertContainsBundle(Bundle[] bundles, String expectedName, String expectedVersion) {
		assertContainsBundle(Arrays.asList(bundles), expectedName, expectedVersion);
	}

	private static void assertNotContainsBundle(List<Bundle> bundles, String expectedName, String expectedVersion) {
		Bundle bundle = new Bundle(expectedName, expectedVersion);
		assertFalse("The list " + bundles + " contains bundle" + bundle, bundles.contains(bundle));
	}

	private static void assertNotContainsBundle(Bundle[] bundles, String expectedName, String expectedVersion) {
		assertNotContainsBundle(Arrays.asList(bundles), expectedName, expectedVersion);
	}

}
