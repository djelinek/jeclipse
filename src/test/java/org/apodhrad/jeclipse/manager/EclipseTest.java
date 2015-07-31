package org.apodhrad.jeclipse.manager;

import org.apache.maven.cli.MavenCli;
import org.junit.Test;

public class EclipseTest {

	@Test
	public void eclipseTest() throws Exception {
		MavenCli mavenCli = new MavenCli();
		mavenCli.doMain(new String[] {"clean", "package"}, "/home/apodhrad/Projects/jbds-installer/eclipse-luna", System.out, System.err);
	}
}
