package org.pgj.typemapping.postgres;

import org.pgj.typemapping.MappingException;

/**
 * @author bitfakk
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public final class PGNull extends AbstractPGField {
	
	static byte[] raw = new byte[0];
	static Class[] classes = new Class[0];
	
	/**
	 * Constructor for PGNull.
	 */
	public PGNull() {
		super();
	}

	/**
	 * @see Field#getJavaClasses()
	 */
	public Class[] getJavaClasses() {
		return classes;
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

	/**
	 * @see AbstractPGField#backMap(Object)
	 */
	protected void backMap(Object obj) throws MappingException {
	}

	/**
	 * @see AbstractPGField#setObject(Object)
	 */
	protected void setObject(Object obj) throws MappingException {
		super.setObject(obj);
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#rdbmsType()
	 */
	public String rdbmsType() {
		return "null";
	}

}
