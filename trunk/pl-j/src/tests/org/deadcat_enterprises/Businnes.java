package org.deadcat_enterprises;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import org.pgj.Channel;
//import org.pgj.CommunicationException;
//import org.pgj.glue.GlueWorker;

public class Businnes {

	public Businnes() {

	}

	public int test() {
		System.out.println("Yes, yes it works!!! Oh what a feeling !!!");
		
		System.out.println("Hello\n\n"+System.getProperty("java.version")+"\n\n");
		
		return 1;
	}

	public int jdbcTest() throws SQLException {
		System.out.println("JDBC test started");

		Connection conn = DriverManager.getConnection("jdbc:default:database");

		Statement statement = conn.createStatement();
		try {

			ResultSet res = statement.executeQuery("select a from b");

			try {
				while (res.next()) {
					System.out.println(">".concat(res.getString(1)));
				}
			} finally {
				if (res != null)
					res.close();
			}

		} finally {
			if (statement != null)
				statement.close();
		}

		conn.close();

		return 1;
	}

//	public void hackMe() {
//		try {
//
//			Channel chanel = GlueWorker.getThreadChannel();
//
//			System.out.println(
//				"Serious security failure: client code can get a channel");
//
//			try {
//				chanel.sendToRDBMS(null);
//			} catch (CommunicationException ce) {
//
//			}
//
//			System.out.println(
//				"Even more serious security failure: client can send messages");
//
//		} catch (SecurityException se) {
//			System.out.println(
//				"I got something security exception, which is good.");
//		}
//
//	}

}
