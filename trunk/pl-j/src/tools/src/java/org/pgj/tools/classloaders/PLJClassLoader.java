package org.pgj.tools.classloaders;

/**
 * A classloader interface for PGJ. 
 * It is not only for loading classes, it stores it too.
 */
public interface PLJClassLoader {

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
	 * @param jar		the name of the jar to store in (has importance only from the point of view of deletion.)
	 */
	void store(String name, byte[] raw, String jar);

	/**
	 * 
	 * @param name
	 * @throws ClassNotFoundException
	 */
	void removeClass(String name) throws ClassNotFoundException;

	/**
	 * Remove a jar file.
	 * @param name	the name of the jar file
	 */
	void removeJar(String name);
}
