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

	private static Class[] classes = {Integer.class};

	{
		raw = new byte[4];
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.postgres.AbstractPGField#backMap(java.lang.Object)
	 */
	protected void backMap(Object obj) throws MappingException {

		if (!(obj instanceof Integer)) {
			throw new MappingException("I can map only Integers, sorry");
		}

		if (obj == null) {
			setNull(true);
			return;
		}

		int value = ((Integer) obj).intValue();
		//TODO false!
		raw[0] = (byte) (value % (256 * 256 * 256));
		raw[1] = (byte) (value % (256 * 256));
		raw[2] = (byte) (value % (256));
		raw[3] = (byte) (value % (1));
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

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#getPreferredClass()
	 */
	public Class getPreferredClass() {
		return Integer.class;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#get(java.lang.Class)
	 */
	public Object get(Class clazz) throws MappingException {
		// TODO Auto-generated method stub
		cat.debug("mapping to " + clazz.getName());
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#defaultGet()
	 */
	public Object defaultGet() throws MappingException {
		//TODO: this is NOT OKAY like this.
		return new Integer(raw[0] + (raw[1] << 8) + (raw[2] << 16)
				+ (raw[3] << 32));
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#rdbmsType()
	 */
	public String rdbmsType() {
		return "int4";
	}

}
