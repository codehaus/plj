/*
 * Created on Oct 10, 2004
 */
package org.pgj.jexec;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.pgj.messages.CallRequest;
import org.pgj.typemapping.Field;


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

	private void recursivelyInstall(JarFile jar, Enumeration entries, String packagename, String jarName) throws Exception{
		if(entries == null)
			entries = jar.entries();
		while(entries.hasMoreElements()){
			ZipEntry zipEntry = (ZipEntry)entries.nextElement();
			if(zipEntry.isDirectory()){
				String newPackName = "".equals(packagename) ? "" : (packagename + ".") + zipEntry.getName();
				recursivelyInstall(jar, entries, newPackName, jarName);
			} else {
				if(!zipEntry.getName().endsWith(".class"))
					continue;
				String newName = "".equals(packagename) ? "" : (packagename + ".") + zipEntry.getName().replaceAll(".class", "");
				ByteArrayOutputStream ou = new ByteArrayOutputStream(4096);
				InputStream ios = jar.getInputStream(zipEntry);
				byte[] buf = new byte[4096];
				int len = 0;
				do{
					len = ios.read(buf);
					ou.write(buf, 0, len);
				} while(len != -1);
				buf = ou.toByteArray();
				this.javaExecutor.classloader.store(newName, buf, jarName);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#perform(org.pgj.messages.CallRequest)
	 */
	public Object perform(CallRequest call) throws Exception {
		Vector params = call.getParams();
		String jarToInstall = (String)(((Field)params.get(0)).get(String.class));
		String jarName = (String)(((Field)params.get(1)).get(String.class));
		JarFile jarFile = new JarFile(jarToInstall);
		recursivelyInstall(jarFile, null, "", jarName);
		return null;
	}

}
