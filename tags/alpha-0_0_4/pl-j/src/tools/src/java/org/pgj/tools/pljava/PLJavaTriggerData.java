/*
 * Created on Sep 5, 2004
 */
package org.pgj.tools.pljava;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.pgj.typemapping.Tuple;
import org.postgresql.pljava.TriggerData;

/**
 * PLJava triggerdata implemementation to have compatibility with the PLJava api.
 * 
 * @author Laszlo Hornyak
 */
public class PLJavaTriggerData implements TriggerData{

	private Tuple old = null;
	private PLJavaTupleResultSet oldRs = null;
	private Tuple _new = null;
	private PLJavaTupleResultSet newRs = null;
	
	/**
	 * 
	 */
	public PLJavaTriggerData(Tuple old, Tuple _new) {
		super();
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
		if(_new != null)
			return _new.getRelationName();
		if(old != null)
			return old.getRelationName();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getTableName()
	 */
	public String getTableName() throws SQLException {
		if(_new != null)
			return _new.getRelationName();
		if(old != null)
			return old.getRelationName();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredAfter()
	 */
	public boolean isFiredAfter() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredBefore()
	 */
	public boolean isFiredBefore() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredForEachRow()
	 */
	public boolean isFiredForEachRow() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredForStatement()
	 */
	public boolean isFiredForStatement() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredByDelete()
	 */
	public boolean isFiredByDelete() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredByInsert()
	 */
	public boolean isFiredByInsert() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#isFiredByUpdate()
	 */
	public boolean isFiredByUpdate() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
