/*
 * Created on Feb 27, 2005
 */

package org.plj.devtools.base;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * PostgreSQL dbplatform implementation.
 * 
 * @author Laszlo Hornyak
 */
public class PostgreSQLPLJ implements DbPlatform {

	public String createUdf(String clazz, String method, Set properties,
			String comment, String udfName, String returns, List params) {
		StringBuffer buf = new StringBuffer();
		buf.append("CREATE OR REPLACE FUNCTION ");
		buf.append(udfName);
		buf.append("\n(");

		Iterator i = params.iterator();
		boolean first = true;
		while(i.hasNext()){
			Parameter p = (Parameter)i.next();
			if(!first)
				buf.append(", ");
			else
				first = false;
			
			buf.append(p.getName());
			buf.append(" ");
			switch(p.getMode()){
				case Parameter.mode_in:
					buf.append("in");
					break;
				default:
					//TODO throw exception here!
			}
			buf.append(p.getRdbmsType());
			buf.append(" ");
		}

		buf.append(")\n");
		buf.append(" RETURNS ");
		buf.append(returns);
		buf.append(" AS \'\n");
		buf.append("classname=").append(clazz).append("\n");
		buf.append("methodname=").append(method).append("\n");
		buf.append("\'\n");
		buf.append(" LANGUAGE \'plj\' ");

		buf.append(";");
		return buf.toString();
	}

	public String deployJar(String jar, String jarname) {
		StringBuffer buf = new StringBuffer("SELECT sqlj.deploy_jar(\'");
		buf.append(jarname);
		buf.append("\');\n");
		return buf.toString();
	}

	public String undeployJar(String jarname) {
		StringBuffer buf = new StringBuffer("SELECT sqlj.remove_jar(\'");
		buf.append(jarname);
		buf.append("\');\n");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see org.plj.devtools.base.DbPlatform#comment(java.lang.String)
	 */
	public String comment(String comment) {
		return "--" + comment.replaceAll("\n", "\n--");
	}
}