/*
 * Created on Jul 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.pgj.typemapping.postgres;

import org.apache.log4j.Category;
import org.pgj.typemapping.MappingException;

/**
 * @author bitfakk
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PGSmallInt extends AbstractPGField {

	private static Category cat = Category.getInstance(PGSmallInt.class);

	private static Class[] classes = { Integer.class };

	{
		raw = new byte[4];
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.postgres.AbstractPGField#backMap(java.lang.Object)
	 */
	protected void backMap(Object obj) throws MappingException {
		
		if(!(obj instanceof Integer)){
			throw new MappingException("I can map only Integers, sorry");
		}
		
		int value = ((Integer)obj).intValue();
		//TODO false!
		raw[0] = (byte)(value % (256*256*256)); 
		raw[1] = (byte)(value % (256*256));
		raw[2] = (byte)(value % (256));
		raw[3] = (byte)(value % (1));
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#getJavaClasses()
	 */
	public Class[] getJavaClasses() {
		cat.debug("getJavaClasses()");
		return classes;
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
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#rdbmsType()
	 */
	public String rdbmsType() {
		return "smallint";
	}

}
