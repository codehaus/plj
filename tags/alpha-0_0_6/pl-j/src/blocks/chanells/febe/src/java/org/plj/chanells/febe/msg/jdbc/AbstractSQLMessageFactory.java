/*
 * Created on Jul 14, 2004
 */

package org.plj.chanells.febe.msg.jdbc;

import org.plj.chanells.febe.msg.MessageFactory;


/**
 * Base of the message factoryes sending SQL messages.
 * @author Laszlo Hornyak
 */
abstract class AbstractSQLMessageFactory implements MessageFactory {

	/*
	 * See plpgj_message_sql_h to see it's pair. 
	 */
	public static final int SQLTYPE_STATEMENT = 1;
	public static final int SQLTYPE_CURSORCLOSE = 2;
	public static final int SQLTYPE_FETCH = 3;
	public static final int SQLTYPE_CURSOR_OPEN = 4;
	public static final int SQLTYPE_PREPARE = 5;
	public static final int SQLTYPE_PEXECUTE = 6;
	public static final int SQLTYPE_UNPREPARE = 7;

	public abstract int getSQLType();
	
	
	public int getMessageHeader() {
		return 0;
	}
}