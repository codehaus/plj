/*
 * Created on Oct 17, 2004
 */
package org.pgj.typemapping.postgres;

import org.pgj.typemapping.MappingException;


/**
 * Bytea data type mapper.
 * 
 * @author Laszlo Hornyak
 */
public class PGBytea extends AbstractPGField {
	
	private final static Class[] classes = new Class[] {byte[].class};
	
	/* (non-Javadoc)
	 * @see org.pgj.typemapping.postgres.AbstractPGField#backMap(java.lang.Object)
	 */
	protected void backMap(Object obj) throws MappingException {
		if(obj == null){
			setNull(true);
			return;
		}
		if(obj instanceof byte[]){
			raw = (byte[])(obj);
			return;
		}
		throw new MappingException("Cannot map back"+obj.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#getJavaClasses()
	 */
	public Class[] getJavaClasses() {
		return classes;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#getPreferredClass()
	 */
	public Class getPreferredClass() {
		return byte[].class;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#get(java.lang.Class)
	 */
	public Object get(Class clazz) throws MappingException {
		if(clazz == byte[].class){
			byte[] ret = new byte[ raw.length - 4 ];
			System.arraycopy(raw, 4, ret, 0, raw.length - 4);
			return ret;
		}
		throw new MappingException("Can not map to "+clazz.getName());
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#defaultGet()
	 */
	public Object defaultGet() throws MappingException {
		return get(byte[].class);
		}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#rdbmsType()
	 */
	public String rdbmsType() {
		return "bytea";
	}

}
