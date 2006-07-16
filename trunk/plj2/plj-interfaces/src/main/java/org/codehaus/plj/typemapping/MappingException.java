package org.codehaus.plj.typemapping;

/**
 * An exception, which is thrown in case of type mapping errors.
 * 
 * @author Laszlo Hornyak
 */
public class MappingException extends Exception {

	public MappingException() {
		super();
	}

	/**
	 * @param cause
	 */
	public MappingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MappingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MappingException(String msg) {
		super(msg);
	}
}