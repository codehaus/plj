/*-------------------------------------------------------------------------
 *
 * PGStream.java
 *      This class is used by Connection for communicating with the
 *      backend.
 *
 * Copyright (c) 2003, PostgreSQL Global Development Group
 *
 * IDENTIFICATION
 *	  $Header: /home/projects/plj/scm-cvs/pl-j/src/blocks/chanells/febe/src/java/org/plj/chanells/febe/core/PGStream.java,v 1.1 2004-01-27 17:35:11 lazlo Exp $
 *
 *-------------------------------------------------------------------------
 */
package org.plj.chanells.febe.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * A modified version of the original JDBC PGStream.
 * @author Laszlo Hornyak (hack)
 * @author PostgreSQL JDBC team (original)
 * @version 0.1
 */
public class PGStream {
	public int port;
	public Socket connection;
	public InputStream pg_input;
	public BufferedOutputStream pg_output;
	private byte[] byte_buf = new byte[8 * 1024];

	/*
	 * Constructor:  Connect to the PostgreSQL back end and return
	 * a stream connection.
	 *
	 * @param host the hostname to connect to
	 * @param port the port number that the postmaster is sitting on
	 * @exception IOException if an IOException occurs below it.
	 */
	public PGStream(Socket connection) throws IOException {
		this.connection = connection;

		// Submitted by Jason Venner <jason@idiom.com> adds a 10x speed
		// improvement on FreeBSD machines (caused by a bug in their TCP Stack)
		connection.setTcpNoDelay(true);

		// Buffer sizes submitted by Sverre H Huseby <sverrehu@online.no>
		pg_input = new BufferedInputStream(connection.getInputStream(), 8192);
		pg_output =
			new BufferedOutputStream(connection.getOutputStream(), 8192);
	}

	/*
	 * Sends a single character to the back end
	 *
	 * @param val the character to be sent
	 * @exception IOException if an I/O error occurs
	 */
	public void SendChar(int val) throws IOException {
		pg_output.write((byte) val);
	}

	/*
	 * Sends an integer to the back end
	 *
	 * @param val the integer to be sent
	 * @param siz the length of the integer in bytes (size of structure)
	 * @exception IOException if an I/O error occurs
	 */
	public void SendInteger(int val, int siz) throws IOException {
		byte[] buf = new byte[siz];

		while (siz-- > 0) {
			buf[siz] = (byte) (val & 0xff);
			val >>= 8;
		}
		Send(buf);
	}

	/*
	 * Sends an integer to the back end
	 *
	 * @param val the integer to be sent
	 * @param siz the length of the integer in bytes (size of structure)
	 * @exception IOException if an I/O error occurs
	 */
	public void SendIntegerR(int val, int siz) throws IOException {
		byte[] buf = new byte[siz];

		for (int i = 0; i < siz; i++) {
			buf[i] = (byte) (val & 0xff);
			val >>= 8;
		}
		Send(buf);
	}

	/*
	 * Send an array of bytes to the backend
	 *
	 * @param buf The array of bytes to be sent
	 * @exception IOException if an I/O error occurs
	 */
	public void Send(byte buf[]) throws IOException {
		pg_output.write(buf);
	}

	/*
	 * Send an exact array of bytes to the backend - if the length
	 * has not been reached, send nulls until it has.
	 *
	 * @param buf the array of bytes to be sent
	 * @param siz the number of bytes to be sent
	 * @exception IOException if an I/O error occurs
	 */
	public void Send(byte buf[], int siz) throws IOException {
		Send(buf, 0, siz);
	}

	/*
	 * Send an exact array of bytes to the backend - if the length
	 * has not been reached, send nulls until it has.
	 *
	 * @param buf the array of bytes to be sent
	 * @param off offset in the array to start sending from
	 * @param siz the number of bytes to be sent
	 * @exception IOException if an I/O error occurs
	 */
	public void Send(byte buf[], int off, int siz) throws IOException {
		int i;

		pg_output.write(
			buf,
			off,
			((buf.length - off) < siz ? (buf.length - off) : siz));
		if ((buf.length - off) < siz) {
			for (i = buf.length - off; i < siz; ++i) {
				pg_output.write(0);
			}
		}
	}

	/*
	 * Receives a single character from the backend
	 *
	 * @return the character received
	 * @exception IOException if an I/O Error returns
	 */
	public int ReceiveChar() throws IOException {
		int c = 0;

		try {
			c = pg_input.read();
			if (c < 0)
				throw new IOException("postgresql.stream.eof");
		} catch (IOException e) {
			throw new IOException("postgresql.stream.ioerror");
		}
		return c;
	}

	/*
	 * Receives an integer from the backend
	 *
	 * @param siz length of the integer in bytes
	 * @return the integer received from the backend
	 * @exception IOException if an I/O error occurs
	 */
	public int ReceiveInteger(int siz) throws IOException {
		int n = 0;

		for (int i = 0; i < siz; i++) {
			int b = pg_input.read();

			if (b < 0)
				throw new IOException("postgresql.stream.eof");
			n = n | (b << (8 * i));
		}
		return n;
	}

	/*
	 * Receives an integer from the backend
	 *
	 * @param siz length of the integer in bytes
	 * @return the integer received from the backend
	 * @exception IOException if an I/O error occurs
	 */
	public int ReceiveIntegerR(int siz) throws IOException {
		int n = 0;

		for (int i = 0; i < siz; i++) {
			int b = pg_input.read();

			if (b < 0)
				throw new IOException("postgresql.stream.eof");
			n = b | (n << 8);
		}
		return n;
	}

	/*
	 * Receives a null-terminated string from the backend.	If we don't see a
	 * null, then we assume something has gone wrong.
	 *
	 * @param encoding the charset encoding to use.
	 * @return string from back end
	 * @exception IOException if an I/O error occurs, or end of file
	 */
	public String ReceiveString(Encoding encoding) throws IOException {
		int s = 0;
		byte[] rst = byte_buf;
		int buflen = rst.length;
		boolean done = false;
		while (!done) {
			while (s < buflen) {
				int c = pg_input.read();
				if (c < 0)
					throw new IOException("postgresql.stream.eof");
				else if (c == 0) {
					rst[s] = 0;
					done = true;
					break;
				} else {
					rst[s++] = (byte) c;
				}
				if (s >= buflen) { // Grow the buffer
					buflen = (int) (buflen * 2); // 100% bigger
					byte[] newrst = new byte[buflen];
					System.arraycopy(rst, 0, newrst, 0, s);
					rst = newrst;
				}
			}
		}
		return encoding.decode(rst, 0, s);
	}

	/*
	 * Read a tuple from the back end.	A tuple is a two dimensional
	 * array of bytes
	 *
	 * @param nf the number of fields expected
	 * @param bin true if the tuple is a binary tuple
	 * @return null if the current response has no more tuples, otherwise
	 *	an array of strings
	 * @exception IOException if a data I/O error occurs
	 */
	public byte[][] ReceiveTupleV3(int nf, boolean bin) throws IOException {
		//TODO: use l_msgSize
		int l_msgSize = ReceiveIntegerR(4);
		int i;
		int l_nf = ReceiveIntegerR(2);
		byte[][] answer = new byte[l_nf][0];

		for (i = 0; i < l_nf; ++i) {
			int l_size = ReceiveIntegerR(4);
			boolean isNull = l_size == -1;
			if (isNull)
				answer[i] = null;
			else {
				answer[i] = Receive(l_size);
			}
		}
		return answer;
	}

	/*
	 * Read a tuple from the back end.	A tuple is a two dimensional
	 * array of bytes
	 *
	 * @param nf the number of fields expected
	 * @param bin true if the tuple is a binary tuple
	 * @return null if the current response has no more tuples, otherwise
	 *	an array of strings
	 * @exception IOException if a data I/O error occurs
	 */
	public byte[][] ReceiveTupleV2(int nf, boolean bin) throws IOException {
		int i, bim = (nf + 7) / 8;
		byte[] bitmask = Receive(bim);
		byte[][] answer = new byte[nf][0];

		int whichbit = 0x80;
		int whichbyte = 0;

		for (i = 0; i < nf; ++i) {
			boolean isNull = ((bitmask[whichbyte] & whichbit) == 0);
			whichbit >>= 1;
			if (whichbit == 0) {
				++whichbyte;
				whichbit = 0x80;
			}
			if (isNull)
				answer[i] = null;
			else {
				int len = ReceiveIntegerR(4);
				if (!bin)
					len -= 4;
				if (len < 0)
					len = 0;
				answer[i] = Receive(len);
			}
		}
		return answer;
	}

	/*
	 * Reads in a given number of bytes from the backend
	 *
	 * @param siz number of bytes to read
	 * @return array of bytes received
	 * @exception IOException if a data I/O error occurs
	 */
	public byte[] Receive(int siz) throws IOException {
		byte[] answer = new byte[siz];
		Receive(answer, 0, siz);
		return answer;
	}

	/*
	 * Reads in a given number of bytes from the backend
	 *
	 * @param buf buffer to store result
	 * @param off offset in buffer
	 * @param siz number of bytes to read
	 * @exception IOException if a data I/O error occurs
	 */
	public void Receive(byte[] b, int off, int siz) throws IOException {
		int s = 0;

		while (s < siz) {
			int w = pg_input.read(b, off + s, siz - s);
			if (w < 0)
				throw new IOException("postgresql.stream.eof");
			s += w;
		}
	}

	/*
	 * This flushes any pending output to the backend. It is used primarily
	 * by the Fastpath code.
	 * @exception IOException if an I/O error occurs
	 */
	public void flush() throws IOException {
		pg_output.flush();
	}

	/*
	 * Closes the connection
	 *
	 * @exception IOException if a IO Error occurs
	 */
	public void close() throws IOException {
		pg_output.close();
		pg_input.close();
		connection.close();
	}
}
