/*
 * Created on Jan 18, 2004
 */

package org.codehaus.plj.febe.msg;

import java.io.IOException;

import org.codehaus.plj.CommunicationException;
import org.codehaus.plj.febe.Encoding;
import org.codehaus.plj.febe.PGStream;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.typemapping.MappingException;

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
	Message getMessage(PGStream stream, Encoding encoding) throws IOException,
			MappingException, CommunicationException;

	/**
	 * 
	 * @throws IOException
	 */
	void sendMessage(Message msg, PGStream stream) throws IOException,
			MappingException, CommunicationException;

	/**
	 * Get the name of the handled class.
	 * @return the name of the class this type can create.
	 */
	String getHandledClassname();
}