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

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author apodhrad
 *
 */
public class DevstudioInstaller {

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
				if (name.contains(Devstudio.CORE_PLUGIN + "_")) {
					return name.substring(name.indexOf("_") + 1).replace(".jar", "");
				}
			}
		}
		throw new NoSuchElementException("Cannot find the core plugin for detecting its version");
	}

	public List<String> getCoreFeatures() throws IOException {
		return getFeatures("res/DevstudioFeaturesSpec.json");
	}

	public List<String> getAdditionalFeatures() throws IOException {
		return getFeatures("res/AdditionalFeaturesSpec.json");
	}

	private List<String> getFeatures(String resource) throws IOException {
		List<String> features = new ArrayList<String>();
		for (DevstudioSpec spec : loadSpecs(resource)) {
			features.add(spec.getId());
		}
		return features;
	}

	public String getFeatureProduct(String feature) throws IOException {
		List<DevstudioSpec> specs = new ArrayList<DevstudioSpec>();
		specs.addAll(Arrays.asList(loadSpecs("res/DevstudioFeaturesSpec.json")));
		specs.addAll(Arrays.asList(loadSpecs("res/AdditionalFeaturesSpec.json")));
		for (DevstudioSpec spec : specs) {
			if (spec.getId().equals(feature)) {
				return spec.getPath();
			}
		}
		throw new NoSuchElementException("Cannot find a product for feature '" + feature + "'");
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

	private static class DevstudioSpec {

		private String id;
		private String label;
		private String description;
		private String selected;
		private String path;
		private String size;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getSelected() {
			return selected;
		}

		public void setSelected(String selected) {
			this.selected = selected;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			this.size = size;
		}

	}

}
