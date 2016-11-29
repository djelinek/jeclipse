package org.apodhrad.jeclipse.maven.plugin;

import static org.apodhrad.jeclipse.manager.Eclipse.installEclipse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.hash.Hash;
import org.apodhrad.jdownload.manager.hash.MD5Hash;
import org.apodhrad.jdownload.manager.hash.NullHash;
import org.apodhrad.jdownload.manager.hash.SHA1Hash;
import org.apodhrad.jdownload.manager.hash.SHA256Hash;
import org.apodhrad.jdownload.manager.hash.URLHash;
import org.apodhrad.jeclipse.manager.Devstudio;
import org.apodhrad.jeclipse.manager.DevstudioConfig;
import org.apodhrad.jeclipse.manager.Eclipse;
import org.apodhrad.jeclipse.manager.EclipseConfig;
import org.apodhrad.jeclipse.manager.util.EclipseUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * Installer for Eclipse JBDS
 * 
 * @author apodhrad
 * 
 */
@Mojo(name = "install", defaultPhase = LifecyclePhase.PACKAGE)
public class Installer extends AbstractMojo {

	public static final String ECLIPSE_MIRROR_DEFAULT = String.valueOf(EclipseUtils.ECLIPSE_DEFAULT_MIRROR_ID);

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter(defaultValue = "${session}")
	private MavenSession session;

	@Component
	private BuildPluginManager manager;

	@Parameter(defaultValue = "${project.build.directory}")
	private String target;

	@Parameter(alias = "jbds.installer")
	private URL jbdsInstaller;

	@Parameter(alias = "jbds.installer.md5")
	private String jbdsInstallerMD5;

	@Parameter(alias = "jbds.installer.sha1")
	private String jbdsInstallerSHA1;

	@Parameter(alias = "jbds.installer.sha256")
	private String jbdsInstallerSHA256;

	@Parameter(alias = "eclipse.version")
	private String eclipseVersion;

	@SuppressWarnings("rawtypes")
	@Parameter(defaultValue = "${project.remoteArtifactRepositories}")
	private java.util.List remoteRepositories;

	@Parameter
	private String[] ius;

	@Parameter
	private Set<String> features;

	@Parameter(alias = "jre.location")
	private String jreLocation;

	@Parameter(alias = "eclipse.mirror")
	private String eclipseMirror;

	@Parameter
	private String[] programArgs;

	@Parameter
	private String[] vmArgs;

	@Parameter
	private String cache;

	@Parameter(defaultValue = "false")
	private boolean nocache;

	@Parameter
	private String timeout;

	public void execute() throws MojoExecutionException {
		setSystemProperties();

		Eclipse eclipse = null;

		DevstudioConfig devstudioConfig = getDevstudioConfig();
		try {
			if (devstudioConfig == null) {
				eclipse = Devstudio.installJBDS(jbdsInstaller.toString(), getDevstudioHash(), devstudioConfig);
			} else {
				eclipse = installEclipse(getEclipseConfig());
			}
		} catch (Exception ioe) {
			throw new MojoExecutionException("An exception occured during installing Eclipse IDE", ioe);
		}

		// Install features
		for (Object obj : remoteRepositories) {
			if (obj instanceof MavenArtifactRepository) {
				MavenArtifactRepository repo = (MavenArtifactRepository) obj;
				if (repo.getLayout().getId().equals("update-site")) {
					getLog().info("Added update site " + repo.getId() + " at " + repo.getUrl());
					eclipse.addUpdateSite(repo.getUrl());
				}
			}
		}
		if (features != null && !features.isEmpty()) {
			eclipse.installFeatures(features);
		}

		if (programArgs != null) {
			getLog().info("Setting program arguments:");
			for (String programArg : programArgs) {
				getLog().info("\t" + programArg);
			}
			eclipse.addProgramArgument(programArgs);
		}

		if (vmArgs != null) {
			getLog().info("Setting VM arguments:");
			for (String vmArg : vmArgs) {
				getLog().info("\t" + vmArg);
			}
			eclipse.addVMArgument(vmArgs);
		}

		getLog().info("Finished");
	}

	protected void setSystemProperties() {
		if (nocache) {
			System.setProperty(JDownloadManager.NOCACHE_PROPERTY, "true");
		}
		if (cache != null && cache.length() > 0) {
			System.setProperty(JDownloadManager.CACHE_PROPERTY, cache);
		}
		if (timeout != null && timeout.length() > 0) {
			System.setProperty(Eclipse.TIMEOUT_PROPERTY, timeout);
		}
		File jdownloadCache = new JDownloadManager().getCache();
		if (jdownloadCache != null) {
			getLog().info("The folder '" + new JDownloadManager().getCache().getAbsolutePath()
					+ "' will be used for caching.");
		} else {
			getLog().info("Download manager won't use any cache folder.");
		}
	}

	protected EclipseConfig getEclipseConfig() throws JsonParseException, JsonMappingException, IOException {
		if (isDefined(eclipseVersion)) {
			return EclipseConfig.init(eclipseVersion).setTarget(new File(target));
		}
		return null;
	}

	protected DevstudioConfig getDevstudioConfig() {
		if (isDefined(jbdsInstaller)) {
			if (ius == null) {
				ius = new String[0];
			}
			DevstudioConfig config = new DevstudioConfig();
			config.setTarget(new File(target, "jbdevstudio").getAbsolutePath());
			config.setJre(jreLocation);
			for (String iu : ius) {
				config.addFeature(iu);
			}
			return config;
		}
		return null;
	}

	public Hash getDevstudioHash() {
		Hash hash = new NullHash();
		if (isDefined(jbdsInstallerMD5)) {
			hash = new MD5Hash(jbdsInstallerMD5);
			if (jbdsInstallerMD5.startsWith("http")) {
				hash = new URLHash(jbdsInstallerMD5);
			}
		}
		if (isDefined(jbdsInstallerSHA1)) {
			hash = new SHA1Hash(jbdsInstallerSHA1);
			if (jbdsInstallerSHA1.startsWith("http")) {
				hash = new URLHash(jbdsInstallerSHA1);
			}
		}
		if (isDefined(jbdsInstallerSHA256)) {
			hash = new SHA256Hash(jbdsInstallerSHA256);
			if (jbdsInstallerSHA256.startsWith("http")) {
				hash = new URLHash(jbdsInstallerSHA256);
			}
		}
		return hash;
	}

	private static boolean isDefined(Object parameter) {
		return parameter != null && parameter.toString().length() > 0;
	}

}
