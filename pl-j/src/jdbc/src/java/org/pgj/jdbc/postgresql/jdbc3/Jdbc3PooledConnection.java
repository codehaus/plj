package org.pgj.jdbc.postgresql.jdbc3;

import java.sql.Connection;

import org.pgj.jdbc.postgresql.jdbc2.optional.PooledConnectionImpl;

/**
 * JDBC3 implementation of PooledConnection, which manages
 * a connection in a connection pool.
 *
 * @author Aaron Mulder (ammulder@alumni.princeton.edu)
 * @version $Revision: 1.1 $
 */
public class Jdbc3PooledConnection extends PooledConnectionImpl
{
    Jdbc3PooledConnection(Connection con, boolean autoCommit)
    {
        super(con, autoCommit);
    }
}
