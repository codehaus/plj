/*
 * Created on Oct 11, 2003
 */
package org.pgj.channels.jcorba;

import org.omg.CORBA.Any;
import org.pgj.corba.callreq;
import org.pgj.corba.callreqHelper;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Message;

// TODO You shoud document this type.
/**
 * @author Laszlo Hornyak
 * @version 0.1
 */
public class CallReqProducer implements Producer {

	/**
	 * 
	 */
	public CallReqProducer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.pgj.channels.jcorba.Producer#create(org.omg.CORBA.Any)
	 */
	public Message create(Any any) {
		CallRequest req = new CallRequest();
		callreq creq = callreqHelper.extract(any);
		req.setClassname(creq.classname);
		//req.setExpect(creq.);
		//req.setInstantiation(creq.instantiation);
		req.setMethodname(creq.methodname);
		req.setSid(creq.sid);
		
		for(int i=0; i<creq.values.length; i++){
			//creq.values[i].;
			//req.addParam();
		}
		return req;
	}

}
