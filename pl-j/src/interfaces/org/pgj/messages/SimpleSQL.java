/*
 * Created on Jun 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.pgj.messages;

/**
 * @author Laszlo Hornyak
 * A Simple SQL message.
 */
public class SimpleSQL extends SQL {
	
	String sql = null;
	
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
