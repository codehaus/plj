package org.pgj.corbachanell_tests;

import junit.framework.TestCase;

import org.pgj.chanells.corba.*;

/**
 * @author bitfakk
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class QueueTest3 extends TestCase {
	
	Queue queue = null;
	
	/**
	 * Constructor for QueueTest2.
	 * @param arg0
	 */
	public QueueTest3(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) throws Exception{
		QueueTest2 test = new QueueTest2("test 2");
		test.setUp();
		
		test.doTest();
		
		test.tearDown();
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		queue = new Queue();
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		queue = null;
	}
	
	public void doTest() throws QueueException{
		
		this.assert( queue.isEmpty() );
		
		Object inobj1 = new Object();
		this.queue.put(inobj1);
		
		Object inobj2 = new Object();
		this.queue.put(inobj2);
		
		this.assert( !queue.isEmpty() );
		
		Object out1 = queue.get();
		this.assert(out1 == inobj1);
		
		Object out2 = queue.get();
		this.assert(out2 == inobj2);
		
		this.assert(queue.isEmpty());
		
	}
	
}
