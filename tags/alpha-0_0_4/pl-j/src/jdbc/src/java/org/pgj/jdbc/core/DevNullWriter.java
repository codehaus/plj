/*
 * Created on Jul 30, 2004
 */
package org.pgj.jdbc.core;

import java.io.IOException;
import java.io.Writer;

/**
 * A Writer that does NOTHING.
 * @author Laszlo Hornyak
 */
class DevNullWriter extends Writer {

	/**
	 * 
	 */
	public DevNullWriter() {
		super();
	}

	/**
	 * @param lock
	 */
	public DevNullWriter(Object lock) {
		super(new Object());
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException {

	}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	public void flush() throws IOException {
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) throws IOException {
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[])
	 */
	public void write(char[] cbuf) throws IOException {
	}
	/* (non-Javadoc)
	 * @see java.io.Writer#write(int)
	 */
	public void write(int c) throws IOException {
	}
	/* (non-Javadoc)
	 * @see java.io.Writer#write(java.lang.String, int, int)
	 */
	public void write(String str, int off, int len) throws IOException {
	}
	/* (non-Javadoc)
	 * @see java.io.Writer#write(java.lang.String)
	 */
	public void write(String str) throws IOException {
	}
}
