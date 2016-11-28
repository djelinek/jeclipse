package org.apodhrad.jeclipse.manager;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author apodhrad
 *
 */
public class DevstudioInstaller {

	public static final Pattern CORE_PLUGIN_PATTERN = Pattern.compile(".*(" + Devstudio.CORE_PLUGIN_PATTERN + ")_.*");
	public static final String DEFAULT_PRODUCT = "jbds";

	private File installer;

	public DevstudioInstaller(File installer) {
		notNull(installer);
		isTrue(installer.exists(), "Installer " + installer.getAbsolutePath() + " doesn't exists");
		this.installer = installer;
	}

	public File getInstaller() {
		return installer;
	}

	public String getCoreVersion() throws IOException {
		String coreFullVersion = getCoreFullVersion();
		return coreFullVersion.substring(0, coreFullVersion.indexOf("-v"));
	}

	public String getCoreFullVersion() throws IOException {
		try (JarFile jarFile = new JarFile(installer)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (CORE_PLUGIN_PATTERN.matcher(name).matches()) {
					return name.substring(name.indexOf("_") + 1).replace(".jar", "");
				}
			}
		}
		throw new NoSuchElementException("Cannot find the core plugin for detecting its version");
	}

	public List<DevstudioSpec> getCoreFeatures() throws IOException {
		String resource = "res/DevstudioFeaturesSpec.json";
		if (resourceExists(resource)) {
			return Arrays.asList(loadSpecs(resource));
		} else {
			return new ArrayList<DevstudioSpec>();
		}
	}

	public List<DevstudioSpec> getAdditionalFeatures() throws IOException {
		String resource = "res/AdditionalFeaturesSpec.json";
		if (resourceExists(resource)) {
			return Arrays.asList(loadSpecs(resource));
		} else {
			return new ArrayList<DevstudioSpec>();
		}
	}

	public String getFeatureProduct(String feature) throws IOException {
		List<DevstudioSpec> specs = new ArrayList<DevstudioSpec>();
		specs.addAll(getCoreFeatures());
		specs.addAll(getAdditionalFeatures());
		specs.addAll(getCoreFeatures());
		for (DevstudioSpec spec : specs) {
			if (spec.getId().equals(feature)) {
				return spec.getPath();
			}
		}
		return DEFAULT_PRODUCT;
	}

	public String getDefaultGroup() throws IOException {
		return getFeatureProduct("com.jboss.devstudio.core.package");
	}

	private DevstudioSpec[] loadSpecs(String resource) throws IOException {
		try (URLClassLoader classLoader = new URLClassLoader(new URL[] { installer.toURI().toURL() },
				this.getClass().getClassLoader())) {
			InputStream input = classLoader.getResourceAsStream(resource);
			if (input == null) {
				throw new RuntimeException("Cannot find resource '" + resource + "'");
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(input, DevstudioSpec[].class);
		}

	}

	public boolean resourceExists(String resource) throws IOException {
		try (URLClassLoader classLoader = new URLClassLoader(new URL[] { installer.toURI().toURL() },
				this.getClass().getClassLoader())) {
			return classLoader.getResource(resource) != null;
		}
	}

}
