/*
 * Created on Jul 20, 2003
 */

package org.pgj.typemapping.postgres;

import org.apache.log4j.Category;
import org.pgj.typemapping.MappingException;

/**
 * Smallint datatype adapter for PostgreSQL.
 * 
 * @author Laszlo Hornyak
 */
public class PGSmallInt extends AbstractPGField {

	private static Category cat = Category.getInstance(PGSmallInt.class);

	private static Class[] classes = { Integer.class };

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.typemapping.postgres.AbstractPGField#backMap(java.lang.Object)
	 */
	protected void backMap(Object obj) throws MappingException {

		if (raw == null)
			raw = new byte[8];

		if (!(obj instanceof Integer)) {
			throw new MappingException("I can map only Integers, sorry");
		}

		if (obj == null) {
			setNull(true);
			return;
		}

		int value = ((Integer) obj).intValue();
		//TODO false!
		raw[7] = (byte) (value & 0x000000ff);
		raw[6] = (byte) ((value & 0x0000ff00) >> 8);
		raw[5] = (byte) ((value & 0x00ff0000) >> 16);
		raw[4] = (byte) ((value & 0xff000000) >> 24);
		raw[3] = 0;
		raw[2] = 0;
		raw[1] = 0;
		raw[0] = 8;
		System.out.println(">" + value);
		System.out.println(">" + (value & 0x000000ff));
		System.out.println(">" + ((value & 0x0000ff00) >> 8));
		System.out.println(">" + ((value & 0x00ff0000) >> 16));
		System.out.println(">" + ((value & 0xff000000) >> 24));
		System.out.println("0:" + raw[0]);
		System.out.println("1:" + raw[1]);
		System.out.println("2:" + raw[2]);
		System.out.println("3:" + raw[3]);
	}

	/**
	 * @see org.pgj.typemapping.Field#getJavaClasses()
	 * 
	 * @todo return classes or classes.clone()? (speed or security?)
	 */
	public Class[] getJavaClasses() {
		cat.debug("getJavaClasses()");
		return (Class[]) classes.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.typemapping.Field#getPreferredClass()
	 */
	public Class getPreferredClass() {
		return Integer.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.typemapping.Field#get(java.lang.Class)
	 */
	public Object get(Class clazz) throws MappingException {
		// TODO Auto-generated method stub
		cat.debug("mapping to " + clazz.getName());
		if ("java.lang.Integer".equals(clazz.getName()))
			return defaultGet();

		throw new MappingException("sorry this is on my todo list");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.typemapping.Field#defaultGet()
	 */
	public Object defaultGet() throws MappingException {
		//TODO: this is NOT OKAY like this.
		System.out.println(">" + raw[0]);
		System.out.println(">" + raw[1]);
		System.out.println(">" + raw[2]);
		System.out.println(">" + raw[3]);
		return new Integer(raw[7] + (((int) raw[6]) << 16)
				+ (((int) raw[5]) << 8) + (((int) raw[4]) << 24));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.typemapping.Field#rdbmsType()
	 */
	public String rdbmsType() {
		return "int4";
	}

}