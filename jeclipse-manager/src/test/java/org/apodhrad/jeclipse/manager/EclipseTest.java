package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author apodhrad
 *
 */
public class EclipseTest {

	public static final String ECLIPSE_VERSION = "jee-luna-SR2";
	public static final String ECLIPSE_LAUNCHER = "org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar";
	public static final String REDDEER_070 = "http://download.jboss.org/jbosstools/updates/stable/luna/core/reddeer/0.7.0/";

	private static String targetPath;
	private static File targetFile;
	private static String eclipsePath;
	private static File eclipseFile;

	@BeforeClass
	public static void beforeClass() throws IOException {
		targetPath = System.getProperty("project.build.directory");
		assertNotNull("Set system property project.build.directory", targetPath);
		targetFile = new File(targetPath);
		assertTrue("'" + targetFile.getAbsolutePath() + "' must exists", targetFile.exists());

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
		eclipse.addUpdateSite(REDDEER_070);
		List<Bundle> features = eclipse.listFeatures();
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature", "0.7.0");
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature.source", "0.7.0");
	}

	@Test
	public void installFeaturesTest() {
		boolean found = false;
		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addUpdateSite(REDDEER_070);
		eclipse.installFeatures("org.jboss.reddeer.rcp.feature.feature.group");
		Bundle[] features = eclipse.getFeatures();
		for (Bundle feature : features) {
			if (feature.getName().equals("org.jboss.reddeer.rcp.feature") && feature.getVersion().equals("0.7.0")) {
				assertEquals("org.jboss.reddeer.rcp.feature_0.7.0", feature.getFullName());
				assertEquals("org.jboss.reddeer.rcp.feature_0.7.0", feature.toString());
				found = true;
				break;
			}
		}
		if (!found) {
			Assert.fail("Cannot find 'org.jboss.reddeer.rcp.feature_0.7.0'");
		}
		Bundle[] plugins = eclipse.getPlugins();
		for (Bundle plugin : plugins) {
			if (plugin.getName().equals("org.jboss.reddeer.swt") && plugin.getVersion().equals("0.7.0")) {
				assertEquals("org.jboss.reddeer.swt_0.7.0", plugin.getFullName());
				assertEquals("org.jboss.reddeer.swt_0.7.0", plugin.toString());
				found = true;
				break;
			}
		}
		if (!found) {
			Assert.fail("Cannot find 'org.jboss.reddeer.swt_0.7.0'");
		}
	}

	@Test
	public void installAllFeaturesTest() {
		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addUpdateSite("http://download.eclipse.org/releases/luna/");
		eclipse.installAllFeaturesFromUpdateSite(REDDEER_070);
		Bundle[] features = eclipse.getFeatures();
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature", "0.7.0");
		assertContainsBundle(features, "org.jboss.reddeer.rcp.feature.source", "0.7.0");
		assertContainsBundle(features, "org.jboss.reddeer.swt.feature", "0.7.0");
		assertContainsBundle(features, "org.jboss.reddeer.swt.feature.source", "0.7.0");
		assertContainsBundle(features, "org.jboss.reddeer.graphiti.feature", "0.7.0");
		assertContainsBundle(features, "org.jboss.reddeer.graphiti.feature.source", "0.7.0");
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

	private static void assertContainsBundle(List<Bundle> bundles, String expectedName, String expectedVersion) {
		Bundle bundle = new Bundle(expectedName, expectedVersion);
		assertTrue("The list " + bundles + " doesn't contain bundle" + bundle, bundles.contains(bundle));
	}

	private static void assertContainsBundle(Bundle[] bundles, String expectedName, String expectedVersion) {
		assertContainsBundle(Arrays.asList(bundles), expectedName, expectedVersion);
	}
}
