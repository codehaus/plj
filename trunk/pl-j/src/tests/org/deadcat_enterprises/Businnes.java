package org.deadcat_enterprises;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

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
		System.out.println("i1: "+i1);
		System.out.println("i2: "+i2);
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


	static Category logcat = Category.getInstance(Businnes.LogThread.class);

	private class LogThread extends Thread {

		boolean should_stop = false;
		Random random = new Random();

		private synchronized void pleaseStop() {
			should_stop = true;
		}

		public void run() {
			try {
				while (!should_stop) {
					logcat.warn(this.getName() +" "+ System.currentTimeMillis());
					sleep(random.nextInt(10));
				}
			} catch (InterruptedException e) {
				logcat.warn("ooops", e);
			}
		}
	}

	public String threadedLogTest() {
		LogThread[] logthreads = new LogThread[100];
		for (int i = 0; i < 100; i++) {
			logthreads[i] = new LogThread();
			logthreads[i].start();
		}

		try {
			Thread.sleep(10000);
			logcat.warn("ok, now i kill everybody");
		} catch (InterruptedException e) {
			logcat.warn("hoppa", e);
		}
		for (int i = 0; i < 100; i++) {
			logthreads[i].pleaseStop();
			try {
				logthreads[i].join();
			} catch (InterruptedException e1) {
				logcat.error("oops", e1);
			}
		}
		logcat.warn("this should be the last log.");

		return "ok";
	}

}
