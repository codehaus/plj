/*
 * Created on Sep 5, 2004
 */
package org.pgj.tools.pljava;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.pljava.TriggerData;

/**
 * PLJava triggerdata implemementation to have compatibility with the PLJava api.
 * 
 * @author Laszlo Hornyak
 */
public class PLJavaTriggerData implements TriggerData{

	/**
	 * 
	 */
	public PLJavaTriggerData() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getNew()
	 */
	public ResultSet getNew() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getOld()
	 */
	public ResultSet getOld() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getArguments()
	 */
	public String[] getArguments() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getName()
	 */
	public String getName() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.postgresql.pljava.TriggerData#getTableName()
	 */
	public String getTableName() throws SQLException {
		// TODO Auto-generated method stub
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
