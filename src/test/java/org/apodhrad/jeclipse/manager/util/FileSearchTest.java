package org.apodhrad.jeclipse.manager.util;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSearchTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void before() {
		System.out.println("\t->" + folder.getRoot().getAbsolutePath());
	}
	
	@Test
	public void findTest() {
		
	}
	
	@Test
	public void find2Test() {
		
	}
}
