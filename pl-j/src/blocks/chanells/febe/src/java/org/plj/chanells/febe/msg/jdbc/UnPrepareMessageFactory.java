/*
 * Created on Nov 8, 2004
 */
package org.plj.chanells.febe.msg.jdbc;

import java.io.IOException;

import org.pgj.CommunicationException;
import org.pgj.messages.Message;
import org.pgj.messages.SQLUnPrepare;
import org.pgj.typemapping.MappingException;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;


/**
 * Sends plan close message.
 * @author Laszlo Hornyak
 */
class UnPrepareMessageFactory extends AbstractSQLMessageFactory {

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.jdbc.AbstractSQLMessageFactory#getSQLType()
	 */
	public int getSQLType() {
		return AbstractSQLMessageFactory.SQLTYPE_UNPREPARE;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding) throws IOException, MappingException, CommunicationException {
		throw new CommunicationException("should be never received by the PL-J server.");
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException, MappingException, CommunicationException {
		stream.SendInteger(((SQLUnPrepare)msg).getPlanid(), 4);
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		// TODO Auto-generated method stub
		return null;
	}

}
