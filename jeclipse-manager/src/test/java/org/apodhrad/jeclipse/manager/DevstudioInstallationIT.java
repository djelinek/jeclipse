package org.apodhrad.jeclipse.manager;

import java.util.ArrayList;
import java.util.Collection;

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
				"jboss-devstudio-9.1.0.GA-installer-standalone", "devstudio-10.0.0.GA-installer-standalone",
				"devstudio-10.1.0.GA-installer-standalone" };

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
		Devstudio.installJBDS(target.getRoot(), getUrl(installer));
	}

	private static String getUrl(String installer) {
		if (!installer.endsWith(".jar")) {
			installer += ".jar";
		}
		return "http://localhost/files/" + installer;
	}

}