package org.pgj.glue;

import org.apache.avalon.framework.logger.*;
import org.apache.avalon.excalibur.pool.*;
import org.apache.avalon.framework.activity.*;
import org.pgj.*;
import org.pgj.messages.*;

/**
 * @author bitfakk
 * Glue worker thread.
 */
public class GlueWorker
	implements Poolable, Executable, LogEnabled, Startable {

	public static final String CHANELL_KEY = "CHANELL_KEY";

	/** the chanell we are dealing with */
	Channel chanell;

	/** The executor object */
	Executor executor;

	/** Client */
	Client client;

	/** Are we asked to terminate? */
	boolean terminating = false;

	/**
	 * ThreadLocal variable to store the channel.
	 */	
	private static ThreadLocal channelThread = new ThreadLocal(){
		protected final Object initialValue(){
			return null;
		}
	};
	
	private static void setThreadChannel(Channel channel){
		channelThread.set(channel);
	}
	
	public final static Channel getThreadChannel(){
		return (Channel)channelThread.get();
	}

	/**
	 * ThreadLocal variable to store the client.
	 */
	private static ThreadLocal clientThread = new ThreadLocal(){
		protected final Object initialValue(){
			return null;
		}
	};
	
	private static void setThreadClient(Client client){
		clientThread.set(client);
	}
	
	public final static Client getThreadClient(){
		return (Client)clientThread.get();
	}

	
	public GlueWorker() {
	}

	/** Logger */
	Logger logger = null;

	/**
	 * @see Executable#execute()
	 */
	public void execute() {
		
		try {

			while (true) {
				Message msg = chanell.receiveFromRDBMS(client);
				logger.debug("message in the glue");
				Message ans = executor.execute((CallRequest) msg);
				if(logger.isDebugEnabled())
					logger.debug("executed, ansver is "+ans);
				ans.setClient(msg.getClient());
				if(logger.isDebugEnabled())
					logger.debug("ansver:"+ans);
				if ((ans instanceof Result) || (ans instanceof org.pgj.messages.Exception)) {
					chanell.sendToRDBMS(ans);
				} else {
					logger.fatalError("hmmmm. result should be Exception or Result");
				}
			}

		} catch (Throwable e) {
			logger.error("problem:", e);
		} finally {

			//cleanup, let gc do its work.
			client = null;
			executor = null;
			chanell = null;

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
			logger.fatalError("GlueWorker: something error in execution.",e);
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