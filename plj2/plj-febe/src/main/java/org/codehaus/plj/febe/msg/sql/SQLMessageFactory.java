/*
 * Created on Mar 25, 2004
 */

package org.codehaus.plj.febe.msg.sql;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.plj.CommunicationException;
import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.febe.msg.MessageFactory;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.SQL;
import org.codehaus.plj.messages.SQLCursorClose;
import org.codehaus.plj.messages.SQLCursorOpenWithSQL;
import org.codehaus.plj.messages.SQLExecute;
import org.codehaus.plj.messages.SQLFetch;
import org.codehaus.plj.messages.SQLPrepare;
import org.codehaus.plj.messages.SQLUnPrepare;
import org.codehaus.plj.messages.SimpleSQL;
import org.codehaus.plj.typemapping.MappingException;


/**
 * MessageFactory to send and receive SQL (JDBC) messages.
 * 
 * @author Laszlo Hornyak
 */
public class SQLMessageFactory implements MessageFactory {

	/** SQL message id for febe */
	public static final int MESSAGE_HEADER_SQL = 'S';
	private final Map map = new HashMap();

	private final static Logger logger = Logger.getLogger(SQLMessageFactory.class);

	/**
	 * 
	 */
	public SQLMessageFactory(Logger logger) {
		super();
		map.put(SimpleSQL.class.getName(), new SimpleSQLMessageFactory());
		map.put(SQLPrepare.class.getName(), new PrepareMessageFactory());
		map.put(SQLExecute.class.getName(), new PExecMessageFactory());
		map.put(SQLFetch.class.getName(), new FetchMessageFactory());
		map.put(SQLCursorClose.class.getName(), new SQLCursorCloseMessageFactory());
		map.put(SQLUnPrepare.class.getName(), new UnPrepareMessageFactory());
		map.put(SQLCursorOpenWithSQL.class.getName(), new CursorOpenWithSQLMessageFactory());
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
			logger.fatal("sender method not implemented for "+clname);
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
