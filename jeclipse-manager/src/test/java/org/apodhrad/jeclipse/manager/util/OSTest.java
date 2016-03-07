package org.apodhrad.jeclipse.manager.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class OSTest {

	private static String TARGET;

	@BeforeClass
	public static void beforeClass() throws IOException {
		TARGET = systemProperty("project.build.directory", "target");
		new File(TARGET, "jre").mkdirs();
		new File(TARGET, "jre/bin").mkdirs();
		new File(TARGET, "jre/bin/java").createNewFile();
		new File(TARGET, "jdk").mkdirs();
		new File(TARGET, "jdk/bin").mkdirs();
		new File(TARGET, "jdk/bin/java").createNewFile();
		new File(TARGET, "jdk/jre").mkdirs();
		new File(TARGET, "jdk/jre/bin").mkdirs();
		new File(TARGET, "jdk/jre/bin/java").mkdirs();
	}

	@Test
	public void getJreFromJreTest() {
		File jre = OS.getJre(new File(TARGET, "jre").getAbsolutePath());
		Assert.assertEquals(new File(TARGET, "jre/bin/java").getAbsolutePath(), jre.getAbsolutePath());
	}
	
	@Test
	public void getJreFromJdkTest() {
		File jre = OS.getJre(new File(TARGET, "jdk/jre").getAbsolutePath());
		Assert.assertEquals(new File(TARGET, "jdk/bin/java").getAbsolutePath(), jre.getAbsolutePath());
	}

	static public String systemProperty(String key, String defaultValue) {
		String value = System.getProperty(key, defaultValue);
		assertTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}
}
