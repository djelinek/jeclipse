package org.apodhrad.jeclipse.manager.matcher;

import java.io.File;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * 
 * @author apodhrad
 * 
 */
public class IsJavaExecutable extends BaseMatcher<File> {

	public boolean matches(Object obj) {
		if (obj instanceof File) {
			File file = (File) obj;
			String fileName = file.getName();
			return fileName.equalsIgnoreCase("java") || fileName.equalsIgnoreCase("java.exe");
		}
		return false;
	}

	public void describeTo(Description desc) {
		desc.appendText("is java executable");
	}

}
