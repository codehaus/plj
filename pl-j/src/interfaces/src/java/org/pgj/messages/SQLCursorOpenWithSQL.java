/*
 * Created on Jun 12, 2003
 */
package org.pgj.messages;

/**
 * Open cursor message.
 * 
 * @author Laszlo Hornyak
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
