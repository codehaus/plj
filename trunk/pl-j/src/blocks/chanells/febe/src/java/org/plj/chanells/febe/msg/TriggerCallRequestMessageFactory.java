/*
 * Created on Apr 4, 2004
 */
package org.plj.chanells.febe.msg;

import java.io.IOException;

import org.apache.avalon.framework.logger.Logger;
import org.pgj.messages.Message;
import org.pgj.messages.TriggerCallRequest;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.Tuple;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;


/**
 * Factory for Trigger calls. 
 * @author Laszlo Hornyak
 */
public class TriggerCallRequestMessageFactory implements MessageFactory {

	public static final int MESSAGE_HEADER_TRIGGER = 'T';

	private Logger logger = null;
	
	/**
	 * 
	 */
	public TriggerCallRequestMessageFactory(Logger logger) {
		super();
		this.logger = logger;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return MESSAGE_HEADER_TRIGGER;
	}

	private Tuple receiveTuple(PGStream stream, Encoding encoding){
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException {
		TriggerCallRequest call = new TriggerCallRequest();
		call.setRowmode(stream.ReceiveInteger(4));
		call.setReason(stream.ReceiveInteger(4));
		switch(call.getReason()){
			case TriggerCallRequest.TRIGGER_REASON_DELETE:
			break;
			case TriggerCallRequest.TRIGGER_REASON_INSERT:
			break;
			case TriggerCallRequest.TRIGGER_REASON_UPDATE:
			break;
		}
		call.setTableName(stream.ReceiveString(encoding));
		call.setClassname(stream.ReceiveString(encoding));
		call.setMethodname(stream.ReceiveString(encoding));
		return call;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException {
			
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return TriggerCallRequest.class.getName();
	}

}
