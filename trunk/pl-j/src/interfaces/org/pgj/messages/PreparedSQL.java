/*
 * Created on Jun 29, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.pgj.messages;

import java.util.ArrayList;

/**
 * @author Laszlo Hornyak
 * Executes a prepared SQL statement with a given data.
 */
public class PreparedSQL extends SQL {

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
