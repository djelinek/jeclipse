package org.apodhrad.jeclipse.manager.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * 
 * @author apodhrad
 *
 */
public class StartsWith extends BaseMatcher<String> {

	private String prefix;

	public StartsWith(String prefix) {
		this.prefix = prefix;
	}

	public boolean matches(Object obj) {
		if (prefix == null) {
			return true;
		}
		if (obj instanceof String) {
			return ((String) obj).startsWith(prefix);
		}
		return false;
	}

	public void describeTo(Description desc) {
		desc.appendText("starts with " + prefix);

	}

}
