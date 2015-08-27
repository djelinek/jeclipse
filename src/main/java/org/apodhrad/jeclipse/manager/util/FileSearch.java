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
		return file.listFiles();
	}

}
