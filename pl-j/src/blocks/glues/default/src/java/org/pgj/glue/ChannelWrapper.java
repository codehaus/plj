/*
 * Created on Aug 19, 2004
 */

package org.pgj.glue;

import org.apache.avalon.framework.logger.Logger;
import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.CommunicationException;
import org.pgj.ExecutionCancelException;
import org.pgj.Executor;
import org.pgj.TriggerExecutor;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Error;
import org.pgj.messages.Message;
import org.pgj.messages.TXOperation;
import org.pgj.messages.TriggerCallRequest;
import org.pgj.tools.transactions.JTAAdapter;

/**
 * Wraps Channel to add some additional logic.
 * 
 * @author Laszlo Hornyak
 */
public class ChannelWrapper implements Channel {

	/**
	 * Avalon logger. (to be replaced)
	 */
	private Logger logger = null;

	/**
	 * The wrapped channel.
	 */
	private Channel realChannel = null;

	/**
	 * Executor to run calls on if call msg is received.
	 */
	private Executor executor = null;

	/**
	 * The JTA adapter.
	 */
	private JTAAdapter jtaadapter = null;

	/**
	 * Client wrapper.
	 */
	private ClientWrapper clientWrapper = null;

	private boolean errorRecoverable = false;

	/**
	 *  
	 */
	public ChannelWrapper(Logger logger, Channel realChannel, Executor executor, GlueConfiguration conf) {
		this.logger = logger;
		this.realChannel = realChannel;
		this.executor = executor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Channel#getConnection(int)
	 */
	public Client getConnection(int timeout) {
		logger
				.fatalError("This method should never be called. Possibly security attack from UDF.");
		throw new ExecutionCancelException("getConnection: I am only a proxy.");
	}

	/**
	 * @see org.pgj.Channel#receiveFromRDBMS(org.pgj.Client)
	 * 
	 * Receives a message from the RDBMS and fires action if needed, such as
	 * executes a call or trigger, handles transaction.
	 * __under developement__
	 * 
	 */
	public Message receiveFromRDBMS(Client client)
			throws CommunicationException {

		/*
		 * Why this evil endless loop instead of an elegant recursion? This
		 * block runs for reentratnt SQL. There may be milions, and that may
		 * easily cause a StackOverflowError.
		 */
		while (true) {
			Message msg = realChannel.receiveFromRDBMS(clientWrapper
					.getRealClient());
			if (msg instanceof CallRequest) {
				executor.execute((CallRequest) msg);
				continue;
			} else if (msg instanceof TriggerCallRequest) {
				if (executor instanceof TriggerExecutor) {
					((TriggerExecutor) executor)
							.executeTrigger((TriggerCallRequest) msg);
					continue;
				}
				logger
						.fatalError("no trigger executor is given now should send back an error.");


			} else if (msg instanceof TXOperation) {
				//TODO do something with the transaction!
				//and recursion to do the same.
				continue;
			} else if (msg instanceof Error) {
				if (!errorRecoverable){
					logger.info("Received an error, and by configuration, cant recover errors");
					logger.info("Error message:"+((Error) msg).getMessage());
					
					throw new ExecutionCancelException(
							"Cant recover RDBMS exceptions: "
									+ ((Error) msg).getMessage());
					
				}
			}

			msg.setClient(clientWrapper);
			return msg;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Channel#sendToRDBMS(org.pgj.messages.Message)
	 */
	public void sendToRDBMS(Message msg) throws CommunicationException {
		msg.setClient(clientWrapper.getRealClient());
		realChannel.sendToRDBMS(msg);
	}

	void setClientWrapper(ClientWrapper clientWrapper) {
		this.clientWrapper = clientWrapper;
	}
}