package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apodhrad.jeclipse.manager.util.InpuStreamUtils;
import org.apodhrad.jeclipse.manager.util.OSUtils;

/**
 * 
 * @author apodhrad
 *
 */
public class DevstudioConfig {

	public static final String DEFAULT_CONFIG_NAME = "InstallConfigRecord.xml";
	public static final String DEFAULT_DEVSTUDIO_DIR = "jbdevstudio";

	private String target;
	private String jre;
	private String group;
	private Set<String> features;
	private Set<String> products;
	private Set<String> runtimes;

	private boolean isDevstudioEap;
	private boolean isDevstudioIntegrationStack;

	public DevstudioConfig() {
		features = new LinkedHashSet<String>();
		products = new LinkedHashSet<String>();
		runtimes = new LinkedHashSet<String>();

		/* default settings */
		target = getDefaultTarget();
		jre = getDefaultJre();
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target == null ? getDefaultTarget() : target;
	}

	public String getDefaultTarget() {
		return OSUtils.getUserHome(DEFAULT_DEVSTUDIO_DIR).getAbsolutePath();
	}

	public String getJre() {
		return jre;
	}

	public void setJre(String jre) {
		this.jre = jre == null ? getDefaultJre() : jre;
	}

	public String getDefaultJre() {
		return OSUtils.getJre(null).getAbsolutePath();
	}

	public Set<String> getFeatures() {
		return features;
	}

	public void addFeature(String feature) {
		features.add(feature);
	}

	public Set<String> getRuntimes() {
		return runtimes;
	}

	public void addRuntime(String runtime) {
		runtimes.add(runtime);
	}

	public String getGroup() {
		return group;
	}

	private void setGroup(String group) {
		this.group = group;
	}

	public Set<String> getProducts() {
		return products;
	}

	private void addProduct(String product) {
		products.add(product);
	}

	public boolean isDevstudioEap() {
		return isDevstudioEap;
	}

	public void setDevstudioEap(boolean isDevstudioEap) {
		this.isDevstudioEap = isDevstudioEap;
	}

	public boolean isDevstudioIntegrationStack() {
		return isDevstudioIntegrationStack;
	}

	public void setDevstudioIntegrationStack(boolean isDevstudioIntegrationStack) {
		this.isDevstudioIntegrationStack = isDevstudioIntegrationStack;
	}

	public File toFile(File installationJar) throws IOException {
		return toFile(installationJar, DEFAULT_CONFIG_NAME);
	}

	public File toFile(File installerFile, String configName) throws IOException {
		return toFile(installerFile, new File(target, configName));
	}

	public File toFile(File installerFile, File installationConfigFile) throws IOException {
		DevstudioInstaller installer = new DevstudioInstaller(installerFile);

		setGroup(installer.getDefaultGroup());
		List<String> newFeatures = new ArrayList<String>();
		for (String feature : this.features) {
			newFeatures.add(feature);
		}
		this.features.clear();
		for (DevstudioSpec featureSpec : installer.getCoreFeatures()) {
			addFeature(featureSpec.getId());
			addProduct(featureSpec.getPath());
		}
		for (String feature : newFeatures) {
			addFeature(feature);
		}
		if (!installer.getAdditionalFeatures().isEmpty()) {
			setDevstudioIntegrationStack(true);
		}

		String devstudioVersion = installer.getCoreVersion();
		String sourceConfigName = null;
		if (devstudioVersion.startsWith("7")) {
			sourceConfigName = "jbds-7.xml";
		}
		if (devstudioVersion.startsWith("8")) {
			sourceConfigName = "jbds-8.xml";
		}
		if (devstudioVersion.startsWith("9")) {
			sourceConfigName = "jbds-9.xml";
			addProduct("jbds");
			setGroup("jbds");
			if (isDevstudioIntegrationStack) {
				addProduct("jbdsis");
			}
		}
		if (devstudioVersion.startsWith("10")) {
			sourceConfigName = "devstudio-10.xml";
			if (!runtimes.isEmpty()) {
				sourceConfigName = "devstudio-10-runtime.xml";
			}
			addProduct("devstudio");
			setGroup("devstudio");
			if (isDevstudioIntegrationStack) {
				addProduct("devstudio-is");
			}
		}
		if (isDevstudioEap) {
			setGroup("jbosseap");
		}

		Map<String, String> vars = new HashMap<String, String>();
		vars.put("@TARGET@", target);
		vars.put("@JRE@", jre);
		vars.put("@GROUP@", String.join(",", group));
		vars.put("@FEATURES@", String.join(",", features));
		vars.put("@PRODUCTS@", String.join(",", products));
		vars.put("@RUNTIMES@", String.join(",", runtimes));

		InputStream source = Devstudio.class.getResourceAsStream("/devstudio/" + sourceConfigName);
		InpuStreamUtils.copyToFileAndReplace(source, installationConfigFile, vars);
		return installationConfigFile;
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
