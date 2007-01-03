/*
 * Created on Mar 6, 2004
 */

package org.codehaus.plj.febe.msg;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.Result;
import org.codehaus.plj.typemapping.Field;
import org.codehaus.plj.typemapping.MappingException;
import org.codehaus.plj.typemapping.TypeMapper;

/**
 * Result message factory.
 * 
 * @author Laszlo Hornyak
 */
public class ResultMessageFactory implements MessageFactory {

	private final static Logger logger = Logger.getLogger(ResultMessageFactory.class);
	private TypeMapper mapper;
	public static final int MESSAGE_HEADER_RESULT = 'R';

	public ResultMessageFactory(TypeMapper mapper) {
		this.mapper = mapper;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessageHeader()
	 */
	public int getMessageHeader() {
		return MESSAGE_HEADER_RESULT;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getMessage(org.plj.chanells.febe.core.PGStream, org.plj.chanells.febe.core.Encoding)
	 */
	public Message getMessage(PGStream stream, Encoding encoding)
			throws IOException, MappingException {
		Result res = new Result();
		int rows = stream.ReceiveIntegerR(4);
		int cols = stream.ReceiveIntegerR(4);
		res.setSize(rows, cols);
		for (int i = 0; i < rows; i++) {

			for (int j = 0; j < cols; j++) {
				char isnull = (char) stream.ReceiveChar();
				if (isnull == 'N') {
					res.set(i, j, null);
				} else {
					int len = stream.ReceiveIntegerR(4);
					byte[] data = new byte[len];
					stream.Receive(data, 0, len);
					String type = stream.ReceiveString(encoding);
					Field field = mapper.map(data, type);
					res.set(i, j, field);
				}
			}
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#sendMessage(org.pgj.messages.Message, org.plj.chanells.febe.core.PGStream)
	 */
	public void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException {
		Result res = (Result) msg;
		int rows = res.getRows();
		stream.SendInteger(rows, 4);
		int cols = res.getColumns();
		stream.SendInteger(cols, 4);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Field fld = res.get(i, j);
				if (fld == null || fld.isNull()) {
					stream.SendChar('N');
				} else {
					stream.SendChar('D');
					byte[] data = fld.get();
					stream.SendInteger(data.length, 4);
					stream.Send(data);
					byte[] typeData = fld.rdbmsType().getBytes();
					stream.SendInteger(typeData.length, 4);
					stream.Send(typeData);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.plj.chanells.febe.msg.MessageFactory#getHandledClassname()
	 */
	public String getHandledClassname() {
		return Result.class.getName();
	}
}