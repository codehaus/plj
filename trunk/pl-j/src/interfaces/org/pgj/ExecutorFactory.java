package org.pgj;

/**
 * A Factory for Executors.
 * @deprecated
 */
public interface ExecutorFactory{
	
	public static String ROLE="org.pgj.ExecutorFactory";
	
	/**
	 * Create an Executor.
	 */
	Executor get();
	
	/**
	 * Release the executor.
	 */
	void release(Executor e);
	
}

