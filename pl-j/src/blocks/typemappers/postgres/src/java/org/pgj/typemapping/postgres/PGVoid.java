package org.pgj.typemapping.postgres;

import org.pgj.typemapping.MappingException;

/**
 * @author Laszlo Hornyak
 * Void datatype for PostgreSQL.
 */
public class PGVoid extends AbstractPGField {

	/**
	 * Constructor for PGVoid.
	 */
	public PGVoid() {
		super();
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

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.postgres.AbstractPGField#backMap(java.lang.Object)
	 */
	protected void backMap(Object obj) throws MappingException {
	}

}
