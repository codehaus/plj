/*
 * Created on Apr 1, 2004
 */
package org.pgj;

import org.pgj.messages.Message;

/**
 * Trigger execution interface.
 * Experimental.
 * 
 * @author Laszlo Hornyak
 */
public interface TriggerExecutor {

	/**
	 * Execute a trigger.
	 * @return the result or whatever
	 */
	public Message executeTrigger();
}
