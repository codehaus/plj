/*
 * Created on Apr 6, 2005
 */
package org.plj.devtools.base;

import java.util.List;
import java.util.Set;


/**
 * Oracle 10g RDBMS platfrom adapter.
 * 
 * @author Laszlo Hornyak
 */
public class Oracle extends GenericDbPlatform {

	public String createUdf(String clazz, String method, Set properties,
			String comment, String udfName, String returns, List params) {
		// TODO Auto-generated method stub
		return null;
	}
	public String deployJar(String jar, String jarname) {
		// TODO Auto-generated method stub
		return null;
	}
	public String undeployJar(String jarname) {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.plj.devtools.base.DbPlatform#getName()
	 */
	public String getName() {
		return "Oracle 10G";
	}
}
