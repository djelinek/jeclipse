package org.apodhrad.jeclipse.manager;

import static org.apodhrad.jeclipse.manager.Eclipse.ECLIPSE_DEFAULT_MIRROR;
import static org.apodhrad.jeclipse.manager.Eclipse.getEclipseUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

import org.apodhrad.jeclipse.manager.util.OS;
import org.junit.Test;

public class EclipseTest {

	@Test
	public void testGettingEclipseUrlJEEOxygen3a() {
		assumeFalse(OS.isMac() || OS.isWindows());

		String expectedUrl = ECLIPSE_DEFAULT_MIRROR + "/oxygen/3a/eclipse-jee-oxygen-3a-linux-gtk-x86_64.tar.gz";
		String actualUrl = getEclipseUrl("jee-oxygen-3a", Eclipse.ECLIPSE_DEFAULT_MIRROR);
		assertEquals(expectedUrl, actualUrl);
	}

	@Test
	public void testGettingEclipseUrlJEEPhotonR() {
		assumeFalse(OS.isMac() || OS.isWindows());

		String expectedUrl = ECLIPSE_DEFAULT_MIRROR + "/photon/R/eclipse-jee-photon-R-linux-gtk-x86_64.tar.gz";
		String actualUrl = getEclipseUrl("jee-photon-R", Eclipse.ECLIPSE_DEFAULT_MIRROR);
		assertEquals(expectedUrl, actualUrl);
	}

	@Test
	public void testGettingEclipseUrlJEE201903R() {
		assumeFalse(OS.isMac() || OS.isWindows());

		String expectedUrl = ECLIPSE_DEFAULT_MIRROR + "/2019-03/R/eclipse-jee-2019-03-R-linux-gtk-x86_64.tar.gz";
		String actualUrl = getEclipseUrl("jee-2019-03-R", Eclipse.ECLIPSE_DEFAULT_MIRROR);
		assertEquals(expectedUrl, actualUrl);
	}
	
}
