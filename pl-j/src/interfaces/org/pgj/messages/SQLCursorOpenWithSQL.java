/*
 * Created on Jun 12, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.pgj.messages;

/**
 * @author bitfakk
 * Open cursor message.
 */
public class SQLCursorOpenWithSQL extends SQLCursor {
	
	/** The SQL query */
	private String query = null; 
	
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param string the new query
	 */
	public void setQuery(String string) {
		query = string;
	}

}
