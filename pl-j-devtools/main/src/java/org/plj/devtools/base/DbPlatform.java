/*
 * Created on Jan 23, 2005
 */

package org.plj.devtools.base;

import java.util.List;
import java.util.Set;


/**
 * RDBMS platform adapter for ant tools.
 * @author Laszlo Hornyak
 */
public interface DbPlatform {

	String comment(String comment);

	String deployJar(String jar, String jarname);

	String undeployJar(String jarname);

	String createUdf(String clazz, String method, Set properties,
			String comment, String udfName, String returns, List params);

}