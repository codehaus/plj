/*
 * Created on Oct 13, 2004
 */
package org.pgj.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.pgj.tests.management.InstallJarTest;
import org.pgj.tests.management.RemoveJarTest;


/**
 * @author Laszlo Hornyak
 */
//TODO: edit comments for AllTests
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.pgj.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(InstallJarTest.class);
		suite.addTestSuite(BasicTest.class);
		suite.addTestSuite(Int4Test.class);
		suite.addTestSuite(RemoveJarTest.class);
		//$JUnit-END$
		return suite;
	}
}
