/*-------------------------------------------------------------------------
 *
 * BaseStatement.java
 *	  The internal interface definition for a jdbc statement
 *
 * Copyright (c) 2003, PostgreSQL Global Development Group
 *
 * IDENTIFICATION
 *	  $Header: /home/projects/plj/scm-cvs/pl-j/src/jdbc/src/org/pgj/jdbc/postgresql/core/BaseStatement.java,v 1.1 2004-06-20 20:51:42 kocka Exp $
 *
 *-------------------------------------------------------------------------
 */
package org.pgj.jdbc.postgresql.core;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Vector;

import org.pgj.jdbc.postgresql.PGRefCursorResultSet;

public interface BaseStatement extends org.pgj.jdbc.postgresql.PGStatement
{
        public BaseResultSet createResultSet(Field[] fields, Vector tuples, String status, int updateCount, long insertOID, boolean binaryCursor) throws SQLException;
        public PGRefCursorResultSet createRefCursorResultSet(String cursorName) throws SQLException;

	public BaseConnection getPGConnection();

	/*
	 * The maxRows limit is set to limit the number of rows that
	 * any ResultSet can contain.  If the limit is exceeded, the
	 * excess rows are silently dropped.
	 */
	public void addWarning(String p_warning) throws SQLException;
	public void close() throws SQLException;
	public int getFetchSize();
 	public int getMaxFieldSize() throws SQLException;
	public int getMaxRows() throws SQLException;
	public int getResultSetConcurrency() throws SQLException;
	public String getFetchingCursorName();
	public SQLWarning getWarnings() throws SQLException;
 	public void setMaxFieldSize(int max) throws SQLException;

}
