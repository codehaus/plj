/*
 * Created on Aug 5, 2004
 */
package org.deadcat.typetests;

import org.apache.log4j.Logger;

/**
 * @author Laszlo Hornyak
 */
public class FloatTests {

	private static Logger logger = Logger.getLogger(FloatTests.class);

	public static void setFloat(Float f) {
		logger.debug("setFloat " + String.valueOf(f));
	}

	public static Float getFolat() {
		return new Float(1234.5678);
	}

}