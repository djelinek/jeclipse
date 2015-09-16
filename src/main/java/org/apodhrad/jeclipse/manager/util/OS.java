package org.apodhrad.jeclipse.manager.util;

public class OS {

	public static String getName() {
		return System.getProperty("os.name").toLowerCase();
	}

	public static String getArch() {
		return System.getProperty("os.arch").toLowerCase();
	}

	public static boolean isWindowsPlatform() {
		return getName().contains("win");
	}
}
