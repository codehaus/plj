package org.pgj.tools.classloaders.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
 * JDBC class loader component.
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="jdbc-classloader"
 * @avalon.service type="org.pgj.tools.classloaders.PLJClassLoader"
 */
public class JDBCClassLoader
	extends ClassLoader
	implements PLJClassLoader, Configurable, Initializable, Startable, LogEnabled{

	String configUrl;
	String configDriver;
	String configUser;
	String configPwd;
	String configQuery;
	String configStore;
	
	Connection connection;
	PreparedStatement QueryStatement;
	PreparedStatement StoreStatement;
	
	/**
	 * @see PLJClassLoader#load(String)
	 */
	public Class load(String fqn) throws ClassNotFoundException {
		ResultSet res = null;

		if(logger.isDebugEnabled())
			logger.debug("loading class: "+fqn);

		try{
		
			QueryStatement.setString(1, fqn);
			res = QueryStatement.executeQuery();
			
			if(!res.next())
				throw new ClassNotFoundException("Class not found in the database");
			
			byte[] data = res.getBytes(1);
			
			Class ret = defineClass(fqn, data, 0, data.length);
			resolveClass(ret);
			
			return ret;
			
		} catch (SQLException sqle){
			throw new ClassNotFoundException("Class not found in the database.",sqle);
		} finally {
			try{
				if(res != null)
					res.close();
			} catch(SQLException sqle){
				logger.error("Error closing result set.",sqle);
			}
		}
	}

	/**
	 * @see PLJClassLoader#store(String, byte[])
	 */
	public void store(String name, byte[] raw) {
		try {
			StoreStatement.setString(1, name);
			StoreStatement.setBytes(2, raw);
			StoreStatement.execute();
		} catch (SQLException e) {
			logger.fatalError("",e);
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
		configDriver = arg0.getAttribute("driver");
		configUrl = arg0.getAttribute("url");
		configUser = arg0.getAttribute("user");
		configPwd = arg0.getAttribute("password");
		configQuery = arg0.getAttribute("select");
		configStore = arg0.getAttribute("store");
	}

	/**
	 * @see Initializable#initialize()
	 */
	public void initialize() throws Exception {

		//load driver class
		Class.forName(configDriver);

		connection = DriverManager.getConnection(configUrl, configUser, configPwd);
		QueryStatement = connection.prepareStatement(configQuery);
		StoreStatement = connection.prepareStatement(configStore);
		
	}

	/**
	 * @see Startable#start()
	 */
	public void start() throws Exception {
	}

	/**
	 * @see Startable#stop()
	 */
	public void stop() throws Exception {
	}

	/**
	 * @see org.pgj.tools.classloaders.PLJClassLoader#hasClass(String)
	 */
	public boolean hasClass(String fqn) {
		//TODO: blank implementation
		return false;
	}

	Logger logger = null;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

}
