package org.pgj.typemapping.postgres;

import org.apache.log4j.Category;
import org.pgj.typemapping.MappingException;

/**
 * Field for PostgreSQL 'varchar' type.
 *
 * @author Laszlo Hornyak
 */
public class PGVarchar extends AbstractPGField {

	static Category cat = Category.getInstance(PGVarchar.class);

	byte[] data;

	private final static Class[] classes = {String.class, byte[].class,
			char[].class};

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
		return classes[0];
	}

	/**
	 * @see Field#get(Class)
	 */
	public Object get(Class clazz) throws MappingException {
		if (clazz.equals(classes[0]))
			return getAsString();
		else if (clazz.equals(classes[1])) {
			return getAsString();
		} else
			throw new MappingException();
	}

	/**
	 * @see Field#defaultGet()
	 */
	public Object defaultGet() throws MappingException {
		return getAsString();
	}

	//--------------------
	// converter functions
	//--------------------

	private String getAsString() throws MappingException {
		return new String(raw, 4, raw.length - 4);
	}

	private byte[] getAsBytea() throws MappingException {
		return raw;
	}

	private char[] getAsChara() throws MappingException {
		return null;
	}

	/**
	 * @see AbstractPGField#backMap(Object)
	 */
	protected void backMap(Object obj) throws MappingException {
		if(obj == null)
		{
			setNull(true);
			return;
		}
		String tmp = obj.toString();
		byte t[] = tmp.getBytes();
		raw = new byte[t.length + 4];
		raw[0] = 0;raw[1] = 0;raw[2] = 0;raw[3] = 0;
		System.arraycopy(t, 0, raw, 4, t.length);
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
		return "varchar";
	}

}