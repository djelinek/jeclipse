package org.apodhrad.jeclipse.manager.util;

import java.io.File;

/**
 * 
 * @author apodhrad
 *
 */
public class FileSearch extends BFS<File> {

	@Override
	public File[] getChildren(File file) {
		File[] children = file.listFiles();
		if (children == null) {
			children = new File[] {};
		}
		return children;
	}

}
