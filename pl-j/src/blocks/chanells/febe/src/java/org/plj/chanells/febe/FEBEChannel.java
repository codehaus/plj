/*
 * Created on Jan 18, 2004
 */
package org.plj.chanells.febe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.TypeMapper;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;
import org.plj.chanells.febe.msg.CallMessageFactory;
import org.plj.chanells.febe.msg.ErrorMessageFactory;
import org.plj.chanells.febe.msg.MessageFactory;

/**
 * A chanell built on FE/BE protocoll basing on the PostgreSQL JDBC team`s 
 * implementation.
 * @author Laszlo Hornyak
 * @version 0.1
 * 
 * @avalon.component name="FEBEChannel" lifestyle="singleton"
 * @avalon.service type="org.pgj.Channel"
 */
public class FEBEChannel
	implements Channel, Configurable, Initializable, Serviceable, LogEnabled, Startable {

	private ServerSocket serverSocket = null;
	TypeMapper typeMapper = null;
	/**
	 * The encoding object of the chanell.
	 */
	private Encoding defaultEncoding = null;

	private Map messageFactoryMap = new HashMap();

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
			Encoding encoding = ((FEBEClient) client).getEncoding();
			int msgtype = stream.ReceiveChar();
			Character type = new Character((char)msgtype);
			
			MessageFactory factory = (MessageFactory)messageFactoryMap.get(type);
			return factory.getMessage(stream, encoding);
			
		} catch (IOException e) {
			throw new CommunicationException(e);
		} catch (MappingException me){
			//TODO: is it right to throw a CommunicationException instead of a MappingException?
			throw new CommunicationException("error at receiveing message.",me);
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.Channel#sendToRDBMS(org.pgj.messages.Message)
	 */
	public void sendToRDBMS(Message msg) throws CommunicationException {
		FEBEClient client = (FEBEClient)msg.getClient();
		PGStream stream = client.getStream();
		Encoding encoding = client.getEncoding();
		
	}

	private int port = 0;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		//TODO: this should work other way.
		defaultEncoding =
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
		//init messageFactories
		messageFactoryMap.put(new Character((char)ErrorMessageFactory.MESSAGE_HEADER_ERROR), new ErrorMessageFactory());
		messageFactoryMap.put(new Character((char)CallMessageFactory.MESSAGE_HEADER_CALL), new CallMessageFactory(typeMapper));
	}

	private ServerSocketFactory serverSocketFactory;

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 * @avalon.dependency key="serversocket-factory" type="org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory"
	 * @avalon.dependency key="type-mapper" type="org.pgj.typemapping.TypeMapper"
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		
		serverSocketFactory =
			(ServerSocketFactory) arg0.lookup("serversocket-factory");
		
		typeMapper = (TypeMapper) arg0.lookup("type-mapper");

	}

	private Logger logger = null;
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
