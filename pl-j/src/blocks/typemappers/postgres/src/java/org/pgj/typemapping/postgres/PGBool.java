package org.pgj.typemapping.postgres;

import org.pgj.typemapping.MappingException;

/**
 * PostgreSQL 'bool' datatype mapper.
 * 
 * @author Laszlo Hornyak
 */
public class PGBool extends AbstractPGField {

	static final Class[] classes = { Boolean.class };
	{
		this.raw = new byte[5];
	}

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
		return Boolean.class;
	}

	/**
	 * @see Field#get(Class)
	 */
	public Object get(Class clazz) throws MappingException {
		if (!clazz.equals(Boolean.class))
			throw new MappingException("Data type not supperted");
		if (raw.length != 5)
			throw new MappingException("Illegal data length");
		return new Boolean(raw[4] != 0);
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
		if (!(obj instanceof Boolean))
			throw new MappingException("Only Boolean can be mapped back.");
		raw = new byte[1];
		raw[0] = (byte) (((Boolean) obj).booleanValue() ? 1 : 0);
	}

	/**
	 * @see AbstractPGField#setObject(Object)
	 */
	protected void setObject(Object obj) throws MappingException {
		super.setObject(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.typemapping.Field#rdbmsType()
	 */
	public String rdbmsType() {
		return "bool";
	}

}