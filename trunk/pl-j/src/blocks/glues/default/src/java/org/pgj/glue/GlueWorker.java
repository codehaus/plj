package org.pgj.glue;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.Executor;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Message;
import org.pgj.messages.Result;
import org.pgj.tools.channelutil.ClientUtils;

/**
 * Glue worker thread.
 * @author Laszlo Hornyak
 */
public class GlueWorker
		implements
			Poolable,
			Runnable,
			Executable,
			LogEnabled,
			Startable {

	/** the chanell we are dealing with */
	private Channel chanell;
	/** The executor object */
	private Executor executor;
	/** Client */
	private Client client;
	/** Are we asked to terminate? */
	private boolean terminating = false;

	/**
	 * ThreadLocal variable to store the client.
	 */
	private static ThreadLocal clientThread = new ThreadLocal() {

		protected final Object initialValue() {
			return null;
		}
	};

	public GlueWorker() {
	}
	/** Logger */
	Logger logger = null;

	/**
	 * @see Executable#execute()
	 */
	public void execute() {
		
		/* if the client is not set, so this is a new call from a client
		 * we must set the Client object for this thread, and unset it
		 * after the call is done. (see finally block)
		 */
		boolean setClient = ClientUtils.getClientforThread() == null;
		if(setClient)
			ClientUtils.setClientforThread(client);

		try {
			while (true) {
				Message msg = chanell.receiveFromRDBMS(client);
				logger.debug("message in the glue");
				Message ans = executor.execute((CallRequest) msg);
				if (logger.isDebugEnabled()){
					logger.debug("executed, ansver is " + ans);
					logger.debug("message: "+msg);
				}
				ans.setClient(msg.getClient());
				if (logger.isDebugEnabled())
					logger.debug("ansver:" + ans);
				if ((ans instanceof Result)
						|| (ans instanceof org.pgj.messages.Error)) {
					chanell.sendToRDBMS(ans);
				} else {
					logger
							.fatalError("hmmmm. result should be Exception or Result");
				}
			}
		} catch (Throwable e) {
			logger.error("problem:", e);
		} finally {
			//cleanup, let gc do its work.
			client = null;
			executor = null;
			chanell = null;

			//no funny tricks ;)
			if(setClient)
				ClientUtils.setClientforThread(null);
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
	 * @param executor The executor to set
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * Sets the chanell.
	 * @param chanell The chanell to set
	 */
	public void setChanell(Channel chanell) {
		this.chanell = chanell;
	}

	/**
	 * Sets the client.
	 * @param client The client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}
}