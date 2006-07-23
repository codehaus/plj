/*
 * Created on Jan 29, 2005
 */
package org.codehaus.plj.core;


/**
 * Configuration object for Glue;
 * @author Laszlo Hornyak
 */
class GlueConfiguration {

	boolean errorRecoverable;

	public boolean isErrorRecoverable() {
		return errorRecoverable;
	}
	public void setErrorRecoverable(boolean errorRecoverable) {
		this.errorRecoverable = errorRecoverable;
	}
}
