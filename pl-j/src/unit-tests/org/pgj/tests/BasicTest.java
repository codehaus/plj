/*
 * Created on Oct 12, 2004
 */
package org.pgj.tests;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;


/**
 * @author Laszlo Hornyak
 */
public class BasicTest extends TestCase {

	protected Connection conn = null;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql:plj","postgres74","postgres");
	}

	protected void tearDown() throws Exception {
		if(conn!= null)
			conn.close();
	}
}
