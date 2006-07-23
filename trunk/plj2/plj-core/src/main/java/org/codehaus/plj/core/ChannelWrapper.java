/*
 * Created on Aug 19, 2004
 */

package org.codehaus.plj.core;

import org.apache.log4j.Logger;
import org.codehaus.plj.Channel;
import org.codehaus.plj.Client;
import org.codehaus.plj.CommunicationException;
import org.codehaus.plj.ExecutionCancelException;
import org.codehaus.plj.JTAAdapter;
import org.codehaus.plj.Runner;
import org.codehaus.plj.TriggerExecutor;
import org.codehaus.plj.messages.CallRequest;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.TXOperation;
import org.codehaus.plj.messages.TriggerCallRequest;
import org.codehaus.plj.typemapping.MappingException;

/**
 * Wraps Channel to add some additional logic.
 * 
 * @author Laszlo Hornyak
 */
public class ChannelWrapper implements Channel {

	/**
	 * Avalon logger. (to be replaced)
	 */
	private Logger logger = Logger.getLogger(ChannelWrapper.class);

	/**
	 * The wrapped channel.
	 */
	private Channel realChannel = null;

	/**
	 * Executor to run calls on if call msg is received.
	 */
	private Runner executor = null;

	/**
	 * The JTA adapter.
	 */
	private JTAAdapter jtaadapter = null;

	/**
	 * Client wrapper.
	 */
	private ClientWrapper clientWrapper = null;
	
	private GlueConfiguration conf = null;

	/**
	 *  
	 */
	public ChannelWrapper(Logger logger, Channel realChannel, Runner executor, GlueConfiguration conf) {
		this.logger = logger;
		this.realChannel = realChannel;
		this.executor = executor;
		this.conf = conf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Channel#getConnection(int)
	 */
	public Client getConnection(int timeout) {
		logger
				.error("This method should never be called. Possibly security attack from UDF.");
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
			throws CommunicationException, MappingException {

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
						.error("no trigger executor is given now should send back an error.");


			} else if (msg instanceof TXOperation) {
				//TODO do something with the transaction!
				//and recursion to do the same.
				continue;
			} else if (msg instanceof org.codehaus.plj.messages.Error) {
				if (!conf.isErrorRecoverable()){
					logger.info("Received an error, and by configuration, cant recover errors");
					logger.info("Error message:"+((org.codehaus.plj.messages.Error) msg).getMessage());
					
					throw new ExecutionCancelException(
							"Cant recover RDBMS exceptions: "
									+ ((org.codehaus.plj.messages.Error) msg).getMessage());
					
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
	public void sendToRDBMS(Message msg) throws CommunicationException, MappingException {
		msg.setClient(clientWrapper.getRealClient());
		realChannel.sendToRDBMS(msg);
	}

	void setClientWrapper(ClientWrapper clientWrapper) {
		this.clientWrapper = clientWrapper;
	}

	public JTAAdapter getJtaadapter() {
		return jtaadapter;
	}

	public void setJtaadapter(JTAAdapter jtaadapter) {
		this.jtaadapter = jtaadapter;
	}
}