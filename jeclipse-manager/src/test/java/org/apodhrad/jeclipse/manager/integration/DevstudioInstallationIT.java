package org.apodhrad.jeclipse.manager.integration;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;

import org.apodhrad.jdownload.manager.hash.NullHash;
import org.apodhrad.jeclipse.manager.Devstudio;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DevstudioInstallationIT {

	@Rule
	public TemporaryFolder target = new TemporaryFolder();

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		String[] versions = new String[] { "jboss-devstudio-8.1.0.GA-installer-standalone",
				"jboss-devstudio-9.1.0.GA-installer-standalone",
				"devstudio-integration-stack-9.0.3.GA-standalone-installer", "devstudio-10.0.0.GA-installer-standalone",
				"devstudio-10.1.0.GA-installer-standalone", "devstudio-10.1.0.GA-installer-eap",
				"devstudio-integration-stack-10.0.0.GA-standalone-installer" };

		Collection<Object[]> data = new ArrayList<Object[]>();
		for (String version : versions) {
			data.add(new Object[] { version });
		}
		return data;
	}

	@Parameter(0)
	public String installer;

	@Test
	public void testInstallingDevstudio() throws Exception {
		String[] features = new String[] {};
		if (installer.contains("integration-stack")) {
			features = new String[] { "com.jboss.devstudio.integration-stack.fuse.feature.feature.group" };
		}
		Devstudio devstudio = Devstudio.installJBDS(target.getRoot(), getUrl(installer), new NullHash(), null,
				features);
		for (String feature : features) {
			feature = feature.replace(".feature.group", "");
			assertNotNull("Cannot find feature '" + feature + "'", devstudio.getFeature(feature));
		}
	}

	private static String getUrl(String installer) {
		if (!installer.endsWith(".jar")) {
			installer += ".jar";
		}
		return "http://localhost/files/" + installer;
	}

}