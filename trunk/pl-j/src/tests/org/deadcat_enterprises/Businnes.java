package org.deadcat_enterprises;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Businnes {

	public Businnes() {

	}

	public int test(Integer i) {
		System.out.println("Wow, i got a number:" + i);

		return 1;
	}


	public int test() {
		System.out.println("Yes, yes it works!!! Oh what a feeling !!!");

		System.out.println("Hello\n\n" + System.getProperty("java.version")
				+ "\n\n");

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

}
