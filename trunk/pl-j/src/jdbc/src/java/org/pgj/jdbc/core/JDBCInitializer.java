/*
 * Created on Jul 10, 2004
 */

package org.pgj.jdbc.core;

import java.sql.Driver;
import java.sql.DriverManager;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.pgj.tools.classloaders.PLJClassLoader;
import org.pgj.tools.jdbc.JDBCConfigurator;

/**
 * @author Laszlo Hornyak
 * 
 * Configures scratch JDBC.
 * 
 * @avalon.component name="jdbc-initializer" lifestyle="singleton"
 * @avalon.service type="org.pgj.tools.jdbc.JDBCConfigurator" version="1.0"
 * 
 * @dna.component
 * @dna.service type="org.pgj.tools.jdbc.JDBCConfigurator" version="1.0"
 * 
 */
public class JDBCInitializer implements Configurable, LogEnabled,
		Initializable, JDBCConfigurator, Serviceable {

	private Logger logger = null;

	private String jdbcDriverClass = null;

	private Configuration conf = null;

	private PLJClassLoader classLoader = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public synchronized void configure(Configuration arg0)
			throws ConfigurationException {
		conf = arg0.getChild("jdbc-config");
		jdbcDriverClass = arg0.getChild("class").getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		Class driverClass = classLoader.load(jdbcDriverClass);
		LoggerAdapter adapter = new LoggerAdapter(new DevNullWriter(null));
		adapter.logger = logger;
		DriverManager.setLogWriter(adapter);
		DriverManager.registerDriver(((Driver) driverClass.newInstance()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.tools.jdbc.JDBCConfigurator#getJDBCConfiguration()
	 */
	public Configuration getJDBCConfiguration() {
		return conf;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 * 
	 * @avalon.dependency type="org.pgj.tools.classloaders.PLJClassLoader" key="classloader"
	 * 
	 * @dna.dependency type="org.pgj.tools.classloaders.PLJClassLoader" key="classloader"
	 * 
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		classLoader = (PLJClassLoader) arg0.lookup("classloader");
	}

}