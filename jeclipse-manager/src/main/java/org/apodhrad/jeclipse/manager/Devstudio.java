package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.IOException;

import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.hash.Hash;
import org.apodhrad.jdownload.manager.hash.NullHash;
import org.apodhrad.jeclipse.manager.util.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author apodhrad
 *
 */
public class Devstudio extends Eclipse {
	
	public static final String CORE_PLUGIN = "com.jboss.devstudio.core";

	private static Logger log = LoggerFactory.getLogger(Devstudio.class);

	public Devstudio(String path) {
		super(path);
	}

	public Devstudio(File file) {
		super(file);
	}

	public static Devstudio installJBDS(File target, String url) throws IOException {
		return installJBDS(target, url, new NullHash(), null);
	}

	public static Devstudio installJBDS(File target, String url, String jreLocation) throws IOException {
		return installJBDS(target, url, new NullHash(), jreLocation);
	}

	public static Devstudio installJBDS(File target, String url, Hash hash) throws IOException {
		return installJBDS(target, url, hash, null);
	}

	public static Devstudio installJBDS(File target, String url, Hash hash, String jreLocation, String... ius)
			throws IOException {
		JDownloadManager manager = new JDownloadManager();
		File installerJarFile = manager.download(url, target, hash);
		return installJBDS(target, installerJarFile, jreLocation, ius);
	}

	public static Devstudio installJBDS(File target, File installerJarFile, String jreLocation, String... ius)
			throws IOException {
		if (target.isDirectory()) {
			target = new File(target, "jbdevstudio");
		}
		// Install JBDS
		String installationFile = null;
		try {
			installationFile = createInstallationFile(target, installerJarFile, jreLocation, ius);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new RuntimeException("Exception occured during creating installation file");
		}

		// Switch IzPack mode to privileged on Windows
		if (OSUtils.isWindows()) {
			System.setProperty("izpack.mode", "privileged");
		}

		JarRunner jarRunner = new JarRunner(installerJarFile.getAbsolutePath());
		jarRunner.setExecutionOutput(new EclipseExecutionOutput());
		jarRunner.setTimeout(getJEclipseTimeout());
		jarRunner.execute(installationFile);

		return new Devstudio(target);
	}
	
	public static Devstudio installJBDS(File target, File installerJarFile, File installerConfigFile)
			throws IOException {
		// Switch IzPack mode to privileged on Windows
		if (OSUtils.isWindows()) {
			System.setProperty("izpack.mode", "privileged");
		}

		JarRunner jarRunner = new JarRunner(installerJarFile.getAbsolutePath());
		jarRunner.setExecutionOutput(new EclipseExecutionOutput());
		jarRunner.setTimeout(getJEclipseTimeout());
		jarRunner.execute(installerConfigFile.getAbsolutePath());

		return new Devstudio(new File(target, "jbdevstudio"));
	}

	public static String createInstallationFile(File target, File installerJarFile, String jreLocation, String... ius)
			throws IOException {
		DevstudioConfig config = DevstudioConfig.createFromInstallerName(installerJarFile.getName());
		config.setTarget(target.getAbsolutePath());
		config.setJre(jreLocation);
		for (String iu : ius) {
			config.addFeature(iu);
		}
		return createInstallationFile(config);
	}

	public static String createInstallationFile(DevstudioConfig config) throws IOException {
		return config.toFile(new File(config.getTarget(), "installation.xml")).getAbsolutePath();
	}

	public String getCoreVersion() {
		Bundle platformPlugin = getPlugin(CORE_PLUGIN);
		if (platformPlugin != null) {
			return platformPlugin.getVersion();
		}
		return null;
	}

	
	public static String getJBDSVersion(File installer) {
		return getJBDSVersion(installer.getName());
	}

	public static String getJBDSVersion(String installer) {
		String[] part = installer.split("-");
		for (int i = 0; i < part.length; i++) {
			if (part[i].contains(".")) {
				return part[i];
			}
		}
		return null;
	}
}
