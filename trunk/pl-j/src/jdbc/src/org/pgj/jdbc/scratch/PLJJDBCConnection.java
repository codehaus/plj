/*
 * Created on Jun 12, 2003
 */

package org.pgj.jdbc.scratch;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Category;
import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.tools.channelutil.ClientUtils;

/**
 * PGJ JDBC Connection.
 * 
 * @author Laszlo Hornyak
 */
public class PLJJDBCConnection implements Connection {

	private final static Category log = Category
			.getInstance(PLJJDBCConnection.class);

	/** We are communicating with the backend using this chanell. */
	private Channel communicationChanell = null;

	/** The client (must attach to each message) */
	private Client client = null;

	/** is it closed? */
	private boolean closed = false;

	/** helps keeping cursors unique */
	private volatile long cursorId = 0;

	/**
	 * 
	 */
	public PLJJDBCConnection() {
		super();
		Client cli = ClientUtils.getClientforThread();
		communicationChanell = cli.getChannel();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		checkClosed();
		return new PLJJDBCStatement(client, this);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		log.error("not implemented: prepareStatement(String sql) ");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		// TODO Auto-generated method stub
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String sql) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		checkClosed();
		throw new PLJJDBCSQLException("transaction operations are unavailable");
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		checkClosed();
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		checkClosed();
		throw new PLJJDBCSQLException("transaction operations are unavailable");
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		checkClosed();
		throw new PLJJDBCSQLException("transaction operations are unavailable");
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#close()
	 */
	public void close() throws SQLException {
		checkClosed();
		closed = true;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return closed;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		checkClosed();
		throw new PLJJDBCSQLException("transaction operations are unavailable");
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		PLJJDBCStatement sta = new PLJJDBCStatement(client, this);
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map map) throws SQLException {
		// TODO Auto-generated method stub
		checkClosed();
		log.error("not implemented");
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability) throws SQLException {
		// TODO Auto-generated method stub
		checkClosed();
		log.error("not implemented");
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	public synchronized String getCursorName() {
		return "pgj-cursor-" + cursorId++;
	}

	/** Check if closed, throw an exception if so. */
	synchronized void checkClosed() throws SQLException {
		if (closed)
			throw new PLJJDBCSQLException("Connection closed");
	}

}
