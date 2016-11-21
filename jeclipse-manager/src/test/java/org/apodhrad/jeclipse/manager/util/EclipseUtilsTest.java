package org.apodhrad.jeclipse.manager.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class EclipseUtilsTest {

	@Test
	public void testCreatingInstance() {
		new EclipseUtils();
	}
	
	@Test
	public void testGettingArchiveNameForLinux() {
		assertEquals("eclipse-jee-mars-1-linux-gtk.tar.gz", EclipseUtils.getArchiveName("jee-mars-1", "Linux", "i386"));
	}

	@Test
	public void testGettingArchiveNameForMac() {
		assertEquals("eclipse-jee-mars-1-macosx-cocoa-x86_64.tar.gz",
				EclipseUtils.getArchiveName("jee-mars-1", "Mac OS X", "x86_64"));
	}

	@Test
	public void testGettingArchiveNameForWin() {
		assertEquals("eclipse-jee-mars-1-win32.zip", EclipseUtils.getArchiveName("jee-mars-1", "Windows 7", "x86"));
	}

	@Test
	public void testGettingArchiveNameForSolaris() {
		try {
			EclipseUtils.getArchiveName("jee-mars-1", "SunOS", "spark");
			Assert.fail("IllegalArgumentException was expected");
		} catch (IllegalArgumentException iae) {
			assertEquals("Cannot get archive name for OS 'SunOS'", iae.getMessage());
		}
	}

	@Test
	public void testGettingPathFromVersion() {
		assertEquals("/technology/epp/downloads/release/mars/1/eclipse-jee-mars-1-linux-gtk.tar.gz",
				EclipseUtils.getPathFromVersion("jee-mars-1", "Linux", "i386"));
	}
}
