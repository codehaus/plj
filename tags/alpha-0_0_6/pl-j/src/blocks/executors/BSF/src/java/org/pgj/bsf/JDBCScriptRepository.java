/*
 * Created on Oct 16, 2004
 */

package org.pgj.bsf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;


/**
 * JDBC script repository to run with embeded JDBC drivers. 
 * 
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="scriptloader" lifestyle="singleton"
 * @avalon.service type="org.pgj.bsf.ScriptRepository"
 * 
 * @dna.component name="scriptloader" lifestyle="singleton"
 * @dna.service type="org.pgj.bsf.ScriptRepository"
 * 
 */
public class JDBCScriptRepository
		implements
			ScriptRepository,
			Configurable,
			LogEnabled {

	String sqlLoadScript = null;
	String sqlStoreScript = null;
	String sqlDeleteScript = null;
	String driverClass = null;
	Map cache = null;

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptLoader#loadScript(java.lang.String)
	 */
	public Script loadScript(String name) throws ScriptNotFoundException,
			ScriptStoreException {
		if (cache != null) {
			if (cache.containsKey(name))
				return (Script) cache.get(name);
		}

		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;

		Script script;
		try {
			conn = getConnection();
			sta = conn.prepareStatement(sqlLoadScript);
			sta.setString(1, name);
			res = sta.executeQuery();
			if (!res.next())
				throw new ScriptNotFoundException("Script \"" + name
						+ "\" not found");
			script = new Script(res.getString(1), res.getString(2), res
					.getString(3));
			return script;
		} catch (SQLException e) {
			throw new ScriptStoreException(e);
		} finally {
			try {
				if (res != null)
					res.close();
				if (sta != null)
					sta.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e1) {
				logger.warn("loadScript, freeing resources", e1);
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptLoader#storeScript(org.pgj.bsf.Script)
	 */
	public void storeScript(Script script) throws ScriptStoreException {

		Connection conn = null;
		PreparedStatement sta = null;

		try {
			conn = getConnection();
			sta = conn.prepareStatement(sqlStoreScript);
			sta.setString(1, script.getName());
			sta.setString(2, script.getLanguage());
			sta.setString(3, script.getSource());
			sta.execute();

			if (cache != null) {
				cache.put(script.getName(), script);
			}

		} catch (SQLException e) {
			throw new ScriptStoreException(e);
		} finally {
			try {
				if (sta != null)
					sta.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e1) {
				logger.warn("storeScript, freeing resources", e1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptLoader#deleteScript(java.lang.String)
	 */
	public void deleteScript(String name) throws ScriptStoreException,
			ScriptNotFoundException {
		Connection conn = null;
		PreparedStatement sta = null;

		try {
			conn = getConnection();
			sta = conn.prepareStatement(sqlStoreScript);
			sta.setString(1, name);
			sta.execute();

			if (cache != null) {
				cache.remove(name);
			}

		} catch (SQLException e) {
			throw new ScriptStoreException(e);
		} finally {
			try {
				if (sta != null)
					sta.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e1) {
				logger.warn("storeScript, freeing resources", e1);
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		if (arg0.getChild("cached").getValueAsBoolean())
			cache = new HashMap();
		sqlLoadScript = arg0.getChild("load").getValue();
		sqlDeleteScript = arg0.getChild("delete").getValue();
		sqlStoreScript = arg0.getChild("store").getValue();
		logger.debug("JDBC scriptloader ready to attack");
	}

	Connection getConnection() throws SQLException {
		try {
			if (driverClass != null)
				Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			logger.warn("driver class not found: " + driverClass, e);
		}
		return DriverManager.getConnection("jdbc:default:connection", "", "");
	}

	Logger logger = null;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

}