/*
 * Created on Apr 1, 2004
 */
package org.pgj.messages;

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
}
