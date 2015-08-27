package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

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
}
