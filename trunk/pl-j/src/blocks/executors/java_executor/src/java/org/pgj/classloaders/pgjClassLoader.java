package org.pgj.classloaders;

/**
 * A classloader interface for PGJ. 
 * It is not only for loading classes, it stores it too.
 */
//TODO: should be called PLJClassLoader
public interface pgjClassLoader {

	/**
	 * Tells if the classloader has a specified class.
	 * @param fqn		the fully qualified class name
	 * @return true of so, false otherwise
	 */
	boolean hasClass(String fqn);

	/**
	 * Load class from repository.
	 * @param fqn		fully qualified name of the class.
	 * @return the loaded class.
	 */
	Class load(String fqn) throws ClassNotFoundException;

	/**
	 * Store compiled class.
	 * @param name		the name of the class
	 * @param raw		the raw data
	 */
	void store(String name, byte[] raw);

}
