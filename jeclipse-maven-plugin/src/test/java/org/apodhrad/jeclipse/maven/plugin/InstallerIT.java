package org.apodhrad.jeclipse.maven.plugin;

import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.junit.Test;

public class InstallerIT extends BetterAbstractMojoTestCase {

	private static String TARGET;
	private static String JBDS_URL;
	private static String JBDS_SHA256;

//	@BeforeClass
	public static void beforeClass() throws IOException {
		TARGET = systemProperty("project.build.directory");
		JBDS_URL = systemProperty("jeclipse.test.jbds.url");
		JBDS_SHA256 = systemProperty("jeclipse.test.jbds.sha256");
	}

//	@Test
	public void installEclipseLunaTest() throws Exception {
		File pomFile = prepareMavenProject("install-eclipse-luna");

		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(pomFile);
		request.setGoals(Collections.singletonList("package"));

		Properties systemProperties = new Properties();
		systemProperties.put("jeclipse.version", System.getProperty("jeclipse.version"));
		request.setProperties(systemProperties);

		Invoker invoker = new DefaultInvoker();
		InvocationResult result = invoker.execute(request);

		int exitCode = result.getExitCode();
		assertEquals("Build failed (exit code " + exitCode + ")", 0, exitCode);
	}
	
	@Test
	public void testinstallEclipseLuna2Test() throws Exception {
		File pom = getTestFile("src/test/resources/install-eclipse-luna-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();
	}
	

	@Test
	public void testinstallEclipseMars2Test() throws Exception {
		File pom = getTestFile("src/test/resources/install-eclipse-mars-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();
	}
	

	@Test
	public void testinstallJBDS2Test() throws Exception {
		assumeSystemProperty("jeclipse.test.jbds.url");
		File pom = getTestFile("src/test/resources/install-jbds-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();
	}
	
	@Test
	public void testinstallJBDSISTest() throws Exception {
		assumeSystemProperty("jeclipse.test.jbdsis.url");
		File pom = getTestFile("src/test/resources/install-jbdsis-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Installer installer = (Installer) lookupConfiguredMojo("install", pom);
		assertNotNull(installer);
		installer.execute();
	}

//	@Test
	public void installEclipseMarsTest() throws Exception {
		File pomFile = prepareMavenProject("install-eclipse-mars");

		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(pomFile);
		request.setGoals(Collections.singletonList("package"));

		Properties systemProperties = new Properties();
		systemProperties.put("jeclipse.version", System.getProperty("jeclipse.version"));
		request.setProperties(systemProperties);

		Invoker invoker = new DefaultInvoker();
		InvocationResult result = invoker.execute(request);

		int exitCode = result.getExitCode();
		assertEquals("Build failed (exit code " + exitCode + ")", 0, exitCode);
	}

	@Test
	public void installJBDSTest() throws Exception {
		assumeSystemProperty("jeclipse.test.jbds.url");

		File pomFile = prepareMavenProject("install-jbds");

		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(pomFile);
		request.setGoals(Collections.singletonList("package"));

		Properties systemProperties = new Properties();
		systemProperties.put("jeclipse.version", System.getProperty("jeclipse.version"));
		systemProperties.put("jeclipse.test.jbds.url", JBDS_URL);
		systemProperties.put("jeclipse.test.jbds.sha256", JBDS_SHA256);
		request.setProperties(systemProperties);

		Invoker invoker = new DefaultInvoker();
		InvocationResult result = invoker.execute(request);

		int exitCode = result.getExitCode();
		assertEquals("Build failed (exit code " + exitCode + ")", 0, exitCode);
	}

	static public String systemProperty(String key) {
		String value = System.getProperty(key);
		assertTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}

	private static String assumeSystemProperty(String key) {
		String value = System.getProperty(key);
		assumeTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}

	private File prepareMavenProject(String name) throws IOException {
		URL url = InstallerIT.class.getResource("/" + name + ".xml");
		File target = new File(TARGET, name);
		target.mkdir();
		File pomFile = new File(target, "pom.xml");
		FileUtils.copyURLToFile(url, pomFile);
		return pomFile;
	}

}
