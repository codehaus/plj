/*
 * Created on Aug 1, 2004
 */
package org.pgj;

/**
 * Please don't catch me!
 * @author Laszlo Hornyak
 */
public class ExecutionCancelException extends Error {

	/**
	 * 
	 */
	public ExecutionCancelException() {
		super();
	}

	/**
	 * @param message
	 */
	public ExecutionCancelException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ExecutionCancelException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ExecutionCancelException(String message, Throwable cause) {
		super(message, cause);
	}

}
