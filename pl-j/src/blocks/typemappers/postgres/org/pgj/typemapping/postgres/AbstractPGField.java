package org.pgj.typemapping.postgres;

import org.apache.log4j.Category;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;

/**
 * @author bitfakk
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
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
		cat.debug("instantiated");
		System.out.println("instantiated");
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
		cat.debug("Alive!!");
		cat.debug(raw);
		return raw;
	}

}