/*
 * Created on Mar 26, 2004
 */

package org.codehaus.plj.febe.msg.sql;

import java.io.IOException;

import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.SimpleSQL;
import org.codehaus.plj.typemapping.MappingException;


/**
 * Sends SQL messages to the RDBMS.
 * @author Laszlo Hornyak
 */
class SimpleSQLMessageFactory extends AbstractSQLMessageFactory {

	public static final int MESSAGE_SQLHEADER_SIMPLE = 'S';

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return MESSAGE_SQLHEADER_SIMPLE;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException {
		//this hould never happen.
		return null;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException {
		SimpleSQL sql = (SimpleSQL) msg;
		String strsql = sql.getSql();
		byte[] bsql = null;
		if (strsql == null) {
			bsql = new byte[0];
		} else {
			bsql = strsql.getBytes();
		}
		stream.SendInteger(bsql.length, 4);
		stream.Send(bsql);
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.jdbc.AbstractSQLMessageFactory#getSQLType()
	 */
	public int getSQLType() {
		return SQLTYPE_STATEMENT;
	}

}
