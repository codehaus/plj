/*
 * Created on Jan 18, 2004
 */
package org.plj.chanells.febe.msg;

import java.io.IOException;

import org.pgj.messages.Message;
import org.pgj.typemapping.MappingException;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;

/**
 * Interface for sending specific message types with FEBE.
 * @author Laszlo Hornyak
 * @version 0.1
 */
public interface MessageFactory {

	/**
	 * Returns the message header.
	 * @return the header of the message the implementation is able to use.
	 */
	int getMessageHeader();

	/**
	 * get
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	Message getMessage(PGStream stream, Encoding encoding) throws IOException, MappingException;

	/**
	 * 
	 * @throws IOException
	 */
	void sendMessage(Message msg, PGStream stream) throws IOException, MappingException;

	/**
	 * Get the name of the handled class.
	 * @return the name of the class this type can create.
	 */
	String getHandledClassname();
}
