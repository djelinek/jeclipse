package org.apodhrad.jeclipse.manager;

import static org.apodhrad.jeclipse.manager.TestingPlatform.Fedora24_64;
import static org.apodhrad.jeclipse.manager.TestingPlatform.Mac_10_11;
import static org.apodhrad.jeclipse.manager.TestingPlatform.Win10_64;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * @author apodhrad
 *
 */
@RunWith(Parameterized.class)
public class EclipseTest {

	public static final String LAUNCHER_VERSION = "1.3.200.v20160318-1642";
	public static final String HELLO_VERSION = "1.2.3.v20161121-9999";

	@Rule
	public TemporaryFolder target = new TemporaryFolder();

	@Parameters(name = "{0}")
	public static Collection<TestingPlatform> getPlatforms() {
		return Arrays.asList(new TestingPlatform[] { Fedora24_64, Mac_10_11, Win10_64 });
	}

	private TestingPlatform platform;
	private File targetFolder;
	private File eclipseFolder;
	private File iniFile;
	private File launcherFile;
	private File wrongFolder;

	public EclipseTest(TestingPlatform platform) {
		this.platform = platform;
	}

	@Before
	public void prepareEclipse() throws IOException {
		targetFolder = target.getRoot();
		wrongFolder = target.newFolder("foo");
		if (platform.getOs().toLowerCase().contains("linux")) {
			prepareEclipseStructureForLinux();
		} else if (platform.getOs().toLowerCase().contains("win")) {
			prepareEclipseStructureForWin();
		} else if (platform.getOs().toLowerCase().contains("mac")) {
			prepareEclipseStructureForMac();
		} else {
			throw new UnsupportedOperationException();
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(iniFile));
		out.write("-showsplash");
		out.newLine();
		out.write("org.eclipse.platform");
		out.newLine();
		out.write("--launcher.defaultAction");
		out.newLine();
		out.write("openFile");
		out.newLine();
		out.write("--launcher.appendVmargs");
		out.newLine();
		out.write("-vmargs");
		out.newLine();
		out.write("-Dosgi.requiredJavaVersion=1.8");
		out.newLine();
		out.flush();
		out.close();
	}

	@Test
	public void testCreatingInstanceWithTargetFolder() throws Exception {
		Eclipse eclipse = new Eclipse(targetFolder);
		File launcher = eclipse.getLauncher();
		assertTrue("'" + launcher.getAbsolutePath() + "' is not a launcher",
				launcher.getName().startsWith("org.eclipse.equinox.launcher_"));
	}

	@Test
	public void testCreatingInstanceWithEclipseFolder() throws Exception {
		Eclipse eclipse = new Eclipse(eclipseFolder);
		File launcher = eclipse.getLauncher();
		assertTrue("'" + launcher.getAbsolutePath() + "' is not a launcher",
				launcher.getName().startsWith("org.eclipse.equinox.launcher_"));
	}

	@Test
	public void testCreatingInstanceWithLauncherFile() throws Exception {
		Eclipse eclipse = new Eclipse(launcherFile);
		File launcher = eclipse.getLauncher();
		assertTrue("'" + launcher.getAbsolutePath() + "' is not a launcher",
				launcher.getName().startsWith("org.eclipse.equinox.launcher_"));
	}

	@Test(expected = EclipseException.class)
	public void testCreatingInstanceWithWrongFolder() throws Exception {
		new Eclipse(wrongFolder);
	}

	@Test
	public void testAddingProgramArguments() throws Exception {
		Eclipse eclipse = new Eclipse(eclipseFolder);
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
	public void testAddingVMArguments() throws Exception {
		Eclipse eclipse = new Eclipse(eclipseFolder);
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
	public void testManagingUpdateSites() {
		Eclipse eclipse = new Eclipse(eclipseFolder);
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
	public void testGettingFeatures() {
		Eclipse eclipse = new Eclipse(eclipseFolder);
		Bundle[] features = eclipse.getFeatures();
		assertContainsBundle(features, "com.example.hello", HELLO_VERSION);
		assertContainsBundle(features, "com.example.hello.source", HELLO_VERSION);
		assertEquals(2, features.length);
	}

	@Test
	public void testGettingPlugins() {
		Eclipse eclipse = new Eclipse(eclipseFolder);
		Bundle[] features = eclipse.getPlugins();
		assertContainsBundle(features, "com.example.hello", HELLO_VERSION);
		assertContainsBundle(features, "com.example.hello.source", HELLO_VERSION);
		assertContainsBundle(features, "org.eclipse.equinox.launcher", LAUNCHER_VERSION);
		assertEquals(3, features.length);
	}

	@Test
	public void testGettingIgnoredFeatures() {
		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.ignoreFeature("abc.*");
		eclipse.ignoreFeature("[abc]+*");
		assertTrue(eclipse.getIgnoredFeatures().contains("abc.*"));
		assertTrue(eclipse.getIgnoredFeatures().contains("[abc]+*"));
		assertEquals(2, eclipse.getIgnoredFeatures().size());
	}

	@Test
	public void testRemovingIgnoredFeatures() {
		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.ignoreFeature("abc");
		eclipse.ignoreFeature("xyz");
		eclipse.removeIgnoredFeatures();
		assertTrue(eclipse.getIgnoredFeatures().isEmpty());
	}

	@Test
	public void testIgnoringFeaturesWithExactName() {
		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.ignoreFeature("com.example.hello");
		assertTrue(eclipse.isFeatureIgnored("com.example.hello"));
		assertFalse(eclipse.isFeatureIgnored("com.example.helloworld"));
	}

	@Test
	public void testIgnoringFeaturesWithRegex() {
		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.ignoreFeature("com\\..+\\.hello.*");
		assertTrue(eclipse.isFeatureIgnored("com.example.hello"));
		assertTrue(eclipse.isFeatureIgnored("com.example.helloworld"));
		assertTrue(eclipse.isFeatureIgnored("com.foo.hello"));
		assertFalse(eclipse.isFeatureIgnored("com.example.worldhello"));
	}

	@Test
	public void testInstallingFeatureWithUpdateSite() {
		TestingExecutionRunner testingRunner = new TestingExecutionRunner("output-ok.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.setExecutionRunner(testingRunner);
		eclipse.addUpdateSite("helloworld");
		eclipse.installFeature("hello");

		String[] command = testingRunner.getArgs();
		assertArrayEquals(new String[] { "-application", "org.eclipse.equinox.p2.director", "-consoleLog",
				"-followReferences", "-nosplash", "-repository", "helloworld", "-installIUs", "hello" }, command);
	}

	@Test
	public void testInstallingFeatureWithoutUpdateSite() {
		TestingExecutionRunner testingRunner = new TestingExecutionRunner("output-ok.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.setExecutionRunner(testingRunner);
		eclipse.installFeature("hello");

		String[] command = testingRunner.getArgs();
		assertArrayEquals(new String[] { "-application", "org.eclipse.equinox.p2.director", "-consoleLog",
				"-followReferences", "-nosplash", "-repository", "", "-installIUs", "hello" }, command);
	}

	@Test
	public void testInstallingFeatureWithoutReferences() {
		TestingExecutionRunner testingRunner = new TestingExecutionRunner("output-ok.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.setExecutionRunner(testingRunner);
		eclipse.installFeature(false, "hello");

		String[] command = testingRunner.getArgs();
		assertArrayEquals(new String[] { "-application", "org.eclipse.equinox.p2.director", "-consoleLog", "-nosplash",
				"-repository", "", "-installIUs", "hello" }, command);
	}

	@Test
	public void testInstallingIncorrectFeature() {
		TestingExecutionRunner testingRunner = new TestingExecutionRunner("output-fail.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.setExecutionRunner(testingRunner);
		try {
			eclipse.installFeature("hell");
			Assert.fail("EclipseException was expected");
		} catch (EclipseException ee) {
			assertEquals("Execution failed", ee.getMessage());
		}
	}

	@Test
	public void testInstallingFeaturesWithoutUpdateSite() {
		TestingExecutionRunner testingRunner = new TestingExecutionRunner("output-ok.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.setExecutionRunner(testingRunner);
		eclipse.installFeatures("hello", "hello.source");

		String[] command = testingRunner.getArgs();
		assertArrayEquals(new String[] { "-application", "org.eclipse.equinox.p2.director", "-consoleLog",
				"-followReferences", "-nosplash", "-repository", "", "-installIUs", "hello,hello.source" }, command);
	}

	@Test
	public void testListingFeaturesWithUpdateSite() {
		TestingExecutionRunner testingRunner = new TestingExecutionRunner("output-list.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.addUpdateSite("helloworld");
		eclipse.setExecutionRunner(testingRunner);
		List<Bundle> features = eclipse.listFeatures();

		String[] command = testingRunner.getArgs();
		assertArrayEquals(new String[] { "-application", "org.eclipse.equinox.p2.director", "-consoleLog",
				"-followReferences", "-nosplash", "-repository", "helloworld", "-list" }, command);
		assertContainsBundle(features, "hello", HELLO_VERSION);
		assertContainsBundle(features, "hello.source", HELLO_VERSION);
	}

	@Test
	public void testListingFeaturesWithoutUpdateSite() {
		TestingExecutionRunner testingRunner = new TestingExecutionRunner("output-ok.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.setExecutionRunner(testingRunner);
		List<Bundle> features = eclipse.listFeatures();

		String[] command = testingRunner.getArgs();
		assertArrayEquals(new String[] { "-application", "org.eclipse.equinox.p2.director", "-consoleLog",
				"-followReferences", "-nosplash", "-repository", "", "-list" }, command);
		assertTrue(features.isEmpty());
	}

	@Test
	public void testInstallingAllFeaturesFromUpdateSite() {
		TestingExecutionRunner testingRunnerList = new TestingExecutionRunner("output-list.txt", "output-ok.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.setExecutionRunner(testingRunnerList);
		eclipse.installAllFeaturesFromUpdateSite("helloworld");

		String[] command = testingRunnerList.getArgs();
		assertArrayEquals(new String[] { "-application", "org.eclipse.equinox.p2.director", "-consoleLog",
				"-followReferences", "-nosplash", "-repository", "helloworld", "-installIUs",
				"hello.feature.group,hello.source.feature.group" }, command);
	}

	@Test
	public void testInstallingAllFeaturesFromUpdateSiteWithIgnoring() {
		TestingExecutionRunner testingRunnerList = new TestingExecutionRunner("output-list.txt", "output-ok.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.setExecutionRunner(testingRunnerList);
		eclipse.ignoreFeature(".*\\.source");
		eclipse.installAllFeaturesFromUpdateSite(false, "helloworld");

		String[] command = testingRunnerList.getArgs();
		assertArrayEquals(new String[] { "-application", "org.eclipse.equinox.p2.director", "-consoleLog", "-nosplash",
				"-repository", "helloworld", "-installIUs", "hello.feature.group" }, command);
	}

	@Test
	public void testMirroringRepository() throws Exception {
		File mirrorFile = target.newFolder("mirror");

		TestingExecutionRunner testingRunnerList = new TestingExecutionRunner("output-ok.txt", "output-ok.txt");

		Eclipse eclipse = new Eclipse(eclipseFolder);
		eclipse.setExecutionRunner(testingRunnerList);
		eclipse.mirrorRepository("helloworld", mirrorFile);

		String[] command = testingRunnerList.getArgs();
		assertArrayEquals(new String[] { "-application", "org.eclipse.equinox.p2.artifact.repository.mirrorApplication",
				"-source", "helloworld", "-destination", mirrorFile.getAbsolutePath(), "-nosplash" }, command);
	}

	private void prepareEclipseStructureForLinux() throws IOException {
		eclipseFolder = target.newFolder("eclipse");
		target.newFile("eclipse/eclipse");
		iniFile = target.newFile("eclipse/eclipse.ini");
		target.newFolder("eclipse", "features");
		target.newFolder("eclipse", "features", "com.example.hello_1.2.3.v20161121-9999");
		target.newFolder("eclipse", "features", "com.example.hello.source_1.2.3.v20161121-9999");
		target.newFolder("eclipse", "plugins");
		launcherFile = target.newFile("eclipse/plugins/org.eclipse.equinox.launcher_" + LAUNCHER_VERSION + ".jar");
		target.newFile("eclipse/plugins/com.example.hello_1.2.3.v20161121-9999.jar");
		target.newFile("eclipse/plugins/com.example.hello.source_1.2.3.v20161121-9999.jar");
	}

	private void prepareEclipseStructureForWin() throws IOException {
		eclipseFolder = target.newFolder("eclipse");
		target.newFile("eclipse/eclipse.exe");
		target.newFile("eclipse/eclipsec.exe");
		iniFile = target.newFile("eclipse/eclipse.ini");
		target.newFolder("eclipse", "features");
		target.newFolder("eclipse", "features", "com.example.hello_1.2.3.v20161121-9999");
		target.newFolder("eclipse", "features", "com.example.hello.source_1.2.3.v20161121-9999");
		target.newFolder("eclipse", "plugins");
		launcherFile = target.newFile("eclipse/plugins/org.eclipse.equinox.launcher_" + LAUNCHER_VERSION + ".jar");
		target.newFile("eclipse/plugins/com.example.hello_1.2.3.v20161121-9999.jar");
		target.newFile("eclipse/plugins/com.example.hello.source_1.2.3.v20161121-9999.jar");
	}

	private void prepareEclipseStructureForMac() throws IOException {
		eclipseFolder = target.newFolder("Eclipse.app");
		target.newFolder("Eclipse.app", "Contents");
		target.newFolder("Eclipse.app", "Contents", "Eclipse");
		iniFile = target.newFile("Eclipse.app/Contents/Eclipse/eclipse.ini");
		target.newFolder("Eclipse.app", "Contents", "Eclipse", "features");
		target.newFolder("Eclipse.app", "Contents", "Eclipse", "features", "com.example.hello_1.2.3.v20161121-9999");
		target.newFolder("Eclipse.app", "Contents", "Eclipse", "features",
				"com.example.hello.source_1.2.3.v20161121-9999");
		target.newFolder("Eclipse.app", "Contents", "Eclipse", "plugins");
		launcherFile = target.newFile(
				"Eclipse.app/Contents/Eclipse/plugins/org.eclipse.equinox.launcher_" + LAUNCHER_VERSION + ".jar");
		target.newFile("Eclipse.app/Contents/Eclipse/plugins/com.example.hello_1.2.3.v20161121-9999.jar");
		target.newFile("Eclipse.app/Contents/Eclipse/plugins/com.example.hello.source_1.2.3.v20161121-9999.jar");
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

	private class TestingExecutionRunner implements ExecutionRunner {

		private Appendable executionOutput;
		private int timeout;
		private String[] args;
		private String[] fileNames;
		private int executionCount;

		public TestingExecutionRunner(String... fileNames) {
			this.fileNames = fileNames;
			this.executionCount = 0;
		}

		@Override
		public void setExecutionOutput(Appendable executionOutput) {
			this.executionOutput = executionOutput;
		}

		public int getTimeout() {
			return timeout;
		}

		@Override
		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}

		public String[] getArgs() {
			return args;
		}

		@Override
		public void execute(String... args) {
			this.args = args;
			try {
				String fileName = "/eclipse/" + fileNames[executionCount++];
				InputStream input = TestingExecutionRunner.class.getResourceAsStream(fileName);
				if (input == null) {
					throw new RuntimeException("Cannot find resource '" + fileName + "'");
				}
				for (String line : IOUtils.readLines(input)) {
					executionOutput.append(line);
				}
			} catch (IOException e) {
				new RuntimeException(e);
			}
		}

	}

}
