package org.apodhrad.jeclipse.manager.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author apodhrad
 *
 */
public class EclipseUtils {

	public static final String ECLIPSE_DOWNLOAD_URL = "http://www.eclipse.org/downloads/download.php";
	public static final String ECLIPSE_BASE_PATH = "/technology/epp/downloads/release";
	public static final int ECLIPSE_DEFAULT_MIRROR_ID = 1196; // CZ.NIC

	public static String getArchiveName(String version, String os, String arch) {
		String os_property = os.toLowerCase();
		String arch_property = arch;

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
			throw new IllegalArgumentException("Cannot get archive name for OS '" + os + "'");
		}

		if (arch_property.contains("64")) {
			platform += "-x86_64";
		}

		return "eclipse-" + version + "-" + platform + "." + archive;
	}

	public static String getUrl(String path, int mirrorId) {
		List<String> params = new ArrayList<String>();
		params.add("r=1");
		if (mirrorId > 0) {
			params.add("mirror_id=" + mirrorId);
		}
		params.add("file=" + path);
		return getUrl(params);
	}

	public static String getUrl(List<String> params) {
		return ECLIPSE_DOWNLOAD_URL + "?" + String.join("&", params);
	}

	public static String getPathFromVersion(String eclipseVersion, String os, String arch) {
		String[] version = eclipseVersion.split("-");
		return ECLIPSE_BASE_PATH + "/" + version[1] + "/" + version[2] + "/"
				+ EclipseUtils.getArchiveName(eclipseVersion, os, arch);
	}

}
