/*
 * Created on Jun 29, 2003
 */
package org.pgj.messages;

/**
 * Message to prepare an SQL statement for execution.
 * 
 * @author Laszlo Hornyak
 */
public class SQLPrepare extends SQL {
	
	/** the statement to prepare */
	private String statement = null;
	
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
