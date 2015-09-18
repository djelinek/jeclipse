package org.apodhrad.jeclipse.manager.matcher;

import java.io.File;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * 
 * @author apodhrad
 *
 */
public class FileNameStartsWith extends BaseMatcher<File> {

	private String prefix;

	public FileNameStartsWith(String prefix) {
		this.prefix = prefix;
	}

	public boolean matches(Object obj) {
		if (prefix == null) {
			return true;
		}
		if (obj instanceof File) {
			return ((File) obj).getName().startsWith(prefix);
		}
		return false;
	}

	public void describeTo(Description desc) {
		desc.appendText("file name starts with " + prefix);

	}

}
