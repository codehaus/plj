/*
 * Created on Mar 26, 2004
 */
package org.plj.chanells.febe.msg.jdbc;

import java.io.IOException;

import org.pgj.messages.Message;
import org.pgj.messages.SQLCursorClose;
import org.pgj.typemapping.MappingException;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;


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
