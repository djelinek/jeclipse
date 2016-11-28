package org.apodhrad.jeclipse.manager.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class XMLUtilsTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testGettingTextByTagName() throws Exception {
		File xmlFile = writeToFile("<abc><xyz>hello</xyz></abc>");
		assertEquals("hello", XMLUtils.getTextByTagName(xmlFile, "xyz"));
	}
	
	@Test
	public void testGettingTextByTagNameWithMoreTags() throws Exception {
		File xmlFile = writeToFile("<abc><xyz>hello</xyz><xyz>world</xyz></abc>");
		assertEquals("world", XMLUtils.getTextByTagName(xmlFile, "xyz", 1));
	}

	private File writeToFile(String content) throws IOException {
		File file = tempFolder.newFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(content);
		out.flush();
		out.close();
		return file;
	}
}
