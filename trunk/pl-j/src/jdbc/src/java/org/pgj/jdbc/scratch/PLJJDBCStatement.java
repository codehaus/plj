/*
 * Created on Jun 12, 2003
 *
 */

package org.pgj.jdbc.scratch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.CommunicationException;
import org.pgj.messages.Result;
import org.pgj.messages.SQLCursorOpenWithSQL;
import org.pgj.messages.SimpleSQL;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;

/**
 * @author Laszlo Hornyak
 *  
 */
public class PLJJDBCStatement implements Statement {

	/**
	 * Communication Channel to RDBMS.
	 */
	Channel channel = null;

	/**
	 * The client of the session.
	 */
	Client client = null;

	/**
	 * The connection this statement is operating on.
	 */
	PLJJDBCConnection connection = null;

	ResultSet lastActionResultSet = null;

	int lastUpdateCount = -1;

	String cursorName = null;
	
	/**
	 * Constructor: set client and connection.
	 */
	public PLJJDBCStatement(Client client, PLJJDBCConnection connection)
			throws SQLException {
		super();
		if (client == null && connection == null)
			throw new SQLException("Client and connection mus not be null");
		this.connection = connection;
		this.client = client;
		this.channel = client.getChannel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	public synchronized ResultSet executeQuery(String sql) throws SQLException {

		synchronized (channel) {

			zeroLastResources();
			checkIfClosed();
			if(cursorName == null)
				cursorName = connection.getCursorName();

			SQLCursorOpenWithSQL msg = new SQLCursorOpenWithSQL();
			//msg.setClient(channel.)
			msg.setCursorName(cursorName);
			msg.setQuery(sql);

			Result r = (Result) connection.doSendReceive(msg);
			try {
				cursorName = (String) ((Field)r.get(0,0)).get(String.class);
			} catch (MappingException e) {
				throw new SQLException(e.getMessage());
			}

			// TODO this method needs testing
			return new PLJJDBCResultSet(connection, cursorName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(String sql) throws SQLException {

		zeroLastResources();
		checkIfClosed();

		connection.checkClosed();
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#close()
	 */
	public void close() throws SQLException {
		checkIfClosed();
		connection.checkClosed();

		channel = null;
		connection = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	public int getMaxFieldSize() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	public void setMaxFieldSize(int max) throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub

	}

	int maxRows = 0;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getMaxRows()
	 */
	public int getMaxRows() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return maxRows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	public void setMaxRows(int max) throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		maxRows = max;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	public int getQueryTimeout() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	public void setQueryTimeout(int seconds) throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#cancel()
	 */
	public void cancel() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		throw new SQLException("cancel() not implemented");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	public void setCursorName(String name) throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		cursorName = name;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	public boolean execute(String sql) throws SQLException {
		zeroLastResources();
		checkIfClosed();
		connection.checkClosed();

		switch (getStatementType(sql)) {
		case type_select:
			lastActionResultSet = executeQuery(sql);
		case type_update:

			break;
		default: {
			SimpleSQL ssql = new SimpleSQL();
			ssql.setClient(client);
			ssql.setSql(sql);
			try {
				synchronized (channel) {
					channel.sendToRDBMS(ssql);
					connection.doReceiveMessage();
				}
			} catch (CommunicationException e) {
				//TODO here it should be fired a runtime exception?
				throw new SQLException("CommunicationException: ".concat(e
						.getMessage()));
			} catch (MappingException e) {
			    throw new SQLException("Type mapping exception:".concat(e.getMessage()));
			}
		}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getResultSet()
	 */
	public ResultSet getResultSet() throws SQLException {
		checkIfClosed();
		connection.checkClosed();
		return lastActionResultSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getUpdateCount()
	 */
	public int getUpdateCount() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		return lastUpdateCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getMoreResults()
	 */
	public boolean getMoreResults() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	public int getResultSetConcurrency() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getResultSetType()
	 */
	public int getResultSetType() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	public void addBatch(String sql) throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#clearBatch()
	 */
	public void clearBatch() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#executeBatch()
	 */
	public int[] executeBatch() throws SQLException {

		checkIfClosed();
		zeroLastResources();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getConnection()
	 */
	public Connection getConnection() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	public boolean getMoreResults(int current) throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	public ResultSet getGeneratedKeys() throws SQLException {

		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {

		zeroLastResources();
		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		zeroLastResources();
		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#executeUpdate(java.lang.String,
	 *      java.lang.String[])
	 */
	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		zeroLastResources();
		checkIfClosed();
		connection.checkClosed();
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		// TODO Auto-generated method stub
		zeroLastResources();
		checkIfClosed();
		connection.checkClosed();

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		// TODO Auto-generated method stub
		connection.checkClosed();
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		// TODO Auto-generated method stub
		zeroLastResources();
		connection.checkClosed();
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		connection.checkClosed();
		return 0;
	}

	// =================
	// utility functions
	// =================

	/**
	 * Checks if the statement is closed ot not. Throws an exception if so,
	 * returns if not.
	 */
	private void checkIfClosed() throws SQLException {
		if (channel == null || connection == null)
			throw new SQLException("Statement is already closed.");
	}

	private final static int type_select = 0;

	private final static int type_insert = 1;

	private final static int type_update = 2;

	private final static int type_delete = 3;

	private final static int type_other = 4;

	private int getStatementType(String SQL) {
		String simpl = SQL.toLowerCase().trim();
		if (simpl.startsWith("select"))
			return type_select;
		if (simpl.startsWith("insert"))
			return type_insert;
		if (simpl.startsWith("update"))
			return type_update;
		if (simpl.startsWith("delete"))
			return type_delete;
		return type_other;
	}

	private void zeroLastResources() {
		lastActionResultSet = null;
		lastUpdateCount = -1;
	}

}