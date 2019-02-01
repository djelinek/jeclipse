package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;

import org.apodhrad.jdownload.manager.hash.NullHash;
import org.junit.BeforeClass;
import org.junit.Test;

public class JBDSIT {

	private static String JBDS_URL;
	private static String TARGET;

	@BeforeClass
	public static void beforeClass() throws IOException {
		TARGET = systemProperty("project.build.directory");
		JBDS_URL = assumeSystemProperty("jeclipse.test.jbds.url");
	}

	@Test
	public void jbdsInstallTest() throws IOException {
		JBDS.installJBDS(new File(TARGET), JBDS_URL, new NullHash());
	}

	static public String systemProperty(String key) {
		String value = System.getProperty(key);
		assertTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}
	
	static public String assumeSystemProperty(String key) {
		String value = System.getProperty(key);
		assumeTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}
}
