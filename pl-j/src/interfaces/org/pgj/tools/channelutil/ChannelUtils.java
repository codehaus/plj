/*
 * Created on Mar 15, 2004
 */

package org.pgj.tools.channelutil;

import org.pgj.Channel;


/**
 * Channel handling utilities.
 * This design will change as soon as we find a cleaner solution for
 * accessing channels independently of execution context.
 * The major problem with the current implementation is that it doesn`t let
 * a stored procedure run multiple threads.
 * 
 * @author Laszlo Hornyak
 */
public final class ChannelUtils {

	/** ThreadLocal variable to hold the channel. */
	private static final ThreadLocal threadChannel = new ThreadLocal();

	/**
	 * Set the threads channel.
	 * @param channel the channel to associate with the current thread (null to disassociate)
	 */
	public static void setChannelforThread(Channel channel) {
		threadChannel.set(channel);
	}

	/**
	 * Get the treads channel.
	 * @return the channel associated with the current thread (may be null)
	 */
	public static Channel getChannelforThread() {
		return (Channel) threadChannel.get();
	}
}
