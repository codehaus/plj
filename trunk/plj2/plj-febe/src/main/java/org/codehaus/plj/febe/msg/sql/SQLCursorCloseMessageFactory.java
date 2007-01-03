/*
 * Created on Mar 26, 2004
 */
package org.codehaus.plj.febe.msg.sql;

import java.io.IOException;

import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.SQLCursorClose;
import org.codehaus.plj.typemapping.MappingException;


/**
 * Send SQL Cursor close.
 * @author Laszlo Hornyak
 */
class SQLCursorCloseMessageFactory extends AbstractSQLMessageFactory {

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException {
		SQLCursorClose close = (SQLCursorClose)msg;
		byte[] b = close.getCursorName().getBytes();
		stream.SendInteger(b.length, 4);
		stream.Send(b);
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return SQLCursorClose.class.getName();
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.jdbc.AbstractSQLMessageFactory#getSQLType()
	 */
	public int getSQLType() {
		return AbstractSQLMessageFactory.SQLTYPE_CURSORCLOSE;
	}

}
