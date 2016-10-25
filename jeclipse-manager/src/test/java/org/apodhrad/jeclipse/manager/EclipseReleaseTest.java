package org.apodhrad.jeclipse.manager;

import org.junit.Assert;
import org.junit.Test;

public class EclipseReleaseTest {

	@Test
	public void getEclipseUrlTestWithoutMirror() {
		Assert.assertEquals("http://www.eclipse.org/downloads/download.php?r=1&file=/technology/epp/downloads/release/", EclipseRelease.getEclipseUrl("jee-mars-1"));
	}
}
