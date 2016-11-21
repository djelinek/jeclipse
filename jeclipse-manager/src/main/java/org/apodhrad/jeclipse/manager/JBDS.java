package org.apodhrad.jeclipse.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.StringJoiner;

import org.apache.commons.io.FileUtils;
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
public class JBDS extends Eclipse {

	private static Logger log = LoggerFactory.getLogger(JBDS.class);

	public JBDS(String path) {
		super(path);
	}

	public JBDS(File file) {
		super(file);
	}

	public static JBDS installJBDS(File target, String url) throws IOException {
		return installJBDS(target, url, new NullHash(), null);
	}

	public static JBDS installJBDS(File target, String url, String jreLocation) throws IOException {
		return installJBDS(target, url, new NullHash(), jreLocation);
	}

	public static JBDS installJBDS(File target, String url, Hash hash) throws IOException {
		return installJBDS(target, url, hash, null);
	}

	public static JBDS installJBDS(File target, String url, Hash hash, String jreLocation, String... ius)
			throws IOException {
		JDownloadManager manager = new JDownloadManager();
		File installerJarFile = manager.download(url, target, hash);
		return installJBDS(target, installerJarFile, jreLocation, ius);
	}

	public static JBDS installJBDS(File target, File installerJarFile, String jreLocation, String... ius)
			throws IOException {
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

		JarRunner jarRunner = new JarRunner(installerJarFile.getAbsolutePath(), installationFile);
		jarRunner.setOutput(new EclipseExecutionOutput());
		jarRunner.setTimeout(getJEclipseTimeout());
		jarRunner.run();

		return new JBDS(new File(target, "jbdevstudio"));
	}

	public static String createInstallationFile(File target, File installerJarFile, String jreLocation, String... ius)
			throws IOException {
		JBDSConfig config = new JBDSConfig();
		config.setTarget(target);
		config.setInstallerJarFile(installerJarFile);
		config.setJreLocation(jreLocation);
		for (String iu : ius) {
			config.addInstallableUnit(iu);
		}
		return createInstallationFile(config);
	}

	public static String createInstallationFile(JBDSConfig config) throws IOException {
		File jre = OSUtils.getJre(config.getJreLocation());
		log.info("JRE: " + jre);
		if (jre == null) {
			throw new IllegalStateException("Cannot find JRE location!");
		}

		StringJoiner iuList = new StringJoiner(",");
		iuList.add("com.jboss.devstudio.core.package");
		iuList.add("org.testng.eclipse.feature.group");
		for (String feature : config.getInstallabelUnits()) {
			iuList.add(feature);
		}

		StringJoiner runtimeList = new StringJoiner(",");
		runtimeList.setEmptyValue("");
		for (String runtime : config.getRuntimes()) {
			runtimeList.add(runtime);
		}

		String group = "devstudio";

		String jbdsVersion = getJBDSVersion(config.getInstallerJarFile());
		StringJoiner productList = new StringJoiner(",");
		productList.add(jbdsVersion.startsWith("10") ? "devstudio" : "jbds");
		if (!config.getInstallabelUnits().isEmpty()) {
			productList.add(jbdsVersion.startsWith("10") ? "devstudio-is" : "jbdsis");
		}

		String dest = new File(config.getTarget(), "jbdevstudio").getAbsolutePath();

		String tempFile = new File(config.getTarget(), "/install.xml").getAbsolutePath();
		String targetFile = new File(config.getTarget(), "/installation.xml").getAbsolutePath();

		String sourceFile = "/install.xml";
		if (jbdsVersion != null && jbdsVersion.startsWith("8")) {
			sourceFile = "/install-8.xml";
		}
		if (jbdsVersion != null && jbdsVersion.startsWith("9")) {
			sourceFile = "/install-9.xml";
		}
		if (jbdsVersion != null && jbdsVersion.startsWith("10")) {
			sourceFile = "/install-10.xml";
			if (runtimeList.length() > 0) {
				sourceFile = "/install-10-runtime.xml";
			}
			if (config.getInstallerJarFile().getName().contains("eap")) {
				group = "jbosseap";
				sourceFile = "/install-10-runtime.xml";
			}
		}
		URL url = JBDS.class.getResource(sourceFile);

		FileUtils.copyURLToFile(url, new File(tempFile));
		BufferedReader in = new BufferedReader(new FileReader(tempFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(targetFile));
		String line = null;
		while ((line = in.readLine()) != null) {
			line = line.replace("@DEST@", dest);
			line = line.replace("@GROUP@", group);
			line = line.replace("@JRE@", jre.getAbsolutePath());
			line = line.replace("@IUS@", iuList.toString());
			line = line.replace("@PRODUCTS@", productList.toString());
			line = line.replace("@RUNTIMES@", runtimeList.toString());
			out.write(line);
			out.newLine();
		}
		out.flush();
		out.close();
		in.close();

		new File(tempFile).delete();
		return targetFile;
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
