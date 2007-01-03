/*
 * Created on Nov 8, 2004
 */
package org.codehaus.plj.febe.msg.sql;

import java.io.IOException;

import org.codehaus.plj.CommunicationException;
import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.SQLUnPrepare;
import org.codehaus.plj.typemapping.MappingException;


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
