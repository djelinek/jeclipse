package org.apodhrad.jeclipse.maven.plugin.fake;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * 
 * @author apodhrad
 *
 */
public class JarBuilder {

	private Class<?> mainClass;
	private Set<Class<?>> classes;
	private Map<String, String> resources;

	public JarBuilder() {
		classes = new HashSet<Class<?>>();
		resources = new HashMap<String, String>();
	}

	public void addClass(Class<?> clazz) {
		classes.add(clazz);
	}

	public void setMainClass(Class<?> clazz) {
		classes.add(clazz);
		this.mainClass = clazz;
	}

	public void addResource(String path, String content) {
		resources.put(path, content);
	}

	public void build(File jarFile) throws FileNotFoundException, IOException {
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		if (mainClass != null) {
			manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass.getName());
		}
		JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile), manifest);

		for (Class<?> clazz : classes) {
			addClass(jarOutputStream, clazz);
		}

		for (String path : resources.keySet()) {
			addResource(jarOutputStream, path, resources.get(path));
		}

		jarOutputStream.flush();
		jarOutputStream.close();
	}

	private static void addClass(JarOutputStream jarOutputStream, Class<?> clazz) throws IOException {
		String path = clazz.getName().replace('.', '/') + ".class";
		jarOutputStream.putNextEntry(new JarEntry(path));
		jarOutputStream.write(toByteArray(clazz.getClassLoader().getResourceAsStream(path)));
		jarOutputStream.closeEntry();
	}

	private static void addResource(JarOutputStream jarOutputStream, String path, String content) throws IOException {
		jarOutputStream.putNextEntry(new JarEntry(path));
		jarOutputStream.write(content.getBytes());
		jarOutputStream.closeEntry();
	}

	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[0x1000];
		while (true) {
			int r = in.read(buf);
			if (r == -1) {
				break;
			}
			out.write(buf, 0, r);
		}
		return out.toByteArray();
	}
}
