package org.apodhrad.jeclipse.manager.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * 
 * @author apodhrad
 *
 */
public class EndsWith extends BaseMatcher<String> {

	private String suffix;

	public EndsWith(String suffix) {
		this.suffix = suffix;
	}

	public boolean matches(Object obj) {
		if (obj instanceof String) {
			return ((String) obj).endsWith(suffix);
		}
		return false;
	}

	public void describeTo(Description desc) {
		desc.appendText("ends with " + suffix);

	}

}
