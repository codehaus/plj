
package org.pgj.tools.classloaders.impl;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.pgj.tools.classloaders.PLJClassLoader;

//TODO test this classLoader!

/**
 * JDBC class loader component. It does not use a connection pool actualy, it is intended to use
 * with the internal driver, where a normal connection pool would fail at initialization.
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="jdbc-classloader"
 * @avalon.service type="org.pgj.tools.classloaders.PLJClassLoader"
 */
public class JDBCClassLoader extends ClassLoader
		implements
			PLJClassLoader,
			Configurable,
			Initializable,
			LogEnabled {

	protected String configUrl;
	protected String configDriver;
	protected String configUser;
	protected String configPwd;
	protected String configQuery;
	protected String configStore;
	protected String configDelete;
	protected String configDeleteJar;
	protected String configGetCnt;

	/**
	 * @see PLJClassLoader#load(String)
	 */
	public Class load(String fqn) throws ClassNotFoundException {
		ResultSet res = null;
		Connection conn = null;
		PreparedStatement sta = null;
		if (logger.isDebugEnabled())
			logger.debug("loading class: " + fqn);

		try {

			conn = getConnection();
			sta = conn.prepareStatement(configQuery);
			sta.setString(1, fqn);
			res = sta.executeQuery();

			if (!res.next())
				throw new ClassNotFoundException(
						"Class not found in the database");

			byte[] data = res.getBytes(1);

			Class ret = defineClass(fqn, data, 0, data.length);
			resolveClass(ret);

			return ret;

		} catch (SQLException sqle) {
			throw new ClassNotFoundException(
					"Class not found in the database.", sqle);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException sqle) {
				logger.error("Error closing result set.", sqle);
			}
		}
	}

	/**
	 * @see PLJClassLoader#store(String, byte[])
	 */
	public void store(String name, byte[] raw) {
		Connection conn = null;
		PreparedStatement sta = null;
		try {
			conn = getConnection();
			sta = conn.prepareStatement(configStore);
			sta.setString(1, name);
			sta.setBytes(2, raw);
			sta.execute();
		} catch (SQLException e) {
			logger.fatalError("", e);
		} finally {
			try {
				if (sta != null)
					sta.close();
			} catch (SQLException e1) {
				logger.error("", e1);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e1) {
				logger.error("", e1);
			}
		}
	}

	/**
	 * Configures the class loader.
	 * Configuration:
	 * <pre>
	 * 	-JDBCClassLoader
	 * 		-url: JDBC URL
	 * 		-driver: Driver class to preload.
	 * 		-user: Database user
	 * 		-password: Database user password
	 * 
	 * 		-select: The preparable query to load class
	 * 		-store: The preparable statement to store classes
	 * 		-check: The prepareble statement to check if the class exists
	 * </pre>
	 * All the attributes are required.
	 * 
	 * An example table for classes:
	 * create table jclasses (fqn varchar not null, classdata bytea not null, primary key(fqn)) 
	 * 
	 * Restrictions on query:<br>
	 *  resultset MUST return a 1 column, 1 row resultset. It MUST have exactly 1 parameter, which is the fully qualified name of the class.
	 *  example: select classdata from jclasses where fqn = ?
	 * <br>
	 * Restrictions on store:<br>
	 *  It MUST have 2 arguments, the first MUST be the fully qualified name, the second MUST be the clasdata. 
	 *  example> insert into jclasses (fqn, classdata) values (?, ?); 
	 * <br>
	 * @see Configurable#configure(Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		configDriver = arg0.getChild("driver").getValue();
		configUrl = arg0.getChild("url").getValue();
		configUser = arg0.getChild("user").getValue();
		configPwd = arg0.getChild("password").getValue();
		configQuery = arg0.getChild("select").getValue();
		configStore = arg0.getChild("store").getValue();
		configDelete = arg0.getChild("delete").getValue();
		configGetCnt = arg0.getChild("count").getValue();
		configDeleteJar = arg0.getChild("deleteJar").getValue();
	}

	/**
	 * @see Initializable#initialize()
	 */
	public void initialize() throws Exception {
		//load driver class
		Class.forName(configDriver);
	}

	/**
	 * @see org.pgj.tools.classloaders.PLJClassLoader#hasClass(String)
	 */
	public boolean hasClass(String fqn) {
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			conn = getConnection();
			sta = conn.prepareStatement(configStore);
			sta.setString(1, fqn);
			res = sta.executeQuery();
			if (!res.next()) {
				logger.error("Does not return a single row");
				return false;
			}

			return res.getInt(1) > 0;
		} catch (SQLException e) {
			logger.fatalError("", e);
		} finally {
			try {
				if (sta != null)
					sta.close();
			} catch (SQLException e1) {
				logger.error("", e1);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e1) {
				logger.error("", e1);
			}
		}
		return false;
	}

	protected Logger logger = null;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.classloaders.PLJClassLoader#removeClass(java.lang.String)
	 */
	public void removeClass(String name) throws ClassNotFoundException {
		Connection conn = null;
		PreparedStatement p = null;
		try {
			conn = getConnection();
			p = conn.prepareStatement(configDelete);
			p.setString(1, name);
			p.execute();
		} catch (SQLException e) {
			throw new ClassNotFoundException(e.getMessage());
		} finally {
			try {
				if (p != null)
					p.close();
			} catch (SQLException e1) {
				throw new ClassNotFoundException(e1.getMessage());
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e1) {
				throw new ClassNotFoundException(e1.getMessage());
			}
		}

	}

	/**
	 * Creates a connection with the database.
	 * @return
	 * @throws SQLException
	 */
	protected Connection getConnection() throws SQLException {
		return DriverManager.getConnection(configUrl, configUser, configPwd);
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.classloaders.PLJClassLoader#store(java.lang.String, byte[], java.lang.String)
	 */
	public void store(String name, byte[] raw, String jar) {
		Connection conn = null;
		PreparedStatement sta = null;
		try {
			conn = getConnection();
			sta = conn.prepareStatement(configStore);
			sta.setBytes(1, raw);
			sta.setString(2, name);
			if (jar == null) {
				sta.setString(3, jar);
			} else {
				sta.setNull(3, Types.VARCHAR);
			}
			sta.execute();
		} catch (SQLException e) {
			logger.error("store", e);
		} finally {
			try {
				if (sta != null)
					sta.close();
			} catch (SQLException e1) {
				logger.error("", e1);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e1) {
				logger.error("", e1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.classloaders.PLJClassLoader#removeJar(java.lang.String)
	 */
	public void removeJar(String name) {
		Connection conn = null;
		PreparedStatement sta = null;
		try {
			conn = getConnection();
			sta = conn.prepareStatement(configDeleteJar);
			sta.setString(1, name);
			sta.execute();
		} catch (SQLException e) {
			logger.error("removeJar", e);
		} finally {
			try {
				if (sta != null)
					sta.close();
			} catch (SQLException e1) {
				logger.error("", e1);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e1) {
				logger.error("", e1);
			}
		}
	}

}