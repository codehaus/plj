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

	public static void setBoolean(Boolean b) {
		logger.debug("setBoolean " + String.valueOf(b));
	}

	public static Boolean getBoolean() {
		logger.debug("getBoolean");
		return Boolean.FALSE;
	}

	public static Boolean doOR(Boolean a, Boolean b) {
		return (a.booleanValue() || b.booleanValue()) ? Boolean.TRUE
				: Boolean.FALSE;
	}
	
	public static Boolean doAND(Boolean a, Boolean b) {
		return (a.booleanValue() && b.booleanValue()) ? Boolean.TRUE
				: Boolean.FALSE;
	}
	
	public static Boolean doNOT(Boolean b){
		return b.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
	}
	
}