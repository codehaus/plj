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

	private static Logger logger = Logger.getLogger(StringTests.class);

	/**
	 * Send a string, log, do nothing.
	 * @jsproc.udf name="plj_tests_string_set"
	 */
	public static void setString(String str) {
		logger.debug("setString ".concat(str));
	}

	/**
	 * Get a string. Returns the system time in a string.
	 * @jsproc.udf name="plj_tests_string_get"
	 */
	public static String getString() {
		return String.valueOf("time right now:" + System.currentTimeMillis());
	}

	/**
	 * Concatenate two strings.
	 * @jsproc.udf name="plj_tests_string_concat"
	 */
	public static String doConcat(String first, String second){
		return first.concat(second);
	}

}