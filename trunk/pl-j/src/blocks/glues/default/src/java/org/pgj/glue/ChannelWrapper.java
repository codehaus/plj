/*
 * Created on Aug 19, 2004
 */
package org.pgj.glue;

import org.apache.avalon.framework.logger.Logger;
import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.CommunicationException;
import org.pgj.Executor;
import org.pgj.TriggerExecutor;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Message;
import org.pgj.messages.TriggerCallRequest;

/**
 * Wraps Channel to add some additional logic.
 * @author Laszlo Hornyak
 */
public class ChannelWrapper implements Channel {

	private Logger logger = null;
	private Channel realChannel = null;
	private Executor executor = null;
	
	/**
	 * 
	 */
	public ChannelWrapper(Logger logger, Channel realChannel, Executor executor) {
		this.logger = logger;
		this.realChannel = realChannel;
		this.executor = executor;
	}

	/* (non-Javadoc)
	 * @see org.pgj.Channel#getConnection(int)
	 */
	public Client getConnection(int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.Channel#receiveFromRDBMS(org.pgj.Client)
	 */
	public Message receiveFromRDBMS(Client client)
			throws CommunicationException {
		// TODO Auto-generated method stub
		Message msg = realChannel.receiveFromRDBMS(client);
		if (msg instanceof CallRequest) {
			executor.execute((CallRequest) msg);
		}
		if (msg instanceof TriggerCallRequest) {
			if(executor instanceof TriggerExecutor){
				((TriggerExecutor)executor).executeTrigger((TriggerCallRequest)msg);
			} else {
				logger.fatalError("no trigger executor is given now should send back an error.");
				//TODO fix execution here!
			}
		}
		return msg;
	}

	/* (non-Javadoc)
	 * @see org.pgj.Channel#sendToRDBMS(org.pgj.messages.Message)
	 */
	public void sendToRDBMS(Message msg) throws CommunicationException {
		realChannel.sendToRDBMS(msg);
	}

}
