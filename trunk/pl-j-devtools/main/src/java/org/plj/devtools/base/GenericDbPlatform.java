/*
 * Created on Mar 2, 2005
 */
package org.plj.devtools.base;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Laszlo Hornyak
 */
//TODO: edit comments for GenericDbPlatform
public abstract class GenericDbPlatform implements DbPlatform{

	private final static Map typeMap = new HashMap();
	
	static {
		typeMap.put(String.class.getName(), "varchar(255)");
		typeMap.put(Integer.class.getName(), "int");
		typeMap.put(Byte.class.getName(), "char");
		typeMap.put(Character.class.getName(),"char");
		typeMap.put(Short.class.getName(), "smallint");
		
		
		typeMap.put(int.class.getName(), "int");
		typeMap.put(byte.class.getName(), "short");
		typeMap.put(char.class.getName(), "char(1)");
		typeMap.put(float.class.getName(), "float");
		typeMap.put(double.class.getName(), "float");
		/* there is no boolean type in all rdbms */
		typeMap.put(boolean.class.getName(), "char(1)");
		//TODO finish this!
	}
	
	public String getDefaultRDBMSType(String fqn) {
		return (String)typeMap.get(fqn);
	}

	public String comment(String comment) {
		if(comment != null)
			return "--" + comment.replaceAll("\n", "\n--") + "\n\n";
		return "-- not documented\n\n";
	}
}
