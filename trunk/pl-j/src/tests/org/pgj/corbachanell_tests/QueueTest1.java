package org.pgj.corbachanell_tests;

import junit.framework.TestCase;
import org.pgj.chanells.corba.*;

/**
 * @author bitfakk
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class QueueTest1 extends TestCase {
	
	Queue queue = null;
	
	/**
	 * Constructor for QueueTest1.
	 * @param arg0
	 */
	public QueueTest1(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) throws Exception{
		QueueTest1 test = new QueueTest1("test");
		test.setUp();
		test.testIsEmpty();
		test.testPut();
		test.testIsEmpty();
		test.testGet();
		test.testIsEmpty();
		
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		queue = new Queue(10);
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		queue = null;
	}

	public void testPut() throws QueueException {
		queue.put(new Object());
	}

	public void testGet() throws QueueException {
		queue.get();
	}

	public void testIsEmpty() {
		queue.isEmpty();
	}

}
