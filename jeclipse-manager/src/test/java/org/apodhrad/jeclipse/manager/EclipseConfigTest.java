package org.apodhrad.jeclipse.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author apodhrad
 *
 */
public class EclipseConfigTest {

	@Test
	public void testGettingName() {
		assertEquals("hello.zip", eclispeConfig("/com/example/hello.zip", null).getName());
	}

	@Test
	public void testGettingNameWithPathWithoutSlash() {
		assertEquals("hello.zip", eclispeConfig("hello.zip", null).getName());
	}

	@Test
	public void testGettingNameWithNullPath() {
		assertEquals(null, eclispeConfig(null, null).getName());
	}

	@Test
	public void testGettingUrlWithDefaultMirror() {
		assertEquals("http://www.eclipse.org/downloads/download.php?r=1&mirror_id=1196&file=/com/example/hello.zip",
				eclispeConfig("/com/example/hello.zip", null).getUrl());
	}

	@Test
	public void testGettingUrlWithSpecifiedMirror() {
		assertEquals("http://www.eclipse.org/downloads/download.php?r=1&mirror_id=123&file=/com/example/hello.zip",
				eclispeConfig("/com/example/hello.zip", null).getUrl(123));
	}

	@Test
	public void testGettingUrlWithNoMirror() {
		assertEquals("http://www.eclipse.org/downloads/download.php?r=1&file=/com/example/hello.zip",
				eclispeConfig("/com/example/hello.zip", null).getUrl(0));
	}
	
	@Test
	public void testGettingHashUrl() {
		assertEquals("http://www.eclipse.org/downloads/download.php?r=1&mirror_id=1196&file=hello.zip.md5",
				eclispeConfig("hello.zip", "eb05524b6eca8ee7aa3a657788bcdbb5").getHashUrl());
	}
	
	@Test
	public void testGettingHashUrlWithMirrorId() {
		assertEquals("http://www.eclipse.org/downloads/download.php?r=1&mirror_id=123&file=hello.zip.md5",
				eclispeConfig("hello.zip", "eb05524b6eca8ee7aa3a657788bcdbb5").getHashUrl(123));
	}

	@Test
	public void testSupportedPlatform() {
		EclipseConfig config = eclispeConfig("mac", "64", null, null);
		assertTrue(config.isSupported("Mac OS X", "x86_64"));
	}

	@Test
	public void testUnsupportedPlatform() {
		EclipseConfig config = eclispeConfig("mac", "64", null, null);
		assertFalse(config.isSupported("Linux", "i386"));
		assertFalse(config.isSupported("Linux", "amd64"));
		assertFalse(config.isSupported("Mac OS X", "i386"));
		assertFalse(config.isSupported("Windows 10", "x86"));
		assertFalse(config.isSupported("Windows 10", "amd64"));
	}

	@Test
	public void testLoadingFromFileForWindows() throws Exception {
		EclipseConfig expectedConfig = eclispeConfig("win", "32",
				"/technology/epp/downloads/release/mars/1/eclipse-jee-mars-1-win32.zip",
				"fd0e5ceebc2a5b40274cb6146e78a4c3");
		EclipseConfig loadedConfig = EclipseConfig.load("jee-mars-1", "Windows 7", "x86");
		assertEquals(expectedConfig, loadedConfig);
	}

	@Test
	public void testLoadingFromFileForLinux() throws Exception {
		EclipseConfig expectedConfig = eclispeConfig("linux", "64",
				"/technology/epp/downloads/release/mars/1/eclipse-jee-mars-1-linux-gtk-x86_64.tar.gz",
				"72a722a59a43e8ed6c47ae279fb3d355");
		EclipseConfig loadedConfig = EclipseConfig.load("jee-mars-1", "Linux", "amd64");
		assertEquals(expectedConfig, loadedConfig);
	}

	@Test
	public void testLoadingFromFileForMac() throws Exception {
		EclipseConfig expectedConfig = eclispeConfig("mac", "64",
				"/technology/epp/downloads/release/mars/1/eclipse-jee-mars-1-macosx-cocoa-x86_64.tar.gz",
				"8d053622066166886d4a7116fd18dc93");
		EclipseConfig loadedConfig = EclipseConfig.load("jee-mars-1", "Mac OS X", "x86_64");
		assertEquals(expectedConfig, loadedConfig);
	}

	private static EclipseConfig eclispeConfig(String path, String md5) {
		return eclispeConfig(null, null, path, md5);
	}

	private static EclipseConfig eclispeConfig(String os, String arch, String path, String md5) {
		EclipseConfig eclipseConfig = new EclipseConfig();
		eclipseConfig.setOs(os);
		eclipseConfig.setArch(arch);
		eclipseConfig.setPath(path);
		eclipseConfig.setMd5(md5);
		return eclipseConfig;
	}
}
