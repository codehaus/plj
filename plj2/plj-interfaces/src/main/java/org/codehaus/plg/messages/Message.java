
package org.codehaus.plg.messages;

import org.codehaus.plj.Client;

/**
 * Represents a <i>message</i> from or to the database.
 * 
 * @author Laszlo Hornyak
 */
public abstract class Message {

	/**
	 * Client MUST be set.
	 */
	private Client client = null;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		//needs java security first!!
		this.client = client;
	}

}