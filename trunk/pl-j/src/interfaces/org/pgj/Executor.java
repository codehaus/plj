package org.pgj;

import org.pgj.messages.CallRequest;
import org.pgj.messages.Message;

/**
 * An interface for call executors.
 * Executor objects performa the calls received from the client.
 * 
 * @author Laszlo Hornyak
 * @since 0.1
 * @version planing
 */

public interface Executor{
	
	/**
	 * Perform a call.
	 * Supposed behavior:<br>
	 * The class SHOULD not throw any exception even if there is an error on execution. 
	 * It should return the result of the operation in a Message object. Exceptions  
	 * thrown by this method should be handled as configuration or system errors. 
	 * <br>
	 * @param call the call request to peform.
	 * @return the ansver Message to the call.
	 * The call must be instance of Result or Error, other messages MUST be sent directly by
	 * Channel implementation.
	 */
	public Message execute(CallRequest call);

	/**
	 * Initialize environment for new client. Called when a new client
	 * connects.
	 * @param sessionClient the client of the session.
	 */
	public void initClientSession(Client sessionClient);

	/**
	 * Free allocated resources in the environment.
	 * @param sessionClient the client of the session.
	 */
	public void destroyClientSession(Client sessionClient);
}
