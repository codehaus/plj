/*
 * Created on Apr 1, 2004
 */

package org.codehaus.plj;

import org.codehaus.plg.messages.Message;
import org.codehaus.plg.messages.TriggerCallRequest;

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