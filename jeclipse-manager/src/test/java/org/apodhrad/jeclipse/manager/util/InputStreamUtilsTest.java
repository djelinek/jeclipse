package org.apodhrad.jeclipse.manager.util;

import static org.apodhrad.jeclipse.manager.util.InpuStreamUtils.copyToFileAndReplace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class InputStreamUtilsTest {

	@Rule
	public TemporaryFolder target = new TemporaryFolder();

	@Test
	public void testCreatingInstance() {
		new InpuStreamUtils();
	}
	
	@Test
	public void testCopyingWithEmptyReplacing() throws Exception {
		File source = target.newFile();
		File destination = new File(target.getRoot(), "destination.txt");

		Collection<String> originLines = Arrays.asList(new String[] { "Hello @NAME@", "Hello John Doe" });
		Collection<String> expectedLines = Arrays.asList(new String[] { "Hello @NAME@", "Hello John Doe" });

		FileUtils.writeLines(source, originLines);
		copyToFileAndReplace(new FileInputStream(source), destination, new HashMap<String, String>());
		assertTrue(destination.exists());
		assertEquals(expectedLines, FileUtils.readLines(destination));
	}

	@Test
	public void testCopyingWithSimpleReplacing() throws Exception {
		File source = target.newFile();
		File destination = new File(target.getRoot(), "destination.txt");

		Collection<String> originLines = Arrays.asList(new String[] { "Hello @NAME@", "Hello John Doe" });
		Collection<String> expectedLines = Arrays.asList(new String[] { "Hello World", "Hello John Doe" });

		FileUtils.writeLines(source, originLines);
		Map<String, String> map = new HashMap<String, String>();
		map.put("@NAME@", "World");
		copyToFileAndReplace(new FileInputStream(source), destination, map);
		assertTrue(destination.exists());
		assertEquals(expectedLines, FileUtils.readLines(destination));
	}

	@Test
	public void testCopyingWithComplexReplacing() throws Exception {

		File source = target.newFile();
		File destination = new File(target.getRoot(), "destination.txt");

		Collection<String> originLines = Arrays.asList(new String[] { "Hello @NAME1@", "Hello @NAME2@" });
		Collection<String> expectedLines = Arrays.asList(new String[] { "Hello World1", "Hello World2" });

		FileUtils.writeLines(source, originLines);
		Map<String, String> map = new HashMap<String, String>();
		map.put("@NAME1@", "World1");
		map.put("@NAME2@", "World2");
		copyToFileAndReplace(new FileInputStream(source), destination, map);
		assertTrue(destination.exists());
		assertEquals(expectedLines, FileUtils.readLines(destination));
	}

	@Test
	public void testCopyingWithPartiallyReplacing() throws Exception {
		File source = target.newFile();
		File destination = new File(target.getRoot(), "destination.txt");

		Collection<String> originLines = Arrays.asList(new String[] { "Hello @NAME1@", "Hello @NAME2@" });
		Collection<String> expectedLines = Arrays.asList(new String[] { "Hello @NAME1@", "Hello World2" });

		FileUtils.writeLines(source, originLines);
		Map<String, String> map = new HashMap<String, String>();
		map.put("@NAME@", "World1");
		map.put("@NAME2@", "World2");
		copyToFileAndReplace(new FileInputStream(source), destination, map);
		assertTrue(destination.exists());
		assertEquals(expectedLines, FileUtils.readLines(destination));
	}	
	
	@Test
	public void testCopyingWithNullReplacing() throws Exception {
		File source = target.newFile();
		File destination = new File(target.getRoot(), "destination.txt");

		Collection<String> originLines = Arrays.asList(new String[] { "Hello @NAME@", "Hello John Doe" });
		Collection<String> expectedLines = Arrays.asList(new String[] { "Hello @NAME@", "Hello John Doe" });

		FileUtils.writeLines(source, originLines);
		copyToFileAndReplace(new FileInputStream(source), destination, null);
		assertTrue(destination.exists());
		assertEquals(expectedLines, FileUtils.readLines(destination));
	}
}
