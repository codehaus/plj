package org.plj.jexec.tests;

import junit.framework.TestCase;
import org.pgj.messages.CallRequest;

/**
 * @author bitfakk
 *
 * I was too lazy to edit the comments.
 */
public class JavaExecutorTest extends TestCase {
	
	JavaExecutor executor = null;
	
	/**
	 * Constructor for JavaExecutorTest.
	 * @param arg0
	 */
	public JavaExecutorTest(String arg0) {
		super(arg0);
	}

	public void testExecute() {
		CallRequest req = new CallRequest();
		req.setClassname("test");
		req.setMethodname("test");
		executor.execute(req);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		//get something value for executor
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
