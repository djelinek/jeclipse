package org.apodhrad.jeclipse.maven.plugin;

import static org.apodhrad.jeclipse.manager.Eclipse.ECLIPSE_DEFAULT_MIRROR;
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
import org.apodhrad.jeclipse.manager.Eclipse;
import org.apodhrad.jeclipse.manager.JBDS;

/**
 * 
 * Installer for Eclipse JBDS
 * 
 * @author apodhrad
 * 
 */
@Mojo(name = "install", defaultPhase = LifecyclePhase.PACKAGE)
public class Installer extends AbstractMojo {

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
	private Set<String> features;

	@Parameter(alias = "jre.location")
	private String jreLocation;

	@Parameter(alias = "eclipse.mirror", defaultValue = ECLIPSE_DEFAULT_MIRROR)
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

		Eclipse eclipse = null;
		if (isDefined(jbdsInstaller)) {
			Hash hash = new NullHash();
			if (isDefined(jbdsInstallerMD5)) {
				hash = new MD5Hash(jbdsInstallerMD5);
			}
			if (isDefined(jbdsInstallerSHA1)) {
				hash = new SHA1Hash(jbdsInstallerSHA1);
			}
			if (isDefined(jbdsInstallerSHA256)) {
				hash = new SHA256Hash(jbdsInstallerSHA256);
			}
			try {
				eclipse = JBDS.installJBDS(new File(target), jbdsInstaller.toString(), jreLocation, hash);
			} catch (IOException ioe) {
				throw new MojoExecutionException("I/O exception occured during installing Eclipse IDE", ioe);
			}
		} else {
			try {
				eclipse = installEclipse(new File(target), eclipseVersion);
			} catch (IOException ioe) {
				throw new MojoExecutionException("I/O exception occured during installing Eclipse IDE", ioe);
			}
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
	
	private static boolean isDefined(Object parameter) {
		return parameter != null && parameter.toString().length() > 0;
	}

}
