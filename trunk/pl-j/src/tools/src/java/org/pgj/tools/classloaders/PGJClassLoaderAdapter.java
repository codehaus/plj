/*
 * Created on Mar 15, 2004
 */

package org.pgj.tools.classloaders;



/**
 * A very basic class to integrate PLJClassloaders.
 * 
 * @author Laszlo Hornyak
 */
public class PGJClassLoaderAdapter extends ClassLoader {

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
	public PGJClassLoaderAdapter(PLJClassLoader cl) {
		super();
		this.cl = cl;
	}
}