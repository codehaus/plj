package org.pgj.jdbc.postgresql.jdbc2.optional;

import java.io.Serializable;

import javax.sql.DataSource;

/**
 * Simple DataSource which does not perform connection pooling.  In order to use
 * the DataSource, you must set the property databaseName.	The settings for
 * serverName, portNumber, user, and password are optional.  Note: these properties
 * are declared in the superclass.
 *
 * @author Aaron Mulder (ammulder@chariotsolutions.com)
 * @version $Revision: 1.1 $
 */
public class SimpleDataSource extends BaseDataSource implements Serializable, DataSource
{
	/**
	 * Gets a description of this DataSource.
	 */
	public String getDescription()
	{
		return "Non-Pooling DataSource from " + org.pgj.jdbc.postgresql.Driver.getVersion();
	}
}
