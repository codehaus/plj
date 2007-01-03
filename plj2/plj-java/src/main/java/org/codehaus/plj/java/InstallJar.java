/*
 * Created on Oct 10, 2004
 */

package org.codehaus.plj.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.codehaus.plj.messages.CallRequest;
import org.codehaus.plj.typemapping.Field;
import org.codehaus.plj.utils.classloaders.ClassStoreException;
import org.codehaus.plj.utils.classloaders.PLJClassLoader;


/**
 * Install JAR.
 * @author Laszlo Hornyak
 */
public class InstallJar extends BasicPrivilegedJSProc {

	protected InstallJar(JavaExecutor je) {
		super(je);
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#getName()
	 */
	public String getName() {
		return "install_jar";
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#perform(org.pgj.messages.CallRequest)
	 */
	public Object perform(CallRequest call) throws Exception {
		List params = call.getParams();
		String jarToInstall = (String) (((Field) params.get(0))
				.get(String.class));
		String jarName = (String) (((Field) params.get(1)).get(String.class));

		return doInstallJar(javaExecutor.classloader, jarName, jarToInstall);
	}

	static Object doInstallJar(PLJClassLoader classLoader, String jarName,
			String fileName) throws IOException, ClassStoreException {

		JarFile jarFile = new JarFile(fileName);
		Enumeration e = jarFile.entries();
		while (e.hasMoreElements()) {

			ZipEntry zipEntry = (ZipEntry) e.nextElement();
			if (zipEntry.isDirectory())
				continue;

			if (!zipEntry.getName().endsWith(".class"))
				continue;

			ByteArrayOutputStream ou = new ByteArrayOutputStream(4096);
			InputStream ios = jarFile.getInputStream(zipEntry);
			byte[] buf = new byte[4096];
			int len = 0;
			do {
				len = ios.read(buf);
				if (len != -1)
					ou.write(buf, 0, len);
			} while (len != -1);
			buf = ou.toByteArray();
			String newName = zipEntry.getName().replaceAll(".class", "")
					.replace('/', '.');
			classLoader.store(newName, buf, jarName);
		}
		return null;
	}

}