/*
 * Created on Sep 11, 2004
 */

package org.pgj.messages;

import org.pgj.typemapping.Field;


/**
 * Execute prepared SQL.
 * 
 * @author Laszlo Hornyak
 */
public class SQLExecute extends SQL {

	public static final int ACTION_EXECUTE = 0;
	public static final int ACTION_UPDATE = 1;
	public static final int ACTION_OPENCURSOR = 2;

	private int planid = -1;
	private Field[] params = null;
	private int action = -1;

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public Field[] getParams() {
		return params;
	}

	public void setParams(Field[] params) {
		this.params = params;
	}

	public int getPlanid() {
		return planid;
	}

	public void setPlanid(int planid) {
		this.planid = planid;
	}
}