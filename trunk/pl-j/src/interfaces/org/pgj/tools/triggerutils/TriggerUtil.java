/*
 * Created on May 3, 2004
 */

package org.pgj.tools.triggerutils;

import java.util.Stack;

import org.pgj.messages.TriggerCallRequest;


/**
 * Utility to handle trigger calls. 
 * @author Laszlo Hornyak
 */
public class TriggerUtil {

	private static final InheritableThreadLocal threadTrigger = new InheritableThreadLocal();

	public static final synchronized void pushTriggerData(TriggerCallRequest t) {
		Stack stack = (Stack) threadTrigger.get();
		if (stack != null) {
			stack = new Stack();
			threadTrigger.set(stack);
		}
		stack.push(t);
	}

	public static final synchronized TriggerCallRequest peekTriggerData() {
		Stack stack = (Stack) threadTrigger.get();
		if (stack != null) {
			stack = new Stack();
			threadTrigger.set(stack);
		}
		return (TriggerCallRequest) (stack.peek());
	}

	public static final synchronized void popTriggerData() {
		Stack stack = (Stack) threadTrigger.get();
		if (stack != null) {
			stack = new Stack();
			threadTrigger.set(stack);
		}
		stack.pop();
	}

}