/*
 * Created on Aug 8, 2004
 */
package org.codehaus.plj.utils;

import java.util.Map;

/**
 * @author Laszlo Hornyak
 */
public class JDBCUtil {

	private final static InheritableThreadLocal tl = new InheritableThreadLocal();

	public static Map getConfiguration() {
		return (Map) tl.get();
	}

	public static void setConfiguration(Map conf) {
		tl.set(conf);
	}

}