/*
 * Created on Aug 5, 2004
 */
package org.deadcat.typetests;

import org.apache.log4j.Logger;

/**
 * Boolean tests.
 * 
 * @author Laszlo Hornyak
 */
public class BooleanTests {

	private static Logger logger = Logger.getLogger(BooleanTests.class);

	/**
	 *  
	 */
	public BooleanTests() {
		super();
	}

	/**
	 * jsproc.udf name="plj_tests_bool_set"
	 */
	public static void setBoolean(Boolean b) {
		logger.debug("setBoolean " + String.valueOf(b));
	}

	/**
	 * Returns FALSE.
	 * jsproc.udf name="plj_tests_bool_get"
	 */
	public static Boolean getBoolean() {
		logger.debug("getBoolean");
		return Boolean.FALSE;
	}

	/**
	 * Performs OR operation on two Booleans.
	 * jsproc.udf name="plj_tests_bool_or"
	 */
	public static Boolean doOR(Boolean a, Boolean b) {
		return (a.booleanValue() || b.booleanValue()) ? Boolean.TRUE
				: Boolean.FALSE;
	}

	/**
	 * Performs AND operation on two Booleans.
	 * jsproc.udf name="plj_tests_bool_and"
	 */
	public static Boolean doAND(Boolean a, Boolean b) {
		return (a.booleanValue() && b.booleanValue()) ? Boolean.TRUE
				: Boolean.FALSE;
	}

	/**
	 * Performs negation on a Boolean.
	 * jsproc.udf name="plj_tests_bool_not"
	 */
	public static Boolean doNOT(Boolean b){
		return b.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
	}
	
}