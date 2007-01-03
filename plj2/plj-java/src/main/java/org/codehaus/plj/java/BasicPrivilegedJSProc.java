/*
 * Created on Oct 10, 2004
 */

package org.codehaus.plj.java;



/**
 * Basic class for privileged calls.
 * @author Laszlo Hornyak
 */
public abstract class BasicPrivilegedJSProc implements PrivilegedJSProc {

	JavaExecutor javaExecutor = null;

	protected BasicPrivilegedJSProc(JavaExecutor je) {
		javaExecutor = je;
	}

}