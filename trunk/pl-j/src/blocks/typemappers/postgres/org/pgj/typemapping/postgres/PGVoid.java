package org.pgj.typemapping.postgres;

import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;

/**
 * @author bitfakk
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class PGVoid implements Field {

	/**
	 * Constructor for PGVoid.
	 */
	public PGVoid() {
		super();
	}

	/**
	 * @see Field#set(byte[])
	 */
	public void set(byte[] raw) {
	}

	/**
	 * @see Field#get()
	 */
	public byte[] get() {
		return null;
	}

	/**
	 * @see Field#getJavaClasses()
	 */
	public Class[] getJavaClasses() {
		return null;
	}

	/**
	 * @see Field#getPreferredClass()
	 */
	public Class getPreferredClass() {
		return null;
	}

	/**
	 * @see Field#get(Class)
	 */
	public Object get(Class clazz) throws MappingException {
		return null;
	}

	/**
	 * @see Field#defaultGet()
	 */
	public Object defaultGet() throws MappingException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#rdbmsType()
	 */
	public String rdbmsType() {
		return "void";
	}

}
