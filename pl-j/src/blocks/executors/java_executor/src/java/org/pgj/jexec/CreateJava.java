/*
 * Created on Oct 3, 2004
 */
package org.pgj.jexec;

import org.pgj.messages.CallRequest;


/**
 * @author Laszlo Hornyak
 */
public class CreateJava extends BasicPrivilegedJSProc {

	protected CreateJava(JavaExecutor je) {
		super(je);
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#getName()
	 */
	public String getName() {
		return "create_java";
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#perform(org.pgj.messages.CallRequest)
	 */
	public Object perform(CallRequest call) throws Exception {
		return null;
	}

}
