/*
 * Created on Oct 10, 2004
 */

package org.pgj.jexec;

import org.apache.log4j.Logger;


/**
 * Basic class for privileged calls.
 * @author Laszlo Hornyak
 */
public abstract class BasicPrivilegedJSProc implements PrivilegedJSProc {

	protected Logger logger = null;
	protected JavaExecutor javaExecutor = null;

	{
		logger = Logger.getLogger(this.getClass());
	}

	protected BasicPrivilegedJSProc(JavaExecutor je) {
		javaExecutor = je;
	}

}