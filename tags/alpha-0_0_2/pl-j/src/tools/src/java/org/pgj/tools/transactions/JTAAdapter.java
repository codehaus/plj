/*
 * Created on Aug 22, 2004
 */
package org.pgj.tools.transactions;

import javax.transaction.UserTransaction;

/**
 * Adopts JTA. Possible implementations can be Tyrex, JOTM integration, 
 * ctx.lookup() based solutions, etc.
 * 
 * @author Laszlo Hornyak
 */
public interface JTAAdapter {

	/**
	 * Get a usertransaction object from the JTA provider.
	 * @return UserTransaction
	 */
	UserTransaction getUserTransaction();

}
