package org.pgj.glue;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.ExecutionCancelException;
import org.pgj.Executor;
import org.pgj.TriggerExecutor;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Message;
import org.pgj.messages.Result;
import org.pgj.messages.TriggerCallRequest;
import org.pgj.messages.TupleResult;

/**
 * Glue worker thread.
 * 
 * @author Laszlo Hornyak
 */
public class GlueWorker implements Poolable, Runnable, Executable, LogEnabled,
		Startable {

	/** the chanell we are dealing with */
	private Channel channel;

	/** The executor object */
	private Executor executor = null;

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

	/**
	 * @see Executable#execute()
	 */
	public void execute() {

		ChannelWrapper channelWrapper = new ChannelWrapper(logger, channel, executor);
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
							|| (ans instanceof org.pgj.messages.Error)
							|| (ans instanceof TupleResult)) {
						channel.sendToRDBMS(ans);
					} else {
						logger
								.fatalError("Result should be Exception, Result or TupleResult");
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
			logger.fatalError("GlueWorker: something error in execution.", e);
		}
	}

	/**
	 * Sets the executor.
	 * 
	 * @param executor
	 *            The executor to set
	 */
	public void setExecutor(Executor executor) {
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

}