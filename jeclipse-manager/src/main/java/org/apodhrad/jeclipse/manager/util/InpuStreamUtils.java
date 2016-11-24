package org.apodhrad.jeclipse.manager.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author apodhrad
 *
 */
public class InpuStreamUtils {

	public static void copyToFileAndReplace(InputStream source, File destination, Map<String, String> map)
			throws IOException {
		if (map == null) {
			map = new HashMap<String, String>();
		}
		
		FileUtils.copyInputStreamToFile(source, destination);
		List<String> originLines = FileUtils.readLines(destination);
		List<String> revisedLines = new ArrayList<String>();
		for (String line : originLines) {
			for (String var : map.keySet()) {
				line = line.replace(var, map.get(var));
			}
			revisedLines.add(line);
		}
		FileUtils.writeLines(destination, revisedLines);
	}

}
