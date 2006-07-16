/*
 * Created on Jun 20, 2003
 */

package org.codehaus.plg.messages;

/**
 * A Simple SQL message.
 * 
 * @author Laszlo Hornyak
 */
public class SimpleSQL extends SQL {

	private String sql = null;

	/**
	 * @return
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param string
	 */
	public void setSql(String string) {
		sql = string;
	}
}
