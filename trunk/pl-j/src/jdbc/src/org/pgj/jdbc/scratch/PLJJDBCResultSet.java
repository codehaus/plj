/*
 * Created on Jun 12, 2003
 */
package org.pgj.jdbc.scratch;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.pgj.Channel;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;

/**
 * @author Laszlo Hornyak
 */
public class PLJJDBCResultSet implements ResultSet {

	/** We can communicate with the RDBMS across this chanell. */
	private Channel chanell = null;
	/** fetch size */
	private int fetchsize = 50;

	/** the names of the columns */
	private String[] column_names;

	private ArrayList rows = null;

	/** the index of the first row that the rows array contains from the total result set. */
	private int rows_from = 0;
	/** the current rows`s index relative to the first stored in rows array */
	private int current_row_index_in = 0;
	/** The current row */
	private Field[] fields = null;

	/** the name of the cursor we can fetch from. */
	private String cursorName = null;

	/**
	 * 
	 */
	public PLJJDBCResultSet(Channel chanell, String cursorName) {
		super();
		this.chanell = chanell;
		this.cursorName = cursorName;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#next()
	 */
	public boolean next() throws SQLException {

		checkIfClosed();
		
		
		if(current_row_index_in >= rows.size()){
			
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#close()
	 */
	public void close() throws SQLException {

		checkIfClosed();
		
		//let the garbage collector work
		rows = null;
		this.fields = null;
		this.cursorName = null;
		//if chanell is null, it is closed.
		this.chanell = null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#wasNull()
	 */
	public boolean wasNull() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return (String)fld.get(String.class);
		} catch (MappingException e) {
			doTypeException(String.class, e);
			//this will never run.
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return ((Boolean)fld.get(Boolean.class)).booleanValue();
		} catch (MappingException e) {
			doTypeException(String.class, e);
			//this will never run.
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getByte(int)
	 */
	public byte getByte(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return ((Byte)fld.get(Byte.class)).byteValue();
		} catch (MappingException e) {
			doTypeException(String.class, e);
			//this will never run.
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getShort(int)
	 */
	public short getShort(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return ((Short)fld.get(Short.class)).shortValue();
		} catch (MappingException e) {
			doTypeException(String.class, e);
			//this will never run.
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getInt(int)
	 */
	public int getInt(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return ((Integer)fld.get(Integer.class)).intValue();
		} catch (MappingException e) {
			doTypeException(String.class, e);
			//this will never run.
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return ((Long)fld.get(Long.class)).longValue();
		} catch (MappingException e) {
			doTypeException(String.class, e);
			//this will never run.
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	public float getFloat(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return ((Float)fld.get(Float.class)).floatValue();
		} catch (MappingException e) {
			doTypeException(String.class, e);
			//this will never run.
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	public double getDouble(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return ((Double)fld.get(Double.class)).doubleValue();
		} catch (MappingException e) {
			doTypeException(Double.class, e);
			//this will never run.
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale)
		throws SQLException {
			checkIfClosed();
			try {
				Field fld = fields[columnIndex - 1];
				return (BigDecimal)fld.get(BigDecimal.class);
			} catch (MappingException e) {
				doTypeException(String.class, e);
				//this will never run.
				return null;
			}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getDate(int)
	 */
	public Date getDate(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return (Date)fld.get(Date.class);
		} catch (MappingException e) {
			doTypeException(Date.class, e);
			//this will never run.
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getTime(int)
	 */
	public Time getTime(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return (Time)fld.get(Time.class);
		} catch (MappingException e) {
			doTypeException(Time.class, e);
			//this will never run.
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return (Timestamp)fld.get(Timestamp.class);
		} catch (MappingException e) {
			doTypeException(Timestamp.class, e);
			//this will never run.
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		try {
			Field fld = fields[columnIndex - 1];
			return (InputStream)fld.get(InputStream.class);
		} catch (MappingException e) {
			doTypeException(InputStream.class, e);
			//this will never run.
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getString(java.lang.String)
	 */
	public String getString(String columnName) throws SQLException {
		return getString(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		return getBoolean(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getByte(java.lang.String)
	 */
	public byte getByte(String columnName) throws SQLException {
		return getByte(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getShort(java.lang.String)
	 */
	public short getShort(String columnName) throws SQLException {
		return getShort(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getInt(java.lang.String)
	 */
	public int getInt(String columnName) throws SQLException {
		return getInt(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getLong(java.lang.String)
	 */
	public long getLong(String columnName) throws SQLException {
		return getLong(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getFloat(java.lang.String)
	 */
	public float getFloat(String columnName) throws SQLException {
		return getFloat(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getDouble(java.lang.String)
	 */
	public double getDouble(String columnName) throws SQLException {
		return getDouble(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
	 */
	public BigDecimal getBigDecimal(String columnName, int scale)
		throws SQLException {
		return getBigDecimal(findColumn(columnName),scale);
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		return getBytes(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getDate(java.lang.String)
	 */
	public Date getDate(String columnName) throws SQLException {
		return getDate(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getTime(java.lang.String)
	 */
	public Time getTime(String columnName) throws SQLException {
		return getTime(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return getTimestamp(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
	 */
	public InputStream getAsciiStream(String columnName) throws SQLException {
		return getAsciiStream(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
	 */
	public InputStream getUnicodeStream(String columnName)
		throws SQLException {
		return getUnicodeStream(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
	 */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return getBinaryStream(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		checkIfClosed();
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		checkIfClosed();
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getCursorName()
	 */
	public String getCursorName() throws SQLException {
		checkIfClosed();
		return cursorName;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		checkIfClosed();
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(int)
	 */
	public Object getObject(int columnIndex) throws SQLException {
		checkIfClosed();
		try {
			Field fld = fields[columnIndex - 1];
			return fld.defaultGet();
		} catch (MappingException e) {
			doTypeException(String.class, e);
			//this will never run.
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String)
	 */
	public Object getObject(String columnName) throws SQLException {
		return getObject(findColumn(columnName));
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#isFirst()
	 */
	public boolean isFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#isLast()
	 */
	public boolean isLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	public void beforeFirst() throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#afterLast()
	 */
	public void afterLast() throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#first()
	 */
	public boolean first() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#last()
	 */
	public boolean last() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getRow()
	 */
	public int getRow() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(int row) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(int rows) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#previous()
	 */
	public boolean previous() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		fetchsize = rows;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return fetchsize;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	public void updateByte(int columnIndex, byte x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	public void updateShort(int columnIndex, short x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	public void updateInt(int columnIndex, int x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	public void updateLong(int columnIndex, long x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	public void updateFloat(int columnIndex, float x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	public void updateDouble(int columnIndex, double x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
	 */
	public void updateBigDecimal(int columnIndex, BigDecimal x)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(int columnIndex, String x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
	 */
	public void updateDate(int columnIndex, Date x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
	 */
	public void updateTime(int columnIndex, Time x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
	 */
	public void updateTimestamp(int columnIndex, Timestamp x)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
	 */
	public void updateCharacterStream(int columnIndex, Reader x, int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
	 */
	public void updateObject(int columnIndex, Object x, int scale)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
	 */
	public void updateObject(int columnIndex, Object x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateNull(java.lang.String)
	 */
	public void updateNull(String columnName) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
	 */
	public void updateBoolean(String columnName, boolean x)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
	 */
	public void updateByte(String columnName, byte x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
	 */
	public void updateShort(String columnName, short x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
	 */
	public void updateInt(String columnName, int x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
	 */
	public void updateLong(String columnName, long x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
	 */
	public void updateFloat(String columnName, float x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
	 */
	public void updateDouble(String columnName, double x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void updateBigDecimal(String columnName, BigDecimal x)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
	 */
	public void updateString(String columnName, String x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
	 */
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
	 */
	public void updateDate(String columnName, Date x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
	 */
	public void updateTime(String columnName, Time x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void updateTimestamp(String columnName, Timestamp x)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateAsciiStream(String columnName, InputStream x, int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateBinaryStream(
		String columnName,
		InputStream x,
		int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void updateCharacterStream(
		String columnName,
		Reader reader,
		int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
	 */
	public void updateObject(String columnName, Object x, int scale)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
	 */
	public void updateObject(String columnName, Object x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateRow()
	 */
	public void updateRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getStatement()
	 */
	public Statement getStatement() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	public Object getObject(int i, Map map) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getArray(int)
	 */
	public Array getArray(int i) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String colName, Map map) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getRef(java.lang.String)
	 */
	public Ref getRef(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBlob(java.lang.String)
	 */
	public Blob getBlob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getClob(java.lang.String)
	 */
	public Clob getClob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getArray(java.lang.String)
	 */
	public Array getArray(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
		throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal)
		throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getURL(java.lang.String)
	 */
	public URL getURL(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
	 */
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
	 */
	public void updateRef(String columnName, Ref x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
	 */
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
	 */
	public void updateBlob(String columnName, Blob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
	 */
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
	 */
	public void updateClob(String columnName, Clob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
	 */
	public void updateArray(int columnIndex, Array x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
	 */
	public void updateArray(String columnName, Array x) throws SQLException {
		// TODO Auto-generated method stub

	}

	// =================
	// utility functions
	// =================

	/** 
	 * Throws the type conversion exception.
	 * @throws SQLException		always, thats why call it.
	 */
	private void doTypeException(Class clazz, MappingException me) throws SQLException{
		throw new SQLException("Type is not a "+clazz.getName()+". root reason:"+me.getMessage());
	}

	/**
	 * Checks if the resultset is closed, throws an SQLException if so.
	 * @throws SQLException if the ResultSet is already closed.
	 */
	private void checkIfClosed() throws SQLException{
		if(chanell == null)
			throw new SQLException("This Resultset is already closed.");
	}

}