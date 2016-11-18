package org.apodhrad.jeclipse.manager.util;

import java.io.File;
import java.util.List;

import org.apodhrad.jeclipse.manager.matcher.IsJavaExecutable;

/**
 * 
 * @author apodhrad
 *
 */
public class OS {

	public static String getName() {
		return System.getProperty("os.name").toLowerCase();
	}

	public static String getArch() {
		return System.getProperty("os.arch").toLowerCase();
	}

	public static boolean isLinux() {
		return getName().contains("linux");
	}

	public static boolean isWindows() {
		return getName().contains("win");
	}

	public static boolean isMac() {
		return getName().contains("mac");
	}

	public static boolean is64() {
		return getArch().contains("64");
	}

	public static File getJre(String location) {
		// find jre location from java home
		String javaHome = location;
		if (location == null || location.length() == 0) {
			javaHome = System.getProperty("java.home");
		}
		FileSearch fileSearch = new FileSearch();

		File javaHomeParent = new File(javaHome).getParentFile();
		List<File> jreLocations = fileSearch.find(new File(javaHomeParent, "bin"), new IsJavaExecutable());
		if (jreLocations.isEmpty()) {
			jreLocations = fileSearch.find(new File(javaHome), new IsJavaExecutable());
		}
		if (jreLocations.isEmpty()) {
			throw new RuntimeException("Cannot find JRE location!");
		}

		return jreLocations.get(0);
	}
}
