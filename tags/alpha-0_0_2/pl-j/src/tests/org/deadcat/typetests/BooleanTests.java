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

	Logger logger = Logger.getLogger(BooleanTests.class);

	/**
	 *  
	 */
	public BooleanTests() {
		super();
	}

	public void setBoolean(Boolean b) {
		logger.debug("setBoolean " + String.valueOf(b));
	}

	public Boolean getBoolean() {
		logger.debug("getBoolean");
		return Boolean.FALSE;
	}

	public Boolean doOR(Boolean a, Boolean b) {
		return (a.booleanValue() || b.booleanValue()) ? Boolean.TRUE
				: Boolean.FALSE;
	}
	
	public Boolean doAND(Boolean a, Boolean b) {
		return (a.booleanValue() && b.booleanValue()) ? Boolean.TRUE
				: Boolean.FALSE;
	}
	
	public Boolean doNOT(Boolean b){
		return b.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
	}
	
}