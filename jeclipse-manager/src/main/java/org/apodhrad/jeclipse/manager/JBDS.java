package org.apodhrad.jeclipse.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.hash.Hash;
import org.apodhrad.jdownload.manager.hash.NullHash;
import org.apodhrad.jeclipse.manager.matcher.IsJavaExecutable;
import org.apodhrad.jeclipse.manager.util.FileSearch;
import org.apodhrad.jeclipse.manager.util.OS;
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
		return installJBDS(target, url, null, new NullHash());
	}

	public static JBDS installJBDS(File target, String url, String jreLocation) throws IOException {
		return installJBDS(target, url, jreLocation, new NullHash());
	}

	public static JBDS installJBDS(File target, String url, Hash hash) throws IOException {
		return installJBDS(target, url, null, hash);
	}

	public static JBDS installJBDS(File target, String url, String jreLocation, Hash hash) throws IOException {
		JDownloadManager manager = new JDownloadManager();
		File installerJarFile = manager.download(url, target, hash);

		// Install JBDS
		String installationFile = null;
		try {
			installationFile = createInstallationFile(target, getJBDSVersion(installerJarFile), jreLocation);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new RuntimeException("Exception occured during creating installation file");
		}

		// Switch IzPack mode to privileged on Windows
		if (OS.isWindows()) {
			System.setProperty("izpack.mode", "privileged");
		}

		JarRunner jarRunner = new JarRunner(installerJarFile.getAbsolutePath(), installationFile);
		jarRunner.setOutput(new EclipseExecutionOutput());
		jarRunner.setTimeout(20 * 60); // 20 minutes
		jarRunner.run();

		return new JBDS(new File(target, "jbdevstudio"));
	}

	private static String createInstallationFile(File target, String jbdsVersion, String jreLocation)
			throws IOException {
		String jre = getJreLocation(jreLocation);
		if (jre == null) {
			throw new IllegalStateException("Cannot find JRE location!");
		}

		String dest = new File(target, "jbdevstudio").getAbsolutePath();

		String tempFile = new File(target, "/install.xml").getAbsolutePath();
		String targetFile = new File(target, "/installation.xml").getAbsolutePath();

		String sourceFile = "/install.xml";
		if (jbdsVersion != null && jbdsVersion.startsWith("8")) {
			sourceFile = "/install-8.xml";
		}
		if (jbdsVersion != null && jbdsVersion.startsWith("9")) {
			sourceFile = "/install-9.xml";
		}
		URL url = JBDS.class.getResource(sourceFile);

		FileUtils.copyURLToFile(url, new File(tempFile));
		BufferedReader in = new BufferedReader(new FileReader(tempFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(targetFile));
		String line = null;
		while ((line = in.readLine()) != null) {
			out.write(line.replace("@DEST@", dest).replace("@JRE@", jre));
			out.newLine();
		}
		out.flush();
		out.close();
		in.close();

		new File(tempFile).delete();
		return targetFile;
	}

	private static String getJreLocation(String location) {
		String jreLoc = null;

		// find jre location from java home
		String javaHome = location;
		if (location == null || location.length() == 0) {
			javaHome = System.getProperty("java.home");
		}
		log.info("JRE: " + javaHome);
		FileSearch fileSearch = new FileSearch();
		List<File> result = fileSearch.find(new File(javaHome), new IsJavaExecutable());
		if (!result.isEmpty()) {
			jreLoc = result.get(0).getAbsolutePath();
		}

		return jreLoc;
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
