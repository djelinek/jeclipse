package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.hash.Hash;
import org.apodhrad.jeclipse.manager.util.OSUtils;
import org.apodhrad.jeclipse.manager.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * 
 * @author apodhrad
 *
 */
public class Devstudio extends Eclipse {

	public static final String CORE_PLUGIN_REGEX = "com.jboss.devstudio.core|com.jboss.jbds.product";
	public static final Pattern CORE_PLUGIN_PATTERN = Pattern.compile(CORE_PLUGIN_REGEX);

	private static Logger log = LoggerFactory.getLogger(Devstudio.class);

	public Devstudio(File file) {
		super(file);
	}

	public String getCoreVersion() {
		Bundle platformPlugin = getPlugin(CORE_PLUGIN_PATTERN);
		if (platformPlugin != null) {
			return platformPlugin.getVersion();
		}
		return null;
	}

	public static Devstudio installJBDS(String url, Hash hash, DevstudioConfig config) throws IOException {
		JDownloadManager manager = new JDownloadManager();
		File installerJarFile = manager.download(url, new File(config.getTarget()).getParentFile(), hash);
		return installJBDS(installerJarFile, config.toFile(installerJarFile));
	}

	public static Devstudio installJBDS(File installerJarFile, DevstudioConfig config) throws IOException {
		return installJBDS(installerJarFile, config.toFile(installerJarFile));
	}

	public static Devstudio installJBDS(File installerJarFile, File installerConfigFile) throws IOException {
		String target;
		try {
			target = XMLUtils.getTextByTagName(installerConfigFile, "installpath");
		} catch (ParserConfigurationException | SAXException e) {
			throw new RuntimeException("Cannot parse " + installerConfigFile.getAbsolutePath());
		}

		// Switch IzPack mode to privileged on Windows
		if (OSUtils.isWindows()) {
			System.setProperty("izpack.mode", "privileged");
		}

		JarRunner jarRunner = new JarRunner(installerJarFile.getAbsolutePath());
		jarRunner.setExecutionOutput(new EclipseExecutionOutput());
		jarRunner.setTimeout(getJEclipseTimeout());
		jarRunner.execute(installerConfigFile.getAbsolutePath());

		return new Devstudio(new File(target));
	}

}
