
package org.codehaus.plj.core;

import org.apache.log4j.Logger;
import org.codehaus.plj.Channel;
import org.codehaus.plj.Client;
import org.codehaus.plj.ExecutionCancelException;
import org.codehaus.plj.Runner;
import org.codehaus.plj.TriggerExecutor;
import org.codehaus.plj.messages.CallRequest;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.Result;
import org.codehaus.plj.messages.TriggerCallRequest;
import org.codehaus.plj.messages.TupleResult;


/**
 * Glue worker thread.
 * 
 * @author Laszlo Hornyak
 */
public class GlueWorker
		implements
			Runnable {

	/** the chanell we are dealing with */
	private Channel channel;

	/** The executor object */
	private Runner executor = null;

	/** The executor object */
	private TriggerExecutor triggerExecutor = null;

	/** Client */
	private Client client;

	/** Are we asked to terminate? */
	private boolean terminating = false;

	public GlueWorker() {
	}

	/** Logger */
	private Logger logger = null;

	private GlueConfiguration gConf = null;

	/**
	 * @see Executable#execute()
	 */
	public void execute() {

		ChannelWrapper channelWrapper = new ChannelWrapper(logger, channel,
				executor, gConf);
		ClientWrapper clientWrapper = new ClientWrapper(channelWrapper, client);
		channelWrapper.setClientWrapper(clientWrapper);
		executor.initClientSession(clientWrapper);

		try {
			while (true) {

				try {
					Message msg = channel.receiveFromRDBMS(client);
					logger.debug("message in the glue");

					Message ans = null;
					if (msg instanceof TriggerCallRequest) {
						ans = ((TriggerExecutor) executor)
								.executeTrigger((TriggerCallRequest) msg);
					} else {
						ans = executor.execute((CallRequest) msg);
					}

					if (logger.isDebugEnabled()) {
						logger.debug("executed, ansver is " + ans);
						logger.debug("message: " + msg);
					}
					ans.setClient(msg.getClient());
					if (logger.isDebugEnabled())
						logger.debug("ansver:" + ans);
					if ((ans instanceof Result)
							|| (ans instanceof org.codehaus.plj.messages.Error)
							|| (ans instanceof TupleResult)) {
						channel.sendToRDBMS(ans);
					} else {
						logger
								.fatal("Result should be Exception, Result or TupleResult");
					}
				} catch (ExecutionCancelException e1) {
					logger.debug("sql execution canceled", e1);
				}
			}
		} catch (Throwable e) {
			logger.error("problem:", e);
		} finally {
			executor.destroyClientSession(clientWrapper);

			//cleanup, let gc do its work.
			client = null;
			executor = null;
			channel = null;
		}
	}

	/**
	 * @see LogEnabled#enableLogging(Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/**
	 * @see Startable#start()
	 */
	public void start() throws java.lang.Exception {
		logger.debug("GlueWorker started");
	}

	/**
	 * @see Startable#stop()
	 */
	public void stop() throws java.lang.Exception {
		//		terminating = true;
		//		logger.debug("asked to stop");
	}

	public void run() {
		try {
			execute();
		} catch (java.lang.Exception e) {
			logger.error("GlueWorker: something error in execution.", e);
		}
	}

	/**
	 * Sets the executor.
	 * 
	 * @param executor
	 *            The executor to set
	 */
	public void setExecutor(Runner executor) {
		this.executor = executor;
	}

	/**
	 * Sets the chanell.
	 * 
	 * @param chanell
	 *            The chanell to set
	 */
	public void setChanell(Channel chanell) {
		this.channel = chanell;
	}

	/**
	 * Sets the client.
	 * 
	 * @param client
	 *            The client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	public void setGConf(GlueConfiguration conf) {
		gConf = conf;
	}
}