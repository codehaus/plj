/*
 * Created on Jun 11, 2003
 *
 */
package org.pgj;

/**
 * An exception class to throw when error occurs on the communication line between the RDBMS 
 * and the java process.
 * 
 * @author Laszlo Hornyak
 */
public class CommunicationException extends Exception {

	/**
	 * @see Exception#Exception()
	 */
	public CommunicationException() {
		super();
	}

	/**
     * @see Exception#Exception(String)
	 */
	public CommunicationException(String message) {
		super(message);
	}

	/**
     * @see Exception#Exception(String, Throwable)
	 */
	public CommunicationException(String message, Throwable cause) {
		super(message);
		
	}

	/**
     * @see Exception#Exception(Throwable)
	 */
	public CommunicationException(Throwable cause) {
		super(cause.getMessage());
	}

}
