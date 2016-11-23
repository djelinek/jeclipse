package org.apodhrad.jeclipse.manager;

import java.io.IOException;
import java.io.InputStream;

import org.apodhrad.jeclipse.manager.util.EclipseUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author apodhrad
 *
 */
public class EclipseConfig {

	private String version;
	private String os;
	private String arch;
	private String path;
	private String md5;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

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
		return getUrl(EclipseUtils.ECLIPSE_DEFAULT_MIRROR_ID);
	}

	public String getUrl(int mirrorId) {
		return EclipseUtils.getUrl(getPath(), mirrorId);
	}

	public String getHashUrl() {
		return getHashUrl(EclipseUtils.ECLIPSE_DEFAULT_MIRROR_ID);
	}

	public String getHashUrl(int mirrorId) {
		return EclipseUtils.getUrl(getPath() + ".md5", mirrorId);
	}

	public String getArchiveName() {
		if (getPath() == null) {
			return null;
		}
		int index = getPath().lastIndexOf('/');
		return getPath().substring(index + 1);
	}

	public static EclipseConfig load(InputStream configInputStream, String os, String arch)
			throws JsonParseException, JsonMappingException, IOException {
		if (configInputStream == null) {
			throw new IllegalArgumentException("The input stream for loading eclipse config is null");
		}
		ObjectMapper mapper = new ObjectMapper();
		EclipseConfig[] configs = mapper.readValue(configInputStream, EclipseConfig[].class);

		for (EclipseConfig config : configs) {
			if (config.isSupported(os, arch)) {
				return config;
			}
		}

		throw new EclipseException("Cannot find a config for OS '" + os + "' with arch '" + arch + "'");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (arch == null) {
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
		return "EclipseConfig [version = " + version + ", os=" + os + ", arch=" + arch + ", path=" + path + ", md5="
				+ md5 + "]";
	}

}
