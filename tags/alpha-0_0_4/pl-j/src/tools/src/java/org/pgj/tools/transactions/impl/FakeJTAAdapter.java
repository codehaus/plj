/*
 * Created on Aug 24, 2004
 */
package org.pgj.tools.transactions.impl;

import javax.transaction.UserTransaction;

import org.pgj.tools.transactions.JTAAdapter;

/**
 * Fake JTA adapter.
 * @author Laszlo Hornyak
 * @avalon.component name="jta-adapter" lifestyle="singleton" 
 * @avalon.service type="org.pgj.tools.transactions.JTAAdapter"
 */
public class FakeJTAAdapter implements JTAAdapter {

	/**
	 * 
	 */
	public FakeJTAAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.transactions.JTAAdapter#getUserTransaction()
	 */
	public UserTransaction getUserTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

}
