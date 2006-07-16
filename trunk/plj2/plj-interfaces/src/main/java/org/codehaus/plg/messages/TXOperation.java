/*
 * Created on Aug 22, 2004
 */
package org.codehaus.plg.messages;

/**
 * Transaction operation message.
 * This message type is under developement with the 
 * transaction externalisation task.
 * 
 * @author Laszlo Hornyak
 */
public class TXOperation extends Message {

	public static final int TXOPERATION_BEGIN = 3;
	public static final int TXOPERATION_COMMIT = 1;
	public static final int TXOPERATION_ROLLBACK = 2;
	public static final int TXOPERATION_PREPARE = 4;

	int txOperation = -1;

	int getTXOperation(){
		return txOperation;
	}
	
}
