package org.apodhrad.jeclipse.manager.integration;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apodhrad.jdownload.manager.hash.NullHash;
import org.apodhrad.jeclipse.manager.Bundle;
import org.apodhrad.jeclipse.manager.Devstudio;
import org.apodhrad.jeclipse.manager.DevstudioConfig;
import org.junit.Assert;
import org.junit.BeforeClass;

public class DevstudioISIT {

	public static final String FUSE_FEATURE = "com.jboss.devstudio.integration-stack.fuse.feature.feature.group";

	private static String JBDS_URL;
	private static String TARGET;

	@BeforeClass
	public static void beforeClass() throws IOException {
		TARGET = systemProperty("project.build.directory");
		JBDS_URL = systemProperty("jeclipse.test.jbdsis.url");
	}

//	@Test
	public void jbdsInstallTest() throws IOException {
		DevstudioConfig config = new DevstudioConfig();
		config.setTarget(TARGET);
		config.addFeature(FUSE_FEATURE);
		Devstudio jbdsis = Devstudio.installJBDS(JBDS_URL, new NullHash(), config);

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
