/*
 * Created on Jun 12, 2003
 */

package org.codehaus.plj.jdbc.stratch;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.codehaus.plj.Channel;
import org.codehaus.plj.Client;
import org.codehaus.plj.CommunicationException;
import org.codehaus.plj.ExecutionCancelException;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.typemapping.MappingException;
import org.codehaus.plj.typemapping.TypeMapper;
import org.codehaus.plj.utils.ClientUtils;
import org.codehaus.plj.utils.JDBCUtil;

/**
 * PLJ JDBC Connection.
 * 
 * @author Laszlo Hornyak
 */
public class PLJJDBCConnection implements Connection {

	PlanPool planPool = null;

	private final static Category log = Category
			.getInstance(PLJJDBCConnection.class);

	/** We are communicating with the backend using this chanell. */
	protected Channel communicationChanell = null;

	/** The client (must attach to each message) */
	protected Client client = null;

	/** is it closed? */
	private boolean closed = false;

	/** helps keeping cursors unique */
	private volatile long cursorId = 0;

	private Map conf = null;

	private Object getConf(String name) throws SQLException {
		if(!conf.containsKey(name)) {
			throw new SQLException(
					"JDBC driver configuration not set properly for "
							.concat(name));
		}
		return conf.get(name);
	}
	
	protected int getIntFromConf(String name) throws SQLException {
		return ((Number)getConf(name)).intValue();
	}

	protected boolean getBooleanFromConf(String name) throws SQLException {
		return ((Boolean)getConf(name)).booleanValue();
	}

	protected String getStringFromConf(String name) throws SQLException {
		return getConf(name).toString();
	}

	protected ResultSet getResultSetFromConf(String name, Object[] params)
			throws SQLException {
		//TODO make me tricky implementation!
		PreparedStatement sta = prepareStatement(getStringFromConf(name));
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				sta.setNull(i, Types.BINARY); //whatever...
			} else if (params[i] instanceof Integer) {
				sta.setInt(i, ((Integer) params[i]).intValue());
			} else if (params[i] instanceof String) {
				sta.setString(i, (String) params[i]);
			} else if (params[i] instanceof Short) {
				sta.setShort(i, ((Short) params[i]).shortValue());
			} else if (params[i] instanceof Float) {
				sta.setFloat(i, ((Float) params[i]).floatValue());
			}
		}
		return sta.executeQuery();
	}

	/**
	 * Driver constructor.
	 * @throws SQLException if the driver is misconfigured 
	 */
	public PLJJDBCConnection() throws SQLException {
		super();
		client = ClientUtils.getClientforThread();
		communicationChanell = client.getChannel();
		conf = JDBCUtil.getConfiguration();
		if (getBooleanFromConf("usePlanPool")) {
			planPool = PlanPool.getPlanPool();
		}
		if(getBooleanFromConf("clientThreadingEnabled")){
			warnings = new Vector();
		} else {
			warnings = new ArrayList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		checkClosed();
		return new PLJJDBCStatement(client, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		checkClosed();
		return new PLJJDBCPreparedStatement(this, sql);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		// TODO Auto-generated method stub
		checkClosed();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String sql) throws SQLException {
		checkClosed();
		return sql;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		checkClosed();
		throw new PLJJDBCSQLException("transaction operations are unavailable");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		checkClosed();
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		checkClosed();
		throw new PLJJDBCSQLException("transaction operations are unavailable");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		checkClosed();
		throw new PLJJDBCSQLException("transaction operations are unavailable");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#close()
	 */
	public void close() throws SQLException {
		checkClosed();
		closed = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return closed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		checkClosed();
		return new PLJJDBCMetaData(this);
	}

	
	private boolean readOnly = false;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		log.error("not implemented");
		checkClosed();
		this.readOnly = readOnly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		log.error("not implemented");
		checkClosed();
		return readOnly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) throws SQLException {
		if(log.isEnabledFor(Priority.WARN))
			log.warn("setCatalog:"+catalog+" -- ignored");
		checkClosed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		checkClosed();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		checkClosed();
		throw new PLJJDBCSQLException("transaction operations are unavailable");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return 0;
	}

	private List warnings = null;
	private int warningPos = 0;

	/**
	 * 
	 * @param w
	 */
	protected void addWarning(SQLWarning w) {
		warnings.add(w);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		if(warnings.size() == 0)
			return null;
		warningPos = 0;
		return (SQLWarning) warnings.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		warnings.clear();
		warningPos = 0;
		checkClosed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		PLJJDBCStatement sta = new PLJJDBCStatement(client, this);
		log.error("not implemented");
		checkClosed();
		return sta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	private Map typeMap = null;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		TypeMapper mapper = this.client.getTypeMapper();
		
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map map) throws SQLException {
		// TODO Auto-generated method stub
		checkClosed();
		log.error("not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability) throws SQLException {
		// TODO Auto-generated method stub
		checkClosed();
		log.error("not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int,
	 *      int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		// TODO Auto-generated method stub
		log.error("not implemented");
		checkClosed();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Connection#prepareStatement(java.lang.String,
	 *      java.lang.String[])
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

	/**
	 * Send message to RDBMS and handle communication errors.
	 * 
	 * @param msg
	 * @throws ExecutionCancelException
	 */
	protected void doSendMessage(Message msg)
			throws ExecutionCancelException, SQLException {
		try {
			synchronized (this.communicationChanell) {
				msg.setClient(client);
				communicationChanell.sendToRDBMS(msg);
			}
		} catch (CommunicationException e) {
			throw new ExecutionCancelException(e);
		} catch (MappingException e) {
			throw new SQLException("Typemapping error at sending: " + e.getMessage());
		}
	}

	/**
	 * Receives a message. If the database sends an error and
	 * isStatementErrorIrrecoverable is true, then throws an
	 * ExecutionCancelException.
	 * 
	 * @return The message received from the RDBMS (if the operation is
	 *         successful)
	 * @throws SQLException
	 * @throws ExecutionCancelException
	 */
	protected Message doReceiveMessage() throws SQLException,
			ExecutionCancelException, SQLException {
		try {
			synchronized (this.communicationChanell) {
				Message msg = communicationChanell
						.receiveFromRDBMS(client);
				if (msg instanceof org.codehaus.plj.messages.Error) {
					throw new SQLException(((org.codehaus.plj.messages.Error) msg).getMessage());
				}
				return msg;
			}
		} catch (CommunicationException e) {
			throw new ExecutionCancelException("Communication failure", e);
		} catch (MappingException e) {
			throw new SQLException("Typemapping error at receiveing: " + e.getMessage());
		}
	}

	protected Message doSendReceive(Message msg)
			throws ExecutionCancelException, SQLException {
		synchronized (this.communicationChanell) {
			doSendMessage(msg);
			return doReceiveMessage();
		}
	}

}