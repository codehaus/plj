/*
 * Created on Jul 22, 2004
 */

package org.pgj.tools.jdbc;

import org.apache.avalon.framework.configuration.Configuration;


/**
 * Utility class for geting JDBC configuration.
 * 
 * @author Laszlo Hornyak
 */
public class JDBCUtil {

	private static final InheritableThreadLocal tl = new InheritableThreadLocal();

	public static Configuration getConfiguration() {
		return (Configuration) tl.get();
	}

	public static void setConfiguration(Configuration conf) {
		tl.set(conf);
	}
}