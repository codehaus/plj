package org.deadcat_enterprises;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Category;

/**
 * A little collection of very stupid methods that can be java UDFs.
 * 
 * @author Laszlo Hornyak
 * 
 */
public class Businnes {

	public Businnes() {

	}

	public String test(Integer i) {
		System.out.println("Wow, i got a number:" + i);

		return "Halleluja, vazzeg!";
	}

	public String test() {
		System.out
				.println("\nIf you see this, it means your stored procedure is running.\n");

		System.out.println("Hello\n\n" + System.getProperty("java.version")
				+ "\n\n");

		return "Haleluja, vazzeg!";
	}

	public String jdbcTest() throws SQLException {
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

		return "Haleluja, vazzeg!";
	}

	public int testInt0() {
		System.out.println("testInt0() was called. its return value is 1984");
		return 1984;
	}

	public int testInt1(Integer i1, Integer i2) {
		return i1.intValue() + i2.intValue();
	}

	public int testInt2(Integer i1) {
		return i1.intValue();
	}

	public String testString0(String str) {
		return str.toLowerCase();
	}

	public String logTest(String logThis) {
		Category category = Category.getInstance(Businnes.class);
		category.error(logThis);
		return "logged: ".concat(logThis);
	}

}
