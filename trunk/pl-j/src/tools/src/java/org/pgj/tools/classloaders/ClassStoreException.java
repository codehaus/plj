/*
 * Created on Oct 18, 2004
 */
package org.pgj.tools.classloaders;


/**
 * This exception is thrown by PLJClassLoader components if the device they use 
 * (FS, JDBC, network, whatever) fails.
 * 
 * @author Laszlo Hornyak
 */
public class ClassStoreException extends Exception {

	/**
	 * 
	 */
	public ClassStoreException() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param message
	 */
	public ClassStoreException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param cause
	 */
	public ClassStoreException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param cause
	 */
	public ClassStoreException(Throwable cause) {
		super(cause);
	}
}
