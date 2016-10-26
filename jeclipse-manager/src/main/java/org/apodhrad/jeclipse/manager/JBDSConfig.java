package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apodhrad.jdownload.manager.hash.Hash;

/**
 * 
 * @author apodhrad
 *
 */
public class JBDSConfig {

	private File target;
//	private String installerUrl;
//	private Hash installerHash;
	private File installerJarFile;
	private String jreLocation;
	private List<String> installableUnits;
	private List<String> runtimes;
	
	public JBDSConfig() {
		installableUnits = new ArrayList<String>();
		runtimes = new ArrayList<String>();
	}

	public File getTarget() {
		return target;
	}

	public void setTarget(File target) {
		this.target = target;
	}

//	public String getInstallerUrl() {
//		return installerUrl;
//	}
//
//	public void setInstallerUrl(String installerUrl) {
//		this.installerUrl = installerUrl;
//	}
//
//	public Hash getInstallerHash() {
//		return installerHash;
//	}
//
//	public void setInstallerHash(Hash installerHash) {
//		this.installerHash = installerHash;
//	}

	public File getInstallerJarFile() {
		return installerJarFile;
	}

	public void setInstallerJarFile(File installerJarFile) {
		this.installerJarFile = installerJarFile;
	}

	public String getJreLocation() {
		return jreLocation;
	}

	public void setJreLocation(String jreLocation) {
		this.jreLocation = jreLocation;
	}

	public List<String> getInstallabelUnits() {
		return installableUnits;
	}

	public void addInstallableUnit(String iu) {
		installableUnits.add(iu);
	}

	public List<String> getRuntimes() {
		return runtimes;
	}

	public void addRuntime(String runtime) {
		runtimes.add(runtime);
	}

}
