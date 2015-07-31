package org.apodhrad.jeclipse.manager.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * 
 * @author apodhrad
 *
 */
public class IsVersion extends BaseMatcher<String> {

	public boolean matches(Object obj) {
		if (obj instanceof String) {
			return ((String) obj).matches("^[\\d]+\\.[\\d]+\\.[\\d]+$");
		}
		return false;
	}

	public void describeTo(Description desc) {
		desc.appendText("starts with a number");

	}

}
