/*
 * Created on Mar 25, 2004
 */

package org.plj.chanells.febe.msg.jdbc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.pgj.CommunicationException;
import org.pgj.messages.Message;
import org.pgj.messages.SQL;
import org.pgj.messages.SimpleSQL;
import org.pgj.typemapping.MappingException;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;
import org.plj.chanells.febe.msg.MessageFactory;


/**
 * MessageFactory to send and receive SQL (JDBC) messages.
 * 
 * @author Laszlo Hornyak
 */
public class SQLMessageFactory implements MessageFactory {

	/** SQL message id for febe */
	public static final int MESSAGE_HEADER_SQL = 'S';
	private final Map map = new HashMap();

	private Logger logger = null;

	/**
	 * 
	 */
	public SQLMessageFactory(Logger logger) {
		super();
		this.logger = logger;
		map.put(SimpleSQL.class.getName(), new SimpleSQLMessageFactory());
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return MESSAGE_HEADER_SQL;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException {
		//TODO: this should never happen, the backend should not send SQL messages.
		return null;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException, CommunicationException{
		SQL sql = (SQL) msg;
		String clname = msg.getClass().getName();
		AbstractSQLMessageFactory msgf = (AbstractSQLMessageFactory) map.get(clname);
		if(msgf == null){
			throw new CommunicationException("sender method not implemented for "+clname);
		}
		stream.SendInteger(msgf.getSQLType(), 4);
		msgf.sendMessage(msg, stream);
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return null;
	}

}
