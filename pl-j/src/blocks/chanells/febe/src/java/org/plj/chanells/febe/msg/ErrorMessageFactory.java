/*
 * Created on Jan 18, 2004
 */
package org.plj.chanells.febe.msg;

import java.io.IOException;

import org.pgj.messages.Message;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;

/**
 * Creates error message.
 * @author Laszlo Hornyak
 * @version 0.1
 */
public class ErrorMessageFactory implements MessageFactory {

	public static final int MESSAGE_HEADER_ERROR = 'E';

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return 'E';
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream)
	 */
	public Message getMessage(PGStream stream, Encoding encoding) throws IOException{
		org.pgj.messages.Error msg = new org.pgj.messages.Error();
		msg.setMessage(stream.ReceiveString(encoding));
		return null;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage()
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException {
		stream.Send(((org.pgj.messages.Error)msg).getExceptionClassName().getBytes());
		stream.Send(((org.pgj.messages.Error)msg).getMessage().getBytes());
		stream.flush();
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return org.pgj.messages.Error.class.getName();
	}

}
