/*
 * Created on Mar 12, 2004
 */
package org.codehaus.plj.typemapping;


/**
 * A Mapping Exception that is thrown when mapping the type back after a call.
 * 
 * @author Laszlo Hornyak
 */
public class ReturnMappingException extends MappingException {

	/**
	 * 
	 */
	public ReturnMappingException() {
		super();
	}

	/**
	 * @param cause
	 */
	public ReturnMappingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ReturnMappingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param msg
	 */
	public ReturnMappingException(String msg) {
		super(msg);
	}

}
