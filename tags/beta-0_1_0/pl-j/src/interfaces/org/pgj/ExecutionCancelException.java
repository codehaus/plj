/*
 * Created on Aug 1, 2004
 */
package org.pgj;

/**
 * Please don't catch me!
 * This exception is thrown if the RDBMS stops the execution of the
 * java code. Typicaly the GLUE components or some privileged code throws it
 * such as JDBC driver.
 * 
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
