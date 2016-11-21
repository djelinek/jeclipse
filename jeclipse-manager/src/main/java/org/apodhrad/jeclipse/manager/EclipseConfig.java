package org.apodhrad.jeclipse.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apodhrad.jeclipse.manager.util.OS;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author apodhrad
 *
 */
public class EclipseConfig {

	public static final String ECLIPSE_DOWNLOAD_URL = "http://www.eclipse.org/downloads/download.php";
	public static final int ECLIPSE_DEFAULT_MIRROR_ID = 1196; // CZ.NIC

	private String os;
	private String arch;
	private String path;
	private String md5;

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public boolean isSupported(String os, String arch) {
		if (os == null || arch == null) {
			throw new IllegalArgumentException("OS or Arch is null!");
		}
		return os.toLowerCase().contains(getOs()) && ((getArch().equals("32") && !arch.contains("64"))
				|| (getArch().equals("64") && arch.contains("64")));
	}

	public String getUrl() {
		return getUrl(ECLIPSE_DEFAULT_MIRROR_ID);
	}

	public String getUrl(int mirrorId) {
		return getUrl(getPath(), mirrorId);
	}

	public String getHashUrl() {
		return getUrl(getPath() + ".md5", ECLIPSE_DEFAULT_MIRROR_ID);
	}

	public String getHashUrl(int mirrorId) {
		return getUrl(getPath() + ".md5", mirrorId);
	}

	protected String getUrl(String path, int mirrorId) {
		List<String> params = new ArrayList<String>();
		params.add("r=1");
		if (mirrorId > 0) {
			params.add("mirror_id=" + mirrorId);
		}
		params.add("file=" + path);
		return getUrl(params);
	}

	protected String getUrl(List<String> params) {
		return ECLIPSE_DOWNLOAD_URL + "?" + String.join("&", params);
	}

	public String getName() {
		if (getPath() == null) {
			return null;
		}
		int index = getPath().lastIndexOf('/');
		return getPath().substring(index + 1);
	}

	public static EclipseConfig load(String version, String os, String arch)
			throws JsonParseException, JsonMappingException, IOException {
		String configFileName = "/eclipse/" + version + ".json";
		InputStream configInputStream = EclipseConfig.class.getResourceAsStream(configFileName);
		ObjectMapper mapper = new ObjectMapper();
		EclipseConfig[] configs = mapper.readValue(configInputStream, EclipseConfig[].class);

		for (EclipseConfig config : configs) {
			if (config.isSupported(os, arch)) {
				return config;
			}
		}

		return null;
	}

	public static String getArchiveName(String version) {
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

		return "eclipse-" + version + "-" + platform + "." + archive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arch == null) ? 0 : arch.hashCode());
		result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
		result = prime * result + ((os == null) ? 0 : os.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EclipseConfig other = (EclipseConfig) obj;
		if (arch == null) {
			if (other.arch != null)
				return false;
		} else if (!arch.equals(other.arch))
			return false;
		if (md5 == null) {
			if (other.md5 != null)
				return false;
		} else if (!md5.equals(other.md5))
			return false;
		if (os == null) {
			if (other.os != null)
				return false;
		} else if (!os.equals(other.os))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EclipseConfig [os=" + os + ", arch=" + arch + ", path=" + path + ", md5=" + md5 + "]";
	}

}
