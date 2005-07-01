/*
 * Created on Jun 29, 2003
 */

package org.pgj.messages;

import java.util.ArrayList;

/**
 * Executes a prepared SQL statement with a given data.
 * 
 * @author Laszlo Hornyak
 */
public class PreparedSQL extends SQL {

	private String statementID = null;
	private ArrayList data = null;

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
		if(data == null)
			data = new ArrayList();
		data.add(nr, d);
	}

	public Object getData(int nr) {
		if(data == null)
			return null;
		return data.get(nr);
	}

	public int getSize() {
		if(data == null)
			return 0;
		return data.size();
	}
}
