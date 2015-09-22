package org.apodhrad.jeclipse.manager;

import java.io.File;

public class Bundle implements Comparable<Bundle> {

	private String name;
	private String version;
	private File file;

	public Bundle(File file) {
		this.file = file;
		String fullName = file.getName();
		int index = fullName.lastIndexOf('_');

		this.name = fullName.substring(0, index);
		this.version = fullName.substring(index + 1).replaceAll(".jar", "");
	}
	
	public Bundle(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String getFullName() {
		return name + "_" + version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public File getFile() {
		return file;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + ((name == null) ? 0 : name.hashCode());
		result = 31 * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bundle) {
			Bundle bundle = (Bundle) obj;
			return getFullName().equals(bundle.getFullName());
		}
		return false;
	}

	@Override
	public String toString() {
		return getFullName();
	}

	public int compareTo(Bundle bundle) {
		return name.compareTo(bundle.getName());
	}

}
