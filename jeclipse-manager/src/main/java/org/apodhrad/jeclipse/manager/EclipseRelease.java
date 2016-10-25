package org.apodhrad.jeclipse.manager;

import org.apodhrad.jeclipse.manager.util.OS;

public class EclipseRelease {

	public static final String ECLIPSE_DEFAULT_MIRROR = "http://www.eclipse.org/downloads/download.php?r=1&file=/technology/epp/downloads/release";
	
	private String eclipseVersion;

	private EclipseRelease(String eclipseVerison) {
		this.eclipseVersion = eclipseVerison;
	}
	
	public String getVersion() {
		return eclipseVersion;
	}
	
	public static String getEclipseUrl(String eclipseVersion) {
		return getEclipseUrl(eclipseVersion, ECLIPSE_DEFAULT_MIRROR);
	}
	

	public static String getEclipseUrl(String eclipseVersion, String eclipseMirror) {
		String[] version = eclipseVersion.split("-");
		return eclipseMirror + "/" + version[1] + "/" + version[2] + "/" + getEclipseInstaller(eclipseVersion);
	}
	
	public static String getEclipseInstaller(String eclipseVersion) {
		String os_property = OS.getName();
		String arch_property = OS.getArch();

		String platform = null;
		String archive = "zip";

		if (os_property.contains("linux")) {
			platform = "linux-gtk";
			archive = "tar.gz";
		} else if (os_property.contains("win")) {
			platform = "win32";
			archive = "zip";
		} else if (os_property.contains("mac")) {
			platform = "macosx-cocoa";
			archive = "tar.gz";
		}

		if (platform == null) {
			throw new RuntimeException("Unknown platform '" + os_property + "'");
		}

		if (arch_property.contains("64")) {
			platform += "-x86_64";
		}

		return "eclipse-" + eclipseVersion + "-" + platform + "." + archive;
	}
}
