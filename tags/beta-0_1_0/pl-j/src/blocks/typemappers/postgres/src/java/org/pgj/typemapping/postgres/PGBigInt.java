/*
 * Created on Sep 1, 2004
 */
package org.pgj.typemapping.postgres;

import java.math.BigInteger;

import org.pgj.typemapping.MappingException;

/**
 * @author Laszlo Hornyak
 */
public class PGBigInt extends AbstractPGField {

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.postgres.AbstractPGField#backMap(java.lang.Object)
	 */
	protected void backMap(Object obj) throws MappingException {
		if(!(obj instanceof Long))
			throw new MappingException("Need Long.");
		if(obj == null){
			raw = null;
		}
		long value = ((Long)obj).longValue();
		raw = new byte[12];
		raw[0] = 12;
		raw[1] = 0;
		raw[2] = 0;
		raw[3] = 0;
		//--
		raw[11] = (byte) (value & 0xff);
		raw[10] = (byte) (value & 0xff << 8);
		raw[9] = (byte) (value & 0xff << 16);
		raw[8] = (byte) (value & 0xff << 24);
		raw[7] = (byte) (value & 0xff << 32);
		raw[6] = (byte) (value & 0xff << 40);
		raw[5] = (byte) (value & 0xff << 48);
		raw[4] = (byte) (value & 0xff << 56);
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#getJavaClasses()
	 */
	public Class[] getJavaClasses() {
		return new Class[]{Long.class};
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#getPreferredClass()
	 */
	public Class getPreferredClass() {
		return Long.class;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#get(java.lang.Class)
	 */
	public Object get(Class clazz) throws MappingException {
		if("java.lang.Long".equals(clazz.getName()))
			return getAsLong();
		throw new MappingException("Not a mapped type:"+clazz.getName());
	}
	
	private Long getAsLong(){
		byte[] b = new byte[raw.length -4];
		System.arraycopy(raw, 4, b, 0, raw.length -4);
		BigInteger i = new BigInteger(b);
		return new Long(i.longValue());
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#defaultGet()
	 */
	public Object defaultGet() throws MappingException {
		return getAsLong();
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#rdbmsType()
	 */
	public String rdbmsType() {
		return "int8";
	}

}
