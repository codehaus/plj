/*-------------------------------------------------------------------------
 *
 * BaseConnection.java
 *	  The internal interface definition for a jdbc connection
 *
 * Copyright (c) 2003, PostgreSQL Global Development Group
 *
 * IDENTIFICATION
 *	  $Header: /home/projects/plj/scm-cvs/pl-j/src/jdbc/org/pgj/jdbc/postgresql/core/BaseConnection.java,v 1.1 2004-03-27 08:02:36 kocka Exp $
 *
 *-------------------------------------------------------------------------
 */
package org.pgj.jdbc.postgresql.core;

import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import org.pgj.jdbc.postgresql.PGConnection;
import org.pgj.jdbc.postgresql.PGNotification;

public interface BaseConnection extends PGConnection
{

	public void addNotification(PGNotification p_notification);
	public void addWarning(String msg);
	public void cancelQuery() throws SQLException;
	public Statement createStatement() throws SQLException;
	public BaseResultSet execSQL(String s) throws SQLException;
	public boolean getAutoCommit();
	public String getCursorName() throws SQLException;
	public Encoding getEncoding() throws SQLException;
	public DatabaseMetaData getMetaData() throws SQLException;
	public Object getObject(String type, String value) throws SQLException;
	public int getPGProtocolVersionMajor();
	public int getPGProtocolVersionMinor();
	public PGStream getPGStream();
	public String getPGType(int oid) throws SQLException;
	public int getPGType(String pgTypeName) throws SQLException;
	public int getSQLType(int oid) throws SQLException;
	public int getSQLType(String pgTypeName) throws SQLException;
	public boolean haveMinimumCompatibleVersion(String ver) throws SQLException;
	public boolean haveMinimumServerVersion(String ver) throws SQLException;
	public void setAutoCommit(boolean autoCommit) throws SQLException;
	public void setCursorName(String cursor) throws SQLException;

}

