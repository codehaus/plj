package org.pgj.jdbc.postgresql.jdbc3;

import org.pgj.jdbc.postgresql.core.Field;

public class Jdbc3ResultSetMetaData extends org.pgj.jdbc.postgresql.jdbc2.AbstractJdbc2ResultSetMetaData implements java.sql.ResultSetMetaData
{

	public Jdbc3ResultSetMetaData(java.util.Vector rows, Field[] fields)
	{
		super(rows, fields);
	}

}

