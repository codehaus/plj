package org.codehaus.plj.postgresql;

import org.apache.log4j.Logger;
import org.codehaus.plj.typemapping.Field;
import org.codehaus.plj.typemapping.MappingException;

/**
 * The functionality needed by most PostgreSQL data type.
 * 
 * @author Laszlo Hornyak
 */
public abstract class AbstractPGField implements Field {

	protected byte[] raw = null;
	
	static Logger logger = Logger.getLogger(AbstractPGField.class);
	
	protected abstract void backMap(Object obj) throws MappingException;
	
	protected void setObject(Object obj) throws MappingException{
		backMap(obj);
	}
	
	/**
	 * Constructor for AbstractPGField.
	 */
	public AbstractPGField() {
		super();
	}

	/**
	 * @see Field#set(byte[])
	 */
	public final void set(byte[] raw) {
		this.raw = raw;
	}

	/**
	 * @see Field#get()
	 */
	public final byte[] get() {
		return raw;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#isNull()
	 */
	public boolean isNull() {
		return raw == null;
	}


	public void setNull(boolean nll){
		if(nll && raw != null){
			raw = null;
		}
		if(!nll && raw == null){
			raw = new byte[0];
		}
	}
}