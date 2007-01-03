/*
 * Created on Apr 1, 2004
 */
package org.codehaus.plj.messages;

import org.codehaus.plj.typemapping.Tuple;

/**
 * Trigger call.
 * 
 * @author Laszlo Hornyak
 */
public class TriggerCallRequest extends AbstractCall {

	public final static int TRIGGER_REASON_INSERT = 1;
	public final static int TRIGGER_REASON_UPDATE = 2;
	public final static int TRIGGER_REASON_DELETE = 3;

	public final static int TRIGGER_FIRED_BEFORE = 0;
	public final static int TRIGGER_FIRED_AFTER = 1;

	private String tableName = null;
	private int reason = TRIGGER_REASON_INSERT;
	private int type = TRIGGER_FIRED_BEFORE;

	public final static int TRIGGER_ROWMODE_ROW = 0;
	public final static int TRIGGER_ROWMODE_STATEMENT = 1;

	private int rowmode = TRIGGER_ROWMODE_ROW;

	private Tuple _new = null;
	private Tuple _old = null;

	/**
	 * @return Returns the reason.
	 */
	public int getReason() {
		return reason;
	}
	/**
	 * @param reason
	 *            The reason to set.
	 */
	public void setReason(int reason) {
		this.reason = reason;
	}
	/**
	 * @return Returns the tableName.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            The tableName to set.
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return Returns the _new.
	 */
	public Tuple getNew() {
		return _new;
	}

	/**
	 * @param _new The _new to set.
	 */
	public void setNew(Tuple _new) {
		this._new = _new;
	}
	/**
	 * @return Returns the _old.
	 */
	public Tuple getOld() {
		return _old;
	}

	/**
	 * @param _old The _old to set.
	 */
	public void setOld(Tuple _old) {
		this._old = _old;
	}
	/**
	 * @return Returns the rowmode.
	 */
	public int getRowmode() {
		return rowmode;
	}

	/**
	 * @param rowmode The rowmode to set.
	 */
	public void setRowmode(int rowmode) {
		this.rowmode = rowmode;
	}
	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}
}
