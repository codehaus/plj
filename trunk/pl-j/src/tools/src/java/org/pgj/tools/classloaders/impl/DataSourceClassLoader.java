/*
 * Created on Oct 11, 2004
 */
package org.pgj.tools.classloaders.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


/**
 * Overrides some methods of the JDBC classloader so enables using pools for
 * class loading. <b>Do not use this classloader with the internal driver</b>
 * 
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="ds-classloader"
 * @avalon.service type="org.pgj.tools.classloaders.PLJClassLoader"
 * 
 * @dna.component
 * @dna.service type="org.pgj.tools.classloaders.PLJClassLoader"
 * 
 */
public class DataSourceClassLoader extends JDBCClassLoader implements Serviceable{
	
	private DataSource dataSource = null;
	
	protected Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	public void configure(Configuration arg0) throws ConfigurationException {
		super.configure(arg0);
	}
	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 * 
	 * //@avalon.dependency key="" type=""
	 * 
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		
	}
}
