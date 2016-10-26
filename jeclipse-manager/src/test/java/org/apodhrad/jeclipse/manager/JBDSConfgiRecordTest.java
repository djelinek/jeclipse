package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

public class JBDSConfgiRecordTest {

	@Test
	public void testLoadingFromFile() throws Exception {
		InputStream input = JBDSConfgiRecordTest.class.getResourceAsStream("/test-install-10.xml");
		JBDSConfigRecord config = JBDSConfigRecord.load(input);

		assertEquals("INSTALL_PATH", config.getInstallPath());
		assertEquals("devstudio", config.getInstallGroup());
		assertEquals("JRE_LOCATION", config.getJreLocation());
		assertTrue(config.getInstallableUnits().contains("com.jboss.devstudio.core.package"));
		assertTrue(config.getInstallableUnits().contains("org.testng.eclipse.feature.group"));
		assertEquals(2, config.getInstallableUnits().size());
	}
}
