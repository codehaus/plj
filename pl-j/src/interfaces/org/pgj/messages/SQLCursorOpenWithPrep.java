/*
 * Created on Jun 29, 2003
 */
package org.pgj.messages;

import java.util.ArrayList;

/**
 * Open cursor with a prepared statement.
 * 
 * @author Laszlo Hornyak
 */
public class SQLCursorOpenWithPrep extends SQLCursor {

	private String statementID = null;
	private ArrayList data = new ArrayList();

	/**
	 * @return
	 */
	public String getStatementID() {
		return statementID;
	}

	/**
	 * @param string
	 */
	public void setStatementID(String string) {
		statementID = string;
	}

	public void setData(int nr, Object d) {
		data.add(nr, d);
	}

	public Object getData(int nr) {
		return data.get(nr);
	}

	public int getSize() {
		return data.size();
	}
}
