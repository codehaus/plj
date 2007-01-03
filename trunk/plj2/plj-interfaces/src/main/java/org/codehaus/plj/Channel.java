package org.codehaus.plj;

import org.codehaus.plj.messages.Message;
import org.codehaus.plj.typemapping.MappingException;

/**
 * A communication channel with the RDBMS.
 * The channel interface is used to send message objects to the RDBMS 
 * and receive results. Typically, the GLUE will handle it and some
 * utility packages in the user-space, such as JDBC drivers, loging
 * tools.<br>
 * Use of this interface is highly privileged, implementations should 
 * be protected.
 * 
 * @author Laszlo Hornyak
 * @since 0.1
 */
public interface Channel {

	/**
	 * Wait for a new incoming connection with timeout.
	 * @param timeout		time to wait before canceling
	 * @return				New Client, of null if timed out.
	 * @since 0.1
	 */
	public Client getConnection(int timeout);

	/**
	 * Receive a message from the RDMBS.
	 * @param client		the client to get a message from.
	 * @return				Message from the client.
	 * @throws org.pgj.CommunicationException	if the communication failed (e.g. client is gone, cables are cut, etc...)
	 * @since 0.1
	 */
	public Message receiveFromRDBMS(Client client) throws CommunicationException, MappingException;

	/**
	 * Send a message to the RDBMS.
	 * @param msg 			A message to the RDBMS. The client is stored in the param.
	 * @throws org.pgj.CommunicationException	if the communication failed (e.g. client is gone, cables are cut, etc...)
	 * @since 0.1
	 */
	public void sendToRDBMS(Message msg) throws CommunicationException, MappingException;
}
