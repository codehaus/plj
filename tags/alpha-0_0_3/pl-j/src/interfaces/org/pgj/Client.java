package org.pgj;

import org.pgj.typemapping.TypeMapper;

/**
 * The interface for objects representing a client.
 * Client serves actualy as context of the session too.
 * 
 * @author Laszlo Hornyak
 */
public interface Client{
	
	/**
	 * Returns the channel the code can use to communicate to the client.
	 * @return the channel.
	 */
	Channel getChannel();
	
	/**
	 * Returns the typemapper used for typemapping by the client's session.
	 * @return
	 */
	TypeMapper getTypeMapper();
}

