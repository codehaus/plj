/*
 * Created on Jan 27, 2004
 */

package org.plj.chanells.febe.msg;

import java.io.IOException;

import org.apache.avalon.framework.logger.Logger;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Message;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.TypeMapper;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;

/**
 * Creates call messages.
 * 
 * @author Laszlo Hornyak
 * @version 0.1
 */
public class CallMessageFactory implements MessageFactory {

	private TypeMapper typeMapper = null;

	private Logger logger = null;

	public CallMessageFactory(TypeMapper typeMapper, Logger logger) {
		this.typeMapper = typeMapper;
		this.logger = logger;
	}

	public static final int MESSAGE_HEADER_CALL = 'C';

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return MESSAGE_HEADER_CALL;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException {

		CallRequest request = new CallRequest();
		request.setClassname(stream.ReceiveString(encoding));
		request.setMethodname(stream.ReceiveString(encoding));
		request.setExpect(stream.ReceiveString(encoding));
		logger.debug("classname:" + request.getClassname());
		logger.debug("method:" + request.getMethodname());
		logger.debug("expects:" + request.getExpect());
		int paramcount = stream.ReceiveIntegerR(4);
		logger.debug("count of params: " + paramcount);

		for (int i = 0; i < paramcount; i++) {
			String paramType = stream.ReceiveString(encoding);
			byte[] paramData = null;
			if(stream.ReceiveChar() == 'D'){
				int paramSize = stream.ReceiveIntegerR(4);
				paramData = stream.Receive(paramSize);
				logger.debug("not null field param");
			} else {
				logger.debug("null param");
			}
			Field fld = typeMapper.map(paramData, paramType);
			request.addParam(fld);
		}

		return request;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException {
		//TODO: this should throw something error!
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return CallRequest.class.getName();
	}

}
