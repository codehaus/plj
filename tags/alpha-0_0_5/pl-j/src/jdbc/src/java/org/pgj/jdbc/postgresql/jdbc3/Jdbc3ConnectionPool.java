package org.pgj.jdbc.postgresql.jdbc3;

import java.sql.SQLException;

import javax.naming.Reference;
import javax.sql.PooledConnection;

import org.pgj.jdbc.postgresql.jdbc2.optional.ConnectionPool;

/**
 * Jdbc3 implementation of ConnectionPoolDataSource.  This is
 * typically the interface used by an app server to interact
 * with connection pools provided by a JDBC driver.  PostgreSQL
 * does not support XADataSource, the other common connection
 * pooling interface (for connections supporting the two-phase
 * commit protocol).
 *
 * @author Aaron Mulder (ammulder@alumni.princeton.edu)
 * @version $Revision: 1.1 $
 */
public class Jdbc3ConnectionPool extends ConnectionPool
{
    /**
     * Gets a description of this DataSource.
     */
    public String getDescription()
    {
        return "Jdbc3ConnectionPool from " + org.pgj.jdbc.postgresql.Driver.getVersion();
    }

    /**
     * Gets a connection which may be pooled by the app server or middleware
     * implementation of DataSource.
     *
     * @throws java.sql.SQLException
     *		   Occurs when the physical database connection cannot be established.
     */
    public PooledConnection getPooledConnection() throws SQLException
    {
        return new Jdbc3PooledConnection(getConnection(), isDefaultAutoCommit());
    }

    /**
     * Gets a connection which may be pooled by the app server or middleware
     * implementation of DataSource.
     *
     * @throws java.sql.SQLException
     *		   Occurs when the physical database connection cannot be established.
     */
    public PooledConnection getPooledConnection(String user, String password) throws SQLException
    {
        return new Jdbc3PooledConnection(getConnection(user, password), isDefaultAutoCommit());
    }

    /**
     * Generates a JDBC object factory reference.
     */
    protected Reference createReference()
    {
        return new Reference(getClass().getName(), Jdbc3ObjectFactory.class.getName(), null);
    }
}
