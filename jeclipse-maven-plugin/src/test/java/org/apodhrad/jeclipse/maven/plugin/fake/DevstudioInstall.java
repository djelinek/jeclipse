package org.apodhrad.jeclipse.maven.plugin.fake;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apodhrad.jeclipse.manager.util.XMLUtils;

public class DevstudioInstall {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			throw new IllegalArgumentException("Please specify config file");
		}
		File installerConfigFile = new File(args[0]);
		String target = XMLUtils.getTextByTagName(installerConfigFile, "installpath");
		prepareDevstudioStructure(new File(target));
	}

	private static void prepareDevstudioStructure(File target) throws IOException {
		File runtimes = new File(target, "runtimes");
		runtimes.mkdirs();
		File studio = new File(target, "studio");
		studio.mkdirs();
		File plugins = new File(studio, "plugins");
		plugins.mkdirs();
		File features = new File(studio, "features");
		features.mkdirs();
		File iniFile = new File(studio, "devstudio.ini");
		BufferedWriter out = new BufferedWriter(new FileWriter(iniFile));
		out.write("-showsplash");
		out.newLine();
		out.write("org.eclipse.platform");
		out.newLine();
		out.write("--launcher.defaultAction");
		out.newLine();
		out.write("openFile");
		out.newLine();
		out.write("--launcher.appendVmargs");
		out.newLine();
		out.write("-vmargs");
		out.newLine();
		out.write("-Dosgi.requiredJavaVersion=1.8");
		out.newLine();
		out.flush();
		out.close();
		File launcherFile = new File(plugins, "org.eclipse.equinox.launcher_123.jar");
		JarBuilder jarBuilder = new JarBuilder();
		jarBuilder.setMainClass(EclipseLauncher.class);
		jarBuilder.build(launcherFile);
		File platformFile = new File(plugins, "org.eclipse.platform_123.jar");
		new JarBuilder().build(platformFile);
		File coreFile = new File(plugins, "com.jboss.devstudio.core_10.1.0.GA-v20160902-1725-B43");
		new JarBuilder().build(coreFile);
	}
}
