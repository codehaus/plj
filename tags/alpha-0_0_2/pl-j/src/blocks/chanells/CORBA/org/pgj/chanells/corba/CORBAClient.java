package org.pgj.chanells.corba;

import org.pgj.Client;

/**
 * @author bitfakk
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public final class CORBAClient implements Client {

	Object fromDB = new Object();
	Object toDB = new Object();
	
	public CORBAClient(int pid){
		this.pid = pid;
	}
	
	int pid = 0;
	/**
	 * Gets the pid.
	 * @return Returns a int
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof CORBAClient)
			return (((CORBAClient) obj).getPid() == pid);
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return pid;
	}

	/**
	 * Call this if a message arrived from the DB.
	 * The function wakes up the glue thread waiting for the event.
	 */
	protected void notifyMessageFromDB() {
		notifyObj(fromDB);
	}
	
	/**
	 * Call this if a message is ready to go back to the DB.
	 * The function wakes up a native thread waiting for the ansver.
	 */
	protected void notifyMessageToDB() {
		notifyObj(toDB);
	}
	
	/**
	 * Does the job of the methods notifyMessageFromDB and notifyMessageToDB.
	 */
	private void notifyObj(Object obj){
		synchronized (obj) {
			obj.notify();
		}
	}
	
	/**
	 * Wait for message from the DB.
	 * Should be called by the CORBAChanell.getMessage().
	 * @param timeout			miliseconds of the timeout. 
	 * 							If timeout is less than or aqual to 0, it waits forever.
	 */
	protected void waitForMessageFromDB(int timeout) {
		waitForObj(fromDB, timeout);
	}
	
	/**
	 * Wait for message to the DB.
	 * Should be called by the native thread to wait until it can send the ansver back.
	 * @param timeout			miliseconds of the timeout. 
	 * 							If timeout is less than or aqual to 0, it waits forever.
	 */
	protected void waitForMessageToDB(int timeout) {
		waitForObj(toDB, timeout);
	}
	
	/**
	 * Do the job of waitForMessageToDB and waitForMessageFromDB.
	 */
	private void waitForObj(Object obj, int timeout){
		synchronized (obj) {
			try {
				if (timeout <= 0) {
					obj.wait();
				} else {
					obj.wait(timeout);
				}
			} catch (InterruptedException ie) {

			}
		}
	}
	
}