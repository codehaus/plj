/*
 * Created on Mar 12, 2004
 */
package org.pgj.typemapping;


/**
 * @author Laszlo Hornyak
 * A Mapping Exception that is thrown when mapping the type back after a call.
 */
public class ReturnMappingException extends MappingException {

	/**
	 * 
	 */
	public ReturnMappingException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ReturnMappingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ReturnMappingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param msg
	 */
	public ReturnMappingException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
