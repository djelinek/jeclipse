package org.apodhrad.jeclipse.manager.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apodhrad.jeclipse.manager.Bundle;
import org.apodhrad.jeclipse.manager.Eclipse;
import org.apodhrad.jeclipse.manager.EclipseConfig;
import org.apodhrad.jeclipse.manager.TestingPlatform;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author apodhrad
 *
 */
@RunWith(Parameterized.class)
public class EclipseInstallationIT {

	@Rule
	public TemporaryFolder target = new TemporaryFolder();

	@Parameters(name = "{0} on {1}")
	public static Collection<Object[]> data() throws Exception {
		String[] eclipseVersions = new String[] { "jee-luna-SR2", "jee-mars-2", "jee-neon-1a" };
		TestingPlatform[] platforms = new TestingPlatform[] { TestingPlatform.Fedora24_32, TestingPlatform.Fedora24_64,
				TestingPlatform.Mac_10_11, TestingPlatform.Win10_32, TestingPlatform.Win10_64 };

		Collection<Object[]> data = new ArrayList<Object[]>();
		for (String version : eclipseVersions) {
			for (TestingPlatform platform : platforms) {
				data.add(new Object[] { version, platform });
			}
		}
		return data;
	}

	@Parameter(0)
	public String eclipseVersion;

	@Parameter(1)
	public TestingPlatform platform;

	@After
	public void restoreLocalPlatform() {
		TestingPlatform.Local.apply();
	}

	@Before
	public void applyTestingPlatform() {
		platform.apply();
	}

	@Test
	public void testInstallingEclipse() throws Exception {
		Eclipse eclipse = Eclipse.installEclipse(EclipseConfig.init(eclipseVersion).setTarget(target.getRoot()));
		assertEclipseVersion(eclipse, eclipseVersion, platform);
	}

	private static void assertEclipseVersion(Eclipse eclipse, String eclipseVersion, TestingPlatform platform)
			throws JsonParseException, JsonMappingException, IOException {
		InputStream input = Eclipse.class.getResourceAsStream("/eclipse/" + eclipseVersion + ".json");
		EclipseConfig config = EclipseConfig.load(input, platform.getOs(), platform.getArch());
		for (Bundle feature : eclipse.getFeatures()) {
			if (feature.getName().equals("org.eclipse.platform")) {
				assertEquals(config.getVersion(), feature.getVersion());
				return;
			}
		}
		fail("Cannot find feature 'org.eclipse.platform'");
	}
}
