package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private String devstudioVersion;
	private List<String> groups;
	private List<String> features;
	private List<String> products;
	private List<String> runtimes;

	public DevstudioConfig(String devstudioVersion) {
		this.devstudioVersion = devstudioVersion;

		groups = new ArrayList<String>();
		features = new ArrayList<String>();
		products = new ArrayList<String>();
		runtimes = new ArrayList<String>();

		/* default settings */
		target = getDefaultTarget();
		jre = getDefaultJre();
		features.add("com.jboss.devstudio.core.package");
		features.add("org.testng.eclipse.feature.group");
		if (devstudioVersion.startsWith("10")) {
			groups.add("devstudio");
			products.add("devstudio");
		} else {
			groups.add("jbds");
			products.add("jbds");
		}
	}

	public String getDevstudioVersion() {
		return devstudioVersion;
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

	public List<String> getFeatures() {
		return features;
	}

	public void addFeature(String iu) {
		features.add(iu);
		if (iu.contains("integration-stack")) {
			if (devstudioVersion.startsWith("10")) {
				addProduct("devstudio-is");
			} else {
				addProduct("jbdsis");
			}
		}
	}

	public List<String> getRuntimes() {
		return runtimes;
	}

	public void addRuntime(String runtime) {
		runtimes.add(runtime);
	}

	public List<String> getGroups() {
		return groups;
	}

	public void addGroup(String group) {
		if ("jbosseap".equals(group)) {
			groups.clear();
		}
		groups.add(group);
	}

	public List<String> getProducts() {
		return products;
	}

	public void addProduct(String product) {
		products.add(product);
	}

	public File toFile(File installationConfigFile) throws IOException {
		if (installationConfigFile.isDirectory()) {
			installationConfigFile = new File(installationConfigFile, "InstallationConfigRecord.xml");
		}

		Map<String, String> vars = new HashMap<String, String>();
		vars.put("@TARGET@", target);
		vars.put("@JRE@", jre);
		vars.put("@FEATURES@", String.join(",", features));
		vars.put("@PRODUCTS@", String.join(",", products));
		vars.put("@GROUPS@", String.join(",", groups));
		vars.put("@RUNTIMES@", String.join(",", runtimes));

		InputStream source = Devstudio.class.getResourceAsStream("/devstudio/" + getSourceConfigName());
		InpuStreamUtils.copyToFileAndReplace(source, installationConfigFile, vars);
		return installationConfigFile;
	}

	private String getSourceConfigName() {
		String sourceConfigName = null;
		if (devstudioVersion != null && devstudioVersion.startsWith("7")) {
			sourceConfigName = "jbds-7.xml";
		}
		if (devstudioVersion != null && devstudioVersion.startsWith("8")) {
			sourceConfigName = "jbds-8.xml";
		}
		if (devstudioVersion != null && devstudioVersion.startsWith("9")) {
			sourceConfigName = "jbds-9.xml";
		}
		if (devstudioVersion != null && devstudioVersion.startsWith("10")) {
			sourceConfigName = "devstudio-10.xml";
			if (!runtimes.isEmpty()) {
				sourceConfigName = "devstudio-10-runtime.xml";
			}
		}
		return sourceConfigName;
	}

	public static DevstudioConfig createFromInstallerName(String installerName) {
		DevstudioConfig config = new DevstudioConfig(getJBDSVersion(installerName));
		if (installerName.contains("eap")) {
			config.addGroup("jbosseap");
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
