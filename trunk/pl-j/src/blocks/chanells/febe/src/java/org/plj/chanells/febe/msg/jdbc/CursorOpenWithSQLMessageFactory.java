/*
 * Created on Dec 26, 2004
 */
package org.plj.chanells.febe.msg.jdbc;

import java.io.IOException;
import java.sql.SQLOutput;

import org.pgj.CommunicationException;
import org.pgj.messages.Message;
import org.pgj.messages.SQLCursorOpenWithSQL;
import org.pgj.typemapping.MappingException;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;


/**
 * Sends an SQLCursorOpenWithSQL message.
 * @author Laszlo Hornyak
 */
public class CursorOpenWithSQLMessageFactory extends AbstractSQLMessageFactory {

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.jdbc.AbstractSQLMessageFactory#getSQLType()
	 */
	public int getSQLType() {
		return SQLTYPE_CURSOR_OPEN;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException, CommunicationException {
		throw new CommunicationException("Cursor open can't be received.");
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException, CommunicationException {
		SQLCursorOpenWithSQL s = (SQLCursorOpenWithSQL)msg;
		byte[] b = s.getCursorName().getBytes();
		stream.SendInteger(b.length, 4);
		stream.Send(b);
		b = s.getQuery().getBytes();
		stream.SendInteger(b.length, 4);
		stream.Send(b);
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return SQLCursorOpenWithSQL.class.getName();
	}

}
