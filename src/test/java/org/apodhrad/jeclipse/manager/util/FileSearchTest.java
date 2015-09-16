package org.apodhrad.jeclipse.manager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apodhrad.jeclipse.manager.matcher.FileNameStartsWith;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSearchTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void before() throws IOException {
		folder.newFile("test1.txt");
		File dir = folder.newFolder("dir");
		File.createTempFile("test2.txt", null, dir);
	}

	@Test
	public void findTest() {
		FileSearch fileSearch = new FileSearch();
		List<File> files = fileSearch.find(folder.getRoot(), new FileNameStartsWith("test"));
		assertEquals(2, files.size());
		assertTrue(files.get(0).getName().startsWith("test"));
		assertTrue(files.get(1).getName().startsWith("test"));
	}
}
