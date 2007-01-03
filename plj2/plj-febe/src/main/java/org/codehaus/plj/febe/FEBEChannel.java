/*
 * Created on Jan 18, 2004
 */

package org.codehaus.plj.febe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.net.ServerSocketFactory;

import org.apache.log4j.Logger;
import org.codehaus.plj.Channel;
import org.codehaus.plj.Client;
import org.codehaus.plj.CommunicationException;
import org.codehaus.plj.febe.msg.CallMessageFactory;
import org.codehaus.plj.febe.msg.ErrorMessageFactory;
import org.codehaus.plj.febe.msg.LogMessageFactory;
import org.codehaus.plj.febe.msg.MessageFactory;
import org.codehaus.plj.febe.msg.ResultMessageFactory;
import org.codehaus.plj.febe.msg.TriggerCallRequestMessageFactory;
import org.codehaus.plj.febe.msg.TupleResultMessageFactory;
import org.codehaus.plj.febe.msg.sql.SQLMessageFactory;
import org.codehaus.plj.messages.Log;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.Result;
import org.codehaus.plj.messages.SQL;
import org.codehaus.plj.messages.TupleResult;
import org.codehaus.plj.typemapping.MappingException;
import org.codehaus.plj.typemapping.TypeMapper;

/**
 * A chanell built on FE/BE protocoll basing on the PostgreSQL JDBC team`s
 * implementation.
 * 
 * @author Laszlo Hornyak
 * @version 0.1
 * 
 * @avalon.component name="FEBEChannel" lifestyle="singleton" 
 * @avalon.service type="org.pgj.Channel"
 * 
 * @dna.component
 * @dna.service type="org.pgj.Channel"
 * @mx.component
 * 
 */
public class FEBEChannel
		implements
			Channel {

	private ServerSocketFactory serverSocketFactory;
	private final static Logger logger = Logger.getLogger(FEBEChannel.class);

	private ServerSocket serverSocket = null;
	private TypeMapper typeMapper = null;
	private Map messageFactoryMap = new HashMap();
	private int port = 1984;
	String socketFactoryName = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Channel#getConnection(int)
	 */
	public Client getConnection(int timeout) {
		logger.debug("waiting for incoming TCP connection");
		try {
			//TODO: timeout is ignored ...
			FEBEClient client;
			Socket sock = serverSocket.accept();
			client = new FEBEClient();
			PGStream stream = new PGStream(sock);
			client.setStream(stream);
			client.setChannel(this);
			client.setTypeMapper(typeMapper);
			return client;
		} catch (IOException e) {
			logger.fatal("GetConnection, accept", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Channel#receiveFromRDBMS(org.pgj.Client)
	 */
	public Message receiveFromRDBMS(Client client)
			throws CommunicationException, MappingException {
		try {
			synchronized (client) {
				PGStream stream = ((FEBEClient) client).getStream();
				Encoding encoding = ((FEBEClient) client).getEncoding();
				if (encoding == null) {
					logger
							.warn("ancodign was null, fallback to hardcoded default");
					encoding = Encoding.defaultEncoding();
				}
				//the length??
				stream.Receive(4);
				int msgtype = stream.ReceiveChar();
				Character type = new Character((char) msgtype);
				logger.debug("message type:" + type);
				MessageFactory factory = (MessageFactory) messageFactoryMap
						.get(type);
				if (factory != null) {
					logger.debug("handling with "
							+ factory.getClass().getName());
					Message ret = factory.getMessage(stream, encoding);
					ret.setClient(client);
					return ret;
				}
					//debug block -->
				try {
					while (true) {
						logger.debug(new Character((char) stream.ReceiveChar())
								.toString());
					}
				} catch (RuntimeException e1) {
					logger.debug("client gone.");
				}
				// <- debug block
				throw new CommunicationException("Unhandled message type:"
						+ type);
			}
		} catch (IOException e) {
			logger.error("I/O exception on receiveing from RDBMS", e);
			throw new CommunicationException("Error at receiveFromRDBMS", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Channel#sendToRDBMS(org.pgj.messages.Message)
	 */
	public void sendToRDBMS(Message msg) throws CommunicationException, MappingException {
		FEBEClient client = (FEBEClient) msg.getClient();
		synchronized (client) {
			PGStream stream = client.getStream();
			//byte[] hdr = {0, 0, 0, 0};
			try {
				//stream.Send(hdr);
				Character type = null;
				if (msg instanceof org.codehaus.plj.messages.Error) {
					type = new Character('E');
				} else if (msg instanceof Result) {
					type = new Character('R');
				} else if (msg instanceof Log) {
					type = new Character('L');
				} else if (msg instanceof TupleResult) {
					type = new Character('U');
				} else if (msg instanceof SQL){
					type = new Character('S');
				}
				if (type == null)
					throw new CommunicationException(
							"unhandled type of message");
				MessageFactory factory = (MessageFactory) messageFactoryMap
						.get(type);
				stream.SendChar(type.charValue());
				logger.debug(msg.toString());
				factory.sendMessage(msg, stream);
				stream.flush();
			} catch (IOException e) {
				logger.error("I/O exception occured at sending MSG to RDBMS.",e);
				throw new CommunicationException(
						"could not send message to DB", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void init() throws Exception {
		logger.debug("listening at " + port);
		if(serverSocketFactory == null){
			serverSocketFactory = ServerSocketFactory.getDefault();
		}
		serverSocket = serverSocketFactory.createServerSocket(port);
		//init messageFactories
		messageFactoryMap.put(new Character(
				(char) ErrorMessageFactory.MESSAGE_HEADER_ERROR),
				new ErrorMessageFactory(logger));
		messageFactoryMap.put(new Character(
				(char) CallMessageFactory.MESSAGE_HEADER_CALL),
				new CallMessageFactory(typeMapper));
		messageFactoryMap.put(new Character(
				(char) LogMessageFactory.MESSAGE_HEADER_LOG),
				new LogMessageFactory());
		messageFactoryMap.put(new Character(
				(char) ResultMessageFactory.MESSAGE_HEADER_RESULT),
				new ResultMessageFactory(typeMapper));
		messageFactoryMap
				.put(
						new Character(
								(char) TriggerCallRequestMessageFactory.MESSAGE_HEADER_TRIGGER),
						new TriggerCallRequestMessageFactory(typeMapper));
		messageFactoryMap.put(new Character(
				(char) TupleResultMessageFactory.MESSAGE_HEADER_TUPLERESULT),
				new TupleResultMessageFactory());
		messageFactoryMap.put(new Character(
				(char) SQLMessageFactory.MESSAGE_HEADER_SQL),
				new SQLMessageFactory(logger));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.activity.Startable#stop()
	 */
	public void shutdown() throws Exception {
		logger.warn("closing connection");
		serverSocket.close();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public TypeMapper getTypeMapper() {
		return typeMapper;
	}

	public void setTypeMapper(TypeMapper typeMapper) {
		this.typeMapper = typeMapper;
	}

	public ServerSocketFactory getServerSocketFactory() {
		return serverSocketFactory;
	}

	public void setServerSocketFactory(ServerSocketFactory serverSocketFactory) {
		this.serverSocketFactory = serverSocketFactory;
	}
}