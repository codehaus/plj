/*
 * Created on Apr 1, 2004
 */

package org.pgj;

import org.pgj.messages.Message;
import org.pgj.messages.TriggerCallRequest;

/**
 * Trigger execution interface.
 * Experimental.
 * 
 * @author Laszlo Hornyak
 */
public interface TriggerExecutor {

	/**
	 * Execute a trigger.
	 * @return the result or whatever, it should be TupleResult or Error.
	 */
	public Message executeTrigger(TriggerCallRequest trigger);
}