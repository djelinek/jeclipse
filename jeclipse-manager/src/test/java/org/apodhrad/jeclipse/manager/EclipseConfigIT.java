package org.apodhrad.jeclipse.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apodhrad.jdownload.manager.hash.URLHash;
import org.apodhrad.jeclipse.manager.util.EclipseUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * @author apodhrad
 *
 */
@RunWith(Parameterized.class)
public class EclipseConfigIT {

	@Parameters(name = "{0}")
	public static Collection<EclipseConfigCombination> getPlatforms() {
		String[] eclipseVersions = new String[] { "jee-luna-SR2", "jee-mars-1", "jee-mars-2", "jee-neon-1a" };
		TestingPlatform[] platforms = new TestingPlatform[] { TestingPlatform.Fedora24_32, TestingPlatform.Fedora24_64,
				TestingPlatform.Mac_10_11, TestingPlatform.Win10_32, TestingPlatform.Win10_64 };

		Collection<EclipseConfigCombination> combinations = new ArrayList<EclipseConfigCombination>();
		for (String eclipseVersion : eclipseVersions) {
			for (TestingPlatform platform : platforms) {
				combinations.add(new EclipseConfigCombination(eclipseVersion, platform));
			}
		}

		return combinations;
	}

	@After
	public void restoreLocalPlatform() {
		TestingPlatform.Local.apply();
	}

	@Before
	public void applyTestingPlatform() {
		platform.apply();
	}

	private String eclipseVersion;
	private TestingPlatform platform;

	public EclipseConfigIT(EclipseConfigCombination combination) {
		this.eclipseVersion = combination.getEclipseVersion();
		this.platform = combination.getPlatform();
	}

	@Test
	public void testArchiveName() throws Exception {
		EclipseConfig config = EclipseConfig.load(inputStream(eclipseVersion), platform.getOs(), platform.getArch());
		Assert.assertEquals(EclipseUtils.getArchiveName(eclipseVersion, platform.getOs(), platform.getArch()),
				config.getArchiveName());
	}

	@Test
	public void testHashSum() throws Exception {
		EclipseConfig config = EclipseConfig.load(inputStream(eclipseVersion), platform.getOs(), platform.getArch());
		String expectedHashSum = new URLHash(config.getHashUrl()).toString();
		Assert.assertEquals(expectedHashSum, "MD5 " + config.getMd5() + "  " + config.getArchiveName());
	}

	private static class EclipseConfigCombination {

		private String eclipseVersion;
		private TestingPlatform platform;

		public EclipseConfigCombination(String eclipseVersion, TestingPlatform platform) {
			this.eclipseVersion = eclipseVersion;
			this.platform = platform;
		}

		public String getEclipseVersion() {
			return eclipseVersion;
		}

		public TestingPlatform getPlatform() {
			return platform;
		}

		@Override
		public String toString() {
			return eclipseVersion + " on " + platform;
		}

	}

	private static InputStream inputStream(String eclipseVersion) {
		String configFileName = "/eclipse/" + eclipseVersion + ".json";
		return EclipseConfig.class.getResourceAsStream(configFileName);
	}
}