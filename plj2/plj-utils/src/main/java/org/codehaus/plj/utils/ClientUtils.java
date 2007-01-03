/*
 * Created on Mar 24, 2004
 */
package org.codehaus.plj.utils;

import org.codehaus.plj.Client;

/**
 * @author Laszlo Hornyak
 */
public class ClientUtils {
	/** ThreadLocal variable to hold the channel. */
	private static final InheritableThreadLocal threadClient = new InheritableThreadLocal();

	/**
	 * Set the threads channel.
	 * @param channel the channel to associate with the current thread (null to disassociate)
	 */
	public static void setClientforThread(Client channel) {
		threadClient.set(channel);
	}

	/**
	 * Get the treads channel.
	 * @return the channel associated with the current thread (may be null)
	 */
	public static Client getClientforThread() {
		return (Client) threadClient.get();
	}

}
