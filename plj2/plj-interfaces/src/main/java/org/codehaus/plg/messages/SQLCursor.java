/*
 * Created on Jun 12, 2003
 */
package org.codehaus.plg.messages;

/**
 * Abstract baseclass of SQL Cursor operations.
 * 
 * @author Laszlo Hornyak
 */
public abstract class SQLCursor extends SQL {

	/** Cursor name. May be null, but not very usefull. */
	private String cursorName;

	/**
	 * @return
	 */
	public String getCursorName() {
		return cursorName;
	}

	/**
	 * @param string
	 */
	public void setCursorName(String string) {
		cursorName = string;
	}

}
