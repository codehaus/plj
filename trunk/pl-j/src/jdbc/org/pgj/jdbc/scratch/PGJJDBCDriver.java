/*
 * Created on Jun 12, 2003
 */
package org.pgj.jdbc.scratch;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.pgj.NotImplementedException;

/**
 * Embeded JDBC driver for PGJ to communicat with the database backend without
 * creating new connection or transaction.
 * 
 * @author Laszlo Hornyak
 */
public class PGJJDBCDriver implements Driver {

	public static final int MAJOR_VERSION = 0;
	public static final int MINOR_VERSION = 1;

	/** 
	 * The regexp to match on the
	 *  
	 */
	//TODO this MUST be configurable!!
	public static final String str_jdbc_url_re = "jdbc:default:.*";

	/** The regular expression object */
	static RE jdbc_url_re = null;
	static {
		try {
			jdbc_url_re = new RE(str_jdbc_url_re);
		} catch (RESyntaxException e) {
			throw new RuntimeException(
				"I have been unable to compile the hardcoded regular expression",
				e);
		}
	}

	/**
	 * 
	 */
	public PGJJDBCDriver() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
	 */
	public Connection connect(String url, Properties info)
		throws SQLException {
		throw new NotImplementedException("Connection connect(String url, Properties info)");
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#acceptsURL(java.lang.String)
	 */
	public boolean acceptsURL(String url) throws SQLException {
		return jdbc_url_re.match(url);
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
