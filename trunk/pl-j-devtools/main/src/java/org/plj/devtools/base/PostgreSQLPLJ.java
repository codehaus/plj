/*
 * Created on Feb 27, 2005
 */

package org.plj.devtools.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PostgreSQL dbplatform implementation.
 * 
 * @author Laszlo Hornyak
 */
public class PostgreSQLPLJ extends AbstractPostgreSQLPlatform {

	public static final Map pgTypeMap = new HashMap();
	
	public String getDefaultRDBMSType(String fqn) {
		return super.getDefaultRDBMSType(fqn);
	}

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
//			buf.append(" ");
//			switch(p.getMode()){
//				case Parameter.mode_in:
//					buf.append("in");
//					break;
//				default:
//					//TODO throw exception here!
//			}
			buf.append(" ");
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

		buf.append(";\n\n");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see org.plj.devtools.base.DbPlatform#getName()
	 */
	public String getName() {
		return "PostgreSQL 8.0 PL-J";
	}

}