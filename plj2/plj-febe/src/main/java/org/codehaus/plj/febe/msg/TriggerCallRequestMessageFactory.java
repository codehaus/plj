/*
 * Created on Apr 4, 2004
 */

package org.codehaus.plj.febe.msg;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.plj.CommunicationException;
import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.TriggerCallRequest;
import org.codehaus.plj.typemapping.Field;
import org.codehaus.plj.typemapping.MappingException;
import org.codehaus.plj.typemapping.Tuple;
import org.codehaus.plj.typemapping.TypeMapper;


/**
 * Factory for Trigger calls. 
 * @author Laszlo Hornyak
 */
public class TriggerCallRequestMessageFactory implements MessageFactory {

	public static final int MESSAGE_HEADER_TRIGGER = 'T';

	private final static Logger logger = Logger.getLogger(TriggerCallRequestMessageFactory.class);

	private TypeMapper typeMapper;

	/**
	 * 
	 */
	public TriggerCallRequestMessageFactory(TypeMapper typeMapper) {
		super();
		this.typeMapper = typeMapper;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return MESSAGE_HEADER_TRIGGER;
	}

	private Tuple receiveTuple(PGStream stream, Encoding encoding,
			String[] paramnames, String[] paramtypes, String relName)
			throws IOException, MappingException {
		logger.debug("receive tuple");
		Tuple tuple = new Tuple();

		for (int i = 0; i < paramnames.length; i++) {
			boolean isnull = stream.ReceiveIntegerR(4) == 1;
			if (isnull) {
				logger.debug("null param");
			} else {
				int sz = stream.ReceiveIntegerR(4);
				logger.debug("waiting for " + sz + " bytes");
				byte[] bytes = stream.Receive(sz);
				logger.debug("got the bytes");
				String type = stream.ReceiveString(encoding);
				if (!type.equals(paramtypes[i]))
					throw new MappingException(
							"Ooops, there is something problem here!!");
				Field fld = typeMapper.map(bytes, type);
				tuple.addField(paramnames[i], fld);
			}
		}
		tuple.setRelationName(relName);
		return tuple;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException, CommunicationException {
		TriggerCallRequest call = new TriggerCallRequest();
		call.setRowmode(stream.ReceiveIntegerR(4));
		logger.debug("rowmode:" + call.getRowmode());
		call.setReason(stream.ReceiveIntegerR(4));
		logger.debug("reason code:" + call.getReason());
		call.setType(stream.ReceiveIntegerR(4));
		call.setRowmode(stream.ReceiveIntegerR(4));

		call.setTableName(stream.ReceiveString(encoding));
		logger.debug("Table name: " + call.getTableName());
		call.setClassname(stream.ReceiveString(encoding));
		logger.debug("class name" + call.getClassname());
		call.setMethodname(stream.ReceiveString(encoding));
		logger.debug("method name" + call.getMethodname());

		String[] paramnames = new String[stream.ReceiveIntegerR(4)];
		String[] paramtypes = new String[paramnames.length];
		logger.debug(">" + paramnames.length);
		for (int i = 0; i < paramnames.length; i++) {
			paramnames[i] = stream.ReceiveString(encoding);
			paramtypes[i] = stream.ReceiveString(encoding);
			logger.debug("names[" + i + "]:" + paramnames[i]);
			logger.debug("types[" + i + "]:" + paramtypes[i]);
		}

		//no tuples for before triggers
		if (call.getType() == TriggerCallRequest.TRIGGER_FIRED_AFTER)
			switch (call.getReason()) {
				case TriggerCallRequest.TRIGGER_REASON_DELETE :
					Tuple _old = receiveTuple(stream, encoding, paramnames,
							paramtypes, call.getTableName());
					break;
				case TriggerCallRequest.TRIGGER_REASON_INSERT :
					call.setNew(receiveTuple(stream, encoding, paramnames,
							paramtypes, call.getTableName()));
					break;
				case TriggerCallRequest.TRIGGER_REASON_UPDATE :
					call.setOld(receiveTuple(stream, encoding, paramnames,
							paramtypes, call.getTableName()));
					call.setNew(receiveTuple(stream, encoding, paramnames,
							paramtypes, call.getTableName()));
					break;
				default :
					throw new CommunicationException(
							"Unknown trigger reason code.");
			}

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