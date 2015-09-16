package org.apodhrad.jeclipse.manager.util;

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

	public static boolean isWindows() {
		return getName().contains("win");
	}
}
