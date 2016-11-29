package org.apodhrad.jeclipse.maven.plugin.fake;

import java.util.ArrayList;
import java.util.List;

public class EclipseLauncher {
	
	private static List<String[]> executedCommands = new ArrayList<String[]>();
	
	public static void main(String[] args) {
		System.out.println("Operation completed in 10 ms.");
	}
	

}
