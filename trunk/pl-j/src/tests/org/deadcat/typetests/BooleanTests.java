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

	void setBoolean(Boolean b) {
		logger.debug("setBoolean " + String.valueOf(b));
	}

	Boolean getBoolean() {
		logger.debug("getBoolean");
		return Boolean.TRUE;
	}

}