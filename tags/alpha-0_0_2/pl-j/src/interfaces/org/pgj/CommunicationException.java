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
		super(message);
		
	}

	/**
	 * @param cause
	 */
	public CommunicationException(Throwable cause) {
		super(cause.getMessage());
		// TODO Auto-generated constructor stub
	}

}
