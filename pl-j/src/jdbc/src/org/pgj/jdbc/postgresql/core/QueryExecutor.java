/*-------------------------------------------------------------------------
 *
 * QueryExecutor.java
 *     Executes a query on the backend.
 *
 * Copyright (c) 2003, PostgreSQL Global Development Group
 *
 * IDENTIFICATION
 *	  $Header: /home/projects/plj/scm-cvs/pl-j/src/jdbc/src/org/pgj/jdbc/postgresql/core/QueryExecutor.java,v 1.2 2004-07-06 18:22:22 kocka Exp $
 *
 *-------------------------------------------------------------------------
 */

package org.pgj.jdbc.postgresql.core;

import java.sql.SQLException;

public class QueryExecutor {

	//This version of execute does not take an existing result set, but 
	//creates a new one for the results of the query
	public static BaseResultSet execute(String[] p_sqlFrags, Object[] p_binds,
			BaseStatement statement) throws SQLException {
		return null;
	}

	//This version of execute reuses an existing result set for the query 
	//results, this is used when a result set is backed by a cursor and 
	//more results are fetched
	public static void execute(String[] p_sqlFrags, Object[] p_binds,
			BaseResultSet rs) throws SQLException {
	}


	private QueryExecutor() {
	}

}