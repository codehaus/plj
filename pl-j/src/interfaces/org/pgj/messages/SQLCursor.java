/*
 * Created on Jun 12, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.pgj.messages;

/**
 * @author Laszlo Hornyak
 * SQL Cursor operations
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
