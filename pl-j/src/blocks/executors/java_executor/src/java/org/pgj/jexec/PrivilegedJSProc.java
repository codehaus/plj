/*
 * Created on Oct 3, 2004
 */
package org.pgj.jexec;

import org.pgj.messages.CallRequest;


/**
 * Represents a privileged java stored procedure.
 * Only 1 instance of the class will be created, all of them must be threadsafe.
 * 
 * @author Laszlo Hornyak
 */
interface PrivilegedJSProc {
	String getName();
	Object perform(CallRequest call) throws Exception;
}
