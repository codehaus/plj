/*
 * Created on Sep 5, 2004
 */

package org.pgj.tools.pljava;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.pgj.Client;
import org.pgj.tools.utils.ClientUtils;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.Tuple;
import org.pgj.typemapping.TypeMapper;

/**
 * Resultset to be returned by PLJavaTriggerData as 'old' and 'new' tuples.
 * This class is under construction! (It is sooo damn boring!)
 * 
 * @author Laszlo Hornyak
 */
class PLJavaTupleResultSet implements ResultSet {

	private Tuple tuple = null;

	String[] columns = null;

	private static final Logger logger = Logger.getLogger(PLJavaTupleResultSet.class);

	/**
	 * 
	 * @param tuple	the PL-J tuple representation of the tuple.
	 */
	public PLJavaTupleResultSet(Tuple tuple) {
		super();
		this.tuple = tuple;
		Map fldMap = tuple.getFieldMap();
		columns = new String[fldMap.entrySet().size()];
		Iterator it = fldMap.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			columns[i] = (String) it.next();
			i++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		logger.warn("int getFetchDirection(): it makes no sense");
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		logger.warn("int getFetchSize(): it makes no sense");
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getRow()
	 */
	public int getRow() throws SQLException {
		logger.warn("int getRow(): makes no sense");
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException {
		logger.warn("int getType(): makes no sense");
		return ResultSet.TYPE_FORWARD_ONLY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#afterLast()
	 */
	public void afterLast() throws SQLException {
		logger.warn("void afterLast(): makes no sense");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	public void beforeFirst() throws SQLException {
		logger.warn("void beforeFirst(): makes no sense");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		//I hope you dont expect some magic from this!
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#close()
	 */
	public void close() throws SQLException {
		logger.warn("close(): does not make sense");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException {
		//I hope you dont expect some magic from this!
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		//I hope you dont expect some magic from this!
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException {
		//I hope you dont expect some magic from this!
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException {
		//I hope you dont expect some magic from this!
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateRow()
	 */
	public void updateRow() throws SQLException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#first()
	 */
	public boolean first() throws SQLException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#isFirst()
	 */
	public boolean isFirst() throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#isLast()
	 */
	public boolean isLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#last()
	 */
	public boolean last() throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#next()
	 */
	public boolean next() throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#previous()
	 */
	public boolean previous() throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException {
		return false;
	}

	private boolean wasNull = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#wasNull()
	 */
	public boolean wasNull() throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getByte(int)
	 */
	public byte getByte(int columnIndex) throws SQLException {
		return ((Byte) _getObject(columnIndex, Byte.class)).byteValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	public double getDouble(int columnIndex) throws SQLException {
		return ((Double) _getObject(columnIndex, Double.class)).doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	public float getFloat(int columnIndex) throws SQLException {
		return ((Float) _getObject(columnIndex, Float.class)).floatValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getInt(int)
	 */
	public int getInt(int columnIndex) throws SQLException {
		return ((Integer) _getObject(columnIndex, Integer.class)).intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(int columnIndex) throws SQLException {
		return ((Long) _getObject(columnIndex, Long.class)).longValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getShort(int)
	 */
	public short getShort(int columnIndex) throws SQLException {
		return ((Short) _getObject(columnIndex, Short.class)).shortValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		logger.warn("setFetchDirection makes no sense");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(int columnIndex) throws SQLException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(int row) throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return ((Boolean) _getObject(columnIndex, Boolean.class))
				.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(int rows) throws SQLException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		return (byte[]) _getObject(columnIndex, byte[].class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	public void updateByte(int columnIndex, byte x) throws SQLException {
		_setObj(columnIndex, new Byte(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	public void updateDouble(int columnIndex, double x) throws SQLException {
		_setObj(columnIndex, new Double(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	public void updateFloat(int columnIndex, float x) throws SQLException {
		_setObj(columnIndex, new Float(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	public void updateInt(int columnIndex, int x) throws SQLException {
		_setObj(columnIndex, new Integer(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	public void updateLong(int columnIndex, long x) throws SQLException {
		_setObj(columnIndex, new Long(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	public void updateShort(int columnIndex, short x) throws SQLException {
		_setObj(columnIndex, new Short(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		_setObj(columnIndex, x ? Boolean.TRUE : Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return (InputStream) _getObject(columnIndex, InputStream.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		//FIXME this should not be the same as getAsciiStream
		return (InputStream) _getObject(columnIndex, InputStream.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		//FIXME this should not be the same as getAsciiStream
		return (InputStream) _getObject(columnIndex, InputStream.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		//FIXME this ignores length
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		//FIXME this ignores length
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		//FIXME this should not be the same as getAsciiStream
		return (Reader) _getObject(columnIndex, Reader.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
	 */
	public void updateCharacterStream(int columnIndex, Reader x, int length)
			throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getObject(int)
	 */
	public Object getObject(int columnIndex) throws SQLException {
		return _getObject(columnIndex, Object.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
	 */
	public void updateObject(int columnIndex, Object x) throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
	 */
	public void updateObject(int columnIndex, Object x, int scale)
			throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getCursorName()
	 */
	public String getCursorName() throws SQLException {
		// TODO Auto-generated method stub
		return "tuple";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(int columnIndex) throws SQLException {
		return (String) _getObject(columnIndex, String.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(int columnIndex, String x) throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getByte(java.lang.String)
	 */
	public byte getByte(String columnName) throws SQLException {
		return ((Byte) _getObject(_getColId(columnName), Byte.class))
				.byteValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getDouble(java.lang.String)
	 */
	public double getDouble(String columnName) throws SQLException {
		return ((Double) _getObject(_getColId(columnName), Double.class))
				.doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getFloat(java.lang.String)
	 */
	public float getFloat(String columnName) throws SQLException {
		return ((Float) _getObject(_getColId(columnName), Float.class))
				.floatValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) throws SQLException {
		return _getColId(columnName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getInt(java.lang.String)
	 */
	public int getInt(String columnName) throws SQLException {
		return ((Integer) _getObject(_getColId(columnName), Integer.class))
				.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getLong(java.lang.String)
	 */
	public long getLong(String columnName) throws SQLException {
		return ((Long) _getObject(_getColId(columnName), Long.class))
				.longValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getShort(java.lang.String)
	 */
	public short getShort(String columnName) throws SQLException {
		return ((Short) _getObject(_getColId(columnName), Short.class))
				.shortValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateNull(java.lang.String)
	 */
	public void updateNull(String columnName) throws SQLException {
		_setObj(_getColId(columnName), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		return ((Boolean) _getObject(_getColId(columnName), Boolean.class))
				.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		return (byte[]) _getObject(_getColId(columnName), byte[].class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
	 */
	public void updateByte(String columnName, byte x) throws SQLException {
		_setObj(_getColId(columnName), new Byte(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
	 */
	public void updateDouble(String columnName, double x) throws SQLException {
		_setObj(columnName, new Double(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
	 */
	public void updateFloat(String columnName, float x) throws SQLException {
		_setObj(columnName, new Float(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
	 */
	public void updateInt(String columnName, int x) throws SQLException {
		_setObj(columnName, new Integer(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
	 */
	public void updateLong(String columnName, long x) throws SQLException {
		_setObj(columnName, new Long(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
	 */
	public void updateShort(String columnName, short x) throws SQLException {
		_setObj(columnName, new Short(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
	 */
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		_setObj(columnName, x ? Boolean.TRUE : Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
	 */
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return (BigDecimal)_getObject(columnIndex, BigDecimal.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		//FIXME this ignores scale
		return (BigDecimal)_getObject(columnIndex, BigDecimal.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
	 */
	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(int columnIndex) throws SQLException {
		return (URL)_getObject(columnIndex, URL.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getArray(int)
	 */
	public Array getArray(int i) throws SQLException {
		return (Array)_getObject(i, Array.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
	 */
	public void updateArray(int columnIndex, Array x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		return (Blob) _getObject(i, Blob.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
	 */
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {
		return (Clob) _getObject(i, Clob.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
	 */
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getDate(int)
	 */
	public Date getDate(int columnIndex) throws SQLException {
		return (Date) _getObject(columnIndex, Date.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
	 */
	public void updateDate(int columnIndex, Date x) throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		return (Ref) _getObject(i, Ref.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
	 */
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		throw new SQLException(
				"Resultset metadata not available for a truigger");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		logger.warn("getWarnings() makes no sense, returning null");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getStatement()
	 */
	public Statement getStatement() throws SQLException {
		logger.warn("getStatement() makes no sense, returning null");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getTime(int)
	 */
	public Time getTime(int columnIndex) throws SQLException {
		return (Time) _getObject(columnIndex, Time.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
	 */
	public void updateTime(int columnIndex, Time x) throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return (Timestamp) _getObject(columnIndex, Timestamp.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
	 */
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		_setObj(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
	 */
	public InputStream getAsciiStream(String columnName) throws SQLException {
		//FIXME ignore ascii or binary
		return (InputStream) _getObject(columnName, InputStream.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
	 */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		//FIXME ignore ascii or binary
		return (InputStream) _getObject(columnName, InputStream.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
	 */
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		return (InputStream) _getObject(columnName, InputStream.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String,
	 *      java.io.InputStream, int)
	 */
	public void updateAsciiStream(String columnName, InputStream x, int length)
			throws SQLException {
		//FIXME this ignores length
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String,
	 *      java.io.InputStream, int)
	 */
	public void updateBinaryStream(String columnName, InputStream x, int length)
			throws SQLException {
		//FIXME this ignores length
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String columnName) throws SQLException {
		return (Reader) _getObject(columnName, Reader.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String,
	 *      java.io.Reader, int)
	 */
	public void updateCharacterStream(String columnName, Reader reader,
			int length) throws SQLException {
		//FIXME this ignores length
		_setObj(columnName, reader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getObject(java.lang.String)
	 */
	public Object getObject(String columnName) throws SQLException {
		return _getObject(columnName, Object.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
	 */
	public void updateObject(String columnName, Object x) throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object,
	 *      int)
	 */
	public void updateObject(String columnName, Object x, int scale)
			throws SQLException {
		//FIXME this ignores scale
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	public Object getObject(int i, Map map) throws SQLException {
		return getObject(columns[i - 1], map);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getString(java.lang.String)
	 */
	public String getString(String columnName) throws SQLException {
		return (String) _getObject(columnName, String.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
	 */
	public void updateString(String columnName, String x) throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return (BigDecimal) _getObject(columnName, BigDecimal.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
	 */
	public BigDecimal getBigDecimal(String columnName, int scale)
			throws SQLException {
		//FIXME this ignoresd scale
		return (BigDecimal) _getObject(columnName, BigDecimal.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String,
	 *      java.math.BigDecimal)
	 */
	public void updateBigDecimal(String columnName, BigDecimal x)
			throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getURL(java.lang.String)
	 */
	public URL getURL(String columnName) throws SQLException {
		return (URL) _getObject(columnName, URL.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getArray(java.lang.String)
	 */
	public Array getArray(String colName) throws SQLException {
		return (Array) _getObject(colName, Array.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
	 */
	public void updateArray(String columnName, Array x) throws SQLException {
		_setObj(columnName, Array.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getBlob(java.lang.String)
	 */
	public Blob getBlob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
	 */
	public void updateBlob(String columnName, Blob x) throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getClob(java.lang.String)
	 */
	public Clob getClob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return (Clob) _getObject(colName, Clob.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
	 */
	public void updateClob(String columnName, Clob x) throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getDate(java.lang.String)
	 */
	public Date getDate(String columnName) throws SQLException {
		return (Date) _getObject(columnName, Date.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
	 */
	public void updateDate(String columnName, Date x) throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		//FIXME this ignores calendar
		return (Date) _getObject(columnIndex, Date.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getRef(java.lang.String)
	 */
	public Ref getRef(String colName) throws SQLException {
		return (Ref) _getObject(colName, Ref.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
	 */
	public void updateRef(String columnName, Ref x) throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getTime(java.lang.String)
	 */
	public Time getTime(String columnName) throws SQLException {
		return (Time) _getObject(columnName, Time.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
	 */
	public void updateTime(String columnName, Time x) throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		//FIXME this ignores calendar
		return (Time) _getObject(columnIndex, Time.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return (Timestamp) _getObject(columnName, Timestamp.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#updateTimestamp(java.lang.String,
	 *      java.sql.Timestamp)
	 */
	public void updateTimestamp(String columnName, Timestamp x)
			throws SQLException {
		_setObj(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		//FIXME this ignores calendar
		return (Timestamp) _getObject(columnIndex, Timestamp.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String colName, Map map) throws SQLException {
		org.pgj.typemapping.Field fld = tuple.getField(colName);
		String type = fld.rdbmsType();
		Class clazz = (Class) map.get(type);
		try {
			if (clazz == null)
				return fld.defaultGet();
			return fld.get(clazz);
		} catch (MappingException e) {
			logger.error("Mapping error: \nfield type:" + type + " \nclass:"
					+ clazz == null ? "<default>" : clazz.getName()
					+ " \n column:" + colName);
			throw new SQLException("Type mapping exception:" + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		//FIXME this ignores calendar
		return (Date) _getObject(columnName, Date.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		//FIXME this ignores calendar
		return (Time) _getObject(columnName, Time.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String,
	 *      java.util.Calendar)
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal)
			throws SQLException {
		//FIXME this ignores calendar
		return (Timestamp) _getObject(columnName, Timestamp.class);
	}

	private int _getColId(String colName) throws SQLException {
		for (int i = 0; i < columns.length; i++) {
			if (colName.equals(colName))
				return i;
		}
		throw new SQLException("Column +\"" + colName + "\" not found.");
	}

	private Object _getObject(String colName, Class clazz) throws SQLException {
		if (clnt == null)
			clnt = ClientUtils.getClientforThread();
		try {
			return tuple.getField(colName).get(clazz);
		} catch (MappingException e) {
			logger.error("Tuple mapping error", e);
			throw new SQLException("Tuple mapping error:" + e.getMessage());
		}
	}

	private Object _getObject(int i, Class clazz) throws SQLException {
		return _getObject(columns[i - 1], clazz);
	}

	private Client clnt = null;

	private void _setObj(String colName, Object obj) throws SQLException {
		if (clnt == null)
			clnt = ClientUtils.getClientforThread();
		TypeMapper tm = clnt.getTypeMapper();
		try {
			tuple.getFieldMap().put(colName, tm.backMap(obj));
		} catch (MappingException e) {
			logger.error("Tuple backmapping error", e);
			throw new SQLException("Tuple backmapping error: " + e.getMessage());
		}
	}


	private void _setObj(int i, Object obj) throws SQLException {
		_setObj(columns[i - 1], obj);
	}

}