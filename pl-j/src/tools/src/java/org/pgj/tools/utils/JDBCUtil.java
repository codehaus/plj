/*
 * Created on Aug 8, 2004
 */
package org.pgj.tools.utils;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * @author Laszlo Hornyak
 */
public class JDBCUtil {

	private final static InheritableThreadLocal tl = new InheritableThreadLocal();

	public static Configuration getConfiguration() {
		return (Configuration) tl.get();
	}

	public static void setConfiguration(Configuration conf) {
		tl.set(conf);
	}

}