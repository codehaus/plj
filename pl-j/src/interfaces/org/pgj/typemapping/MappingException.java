package org.pgj.typemapping;

/**
 * An exception, which is thrown in case of type mapping errors.
 */
public class MappingException extends Exception {
	public MappingException() {
		super();
	}
	public MappingException(String msg) {
		super(msg);
	}
}