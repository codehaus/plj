package org.pgj.jdbc.postgresql.jdbc3;


import java.sql.SQLException;

public class Jdbc3Blob extends org.pgj.jdbc.postgresql.jdbc3.AbstractJdbc3Blob implements java.sql.Blob
{

	public Jdbc3Blob(org.pgj.jdbc.postgresql.PGConnection conn, int oid) throws SQLException
	{
		super(conn, oid);
	}

}
