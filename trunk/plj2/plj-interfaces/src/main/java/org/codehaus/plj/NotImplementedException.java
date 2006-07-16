/*
 * Created on Jun 12, 2003
 */
package org.codehaus.plj;

/**
 * This method can be thrown from anywhere in PGJ telling that road is under construction.
 * 
 * @author Laszlo Hornyak
 */
public class NotImplementedException extends RuntimeException {

	/**
	 * 
	 */
	public NotImplementedException() {
		super();
	}

	/**
	 * @param message
	 */
	public NotImplementedException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotImplementedException(String message, Throwable cause) {
	//	super(message, cause);
	}

	/**
	 * @param cause
	 */
	public NotImplementedException(Throwable cause) {
	//	super(cause);
	}

}
