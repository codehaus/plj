/*
 * Created on Oct 17, 2004
 */
package org.deadcat.typetests;


/**
 * Tests for bytea data type..
 * @author Laszlo Hornyak
 */
public class ByteaTest {
	public static void printBytea(byte[] b){
		System.out.println("-------");
		for(int i = 0; i<b.length; i++){
			System.out.println(b[i]);
		}
		System.out.println("-------");
	}
	public static byte[] returnTheSameBytea(byte[] b){
		System.out.println("-------");
		for(int i = 0; i<b.length; i++){
			System.out.println(b[i]);
		}
		System.out.println("-------");
		return b;
	}
	
}
