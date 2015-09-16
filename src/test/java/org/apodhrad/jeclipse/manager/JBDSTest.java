package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

public class JBDSTest {

	private static String JBDS_URL;
	private static String TARGET;

	@BeforeClass
	public static void beforeClass() throws IOException {
		TARGET = systemProperty("project.build.directory");
		JBDS_URL = systemProperty("jbds.url");
	}

	@Test
	public void jbdsInstalltest() throws IOException {
		JBDS.installJBDS(new File(TARGET), JBDS_URL, null);
	}

	static public String systemProperty(String key) {
		String value = System.getProperty(key);
		assertTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}
}
