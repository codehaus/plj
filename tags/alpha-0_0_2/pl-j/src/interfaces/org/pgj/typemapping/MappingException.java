package org.pgj.typemapping;

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
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MappingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public MappingException(String msg) {
		super(msg);
	}
}