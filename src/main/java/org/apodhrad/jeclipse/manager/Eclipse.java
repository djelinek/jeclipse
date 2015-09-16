package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jeclipse.manager.matcher.FileNameStartsWith;
import org.apodhrad.jeclipse.manager.util.FileSearch;
import org.apodhrad.jeclipse.manager.util.OS;

/**
 * This class represents an eclipse instance
 * 
 * @author apodhrad
 * 
 */
public class Eclipse {

	public static final String ECLIPSE_DEFAULT_VERSION = "jee-luna-SR2";
	public static final String ECLIPSE_DEFAULT_MIRROR = "http://www.eclipse.org/downloads/download.php?r=1&file=/technology/epp/downloads/release";

	private File jarFile;
	private Set<String> updateSites;

	private static final String LAUNCHER_PREFIX = "org.eclipse.equinox.launcher_";

	public Eclipse(String path) {
		this(new File(path));
	}

	public Eclipse(File file) {
		List<File> launchers = null;
		if (file.isDirectory()) {
			launchers = new FileSearch().find(file, new FileNameStartsWith(LAUNCHER_PREFIX));
			if (launchers.isEmpty()) {
				throw new EclipseException("Cannot find any eclipse structure in '" + file.getAbsolutePath() + "'");
			}
			if (launchers.size() > 1) {
				throw new EclipseException("There are more eclipse structures in '" + file.getAbsolutePath() + "'");
			}
			file = launchers.get(0);
		}
		if (!isEclipseStructure(file)) {
			throw new EclipseException("Cannot find any eclipse structure in '" + file.getAbsolutePath() + "'");
		}
		this.jarFile = file;
		this.updateSites = new HashSet<String>();
	}

	public File getLauncher() {
		return jarFile;
	}

	private static boolean isEclipseStructure(File launcher) {
		if (launcher.isDirectory() || !launcher.exists()) {
			return false;
		}
		if (!launcher.getName().startsWith(LAUNCHER_PREFIX)) {
			return false;
		}
		File pluginsFolder = launcher.getParentFile();
		if (pluginsFolder == null || !pluginsFolder.getName().equals("plugins")) {
			return false;
		}
		File featuresFolder = new File(pluginsFolder.getParentFile(), "features");
		if (featuresFolder == null || !featuresFolder.exists()) {
			return false;
		}
		return true;
	}

	public Bundle[] getFeatures() {
		File featuresDir = new File(jarFile.getParentFile().getParentFile(), "features");
		File[] featureFiles = featuresDir.listFiles();
		Bundle[] featureBundles = new Bundle[featureFiles.length];
		for (int i = 0; i < featureFiles.length; i++) {
			featureBundles[i] = new Bundle(featureFiles[i]);
		}
		return featureBundles;
	}

	public Bundle[] getPlugins() {
		File pluginsDir = new File(jarFile.getParentFile().getParentFile(), "plugins");
		File[] pluginFiles = pluginsDir.listFiles();
		Bundle[] pluginBundles = new Bundle[pluginFiles.length];
		for (int i = 0; i < pluginFiles.length; i++) {
			pluginBundles[i] = new Bundle(pluginFiles[i]);
		}
		return pluginBundles;
	}

	public void addUpdateSite(String updateSite) {
		updateSites.add(updateSite);
	}

	public Set<String> getUpdateSites() {
		return Collections.unmodifiableSet(updateSites);
	}

	public void listFeatures() {
		for (String updateSite : updateSites) {
			System.out.println("Update Site: " + updateSite);
			listFeatures(updateSite);
		}
	}

	public void listFeatures(String updateSite) {
		List<String> command = new ArrayList<String>();
		command.add("-application");
		command.add("org.eclipse.equinox.p2.director");
		command.add("-consoleLog");
		command.add("-followReferences");
		command.add("-nosplash");
		command.add("-repository");
		command.add(updateSite);
		command.add("-list");

		execute(command);
	}

	public void installFeature(String feature) {
		List<String> command = new ArrayList<String>();
		command.add("-application");
		command.add("org.eclipse.equinox.p2.director");
		command.add("-consoleLog");
		command.add("-followReferences");
		command.add("-nosplash");
		command.add("-repository");
		command.add(collectionToString(updateSites));
		command.add("-installIUs");
		command.add(feature);

		execute(command);
	}

	public void installFeatures(String... features) {
		installFeature(arrayToString(features));
	}

	public void installFeatures(Collection<String> features) {
		installFeature(collectionToString(features));
	}

	public void execute(List<String> command) {
		execute(command.toArray(new String[command.size()]));
	}

	public void execute(String[] command) {
		new JarRunner(jarFile.getAbsolutePath(), command).run();
	}

	public static File findLauncher(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			throw new EclipseException(path + " doesn't exist");
		}
		File pluginDir = null;
		File[] homeDir = dir.listFiles();
		for (int i = 0; i < homeDir.length; i++) {
			if (homeDir[i].getName().equals("plugins") && homeDir[i].isDirectory()) {
				pluginDir = homeDir[i];
				break;
			}
		}
		if (pluginDir == null) {
			throw new EclipseException("Plugins dir not found");
		}
		File jarFile = null;
		File[] pluginsDir = pluginDir.listFiles();
		for (int i = 0; i < pluginsDir.length; i++) {
			if (pluginsDir[i].getName().startsWith("org.eclipse.equinox.launcher_")) {
				jarFile = pluginsDir[i];
				break;
			}
		}
		return jarFile;
	}

	private static String collectionToString(Collection<String> collection) {
		if (collection.size() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (String item : collection) {
			sb.append(",").append(item);
		}
		return sb.substring(1);
	}

	private static String arrayToString(String[] array) {
		if (array.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(",").append(array[i]);
		}
		return sb.substring(1);
	}

	public void addProgramArgument(String... args) {
		File iniFile = getIniFile();
		List<String> originalLines = null;
		try {
			originalLines = FileUtils.readLines(iniFile);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read from '" + iniFile.getAbsolutePath() + "'", e);
		}

		List<String> revisedLines = new ArrayList<String>();
		for (String line : originalLines) {
			if (line.startsWith("-vmargs") || line.startsWith("--launcher.appendVmargs")) {
				for (String arg : args) {
					revisedLines.add(arg);
				}
				args = new String[] {};
			}
			revisedLines.add(line);
		}

		try {
			FileUtils.writeLines(iniFile, revisedLines);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write to '" + iniFile.getAbsolutePath() + "'", e);
		}
	}

	public void addVMArgument(String... args) {
		File iniFile = getIniFile();
		List<String> originalLines = null;
		try {
			originalLines = FileUtils.readLines(iniFile);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read from '" + iniFile.getAbsolutePath() + "'", e);
		}

		List<String> revisedLines = new ArrayList<String>(originalLines);
		for (String arg : args) {
			revisedLines.add(arg);
		}

		try {
			FileUtils.writeLines(iniFile, revisedLines);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write to '" + iniFile.getAbsolutePath() + "'", e);
		}
	}

	public File getIniFile() {
		File eclipseDir = jarFile.getParentFile().getParentFile();
		for (File file : eclipseDir.listFiles()) {
			if (file.getName().endsWith(".ini")) {
				return file;
			}
		}
		for (File dir : eclipseDir.listFiles()) {
			if (dir.getName().endsWith(".app") && dir.isDirectory()) {
				for (File file : new File(dir, "Contents/MacOS").listFiles()) {
					if (file.getName().endsWith(".ini")) {
						return file;
					}
				}
			}
		}
		throw new RuntimeException("Cannot find .ini file at '" + eclipseDir.getAbsolutePath() + "'");
	}

	public static Eclipse installEclipse(File target) throws IOException {
		return installEclipse(target, ECLIPSE_DEFAULT_VERSION);
	}

	public static Eclipse installEclipse(File target, String eclipseVersion) throws IOException {
		return installEclipse(target, eclipseVersion, null);
	}

	public static Eclipse installEclipse(File target, String eclipseVersion, String md5) throws IOException {
		JDownloadManager manager = new JDownloadManager();
		manager.download(getEclipseUrl(eclipseVersion), target, getEclipseInstaller(eclipseVersion), true, md5);
		return new Eclipse(new File(target, "eclipse"));
	}

	private static String getEclipseUrl(String eclipseVersion) {
		String[] version = eclipseVersion.split("-");
		return ECLIPSE_DEFAULT_MIRROR + "/" + version[1] + "/" + version[2] + "/" + getEclipseInstaller(eclipseVersion);
	}

	private static String getEclipseInstaller(String eclipseVersion) {
		String os_property = OS.getName();
		String arch_property = OS.getArch();

		String platform = null;
		String archive = "zip";

		if (os_property.contains("linux")) {
			platform = "linux-gtk";
			archive = "tar.gz";
		} else if (os_property.contains("win")) {
			platform = "win32";
			archive = "zip";
		} else if (os_property.contains("mac")) {
			platform = "macosx-cocoa";
			archive = "tar.gz";
		}

		if (platform == null) {
			throw new RuntimeException("Unknown platform '" + os_property + "'");
		}

		if (arch_property.contains("64")) {
			platform += "-x86_64";
		}

		return "eclipse-" + eclipseVersion + "-" + platform + "." + archive;
	}

}
