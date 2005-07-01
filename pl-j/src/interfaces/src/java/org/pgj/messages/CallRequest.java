package org.pgj.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.pgj.typemapping.Field;

/**
 * Describes a object oriented method call on an object.<br>
 * <br>
 * In general about a call: A call has 
 * 
 * @author Laszlo Hornyak
 * @since 0.1
 */
public class CallRequest extends AbstractCall {

	/** Parameter vector. */
	private List params = null;
	/** Expected return type */
	private String expect = null;
	/** is oneway */
	private boolean oneWay;
	/**
	 * Add a parameter to the call.
	 * @param param pam-param :)
	 */
	public void addParam(Field param) {
		if(params == null)
			params = new ArrayList();
		params.add(param);
	}

	/**
	 * 
	 * @return
	 */
	public List getParams() {
		if(params == null)
			params = new Vector();
		return params;
	}

	/**
	 * Get the expected result type.
	 * @return the expected result type (RDBMS type name)
	 */
	public String getExpect() {
		return expect;
	}

	/**
	 * Set the expected result type (RDBMS type name).
	 * @param expected the expected result type
	 */
	public void setExpect(String expected) {
		expect = expected;
	}

	/**
	 * Oneway call.
	 * @return true if the call is oneway.
	 */
	public boolean isOneWay() {
		return oneWay;
	}

	/**
	 * Set oneway call.
	 * @param oneWay
	 */
	public void setOneWay(boolean oneWay) {
		this.oneWay = oneWay;
	}

}
