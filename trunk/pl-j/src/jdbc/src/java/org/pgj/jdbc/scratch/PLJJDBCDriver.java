/*
 * Created on Jun 12, 2003
 */

package org.pgj.jdbc.scratch;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Embeded JDBC driver for PGJ to communicat with the database backend without
 * creating new connection or transaction.
 * WARNING: This driver is for developement purposes only.
 * 
 * @author Laszlo Hornyak
 */
public class PLJJDBCDriver implements Driver {

	public static final int MAJOR_VERSION = 0;
	public static final int MINOR_VERSION = 1;

	static {
		try {
			DriverManager.registerDriver(new PLJJDBCDriver());
		} catch (SQLException e) {
			System.out.println("gebasz");
		}
	}

	/**
	 * 
	 */
	public PLJJDBCDriver() {
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
	 */
	public Connection connect(String url, Properties info) throws SQLException {
		return new PLJJDBCConnection();
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#acceptsURL(java.lang.String)
	 */
	public boolean acceptsURL(String url) throws SQLException {
		System.out.println("jdbc url:" + url);
		return url == null ? false : url.startsWith("jdbc:default");
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)
	 */
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {

		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#getMajorVersion()
	 */
	public int getMajorVersion() {
		return MAJOR_VERSION;
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#getMinorVersion()
	 */
	public int getMinorVersion() {
		return MINOR_VERSION;
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#jdbcCompliant()
	 */
	public boolean jdbcCompliant() {
		return false;
	}

}