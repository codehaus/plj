/*
 * Created on Jun 11, 2003
 *
 */
package org.pgj;

/**
 * @author Laszlo Hornyak
 * An exception class to throw when error occurs on the communication line between the RDBMS 
 * and the java process.
 */
public class CommunicationException extends Exception {

	/**
	 * 
	 */
	public CommunicationException() {
		super();
	}

	/**
	 * @param message
	 */
	public CommunicationException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CommunicationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public CommunicationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
