/*
 * Created on Mar 15, 2004
 */

package org.pgj.jexec;

import org.pgj.tools.classloaders.PLJClassLoader;


/**
 * @author Laszlo Hornyak
 */
public class PGJClassLoaderAdapter extends ClassLoader {

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		return cl.load(name);
	}

	PLJClassLoader cl = null;

	/**
	 * 
	 */
	public PGJClassLoaderAdapter(PLJClassLoader cl) {
		super();
		this.cl = cl;
	}
}
