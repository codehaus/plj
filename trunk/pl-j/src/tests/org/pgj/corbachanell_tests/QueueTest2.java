package org.pgj.corbachanell_tests;

import junit.framework.TestCase;

import org.pgj.chanells.corba.*;

/**
 * @author bitfakk
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class QueueTest2 extends TestCase {
	
	Queue queue = null;
	
	/**
	 * Constructor for QueueTest2.
	 * @param arg0
	 */
	public QueueTest2(String arg0) {
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
		System.out.println(queue.isEmpty());
		Object obj = "hello";
		this.queue.put(obj);
		this.assert( !queue.isEmpty() );
		
		System.out.print(queue.isEmpty());
		
		Object obj2 = queue.get();
		
		this.assert(obj2 == obj);
		System.out.println(obj2);
		
	}
	
}
