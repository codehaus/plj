/*
 * Created on May 10, 2004
 */
package org.plj.chanells.febe.msg;

import java.io.IOException;

import org.pgj.CommunicationException;
import org.pgj.messages.Message;
import org.pgj.typemapping.MappingException;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;


/**
 * @author Laszlo Hornyak
 */
//TODO: edit comments for TupleResultMessageFactory
public class TupleResultMessageFactory implements MessageFactory {

	public static final int MESSAGE_HEADER_TUPLERESULT = 'U';

	public TupleResultMessageFactory(){
		
	}
	
	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return MESSAGE_HEADER_TUPLERESULT;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding) throws IOException, MappingException, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException, MappingException, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		// TODO Auto-generated method stub
		return null;
	}

}
