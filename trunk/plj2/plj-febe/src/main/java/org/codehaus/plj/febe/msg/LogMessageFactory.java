/*
 * Created on Mar 6, 2004
 */

package org.codehaus.plj.febe.msg;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.messages.Log;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.typemapping.MappingException;

/**
 * Log message factory. Sends and receives Log messages to and from the RDBMS.
 * 
 * @author Laszlo Hornyak
 */
public class LogMessageFactory implements MessageFactory {

	private final static Logger logger = Logger.getLogger(LogMessageFactory.class);

	public static final int MESSAGE_HEADER_LOG = 'L';

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return MESSAGE_HEADER_LOG;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException {
		//This should never be called!
		return null;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException {
		logger.debug("LogMessageFactory: sending log");
		Log log = (Log) msg;
		stream.SendInteger(log.getLevel(), 4);
		byte[] cat = log.getCategory().getBytes();
		stream.SendInteger(cat.length, 4);
		stream.Send(cat);
		byte[] mesg = log.getMessage().getBytes();
		stream.SendInteger(mesg.length, 4);
		stream.Send(mesg);
		logger.debug("LogMessageFactory: log sent");
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return Log.class.getName();
	}
}
