package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apodhrad.jdownload.manager.hash.NullHash;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class JBDSISIT {

	public static final String FUSE_FEATURE = "com.jboss.devstudio.integration-stack.fuse.feature.feature.group";

	private static String JBDS_URL;
	private static String TARGET;

	@BeforeClass
	public static void beforeClass() throws IOException {
		TARGET = systemProperty("project.build.directory");
		JBDS_URL = systemProperty("jeclipse.test.jbdsis.url");
	}

	@Test
	public void jbdsInstallTest() throws IOException {
		JBDS jbdsis = JBDS.installJBDS(new File(TARGET), JBDS_URL, new NullHash(), null, new String[] {},
				new String[] { FUSE_FEATURE });

		for (Bundle feature : jbdsis.getFeatures()) {
			if (feature.getName().contains("fusesource")) {
				return;
			}
		}
		Assert.fail("Cannot find any feature matching '.*fusesource.*'");
	}

	static public String systemProperty(String key) {
		String value = System.getProperty(key);
		assertTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}
}
