package org.apodhrad.jeclipse.manager;

import java.util.Arrays;
import java.util.Collection;

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
public class EclipseConfigPlatformTest {

	@Parameters(name = "{0}")
	public static Collection<TestingPlatform> getPlatforms() {
		return Arrays.asList(new TestingPlatform[] { TestingPlatform.Fedora24_32, TestingPlatform.Fedora24_64,
				TestingPlatform.Mac_10_11, TestingPlatform.Win10_32, TestingPlatform.Win10_64 });
	}

	@After
	public void restoreLocalPlatform() {
		TestingPlatform.Local.apply();
	}

	@Before
	public void applyTestingPlatform() {
		platform.apply();
	}

	private TestingPlatform platform;

	public EclipseConfigPlatformTest(TestingPlatform platform) {
		this.platform = platform;
	}

	@Test
	public void testGettingArchiveNameForJEEMars1() throws Exception {
		EclipseConfig config = EclipseConfig.load("jee-mars-1", platform.getOs(), platform.getArch());
		Assert.assertEquals(config.getName(), EclipseConfig.getArchiveName("jee-mars-1"));
	}

}
