/*
 * Created on May 10, 2004
 */

package org.codehaus.plj.febe.msg;

import java.io.IOException;
import java.util.Iterator;

import org.codehaus.plj.CommunicationException;
import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.TupleResult;
import org.codehaus.plj.typemapping.Field;
import org.codehaus.plj.typemapping.MappingException;
import org.codehaus.plj.typemapping.Tuple;


/**
 * Sends back the TupleResult to the RDBMS.
 * @author Laszlo Hornyak
 */
public class TupleResultMessageFactory implements MessageFactory {

	public static final int MESSAGE_HEADER_TUPLERESULT = 'U';

	public TupleResultMessageFactory() {

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
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException, CommunicationException {
		throw new CommunicationException("DB won`t send tupleresults!");
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException, CommunicationException {
		TupleResult res = (TupleResult) msg;
		Tuple t = res.getTuple();
		byte[] b = t.getRelationName().getBytes();
		stream.SendInteger(b.length, 4);
		stream.Send(b);

		int cnt = t.getFieldMap().keySet().size();
		stream.SendInteger(cnt, 4);
		Iterator i = t.getFieldMap().keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			Field fld = t.getField(key);
			byte[] keyb = key.getBytes();
			stream.SendInteger(keyb.length, 4);
			stream.Send(keyb);
			if (fld == null && fld.isNull()) {
				stream.SendChar('n');
			} else {
				stream.SendChar('v');
				byte[] typeb = fld.rdbmsType().getBytes();
				stream.SendInteger(typeb.length, 4);
				stream.Send(typeb);
				byte[] datab = fld.get();
				stream.SendInteger(datab.length, 4);
				stream.Send(datab);
			}
		}
		t.getFieldMap();
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		// TODO Auto-generated method stub
		return null;
	}

}