package org.pgj.typemapping.postgres;

import org.pgj.typemapping.MappingException;

/**
 * PostgreSQL 'bool' datatype mapper.
 * 
 * @author Laszlo Hornyak
 */
public class PGBool extends AbstractPGField {

	static final Class[] classes = {Boolean.class};

	/**
	 * Constructor for PGBool.
	 */
	public PGBool() {
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
		if (!clazz.equals(Boolean.class))
			throw new MappingException("Data type not supperted");
		if (raw.length != 1)
			throw new MappingException("Illegal data length");
		return new Boolean(raw[0] != 0);
	}

	/**
	 * @see Field#defaultGet()
	 */
	public Object defaultGet() throws MappingException {
		return get(Boolean.class);
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
		return "bool";
	}

}
