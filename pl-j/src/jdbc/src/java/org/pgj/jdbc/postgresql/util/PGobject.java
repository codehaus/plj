/*-------------------------------------------------------------------------
 *
 * PGobject.java
 *     PGobject is a class used to describe unknown types
 *     An unknown type is any type that is unknown by JDBC Standards
 *
 * Copyright (c) 2003, PostgreSQL Global Development Group
 *
 * IDENTIFICATION
 *	  $Header: /home/projects/plj/scm-cvs/pl-j/src/jdbc/src/java/org/pgj/jdbc/postgresql/util/PGobject.java,v 1.1 2004-08-01 11:30:27 kocka Exp $
 *
 *-------------------------------------------------------------------------
 */
package org.pgj.jdbc.postgresql.util;

import java.io.Serializable;
import java.sql.SQLException;

public class PGobject implements Serializable, Cloneable
{
	protected String	type;
	protected String	value;

	/*
	 * This is called by org.pgj.jdbc.postgresql.Connection.getObject() to create the
	 * object.
	 */
	public PGobject()
	{}

	/*
	 * This method sets the type of this object.
	 *
	 * <p>It should not be extended by subclasses, hence its final
	 *
	 * @param type a string describing the type of the object
	 */
	public final void setType(String type)
	{
		this.type = type;
	}

	/*
	 * This method sets the value of this object. It must be overidden.
	 *
	 * @param value a string representation of the value of the object
	 * @exception SQLException thrown if value is invalid for this type
	 */
	public void setValue(String value) throws SQLException
	{
		this.value = value;
	}

	/*
	 * As this cannot change during the life of the object, it's final.
	 * @return the type name of this object
	 */
	public final String getType()
	{
		return type;
	}

	/*
	 * This must be overidden, to return the value of the object, in the
	 * form required by org.pgj.jdbc.postgresql.
	 * @return the value of this object
	 */
	public String getValue()
	{
		return value;
	}

	/*
	 * This must be overidden to allow comparisons of objects
	 * @param obj Object to compare with
	 * @return true if the two boxes are identical
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof PGobject)
			return ((PGobject)obj).getValue().equals(getValue());
		return false;
	}

	/*
	 * This must be overidden to allow the object to be cloned
	 */
	public Object clone()
	{
		PGobject obj = new PGobject();
		obj.type = type;
		obj.value = value;
		return obj;
	}

	/*
	 * This is defined here, so user code need not overide it.
	 * @return the value of this object, in the syntax expected by org.pgj.jdbc.postgresql
	 */
	public String toString()
	{
		return getValue();
	}
}
