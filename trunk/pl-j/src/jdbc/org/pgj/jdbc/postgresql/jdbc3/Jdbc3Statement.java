package org.pgj.jdbc.postgresql.jdbc3;


import java.sql.*;
import java.util.Vector;
import org.pgj.jdbc.postgresql.PGRefCursorResultSet;
import org.pgj.jdbc.postgresql.core.BaseResultSet;
import org.pgj.jdbc.postgresql.core.Field;

/* $Header: /home/projects/plj/scm-cvs/pl-j/src/jdbc/org/pgj/jdbc/postgresql/jdbc3/Jdbc3Statement.java,v 1.1 2004-03-27 08:02:36 kocka Exp $
 * This class implements the java.sql.Statement interface for JDBC3.
 * However most of the implementation is really done in
 * org.pgj.jdbc.postgresql.jdbc3.AbstractJdbc3Statement or one of it's parents
 */
public class Jdbc3Statement extends org.pgj.jdbc.postgresql.jdbc3.AbstractJdbc3Statement implements java.sql.Statement
{

	public Jdbc3Statement (Jdbc3Connection c)
	{
		super(c);
	}

	public BaseResultSet createResultSet (Field[] fields, Vector tuples, String status, int updateCount, long insertOID, boolean binaryCursor) throws SQLException
	{
		return new Jdbc3ResultSet(this, fields, tuples, status, updateCount, insertOID, binaryCursor);
	}

  	public PGRefCursorResultSet createRefCursorResultSet (String cursorName) throws SQLException
	{
                return new Jdbc3RefCursorResultSet(this, cursorName);
	}
}
