package org.pgj.messages;

/**
 * @author bitfakk
 *
 * I was too lazy to edit the comments.
 */
public class Exception extends Message {

	String message = null;
	String stackTrace = null;
	String exceptionClassName = null;

	public static Exception fromThrowable(Throwable t) {

		Exception exception = new Exception();
		exception.setMessage(t.getMessage());
		StackTraceElement[] trace = t.getStackTrace();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < trace.length; i++) {
			buff
				.append(trace[i].getClassName())
				.append('.')
				.append(trace[i].getMethodName())
				.append(" (")
				.append(trace[i].getFileName())
				.append(':')
				.append(trace[i].getLineNumber())
				.append(")\n");
		}
		exception.setStackTrace(buff.toString());
		exception.setExceptionClassName(t.getClass().getName());

		return exception;
	}

	/**
	 * Gets the message.
	 * @return Returns a String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 * @param message The message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the stackTrace.
	 * @return Returns a String
	 */
	public String getStackTrace() {
		return stackTrace;
	}

	/**
	 * Sets the stackTrace.
	 * @param stackTrace The stackTrace to set
	 */
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	/**
	 * Gets the exceptionClassName.
	 * @return Returns a String
	 */
	public String getExceptionClassName() {
		return exceptionClassName;
	}

	/**
	 * Sets the exceptionClassName.
	 * @param exceptionClassName The exceptionClassName to set
	 */
	public void setExceptionClassName(String exceptionClassName) {
		this.exceptionClassName = exceptionClassName;
	}
	
//	public String toString(){
//		return exceptionClassName+"\n"+message+"\n"+stackTrace;
//	}
	
}