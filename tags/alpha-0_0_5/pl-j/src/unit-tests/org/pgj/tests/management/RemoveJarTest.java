/*
 * Created on Oct 13, 2004
 */
package org.pgj.tests.management;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.pgj.tests.BasicTest;


/**
 * @author Laszlo Hornyak
 */
public class RemoveJarTest extends BasicTest {

	public void testRemoveJar() throws SQLException{
		PreparedStatement sta = this.conn.prepareStatement("{call remove_jar (?)}");
		sta.setString(1, "plj-tests");
		sta.execute();
		sta.close();
	}
	
}
