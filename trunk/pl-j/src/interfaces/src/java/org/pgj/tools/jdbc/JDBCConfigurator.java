/*
 * Created on Jul 22, 2004
 */
package org.pgj.tools.jdbc;

import org.apache.avalon.framework.configuration.Configuration;


/**
 * Avalon based interface to get JDBC configuration.
 * 
 * @author Laszlo Hornyak
 */
public interface JDBCConfigurator {
	Configuration getJDBCConfiguration();
}
