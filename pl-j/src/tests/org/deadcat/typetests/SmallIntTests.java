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

	public Integer add(Integer a, Integer b){
		return new Integer(a.intValue() + b.intValue());
	}

	public Integer sub(Integer a, Integer b){
		return new Integer(a.intValue() - b.intValue());
	}

	public Integer mul(Integer a, Integer b){
		return new Integer(a.intValue() * b.intValue());
	}

}
