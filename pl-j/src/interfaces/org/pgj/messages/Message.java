package org.pgj.messages;

import org.pgj.*;

/**
 * Represents a <i>message</i> from or to the database.
 */
public abstract class Message {

	String protocol_version = null;
	String pgj_version = null;
	String jvm = null;
	Object sid = null;

	/**
	 * Client MUST be set.
	 */
	Client client = null;

	/**
	 * Constructs a message.
	 */
	public Message() {
	}

	/**
	 * Tells the protocol version of the message.
	 */
	public String getProtocolVersion() {
		return protocol_version;
	}

	/**
	 * Sets the protocol version.
	 */
	public void setProtocolVersion(String p) {
		protocol_version = p;
	}

	/**
	 * Set <b>PGJ</b> version string.
	 */
	public void setPGJVersion(String p) {
		pgj_version = p;
	}

	/**
	 * Returns <b>PGJ</b> version string.
	 */
	public String getPGJVersion() {
		return pgj_version;
	}

	/**
	 * Sets the Java Virtual Machine version string.
	 */
	public void setJVMVersion(String s) {
		jvm = s;
	}

	/**
	 * Returns the the Java Virtual Machine version string.
	 */
	public String getJVMVersion() {
		return jvm;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		//needs java security first!!
		this.client = client;
	}

	/**
	 * Gets the sid.
	 * @return Returns a Object
	 */
	public Object getSid() {
		return sid;
	}

	/**
	 * Sets the sid.
	 * @param sid The sid to set
	 */
	public void setSid(Object sid) {
		this.sid = sid;
	}

}