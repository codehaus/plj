/*
 * Created on Jun 12, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.pgj;

/**
 * @author Laszlo Hornyak
 * This method can be thrown from anywhere in PGJ telling that road is under construction.
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
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public NotImplementedException(Throwable cause) {
		super(cause);
	}

}
