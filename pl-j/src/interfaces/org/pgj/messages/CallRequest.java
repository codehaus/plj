package org.pgj.messages;

import java.util.Vector;

import org.pgj.typemapping.Field;

/**
 * Describes a object oriented method call on an object.
 * 
 * @author Laszlo Hornyak
 * @since 0.1
 */
public class CallRequest extends AbstractCall {

	/**
	 * Parameter vector.
	 */
	private Vector params = new Vector();
	/** Expected return type */
	private String expect = null;
	/** is oneway */
	private boolean oneWay;
	public final static int INSTATNTIATION_SESSION = 1;
	public final static int INSTATNTIATION_CALL = 2;
	public final static int INSTATNTIATION_GLOBAL = 3;
	/** instantiation of the object. By default INSTATNTIATION_CALL*/
	private int instantiation = INSTATNTIATION_CALL;

	/**
	 * Add a parameter to the call.
	 * @param param pam-param :)
	 */
	public void addParam(Field param) {
		params.add(param);
	}

	/**
	 * 
	 */
	public Vector getParams() {
		return params;
	}

	/**
	 * Get the @link #expect
	 * 
	 */
	public String getExpect() {
		return expect;
	}

	public void setExpect(String e) {
		expect = e;
	}

	/**
	 * @return
	 */
	public boolean isOneWay() {
		return oneWay;
	}

	/**
	 * @param b
	 */
	public void setOneWay(boolean b) {
		oneWay = b;
	}

	/**
	 * @return
	 */
	public int getInstantiation() {
		return instantiation;
	}

	/**
	 * @param i
	 */
	public void setInstantiation(int i) {
		instantiation = i;
	}
}
