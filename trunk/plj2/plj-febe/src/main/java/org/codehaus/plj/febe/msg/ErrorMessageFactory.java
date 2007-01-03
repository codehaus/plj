/*
 * Created on Jan 18, 2004
 */

package org.codehaus.plj.febe.msg;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.messages.Message;

/**
 * Creates error message.
 * 
 * @author Laszlo Hornyak
 * @version 0.1
 */
public class ErrorMessageFactory implements MessageFactory {

	private Logger logger = null;

	public ErrorMessageFactory(Logger logger) {
		this.logger = logger;
	}

	public static final int MESSAGE_HEADER_ERROR = 'E';

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return 'E';
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException {
		org.codehaus.plj.messages.Error msg = new org.codehaus.plj.messages.Error();
		msg.setExceptionClassName(stream.ReceiveString(encoding));
		msg.setMessage(stream.ReceiveString(encoding));
		msg.setStackTrace(stream.ReceiveString(encoding));
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage()
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException {
		byte[] cname = ((org.codehaus.plj.messages.Error) msg).getExceptionClassName()
				.getBytes();
		stream.SendInteger(cname.length, 4);
		stream.Send(cname);
		byte[] cmsg = null;
		String mesg = ((org.codehaus.plj.messages.Error) msg).getMessage();
		if (mesg == null) {
			cmsg = new byte[0];
		} else {
			cmsg = mesg.getBytes();
		}
		stream.SendInteger(cmsg.length, 4);
		stream.Send(cmsg);
		stream.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return org.codehaus.plj.messages.Error.class.getName();
	}
}