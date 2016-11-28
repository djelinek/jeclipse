package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

	private String target;
	private String jre;
	private String group;
	private String devstudioVersion;
	private Set<String> features;
	private Set<String> products;
	private Set<String> runtimes;

	private boolean isDevstudioEap;
	private boolean isDevstudioIntegrationStack;

	public DevstudioConfig(String devstudioVersion) {
		this.devstudioVersion = devstudioVersion;

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
		return OSUtils.getUserHome("jbdevstudio").getAbsolutePath();
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

	public File toFile(File installationConfigFile) throws IOException {
		if (installationConfigFile.isDirectory()) {
			installationConfigFile = new File(installationConfigFile, "InstallationConfigRecord.xml");
		}

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

	public static DevstudioConfig createFromInstallerName(String installerName) {
		String devstudioVersion = getJBDSVersion(installerName);

		DevstudioConfig config = new DevstudioConfig(devstudioVersion);
		config.addFeature("com.jboss.devstudio.core.package");
		config.addFeature("org.testng.eclipse.feature.group");
		if (installerName.contains("eap")) {
			config.setDevstudioEap(true);
		}
		if (installerName.contains("integration-stack") || installerName.contains("devstudio-is")) {
			config.setDevstudioIntegrationStack(true);
		}
		return config;
	}

	public static DevstudioConfig createFromInstallerFile(File installerJar) throws IOException {
		DevstudioInstaller installer = new DevstudioInstaller(installerJar);

		DevstudioConfig config = new DevstudioConfig(installer.getCoreVersion());
		config.setGroup(installer.getDefaultGroup());
		for (DevstudioSpec featureSpec : installer.getCoreFeatures()) {
			config.addFeature(featureSpec.getId());
			config.addProduct(featureSpec.getPath());
		}
		if (!installer.getAdditionalFeatures().isEmpty()) {
			config.setDevstudioIntegrationStack(true);
		}
		return config;
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
