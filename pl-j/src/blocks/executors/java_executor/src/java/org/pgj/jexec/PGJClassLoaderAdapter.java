/*
 * Created on Mar 15, 2004
 */

package org.pgj.jexec;

import org.pgj.tools.classloaders.PLJClassLoader;


/**
 * A very basic class to integrate PLJClassloaders.
 * 
 * @author Laszlo Hornyak
 */
class PGJClassLoaderAdapter extends ClassLoader {

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		return cl.load(name);
	}

	private PLJClassLoader cl = null;

	/**
	 * 
	 */
	protected PGJClassLoaderAdapter(PLJClassLoader cl) {
		super();
		this.cl = cl;
	}
}