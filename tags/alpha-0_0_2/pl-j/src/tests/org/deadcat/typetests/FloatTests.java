/*
 * Created on Aug 5, 2004
 */
package org.deadcat.typetests;

import org.apache.log4j.Logger;

/**
 * @author Laszlo Hornyak
 */
//TODO: edit comments for FloatTests
public class FloatTests {

	Logger logger = Logger.getLogger(FloatTests.class);

	/**
	 *  
	 */
	public FloatTests() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setFloat(Float f) {
		logger.debug("setFloat " + String.valueOf(f));
	}

	public Float getFolat() {
		return new Float(1234.5678);
	}

}