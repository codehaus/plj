/*
 * Created on Jun 6, 2005
 */
package org.pgj.jexec;

import java.util.Map;

import org.pgj.Client;
import org.pgj.messages.CallRequest;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.TypeMapper;


/**
 * Get default mapping for a given class.
 * @author Laszlo Hornyak
 */
public class GetDefaultMappingForClass extends BasicPrivilegedJSProc {

	/**
	 * @param je
	 */
	protected GetDefaultMappingForClass(JavaExecutor je) {
		super(je);
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#getName()
	 */
	public String getName() {
		return "default_mapping";
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#perform(org.pgj.messages.CallRequest)
	 */
	public Object perform(CallRequest call) throws Exception {
		Client cl =  call.getClient();
		TypeMapper tm = cl.getTypeMapper();
		Map mapping = tm.getClassMap();
		String clName = (String)((Field)call.getParams().get(0)).get(String.class);
		String ret = (String)mapping.get(clName);
		return ret;
	}

}
