package org.pgj;

import org.pgj.messages.Message;

/**
 * A communication channel with the RDBMS.
 * 
 * @author Laszlo Hornyak
 * @since 0.1
 */
public interface Channel {
	
	//public model non_null Client[] new_clients;
	
	
	/**
	 * Wait for a new incoming connection with timeout.
	 * @param timeout		time to wait before canceling
	 * @return				New Client, of null if timed out.
	 * @since 0.1
	 */
	/*@ public normal_behavior
	  @ ensures \result == ()
	  @*/
	public Client getConnection(int timeout);

	/**
	 * Receive a message from the RDMBS.
	 * @param client		the client to get a message from.
	 * @return				Message from the client.
	 * @throws CommunicationException	if the communication failed (e.g. client is gone, cables are cut, etc...)
	 * @since 0.1
	 */
	public Message receiveFromRDBMS(Client client) throws CommunicationException;

	/**
	 * Send a message to the RDBMS.
	 * @param msg 			A message to the RDBMS. The client is stored in the param.
	 * @throws CommunicationException	if the communication failed (e.g. client is gone, cables are cut, etc...)
	 * @since 0.1
	 */
	public void sendToRDBMS(Message msg) throws CommunicationException;
}
