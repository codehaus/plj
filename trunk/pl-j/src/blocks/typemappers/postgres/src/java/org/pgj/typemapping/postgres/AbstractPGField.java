package org.pgj.typemapping.postgres;

import org.apache.log4j.Category;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;

/**
 * The functionality needed by most PostgreSQL data type.
 * 
 * @author Laszlo Hornyak
 */
public abstract class AbstractPGField implements Field {

	protected byte[] raw = null;
	
	static Category cat = Category.getInstance(AbstractPGField.class);
	
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