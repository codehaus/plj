/*
 * Created on Mar 6, 2004
 */

package org.plj.chanells.febe.msg;

import java.io.IOException;

import org.pgj.messages.Message;
import org.pgj.messages.Result;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.TypeMapper;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;

/**
 * Result message factory.
 * 
 * @author Laszlo Hornyak
 */
public class ResultMessageFactory implements MessageFactory {

	org.apache.avalon.framework.logger.Logger logger = null;
	TypeMapper mapper = null;
	public static final int MESSAGE_HEADER_RESULT = 'R';

	public ResultMessageFactory(
			org.apache.avalon.framework.logger.Logger logger, TypeMapper mapper) {
		this.logger = logger;
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
		return null;
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
				if (fld == null) {
					stream.SendChar('N');
				} else {
					stream.SendChar('D');
					byte[] data = fld.get();
					stream.SendInteger(data.length, 4);
					stream.Send(data);
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