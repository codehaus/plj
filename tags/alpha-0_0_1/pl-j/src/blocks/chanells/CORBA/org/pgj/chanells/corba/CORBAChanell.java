package org.pgj.chanells.corba;

import java.io.*;
import java.util.*;

import org.apache.avalon.framework.activity.*;
import org.apache.avalon.framework.configuration.*;
import org.apache.avalon.framework.logger.*;
import org.apache.avalon.framework.service.*;
import org.pgj.*;
import org.pgj.messages.*;
import org.pgj.typemapping.*;

/**
 * @author bitfakk
 * @since 0.1
 * 
 * Native CORBA implementation with ORbit2.
 * 
 */
public final class CORBAChanell
	implements Channel, LogEnabled, Initializable, Configurable, Startable, Serviceable {

	/**
	 * This will run the native ORB.
	 */
	private volatile CORBAChanellWorker worker;
	/**
	 * Configuration to publish IOR.
	 */
	Configuration iorPublisherConfiguration = null;
	/**
	 * Type mapper application block.
	 */
	TypeMapper mapper = null;

	/**
	 * Class code initialization.
	 * This code loads the binary implementation.
	 */
	static {
		//load native implementation
		try {
			String libhome = System.getProperty("plpgj.binary.path");
			if (libhome == null) {
				libhome = "";
			} else {
				libhome = libhome.concat("/");
			}
			System.load(libhome + "CORBAChanellWorker.so");
		} finally {

		}
	}

	/** The configuration for this chanell */
	Configuration conf;

	/** Queus for new patients */
	protected volatile Queue connectionQueues;

	/** Messages from the clients */
	protected volatile HashMap clientQueues = new HashMap();

	/** Messages to the clients */
	protected volatile HashMap outQueues = new HashMap();

	/** Avalon logger */
	private Logger logger = null;

	public CORBAChanell() {

	}

	public void sendToRDBMS(Message msg) throws CommunicationException{
		if (msg == null)
			throw new NullPointerException("Message to Db musn`t be (null).");
		logger.debug("CORBAChanell -> sendMessage(Message msg)");
		//must be a CORBAClient, panic if not...
		CORBAClient clnt = (CORBAClient) msg.getClient();
		outQueues.put(clnt, msg);
		clnt.notifyMessageToDB();
	}

	/**
	 * @see LogEnabled#enableLogging(Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/**
	 * @see Initializable#initialize()
	 */
	public void initialize() {
		connectionQueues = new Queue();
		worker = new CORBAChanellWorker(this, mapper, logger);
		logger.debug("initialized");
	}

	/**
	 * @see Configurable#configure(Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		Configuration nsconf;
		try {
			iorPublisherConfiguration = arg0.getChild("ior-file");
			if (iorPublisherConfiguration == null) {
				iorPublisherConfiguration = arg0.getChild("nameserver");
			}
			if (iorPublisherConfiguration == null) {
				logger.warn("ior publishing configuration not found");
				logger.warn("It will be hard to make it work...");
			}
		} finally {
			logger.debug("configured");
		}
	}

	/**
	 * @see Channel#getConnection(int)
	 */
	public Client getConnection(int timeout) {
		Client client;

		logger.debug("getConnection("+timeout+")");

		client = null;

		try {
			synchronized (connectionQueues) {
				if (connectionQueues.isEmpty()) {
					logger.debug("queue is empty, so waiting:" + timeout);
					if (timeout > 0) {
						connectionQueues.wait(timeout);
					} else {
						connectionQueues.wait();
					}
					logger.debug("woken up.");
					if (connectionQueues.isEmpty()) {
						logger.debug("queue is still empty.");
						return null;
					}
				}
				logger.debug("returning a client");
				return (Client) connectionQueues.get();

			}
		} catch (QueueException e) {
			logger.fatalError("fatal error in queue handling:", e);
		} catch (InterruptedException ie) {
			logger.info("waiting thread was interrupted at getConnection.");
		} catch (Throwable t) {
			logger.error("oops", t);
		}

		return client;
	}

	/**
	 * @see Channel#receiveFromRDBMS(Client)
	 */
	public Message receiveFromRDBMS(Client client) throws CommunicationException{

		CORBAClient cclient = (CORBAClient) client;

		Queue cq = (Queue) clientQueues.get(client);
		Message m = null;

		try {
			synchronized (cq) {
				while (cq.isEmpty()) {
					logger.debug("waiting for a message in the que.");
					cclient.waitForMessageFromDB(-1);
					logger.debug("got mesage from the client que");
				}
				logger.debug("geting msg");
				m = (Message) cq.get();

			}
		} catch (QueueException qe) {
			logger.error("Queue handlin problems in receiveMessage", qe);
		} /*catch (InterruptedException ie) {
					logger.error("this thread should stop.");
				}*/

		return m;
	}

	/**
	 * @see Startable#start()
	 */
	public void start() throws java.lang.Exception {
		worker.start();
	}

	/**
	 * @see Startable#stop()
	 */
	public void stop() throws java.lang.Exception {
		worker.terminate();
	}

	/**
	 * Publish the IOR of the server process.
	 * The function is a front-end for the system specific IOR publishing methods.
	 * It calls them using the configuration.
	 * @param ior		The IOR string
	 */
	protected void publishIor(String ior) {

		logger.debug("publishing IOR");
		try {
			if (iorPublisherConfiguration.getName().equals("ior-file")) {
				fileIORPublish(iorPublisherConfiguration, ior);
			} else if (
				iorPublisherConfiguration.getName().equals("nameserver")) {
				nsIORPublish(iorPublisherConfiguration, ior);
			} else {
				logger.error("hmmmm...");
			}
		} catch (Throwable t) {
			logger.error("publishing of the call server ior failed.");
			logger.error("the system may be unavailable. continuing.");
			logger.error("cause:", t);
		}
	}

	/**
	 * Publish the IOR using a (Local or remote) file system.
	 * @param conf	the configuration to use for  publishing.
	 * @param ior	the IOR string
	 */
	void fileIORPublish(Configuration conf, String ior)
		throws IOException, ConfigurationException {
		String filename = conf.getAttribute("filename");
		logger.debug("write ior to file " + filename);
		OutputStream os = new FileOutputStream(filename);
		os.write(ior.getBytes());
		os.close();
	}

	/**
	 * Publish the IOR using a COS nameserver.
	 */
	void nsIORPublish(Configuration conf, String ior) {
		throw new RuntimeException("Unimplemented");
	}

	/**
	 * @see Serviceable#service(ServiceManager)
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		mapper = (TypeMapper) arg0.lookup("org.pgj.typemapping.TypeMapper");
		logger.debug("serviced");
	}

	/**
	 * Gets the connectionQueues.
	 * @return Returns a Queue
	 */
	protected Queue getConnectionQueues() {
		return connectionQueues;
	}

}