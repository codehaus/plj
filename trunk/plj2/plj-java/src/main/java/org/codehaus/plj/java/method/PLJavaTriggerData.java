/*
 * Created on Sep 5, 2004
 */

package org.codehaus.plj.java.method;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.codehaus.plj.messages.TriggerCallRequest;
import org.codehaus.plj.typemapping.Tuple;
import org.postgresql.pljava.TriggerData;

/**
 * PLJava triggerdata implemementation to have compatibility with the PLJava api.
 * 
 * @author Laszlo Hornyak
 */
public class PLJavaTriggerData implements TriggerData {

	TriggerCallRequest call = null;
	private Tuple old = null;
	private PLJavaTupleResultSet oldRs = null;
	private Tuple _new = null;
	private PLJavaTupleResultSet newRs = null;

	/**
	 * 
	 * @param call trigger call request
	 */
	public PLJavaTriggerData(TriggerCallRequest call) {
		super();
		Tuple old = call.getOld();
		Tuple _new = call.getNew();
		this.call = call;
		this.old = old;
		oldRs = new PLJavaTupleResultSet(old);
		this._new = _new;
		newRs = new PLJavaTupleResultSet(_new);
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getNew()
	 */
	public ResultSet getNew() throws SQLException {
		return newRs;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getOld()
	 */
	public ResultSet getOld() throws SQLException {
		return oldRs;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getArguments()
	 */
	public String[] getArguments() throws SQLException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getName()
	 */
	public String getName() throws SQLException {
		if (_new != null)
			return _new.getRelationName();
		if (old != null)
			return old.getRelationName();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getTableName()
	 */
	public String getTableName() throws SQLException {
		if (_new != null)
			return _new.getRelationName();
		if (old != null)
			return old.getRelationName();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredAfter()
	 */
	public boolean isFiredAfter() throws SQLException {
		return (call.getType() == TriggerCallRequest.TRIGGER_FIRED_AFTER);
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredBefore()
	 */
	public boolean isFiredBefore() throws SQLException {
		return (call.getType() == TriggerCallRequest.TRIGGER_FIRED_BEFORE);
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredForEachRow()
	 */
	public boolean isFiredForEachRow() throws SQLException {
		return (call.getRowmode() == TriggerCallRequest.TRIGGER_ROWMODE_ROW);
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredForStatement()
	 */
	public boolean isFiredForStatement() throws SQLException {
		return (call.getRowmode() == TriggerCallRequest.TRIGGER_ROWMODE_STATEMENT);
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredByDelete()
	 */
	public boolean isFiredByDelete() throws SQLException {
		return (call.getReason() == TriggerCallRequest.TRIGGER_REASON_DELETE);
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredByInsert()
	 */
	public boolean isFiredByInsert() throws SQLException {
		return (call.getReason() == TriggerCallRequest.TRIGGER_REASON_INSERT);
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredByUpdate()
	 */
	public boolean isFiredByUpdate() throws SQLException {
		return (call.getReason() == TriggerCallRequest.TRIGGER_REASON_UPDATE);
	}

}