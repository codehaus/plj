/*
 * Created on Sep 1, 2004
 */
package org.deadcat.typetests;

/**
 * @author Laszlo Hornyak
 */
public class BigIntTests {

	public static Long add(Long a, Long b) {
		return new Long(a.longValue() + b.longValue());
	}

	public static Long sub(Long a, Long b) {
		return new Long(a.longValue() - b.longValue());
	}

	public static Long mul(Long a, Long b) {
		return new Long(a.longValue() * b.longValue());
	}

}