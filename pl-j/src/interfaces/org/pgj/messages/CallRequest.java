package org.pgj.messages;

import java.util.Vector;
import org.pgj.typemapping.*;

/**
 * Describes a object oriented method call on an object.
 * @author Laszlo Hornyak
 * @since 0.1
 */
public class CallRequest extends Message {

	/**
	 * Parameter vector.
	 */
	Vector params = new Vector();

	/** Name of the method to call. */
	String methodname = null;
	/** The class that contains the method. */
	String classname = null;
	/** Expected return type */
	String expect = null;

	/** is oneway */
	boolean oneWay;

	public final static int INSTATNTIATION_SESSION = 1;
	public final static int INSTATNTIATION_CALL = 2;
	public final static int INSTATNTIATION_GLOBAL = 3;

	/** instantiation of the object. By default INSTATNTIATION_CALL*/
	int instantiation = INSTATNTIATION_CALL;
	
	/**
	 * Default constructor.
	 */
	public CallRequest() {
	}

	/**
	 * Set the @link #methodname
	 * @param name the name of the method.
	 */
	public void setMethodname(String name) {
		methodname = name;
	}

	/**
	 * Get the @link #methodname
	 * @return the name of the method.
	 */
	public String getMethodname() {
		return methodname;
	}

	/**
	 * Set the @link #classname
	 * @param name the class name.
	 */
	public void setClassname(String name) {
		classname = name;
	}
	
	/**
	 * Get the @link #classname
	 * @return the class name.
	 */
	public String getClassname() {
		return classname;
	}
	
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
