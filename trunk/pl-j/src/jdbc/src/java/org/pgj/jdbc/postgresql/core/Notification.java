/*-------------------------------------------------------------------------
 *
 * Notification.java
 *     This is the implementation of the PGNotification interface
 *
 * Copyright (c) 2003, PostgreSQL Global Development Group
 *
 * IDENTIFICATION
 *	  $Header: /home/projects/plj/scm-cvs/pl-j/src/jdbc/src/java/org/pgj/jdbc/postgresql/core/Notification.java,v 1.1 2004-08-01 11:30:27 kocka Exp $
 *
 *-------------------------------------------------------------------------
 */
package org.pgj.jdbc.postgresql.core;

import org.pgj.jdbc.postgresql.PGNotification;

public class Notification implements PGNotification
{
	public Notification(String p_name, int p_pid)
	{
		m_name = p_name;
		m_pid = p_pid;
	}

	/*
	 * Returns name of this notification
	 */
	public String getName()
	{
		return m_name;
	}

	/*
	 * Returns the process id of the backend process making this notification
	 */
	public int getPID()
	{
		return m_pid;
	}

	private String m_name;
	private int m_pid;
	/* (non-Javadoc)
	 * @see org.pgj.jdbc.postgresql.PGNotification#getParameter()
	 */
	public String getParameter() {
		// TODO Auto-generated method stub
		return null;
	}

}

