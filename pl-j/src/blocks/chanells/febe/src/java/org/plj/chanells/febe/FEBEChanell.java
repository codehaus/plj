/*
 * Created on Jan 18, 2004
 */
package org.plj.chanells.febe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.CommunicationException;
import org.pgj.messages.Message;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;
import org.plj.chanells.febe.msg.ErrorMessageFactory;

/**
 * A chanell built on FE/BE protocoll basing on the PostgreSQL JDBC team`s 
 * implementation.
 * @author Laszlo Hornyak
 * @version 0.1
 */
public class FEBEChanell
	implements Channel, Configurable, Initializable, Serviceable, LogEnabled, Startable {

	ServerSocket serverSocket = null;
	/**
	 * The encoding object of the chanell.
	 */
	Encoding encoding = null;

	ErrorMessageFactory errorMessageFactory = new ErrorMessageFactory();

	/* (non-Javadoc)
	 * @see org.pgj.Channel#getConnection(int)
	 */
	public Client getConnection(int timeout) {
		try {
			//TODO: timeout is ignored ...
			FEBEClient client;
			Socket sock = serverSocket.accept();
			client = new FEBEClient();
			PGStream stream = new PGStream(sock);
			client.setStream(stream);
		} catch (IOException e) {
			logger.fatalError("GetConnection, accept", e);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.Channel#receiveFromRDBMS(org.pgj.Client)
	 */
	public Message receiveFromRDBMS(Client client)
		throws CommunicationException {
		try {
			PGStream stream = ((FEBEClient) client).getStream();
			int msgtype = stream.ReceiveChar();
			switch (msgtype) {
				case ErrorMessageFactory.MESSAGE_HEADER_ERROR :
					return errorMessageFactory.getMessage(stream, encoding);
				default :
					throw new CommunicationException("unknown message type.");
			}
		} catch (IOException e) {
			throw new CommunicationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.Channel#sendToRDBMS(org.pgj.messages.Message)
	 */
	public void sendToRDBMS(Message msg) throws CommunicationException {

	}

	int port = 0;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		//TODO: this should work other way.
		encoding =
			Encoding.getEncoding(
				arg0.getChild("database-encoding").getValue(),
				arg0.getChild("passed-encoding").getValue());
		port = arg0.getChild("port").getValueAsInteger();
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		serverSocket = serverSocketFactory.createServerSocket(port);
	}

	ServerSocketFactory serverSocketFactory;
	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		serverSocketFactory =
			(ServerSocketFactory) arg0.lookup("serversocket-factory");
	}

	Logger logger = null;
	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Startable#start()
	 */
	public void start() throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Startable#stop()
	 */
	public void stop() throws Exception {
		serverSocket.close();
	}

}
