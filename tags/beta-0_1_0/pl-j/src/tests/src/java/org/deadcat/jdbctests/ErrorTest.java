/*
 * Created on Feb 8, 2005
 */

package org.deadcat.jdbctests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;


/**
 * Error test utility UDF.
 * 
 * @author Laszlo Hornyak
 */
public class ErrorTest {

	static Logger logger = Logger.getLogger(ErrorTest.class);

	/**
	 * UDF for executing SQL queries. Returns something as result.
	 * 
	 * @param statement	an SQL query
	 * @return a string generated from the result
	 * 
	 * @jsproc.udf name="plj_test_exec_statement"
	 */
	public static String execSql(String statement) {
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;

		StringBuffer buf = new StringBuffer();
		try {
			conn = DriverManager.getConnection("jdbc:default:connection");
			sta = conn.prepareStatement(statement);
			res = sta.executeQuery();
			while (res.next()) {
				String s = res.getString(1);
				buf.append(s);
				buf.append("-----\n");
			}
		} catch (SQLException sqle) {
			logger.warn("Error:" + sqle);
			return "Error "+sqle.getMessage();
		} finally {
			if (res != null)
				try {
					res.close();
				} catch (SQLException e) {
					logger.warn("Closing resultset", e);
				}
			if (sta != null)
				try {
					sta.close();
				} catch (SQLException e) {
					logger.warn("Closing resultset", e);
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					logger.warn("Closing resultset", e);
				}

		}

		return buf.toString();
	}

}