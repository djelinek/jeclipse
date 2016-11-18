package org.apodhrad.jeclipse.manager;

public enum TestingPlatform {

	Local(System.getProperty("os.name"), System.getProperty("os.arch")), Fedora24_32("Linux", "i386"), Fedora24_64(
			"Linux",
			"amd64"), Mac_10_11("Mac OS X", "x86_64"), Win10_32("Windows 10", "x86"), Win10_64("Windows 10", "amd64");

	private String os;
	private String arch;

	private TestingPlatform(String os, String arch) {
		this.os = os;
		this.arch = arch;
	}

	public String getOs() {
		return os;
	}

	public String getArch() {
		return arch;
	}

	public void apply() {
		System.setProperty("os.name", getOs());
		System.setProperty("os.arch", getArch());
	}
}