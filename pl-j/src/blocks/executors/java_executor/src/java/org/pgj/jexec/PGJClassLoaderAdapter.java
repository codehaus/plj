/*
 * Created on Mar 15, 2004
 */

package org.pgj.jexec;

import org.pgj.tools.classloaders.ClassStoreException;
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
		try {
			return cl.load(name);
		} catch (ClassStoreException e) {
			throw new ClassNotFoundException("Class store exception", e);
		}
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