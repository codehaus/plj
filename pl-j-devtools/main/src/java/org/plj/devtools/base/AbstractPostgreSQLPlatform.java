/*
 * Created on Apr 6, 2005
 */
package org.plj.devtools.base;


/**
 * Abstract PostgreSQL RDBSM platform adapter.
 * Intended to be baseclass of PL-J and PLJava adapters.
 * 
 * @author Laszlo Hornyak
 */
public abstract class AbstractPostgreSQLPlatform extends GenericDbPlatform {

	public String deployJar(String jar, String jarname) {
		StringBuffer buf = new StringBuffer("SELECT sqlj.deploy_jar(\'");
		buf.append(jarname);
		buf.append("\');\n\n");
		return buf.toString();
	}

	public String undeployJar(String jarname) {
		StringBuffer buf = new StringBuffer("SELECT sqlj.remove_jar(\'");
		buf.append(jarname);
		buf.append("\');\n\n");
		return buf.toString();
	}

}
