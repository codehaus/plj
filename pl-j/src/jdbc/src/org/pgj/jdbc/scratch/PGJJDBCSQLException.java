/*
 * Created on Mar 27, 2004
 */
package org.pgj.jdbc.scratch;

import java.sql.SQLException;


/**
 * SQL exception.
 * @author Laszlo Hornyak
 */
public class PGJJDBCSQLException extends SQLException {

	/**
	 * 
	 */
	public PGJJDBCSQLException() {
		super();
	}

	/**
	 * @param reason
	 */
	public PGJJDBCSQLException(String reason) {
		super(reason);
	}

	/**
	 * @param reason
	 * @param SQLState
	 */
	public PGJJDBCSQLException(String reason, String SQLState) {
		super(reason, SQLState);
	}

	/**
	 * @param reason
	 * @param SQLState
	 * @param vendorCode
	 */
	public PGJJDBCSQLException(String reason, String SQLState, int vendorCode) {
		super(reason, SQLState, vendorCode);
	}

}
