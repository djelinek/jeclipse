package org.apodhrad.jeclipse.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author apodhrad
 *
 */
public class JarRunner implements Runnable {

	public static final int RUNNER_TIMEOUT = 10 * 60;

	private Logger log = LoggerFactory.getLogger(getClass());

	private String jarFile;
	private String[] args;
	private StreamGobbler input;

	public JarRunner(String installer, String... args) {
		this.jarFile = installer;
		this.args = args;
	}

	public void run() {
		Process process = null;

		JarCommand jarCommand = new JarCommand(jarFile);
		for (int i = 0; i < args.length; i++) {
			jarCommand.addParameter(args[i]);
		}

		log.info("Command: " + jarCommand);
		ProcessBuilder pb = new ProcessBuilder(jarCommand.getCommand());
		pb.redirectErrorStream(true);
		try {
			process = pb.start();
		} catch (IOException e) {
			log.warn(e.getLocalizedMessage(), e);
			Assert.fail("Failed to start the auto.xml installation process.\n" + e.toString());
		}
		input = new StreamGobbler(process.getInputStream());
		input.start();
		try {
			input.join(RUNNER_TIMEOUT * 1000);
		} catch (InterruptedException e) {
			log.warn(e.getLocalizedMessage(), e);
			log.warn(e.toString());
			process.destroy();
		}
		if (input.isAlive()) {
			// installation is still running
			log.warn("Failed to finish the auto.xml installation within " + RUNNER_TIMEOUT + "s.");
			input.interrupt();
			process.destroy();
		}
		if (input.getStatus() != null) {
			Assert.fail(input.getStatus());
		}
	}
	
	public List<String> getOutputLines() {
		return input.getOutpuLines();
	}

	protected class StreamGobbler extends Thread {
		private final InputStream is;
		private boolean successful = false;
		private String status = null;
		private List<String> outputLines;

		private StreamGobbler(InputStream is) {
			this.is = is;
			outputLines = new ArrayList<String>();
		}

		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					log.info(line);
					outputLines.add(line);
					if (line.contains("ERROR") && !line.contains("level=ERROR")) {
						status = "Following line was found during auto.xml installation: " + line;
						break;
					}
					if (line.contains("Automated installation FAILED!")) {
						status = "Automated installation has failed.";
						break;
					}
					if (line.contains("Automated installation done")) {
						successful = true;
					}
				}
			} catch (IOException ioe) {
				log.warn(ioe.getLocalizedMessage(), ioe);
			}
		}
		
		public List<String> getOutpuLines() {
			return outputLines;
		}

		public boolean isSuccessful() {
			return successful;
		}

		public String getStatus() {
			return status;
		}
	}

	protected class JarCommand {

		private String jarPath;
		private List<String> parameters;
		private Properties systemProperties;
		private Properties variables;

		public JarCommand(String jarPath) {
			this.jarPath = jarPath;
			parameters = new ArrayList<String>();
			systemProperties = new Properties();
			variables = new Properties();
		}

		public void addParameter(String parameter) {
			if (parameter != null) {
				parameters.add(parameter);
			}
		}

		public void addSystemPropeties(Properties systemProperties) {
			if (systemProperties != null) {
				this.systemProperties.putAll(systemProperties);
			}
		}

		public void addVariables(Properties variables) {
			if (variables != null) {
				this.variables.putAll(variables);
			}
		}

		public List<String> getCommand() {
			List<String> command = new ArrayList<String>();
			command.add(System.getProperty("java.home") + "/bin/java");
			// set system properties
			for (String var : systemProperties.stringPropertyNames()) {
				String value = systemProperties.getProperty(var);
				command.add("-D" + var + "=" + value);
			}
			command.add("-jar");
			command.add(jarPath);
			// set parameters
			for (String parameter : parameters) {
				command.add(parameter);
			}
			// set variables
			StringBuffer sb = new StringBuffer();
			for (String var : variables.stringPropertyNames()) {
				String value = variables.getProperty(var);
				sb.append("," + var + "=" + value);
			}
			if (sb.length() > 0) {
				command.add("-variables");
				command.add(sb.substring(1));
			}
			return command;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (String cmd : getCommand()) {
				sb.append(" " + cmd);
			}
			return sb.substring(1);
		}

	}

}
