/*
 * Created on Apr 1, 2004
 */

package org.pgj.messages;

import java.util.Collection;
import java.util.HashSet;

/**
 * Base class for method calls.
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

	/** Flags to use with */
	private Collection flags;

	/**
	 * Add a flag for the call.
	 * @param flag
	 */
	public void addFlag(String flag) {
		if (flags != null) {
			flags = new HashSet();
		}
		flags.add(flag);
	}

	/**
	 * Return true if it has flags.
	 * @return true if it has flags
	 */
	public boolean hasFlags() {
		return flags != null;
	}

	/**
	 * Get the flags.
	 * @return the flags
	 */
	public Collection getFlags() {
		return flags;
	}

	/**
	 * 
	 * @param flag
	 * @return true if the call has a given flag
	 */
	public boolean hasFlag(String flag) {
		if (flags == null)
			return false;
		return flags.contains(flag);
	}
}