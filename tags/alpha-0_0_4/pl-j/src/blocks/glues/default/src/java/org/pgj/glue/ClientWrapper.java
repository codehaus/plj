/*
 * Created on Aug 19, 2004
 */
package org.pgj.glue;

import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.typemapping.TypeMapper;

/**
 * Wraps the client by glue code.
 * 
 * @author Laszlo Hornyak
 */
class ClientWrapper implements Client {

	private Client client = null;

	private Channel channel = null;

	/**
	 *  
	 */
	public ClientWrapper(Channel channel, Client client) {
		super();
		this.channel = channel;
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Client#getChannel()
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Returns the real client.
	 * 
	 * @return
	 */
	protected Client getRealClient() {
		return client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Client#getTypeMapper()
	 */
	public TypeMapper getTypeMapper() {
		return client.getTypeMapper();
	}

}