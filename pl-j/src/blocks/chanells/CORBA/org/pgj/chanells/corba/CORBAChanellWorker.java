package org.pgj.chanells.corba;

import java.util.*;
import org.apache.avalon.framework.logger.*;
import org.pgj.messages.*;
import org.pgj.typemapping.*;

/**
 * @author bitfakk
 *
 * Runs the ORB.
 * CORBAChanellWorker is a thread for running the native ORB itself.
 * The class is in contact with it`s parent, CORBAChanell and its TypeMapper.
 */
public final class CORBAChanellWorker extends Thread {

	/** The parent CORBAChanell of this worker object. */
	private CORBAChanell parent = null;
	/** The typemapper block of given for this chanell. */
	private TypeMapper mapper = null;
	/** Avalon logger class. */
	Logger logger = null;

	/**
	 * To simplify the ChanellWorker native implementation`s job.
	 * This method is called from the native code, it calls publishIor on the
	 * parent CORBAChanell to publish reference to this ORB basing on 
	 * configuration data.
	 */
	private void putIor(byte[] ior) {
		parent.publishIor(new String(ior));
	}

	/**
	 * Constructs a worker.
	 * @param parent		the parent CORBAChanell of the worker.
	 * @param mapper		The typemapper block of the Channel block.
	 * @param Logger		The avalon logger to use.
	 */
	public CORBAChanellWorker(
		CORBAChanell parent,
		TypeMapper mapper,
		Logger logger) {
		super();
		this.parent = parent;
		this.mapper = mapper;
		this.logger = logger;
	}

	/**
	 * @see Runnable#run()
	 * Runs the ORB. This method blocks the thread execution, calls from 
	 * native threads come back.
	 */
	public native void run();

	/**
	 * Ask worker to terminate.
	 * See native implementation documentation.
	 */
	public native void terminate();

	/**
	 * Creates a result bean.
	 * This method helps the native system creating bean.
	 */
	protected static Result createResultBean() {
		return new Result();
	}

	/**
	 * Creates a call request bean.
	 * This method helps the native system creating bean.
	 */
	protected static CallRequest createCallRequestBean() {
		return new CallRequest();
	}

	/**
	 * Creates a result bean.
	 * This method helps the native system creating bean.
	 */
	protected static org.pgj.messages.Exception createExceptionBean() {
		return new org.pgj.messages.Exception();
	}

	/**
	 * Sets the Client ID in the Message.
	 */
	protected void setClient(int PID, Message msg) {
		//should search instead.
		CORBAClient clnt = getClient(PID);
		msg.setClient(clnt);
	}

	/**
	 * Called from native code when a message arrives.
	 * This method checks if the Client already has a connection.
	 * 
	 */
	private synchronized void msgArrival(Message msg) {
		HashMap cq = parent.clientQueues;
		boolean isNew = false;

		CORBAClient client = (CORBAClient) msg.getClient();
		Queue queue = (Queue) cq.get(client);

		logger.debug("--message arrived--");

		//---
		try {
			logger.debug(msg.getClass().getName());
			logger.debug(msg.getSid().toString());
		} catch (Throwable t) {
			logger.debug("ooops.", t);
		}
		//---
		if (queue == null) {
			logger.debug("msg queue is null. creating.");
			queue = new Queue();
			cq.put(client, queue);

			isNew = true;

		}

		try {
			logger.debug("puting msg to the que");
			queue.put(msg);
			logger.debug("message in the client`s msg que");
		} catch (QueueException qe) {
			logger.error("queue handling problems: ", qe);
			//TODO: handle this correctly.
		}

		if (isNew) {
			synchronized (parent.connectionQueues) {
				try {
					parent.connectionQueues.put(client);
				} catch (QueueException qe) {
					logger.fatalError(
						"cannot load store new connection request, queue is full");
				}
				parent.connectionQueues.notify();
			}
		}

		logger.debug("notifying ");
		client.notifyMessageFromDB();
		logger.debug("notified");
	}

	public static final int NDL_DEBUG = 0;
	public static final int NDL_INFO = 1;
	public static final int NDL_WARN = 2;
	public static final int NDL_ERROR = 3;
	public static final int NDL_FATAL = 4;

	/**
	 * Logging helper for native code.
	 */
	protected void nativeLog(int level, String log) {
		if (logger != null)
			switch (level) {
				case NDL_DEBUG :
					logger.debug("[native]".concat(log));
					break;
				case NDL_INFO :
					logger.info("[native]".concat(log));
					break;
				case NDL_WARN :
					logger.warn("[native]".concat(log));
					break;
				case NDL_ERROR :
					logger.error("[native]".concat(log));
					break;
				case NDL_FATAL :
					logger.debug("[native]".concat(log));
					break;
				default :
					break;
			}
	}

	/**
	 * Native function helper.
	 */
	protected CORBAClient getClient(int pid) {
		Iterator it = parent.clientQueues.keySet().iterator();
		CORBAClient ret = null;
		while (it.hasNext()) {
			ret = (CORBAClient) it.next();
			if (ret.getPid() == pid) {
				logger.debug("found an existing client");
				return ret;
			}
		}
		logger.debug("Client not found, so I create a new one.");
		ret = new CORBAClient(pid);
		return ret;
	}

	/**
	 * Called from the native thread.
	 */
	protected Message getMessage(CORBAClient cl) {
		Message msg = (Message) parent.outQueues.get(cl);
		if (msg == null) {
			logger.debug("out que seems empty, so waiting.");
			cl.waitForMessageToDB(-1);
			msg = (Message) parent.outQueues.get(cl);
		}
		logger.debug("sending back msg to RDBMS: " + msg);
		return msg;
	}

}