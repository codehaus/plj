/*
 * Created on Jul 11, 2004
 */

package org.deadcat.jdbctests;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author Laszlo Hornyak
 * 
 * JDBC tests both for experimental and PostgreSQL driver.
 */
public class JDBCTests {

	/**
	 * Log4J Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(JDBCTests.class);

	static {
		try {
			Class.forName("org.pgj.jdbc.scratch.PLJJDBCDriver");
		} catch (ClassNotFoundException e) {
			logger
					.warn("The plj jdbc driver couldn't load. There may be JDBC errors.");
		}
	}


	/**
	 * The most evident UDF using JDBC.
	 * 
	 * @return an integer (1)
	 */
	public static int doTest1() throws SQLException {

		Statement sta = null;
		Connection conn = null;

		try {
			conn = DriverManager.getConnection("jdbc:default");
			logger.debug("connection created");
			sta = conn.createStatement();
			logger.debug("statement created");
			sta
					.execute("insert into plj_testtable values (nextval('testable_seq'), 'Greetings from the JDBC driver :)')");
			logger.debug("statement executed");
			return 1;
		} finally {
			try {
				if (sta != null) {
					sta.close();
				}
			} catch (SQLException e) {
				logger.error("error closing statement");
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error("error closing connection");
			}
		}
	}

	public static void doPreparedTest1() throws SQLException{
		Connection conn = null;
		PreparedStatement sta = null;
		try{
			conn = DriverManager.getConnection("jdbc:default:connection");
			sta = conn.prepareStatement("insert into plj_prepsta_xmpl(str, str_maynull) values (?, ?)");
			for(int i=0; i<1000; i++){
				sta.setString(1, String.valueOf(System.currentTimeMillis()));
				sta.setString(2, "Greetings from Mirkwood!");
				sta.execute();
			}
		} finally {
			if(sta != null)
				sta.close();
			if(conn != null)
				conn.close();
		}
	}

	public static void doPreparedQueryTest1(Integer i) throws SQLException{
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try{
			conn = DriverManager.getConnection("jdbc:default:connection");
			sta = conn.prepareStatement("select id, str from plj_prepsta_xmpl where id = ?");
			sta.setInt(1, i.intValue());
			res = sta.executeQuery();
			while(res.next()){
				logger.debug("id: "+res.getInt(1));
				logger.debug("str: "+res.getString(2));
			}
		} finally {
			if(res != null)
				res.close();
			if(sta != null)
				sta.close();
			if(conn != null)
				conn.close();
		}
	}
	
}