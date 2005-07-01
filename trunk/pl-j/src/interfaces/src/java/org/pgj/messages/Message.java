
package org.pgj.messages;

import org.pgj.Client;

/**
 * Represents a <i>message</i> from or to the database.
 * 
 * @author Laszlo Hornyak
 */
public abstract class Message {

	/**
	 * Client MUST be set.
	 */
	Client client = null;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		//needs java security first!!
		this.client = client;
	}

}