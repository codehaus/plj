
package org.pgj.tools.classloaders.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.pgj.tools.classloaders.ClassStoreException;
import org.pgj.tools.classloaders.PLJClassLoader;

/**
 * File system classloader. Loads and stores classes using a filesystem.
 * 
 * @author Laszlo Hornyak
 * @since 0.1
 * 
 * @avalon.component name="fsclassloader" lifestyle="singleton"
 * @avalon.service type="org.pgj.tools.classloaders.PLJClassLoader"
 * 
 * @dna.component
 * @dna.service type="org.pgj.tools.classloaders.PLJClassLoader"
 * 
 */
public class FSClassLoader extends SecureClassLoader
		implements
			PLJClassLoader,
			Configurable,
			Initializable,
			LogEnabled,
			FSClassLoaderMBean {

	/** avalon logger object */
	Logger logger = null;

	/** root of the classpath */
	String root = null;

	Configuration config = null;

	// Permission collection lookup table. Used by this.getPermission(CodeSource
	// cs)
	Hashtable permissionTable = null;

	/** Class map */
	private volatile HashMap map = new HashMap();

	/**
	 * @see PLJClassLoader#load(String)
	 */
	public Class load(String fqn) throws ClassNotFoundException,
			ClassStoreException {

		try {
			return this.getClass().getClassLoader().loadClass(fqn);
		} catch (ClassNotFoundException clne) {
			logger.debug("ok, trying from repo:" + fqn);
		}

		Class cl = (Class) map.get(fqn);
		if (cl != null)
			return cl;

		String[] repoJars = new File(root).list();
		String filename = null;
		for (int i = 0; i < repoJars.length; i++) {
			String tmpFilename = root + File.separatorChar + repoJars[i]
					+ File.separatorChar
					+ fqn.replace('.', File.separatorChar).trim() + ".class";
			File file = new File(tmpFilename);
			if (file.exists() || file.isFile()) {
				filename = tmpFilename;
				break;
			}
		}

		if (filename == null) {
			throw new ClassNotFoundException("there are no jars containing "
					+ fqn);
		}

		logger.debug("loading class file:" + filename);

		try {
			FileInputStream stream = new FileInputStream(filename);
			byte[] raw = new byte[stream.available()];
			stream.read(raw);
			stream.close();
			cl = this
					.defineClass(fqn, raw, 0, raw.length/* , getCodeSource(fqn) */);
			resolveClass(cl);
			map.put(fqn, cl);
			return cl;
		} catch (IOException ioe) {
			logger.debug("Using the system class loader." + ioe.getMessage());
			//return getSystemClassLoader().loadClass(fqn);
			throw new ClassNotFoundException("Could not find: " + filename);
		}
	}

	protected PermissionCollection getPermissions(CodeSource cs) {
		String url = cs.getLocation().toString();
		PermissionCollection pc = (PermissionCollection) permissionTable
				.get(url);
		if (pc == null)
			pc = super.getPermissions(cs);
		return pc;
	}

	private CodeSource getCodeSource(String fqn) {
		CodeSource cs = null;
		try {
			URL codeURL = new URL("file://" + fqn);
			// TODO Remove this !!!
			logger.debug(codeURL.toString());
			cs = new CodeSource(codeURL, (Certificate [])null);
		} catch (MalformedURLException ex) {
			logger.fatalError("Can not happen.", ex);
		}
		return cs;
	}

	/**
	 * @see Configurable#configure(Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		config = arg0;
		root = arg0.getChild("root").getValue();
		logger.debug("class root:");
		logger.debug(root);
		Configuration[] preload = arg0.getChildren("preload");
		for (int i = 0; i < preload.length; i++) {
			try {
				try {
					this.load(preload[i].getValue());
				} catch (ClassStoreException e1) {
					throw new ConfigurationException(
							"preload class "
									+ preload[i].getValue()
									+ " could not be loaded becouse of class store errors",
							e1);
				}
			} catch (ClassNotFoundException e) {
				logger.warn("preload class could not be loaded.", e);
				logger
						.warn("continuing, though this may couse problems (e.g. JDBC)");
			}
		}
		logger.debug("configured");
	}

	/**
	 * @see Initializable#initialize()
	 */
	public void initialize() throws Exception {
		//check if root exists.
		try {
			File f = new File(root);
			if (!f.exists()) {
				logger.warn("oops. repository doesn`t exist.");
				logger.warn("creating root dir: " + root);
				f.mkdirs();
			}
			if (!f.canWrite()) {
				logger.warn("No write access granted to root.");
			}
			if (!f.isDirectory()) {
				logger.fatalError("The root directory is not a directory!!");
			}

			// Creating permissionTable for classes

			permissionTable = new Hashtable();
			CodeSource cs = new CodeSource(new URL("file://jsp.files"), (Certificate [])null);

			// Class loader to load xxx.xxx.xxxxPermission class
			ClassLoader cl = java.lang.ClassLoader.getSystemClassLoader();

			// Filling it with "java.security.PermissionCollection's"

			Configuration[] classPermission = config
					.getChildren("class-permission");
			for (int cp = 0; cp < classPermission.length; cp++) {

				String fqn = classPermission[cp].getAttribute("fqn");
				PermissionCollection permissionCollection = super
						.getPermissions(cs);

				Configuration[] permission = classPermission[cp]
						.getChildren("permission");
				for (int p = 0; p < permission.length; p++) {

					// Parameters for the permission. Example
					// java.util.PropertyPermission "user.home", "read"
					// (user.home and read is parameter)

					Configuration[] param = permission[p].getChildren("param");

					Object[] initargs = new Object[param.length]; // Used when
					// loading
					// Permission
					// class
					Class[] classes = new Class[param.length]; // Used when
					// creating
					// Constructor
					// (See below)

					for (int pa = 0; pa < param.length; pa++) {

						initargs[pa] = param[pa].getValue();
						classes[pa] = initargs[pa].getClass();

					}

					// loading Permission class with above parameters. Example:
					// java.util.PropertyPermission
					Constructor con = cl.loadClass(
							permission[p].getChild("class").getValue())
							.getConstructor(classes);
					permissionCollection.add((Permission) con
							.newInstance(initargs));
					logger.debug(permissionCollection.toString());

				}

				// Put fqn as key for permissionCollection for later lookup
				// "file://" is added so i dont have to strip it out from
				// ClassSource when i get from table.See
				// "this.getPermissions(CodeSource cs)"
				permissionTable.put("file://" + fqn, permissionCollection);

			}
		} catch (Exception ex) {
			throw new ConfigurationException("Init failed... ( "
					+ ex.getMessage() + " )");
		}
	}

	/**
	 * @see LogEnabled#enableLogging(Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/**
	 * @see org.pgj.tools.classloaders.PLJClassLoader#hasClass(String)
	 */
	public boolean hasClass(String fqn) {
		return false;
	}

	/**
	 * Flusehes cache. Called from the management api.
	 * 
	 * @phoenix:mx-operation @phoenix:mx-description Clears the class-cache.
	 */
	public void flushCache() {
		map.clear();
	}

	/**
	 * Reloads a specified class.
	 * 
	 * @param fqn
	 *            the fully qualifyed name of the class
	 * @throws ClassNotFoundException
	 * @phoenix:mx-operation @phoenix:mx-description Reloads a class.
	 */
	public void reloadClass(String fqn) throws ClassNotFoundException, ClassStoreException {
		Class cl = (Class) map.get(fqn);
		load(fqn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		try {
			return load(name);
		} catch (ClassStoreException e) {
			logger.error("Class store exception", e);
			throw new ClassNotFoundException("Class store error", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#getResource(java.lang.String)
	 */
	public URL getResource(String name) {
		return this.getClass().getClassLoader().getResource(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String name) {
		return this.getClass().getClassLoader().getResourceAsStream(name);
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.classloaders.PLJClassLoader#removeClass(java.lang.String)
	 */
	public void removeClass(String name) throws ClassNotFoundException {
		File repo = new File(root);
		String[] list = repo.list();
		for (int i = 0; i < list.length; i++) {
			File f = new File(this.root + File.separatorChar + list[i]
					+ File.separatorChar
					+ name.replace('.', File.separatorChar));
			if (f.exists()) {
				f.delete();
			} else {
				throw new ClassNotFoundException("not found: " + name);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.classloaders.PLJClassLoader#store(java.lang.String, byte[], java.lang.String)
	 */
	public void store(String name, byte[] raw, String jar) {
		File f = new File(this.root + jar + File.separatorChar
				+ name.replace('.', File.separatorChar) + ".class");
		int lastDot = name.lastIndexOf('.');
		try {
			if (lastDot != 0) {
				File dir = new File(this.root
						+ jar
						+ File.separatorChar
						+ File.separatorChar
						+ name.substring(0, lastDot).replace('.',
								File.separatorChar));
				dir.mkdirs();
			}

			f.createNewFile();
			OutputStream o = new FileOutputStream(f);
			o.write(raw);
			o.flush();
			o.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.classloaders.PLJClassLoader#removeJar(java.lang.String)
	 */
	public void removeJar(String name) {
		File f = new File(root + File.separatorChar + name);
		deleteRecursively(f);
	}

	private void deleteRecursively(File f) {
		if (f.isFile()) {
			f.delete();
		} else {
			String[] files = f.list();
			if (files != null)
				for (int i = 0; i < files.length; i++) {
					deleteRecursively(new File(f.getPath() + File.separatorChar
							+ files[i]));
				}
			f.delete();
		}
	}

}
