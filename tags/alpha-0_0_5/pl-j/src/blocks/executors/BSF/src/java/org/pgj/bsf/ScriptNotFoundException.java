/*
 * Created on Oct 16, 2004
 */
package org.pgj.bsf;


/**
 * Thrown if the scipt is not found.
 * 
 * @author Laszlo Hornyak
 */
public class ScriptNotFoundException extends Exception {

	/**
	 * 
	 */
	public ScriptNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ScriptNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ScriptNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ScriptNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
