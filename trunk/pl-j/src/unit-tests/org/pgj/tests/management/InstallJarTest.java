/*
 * Created on Oct 12, 2004
 */
package org.pgj.tests.management;

import java.sql.PreparedStatement;

import org.pgj.tests.BasicTest;


/**
 * @author Laszlo Hornyak
 */
public class InstallJarTest extends BasicTest {

	public void testInstallJar() throws Exception {
		PreparedStatement sta = this.conn.prepareStatement("{call install_jar (?, ?, ?)}");
		sta.setString(1, "/tmp/plj-tests.jar");
		sta.setString(2, "plj-tests");
		sta.setInt(3, 1);
		sta.execute();
		sta.close();
	}
}
