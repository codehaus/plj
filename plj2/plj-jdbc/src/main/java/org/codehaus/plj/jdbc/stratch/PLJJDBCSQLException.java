/*
 * Created on Mar 27, 2004
 */
package org.codehaus.plj.jdbc.stratch;

import java.sql.SQLException;


/**
 * SQL exception.
 * @author Laszlo Hornyak
 */
public class PLJJDBCSQLException extends SQLException {

	/**
	 * 
	 */
	public PLJJDBCSQLException() {
		super();
	}

	/**
	 * @param reason
	 */
	public PLJJDBCSQLException(String reason) {
		super(reason);
	}

	/**
	 * @param reason
	 * @param SQLState
	 */
	public PLJJDBCSQLException(String reason, String SQLState) {
		super(reason, SQLState);
	}

	/**
	 * @param reason
	 * @param SQLState
	 * @param vendorCode
	 */
	public PLJJDBCSQLException(String reason, String SQLState, int vendorCode) {
		super(reason, SQLState, vendorCode);
	}

}
