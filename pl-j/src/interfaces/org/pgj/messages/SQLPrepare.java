/*
 * Created on Jun 29, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.pgj.messages;

/**
 * @author Laszlo Hornyak
 * Message to prepare an SQL statement for execution.
 */
public class SQLPrepare extends SQL {
	
	/** the statement to prepare */
	String statement = null;
	
	/**
	 * @return
	 */
	public String getStatement() {
		return statement;
	}

	/**
	 * @param string
	 */
	public void setStatement(String string) {
		statement = string;
	}

}
