package org.pgj.jdbc.postgresql.jdbc3;


import java.sql.SQLException;

import org.pgj.jdbc.postgresql.PGRefCursorResultSet;
import org.pgj.jdbc.postgresql.core.BaseResultSet;
import org.pgj.jdbc.postgresql.core.BaseStatement;
import org.pgj.jdbc.postgresql.core.Field;

public class Jdbc3PreparedStatement extends org.pgj.jdbc.postgresql.jdbc3.AbstractJdbc3Statement implements java.sql.PreparedStatement
{

	public Jdbc3PreparedStatement(Jdbc3Connection connection, String sql) throws SQLException
	{
		super(connection, sql);
	}

	public BaseResultSet createResultSet (Field[] fields, java.util.Vector tuples, String status, int updateCount, long insertOID, boolean binaryCursor) throws SQLException
	{
                return new Jdbc3ResultSet((BaseStatement)this, fields, tuples, status, updateCount, insertOID, binaryCursor);
	}
         
  	public PGRefCursorResultSet createRefCursorResultSet (String cursorName) throws SQLException
	{
                return new Jdbc3RefCursorResultSet(this, cursorName);
	}
}

