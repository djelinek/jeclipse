package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author apodhrad
 *
 */
public class EclipseIntegrationTest {

	private static String targetPath;
	private static File targetFile;
	private static String eclipsePath;
	private static File eclipseFile;

	@BeforeClass
	public static void beforeClass() {
		targetPath = System.getProperty("project.build.directory");
		assertNotNull("Set system property project.build.directory", targetPath);
		targetFile = new File(targetPath);
		assertTrue("'" + targetFile.getAbsolutePath() + "' must exists", targetFile.exists());
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
		File launcherFile = new File(eclipseFile, "plugins/org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar");
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
	public void installFeaturesTest() {
		boolean found = false;
		Eclipse eclipse = new Eclipse(eclipsePath);
		eclipse.addUpdateSite("http://download.jboss.org/jbosstools/updates/stable/luna/core/reddeer/0.7.0/");
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
}
