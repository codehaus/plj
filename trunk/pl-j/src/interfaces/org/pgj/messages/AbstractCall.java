/*
 * Created on Apr 1, 2004
 */
package org.pgj.messages;

/**
 * @author Laszlo Hornyak
 */
public abstract class AbstractCall extends Message {

	/** Name of the method to call. */
	private String methodname = null;
	/** The class that contains the method. */
	private String classname = null;
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

}
