/*
 * Created on Aug 6, 2004
 */
package org.deadcat.typetests;

import org.apache.log4j.Logger;

/**
 * StringTestes
 * 
 * @author Laszlo Hornyak
 */
public class StringTests {

	private Logger logger = Logger.getLogger(StringTests.class);

	/**
	 *  
	 */
	public StringTests() {
		super();
	}

	public void setString(String str) {
		logger.debug("setString ".concat(str));
	}

	public String getString() {
		return String.valueOf("time right now:" + System.currentTimeMillis());
	}

}