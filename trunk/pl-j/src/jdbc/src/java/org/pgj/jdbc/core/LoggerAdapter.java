/*
 * Created on Jul 25, 2004
 */

package org.pgj.jdbc.core;

import java.io.PrintWriter;
import java.io.Writer;

import org.apache.avalon.framework.logger.Logger;


/**
 * @author Laszlo Hornyak
 */
class LoggerAdapter extends PrintWriter {

	/**
	 * @param out
	 */
	public LoggerAdapter(Writer out) {
		super(out);
	}

	Logger logger = null;

	/**
	 * @param out
	 */

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(boolean)
	 */
	public void print(boolean b) {
		logger.debug(String.valueOf(b));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(char)
	 */
	public void print(char c) {
		logger.debug(String.valueOf(c));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(char[])
	 */
	public void print(char[] s) {
		logger.debug(String.valueOf(s));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(double)
	 */
	public void print(double d) {
		logger.debug(String.valueOf(d));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(float)
	 */
	public void print(float f) {
		logger.debug(String.valueOf(f));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(int)
	 */
	public void print(int i) {
		logger.debug(String.valueOf(i));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(long)
	 */
	public void print(long l) {
		logger.debug(String.valueOf(l));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(java.lang.Object)
	 */
	public void print(Object obj) {
		logger.debug(String.valueOf(obj));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(java.lang.String)
	 */
	public void print(String s) {
		logger.debug(s);
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println()
	 */
	public void println() {
		logger.debug("");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(boolean)
	 */
	public void println(boolean x) {
		logger.debug(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(char)
	 */
	public void println(char x) {
		logger.debug(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(char[])
	 */
	public void println(char[] x) {
		logger.debug(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(double)
	 */
	public void println(double x) {
		logger.debug(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(float)
	 */
	public void println(float x) {
		logger.debug(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(int)
	 */
	public void println(int x) {
		logger.debug(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(long)
	 */
	public void println(long x) {
		logger.debug(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(java.lang.Object)
	 */
	public void println(Object x) {
		logger.debug(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(java.lang.String)
	 */
	public void println(String x) {
		logger.debug(String.valueOf(x));
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] buf, int off, int len) {
		logger.debug(String.valueOf(buf, off, len));
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[])
	 */
	public void write(char[] buf) {
		logger.debug(String.valueOf(buf));
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(int)
	 */
	public void write(int c) {
		logger.debug(String.valueOf(c));
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(java.lang.String, int, int)
	 */
	public void write(String s, int off, int len) {
		logger.debug(String.valueOf(s));
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(java.lang.String)
	 */
	public void write(String s) {
		logger.debug(String.valueOf(s));
	}
}