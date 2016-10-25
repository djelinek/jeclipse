package org.apodhrad.jeclipse.manager;

import java.io.File;

import org.apodhrad.jdownload.manager.hash.Hash;

/**
 * 
 * @author apodhrad
 *
 */
public class JBDSConfig {

	private File target;
	private String installerUrl;
	private Hash installerHash;
	private File installerJarFile;
	private String jreLocation;
	private String[] ius;
	private String[] runtimes;


}
