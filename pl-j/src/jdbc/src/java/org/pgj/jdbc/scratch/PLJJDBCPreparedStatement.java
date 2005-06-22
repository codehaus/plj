/*
 * Created on Aug 8, 2004
 */

package org.pgj.jdbc.scratch;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import org.pgj.messages.Result;
import org.pgj.messages.SQLExecute;
import org.pgj.messages.SQLPrepare;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.TypeMapper;

/**
 * JDBC preparedstatement for the scratch driver.
 * 
 * @author Laszlo Hornyak
 */
public class PLJJDBCPreparedStatement extends PLJJDBCStatement implements PreparedStatement {

	private PLJJDBCConnection conn = null;

	private ArrayList args = new ArrayList(0);

	/**
	 * The statement to be prepared.
	 */
	private String statement = null;

	private int paramcnt = 0;

	/**
	 * The statement to send to the RDBMS.
	 */
	private String dbstatement = null;

	/**
	 * Is the statement actualy prepared?
	 */
	boolean prepared = false;

	/**
	 * the plan ID, or -1 if not prepared.
	 */
	private int plan = -1;

	/**
	 * The parameters.
	 */
	private Vector params = new Vector(0);

	/**
	 * The classes of the parameters.
	 */
	private Vector paramClasses = new Vector(0);

	private void mkPrepare() {
		Class[] clazzes = new Class[paramClasses.size()];
	}

	private void parse() {
		StringBuffer buf = new StringBuffer(statement);
		paramcnt = 0;
		int where = 0;
		boolean instr = false;
		for (where = 0; where < buf.length(); where++) {
			switch (buf.charAt(where)) {
				case '\'' :
					instr = !instr;
					break;
				case '?' :
					if (!instr) {
						paramcnt++;
						buf.replace(where, where + 1, "$" + paramcnt);
					}
					break;
				default :
			}
		}
		dbstatement = buf.toString();
	}

	/**
	 * Actualy prepares the statement and sets the plan ID.
	 * @throws SQLException on mapping problems mostly
	 */
	private void doPrepare() throws SQLException {

		if (prepared)
			return;

		Class[] args = new Class[paramClasses.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = (Class) paramClasses.get(i);
		}

		ArrayList lst;
		int i = 0;
		try {
			TypeMapper tm = conn.client.getTypeMapper();
			lst = new ArrayList();
			for (i = 0; i < args.length; i++) {
				lst.add(tm.getRDBMSTypeFor(args[i]));
			}
		} catch (MappingException e) {
			//XXX log this!!
			throw new SQLException("mapping exception:" + args[i].getName());
		}

		if(conn.planPool != null)
		synchronized (conn.planPool) {
			int pplan = conn.planPool.getPlan(dbstatement, args, conn);
			if (pplan != -1) {
				this.plan = pplan;
				prepared = true;
				return;
			}
		}

		SQLPrepare prep = new SQLPrepare();
		prep.setStatement(dbstatement);
		prep.setClient(conn.client);
		prep.setParamtypes(lst);
		Result ans = null;
		synchronized (conn.communicationChanell) {
			conn.doSendMessage(prep);
			ans = (Result) conn.doReceiveMessage();
		}
		try {
			this.plan = ((Integer) ((Result) ans).get(0, 0).get(Integer.class))
					.intValue();
			prepared = true;
		} catch (MappingException e2) {
			throw new SQLException("Result cannot be mapped to int");
		}
		if(conn.planPool != null)
		synchronized(conn.planPool){
			conn.planPool.putPlan(dbstatement, args, plan, conn);
		}
	}

	private Field[] doMakeFields() throws MappingException {
		TypeMapper mapper = conn.client.getTypeMapper();
		Field[] flds = new Field[params.size()];
		for (int i = 0; i < flds.length; i++) {
			Object o = params.get(i);
			if (o == null) {
				flds[i] = null;
			} else {
				flds[i] = mapper.backMap(o, mapper
						.getRDBMSTypeFor((Class) this.paramClasses.get(i)));
			}
		}
		return flds;
	}

	/**
	 * Construct prepared statements.
	 * @param conn			Connection to work on
	 * @param statement		The statement to prepare
	 * @throws SQLException if the statement is wrong.
	 */
	PLJJDBCPreparedStatement(PLJJDBCConnection conn, String statement)
			throws SQLException {
		super(conn.client, conn);
		this.conn = conn;
		this.statement = statement;
		parse();
		fetchSize = conn.getIntFromConf("defaultFetchSize");
		userFetchSizeOverride = conn
				.getBooleanFromConf("canUserOverrideFetchSize");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
		try {
			doPrepare();
			Field[] flds = doMakeFields();
			SQLExecute exec = new SQLExecute();
			exec.setAction(SQLExecute.ACTION_UPDATE);
			exec.setParams(flds);
			exec.setPlanid(plan);
			Result ans = (Result) conn.doSendReceive(exec);
			return ((Integer) ((Result) ans).get(0, 0).get(Integer.class))
					.intValue();
		} catch (MappingException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	public void addBatch() throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	public void clearParameters() throws SQLException {
		paramClasses.clear();
		params.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
		try {
			doPrepare();
			SQLExecute exec = new SQLExecute();
			Field[] flds = doMakeFields();
			exec.setPlanid(plan);
			exec.setParams(flds);
			exec.setAction(SQLExecute.ACTION_EXECUTE);
			Result res = (Result) conn.doSendReceive(exec);
		} catch (MappingException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
		return false;
	}

	private void setParam(int parameterindex, Class clazz, Object obj) {
		int i = parameterindex - 1;
		if (i >= params.size()) {
			params.setSize(i + 1);
		}
		params.set(i, obj);
		if (i >= paramClasses.size()) {
			paramClasses.setSize(i + 1);
			prepared = false;
		} else {
			if (paramClasses.get(i) != clazz)
				prepared = false;
		}
		paramClasses.set(i, clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		setParam(parameterIndex, Byte.class, new Byte(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		setParam(parameterIndex, Double.class, new Double(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		setParam(parameterIndex, Float.class, new Float(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		setParam(parameterIndex, Integer.class, new Integer(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		if (parameterIndex > this.paramcnt)
			throw new SQLException("Number of parameters from SQL: " + paramcnt);
		if (params.size() < paramcnt)
			params.setSize(paramcnt);

		params.set(parameterIndex - 1, null);
		switch (sqlType) {
			case Types.VARCHAR :
				paramClasses.set(parameterIndex - 1, String.class);
				break;
			case Types.BIGINT :
				paramClasses.set(parameterIndex - 1, BigInteger.class);
				break;
			case Types.BINARY :
				paramClasses.set(parameterIndex - 1, byte[].class);
				break;
			case Types.BIT :
				paramClasses.set(parameterIndex - 1, Boolean.class);
				break;
			case Types.BLOB :
				paramClasses.set(parameterIndex - 1, byte[].class);
				break;
			case Types.BOOLEAN :
				paramClasses.set(parameterIndex - 1, Boolean.class);
				break;
			case Types.CHAR :
				paramClasses.set(parameterIndex - 1, Character.class);
				break;
			case Types.CLOB :
				paramClasses.set(parameterIndex - 1, byte[].class);
				break;
			case Types.DATE :
				paramClasses.set(parameterIndex - 1, Date.class);
				break;
			case Types.DECIMAL :
				paramClasses.set(parameterIndex - 1, Number.class);
				break;
			case Types.DOUBLE :
				paramClasses.set(parameterIndex - 1, Double.class);
				break;
			case Types.FLOAT :
				paramClasses.set(parameterIndex - 1, Float.class);
				break;
			case Types.INTEGER :
				paramClasses.set(parameterIndex - 1, Integer.class);
				break;
			case Types.JAVA_OBJECT :
				paramClasses.set(parameterIndex - 1, byte[].class);
				break;
			default :
				throw new SQLException("Unhandled type:" + sqlType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		setParam(parameterIndex, Long.class, new Long(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		setParam(parameterIndex, Short.class, new Short(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		setParam(parameterIndex, Boolean.class, x
				? Boolean.TRUE
				: Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		setParam(parameterIndex, byte[].class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream,
	 *      int)
	 */
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream,
	 *      int)
	 */
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setUnicodeStream(int,
	 *      java.io.InputStream, int)
	 */
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader,
	 *      int)
	 */
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int,
	 *      int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scale) throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	public void setNull(int paramIndex, int sqlType, String typeName)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		setParam(parameterIndex, String.class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		setParam(parameterIndex, BigDecimal.class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	public void setURL(int parameterIndex, URL x) throws SQLException {
		setParam(parameterIndex, URL.class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	public void setArray(int i, Array x) throws SQLException {
		setParam(i, Array.class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	public void setBlob(int i, Blob x) throws SQLException {
		setParam(i, Blob.class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	public void setClob(int i, Clob x) throws SQLException {
		setParam(i, Clob.class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	public void setDate(int parameterIndex, Date x) throws SQLException {
		setParam(parameterIndex, Date.class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	public void setRef(int i, Ref x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
		try {
			doPrepare();
			Field[] flds = doMakeFields();
			SQLExecute exec = new SQLExecute();
			exec.setAction(SQLExecute.ACTION_OPENCURSOR);
			exec.setParams(flds);
			exec.setPlanid(plan);

			Result ansver = (Result) conn.doSendReceive(exec);


			PLJJDBCResultSet res = new PLJJDBCResultSet(conn, (String) ansver
					.get(0, 0).get(String.class));

			return res;
		} catch (MappingException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	public void setTime(int parameterIndex, Time x) throws SQLException {
		setParam(parameterIndex, Time.class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		setParam(parameterIndex, Timestamp.class, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date,
	 *      java.util.Calendar)
	 */
	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time,
	 *      java.util.Calendar)
	 */
	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp,
	 *      java.util.Calendar)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	/** Fetch direction */
	private int fetchdirection = ResultSet.FETCH_FORWARD;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return fetchdirection;
	}

	/** Fetch size of the cursor to open. */
	private int fetchSize = 0;

	/** Can the user code override fetch size? */
	private boolean userFetchSizeOverride = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return fetchSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getResultSetType()
	 */
	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getUpdateCount()
	 */
	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#cancel()
	 */
	public void cancel() throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#clearBatch()
	 */
	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#close()
	 */
	public void close() throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getMoreResults()
	 */
	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#executeBatch()
	 */
	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		if (userFetchSizeOverride)
			fetchSize = rows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	public void setMaxFieldSize(int max) throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	public boolean getMoreResults(int current) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	public void addBatch(String sql) throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	public boolean execute(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
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
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.Statement#getResultSet()
	 */
	public ResultSet getResultSet() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}