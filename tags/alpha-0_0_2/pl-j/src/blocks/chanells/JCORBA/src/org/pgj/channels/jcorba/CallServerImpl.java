/*
 * Created on Sep 10, 2003
 */
package org.pgj.channels.jcorba;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.pgj.corba.CallServerPOA;
import org.pgj.corba.callreqHelper;
import org.pgj.corba.resultHelper;

/**
 * Implementation.
 * @author Laszlo Hornyak
 * @version 0.1
 */
public class CallServerImpl extends CallServerPOA {

	protected CallServerImpl(JCORBAChannel parent){
		this.parent = parent;
	}

	private JCORBAChannel parent = null;
	private Logger logger = null;
	private static final Map msgTypeMap = new HashMap();
	static {
		msgTypeMap.put(callreqHelper.id(), callreqHelper.class);
		msgTypeMap.put(resultHelper.id(), resultHelper.class);
	}
	
	protected void setLogger(Logger logger){
		this.logger = logger;
	}

	/* (non-Javadoc)
	 * @see org.pgj.corba.CallServerOperations#sendMessage(org.omg.CORBA.Any)
	 */
	public void sendMessage(Any message) {
		logger.debug("sendMessage.");
		try {
			String id = message.type().id();
		} catch (BadKind e) {
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.corba.CallServerOperations#receiveAnsver(java.lang.String)
	 */
	public Any receiveAnsver(String sid) {
		logger.debug("receiveAnsver.");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.corba.CallServerOperations#SendReceive(org.omg.CORBA.Any)
	 */
	public Any SendReceive(Any message) {
		logger.debug("SendReceive");
		return null;
	}

}
