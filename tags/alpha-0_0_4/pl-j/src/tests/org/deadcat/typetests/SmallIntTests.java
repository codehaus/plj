/*
 * Created on Aug 30, 2004
 */
package org.deadcat.typetests;

/**
 * 
 * 
 * @author Laszlo Hornyak
 * 
 */
public class SmallIntTests {

	public static Integer add(Integer a, Integer b){
		return new Integer(a.intValue() + b.intValue());
	}

	public static Integer sub(Integer a, Integer b){
		return new Integer(a.intValue() - b.intValue());
	}

	public static Integer mul(Integer a, Integer b){
		return new Integer(a.intValue() * b.intValue());
	}

}
