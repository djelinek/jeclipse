package org.apodhrad.jeclipse.manager.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

/**
 * 
 * @author apodhrad
 *
 */
public class FileSearch {

	private List<File> result;

	public FileSearch() {
		result = new ArrayList<File>();
	}

	public List<File> getResult() {
		return result;
	}

	public void find(String path, Matcher<File> matcher) {
		File file = new File(path);
		find(file, matcher);
	}

	public void find(File file, Matcher<File> matcher) {
		if (matcher.matches(file)) {
			result.add(file);
		}
		if (!file.isDirectory()) {
			return;
		}
		File[] list = file.listFiles();
		for (int i = 0; i < list.length; i++) {
			find(list[i], matcher);
		}
	}
}
