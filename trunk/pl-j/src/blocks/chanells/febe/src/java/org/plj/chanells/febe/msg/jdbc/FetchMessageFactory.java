/*
 * Created on Sep 27, 2004
 */
package org.plj.chanells.febe.msg.jdbc;

import java.io.IOException;

import org.pgj.CommunicationException;
import org.pgj.messages.Message;
import org.pgj.messages.SQLFetch;
import org.pgj.typemapping.MappingException;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;


/**
 * Sends fetch message to RDBMS.
 * @author Laszlo Hornyak
 */
class FetchMessageFactory extends AbstractSQLMessageFactory {

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.jdbc.AbstractSQLMessageFactory#getSQLType()
	 */
	public int getSQLType() {
		return SQLTYPE_FETCH;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException, CommunicationException {
		throw new CommunicationException("DB will not send a fetch msg.");
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException, CommunicationException {
		
		byte[] b = ((SQLFetch)msg).getCursorName().getBytes();
		stream.SendInteger(b.length,4);
		stream.Send(b);
		stream.SendInteger(((SQLFetch)msg).getCount(), 4);
		stream.SendInteger(((SQLFetch)msg).getDirection(), 4);
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return SQLFetch.class.getName();
	}

}
