/*
 * Created on Oct 17, 2004
 */

package org.codehaus.plj.java;

import org.codehaus.plj.messages.CallRequest;
import org.codehaus.plj.typemapping.Field;
import org.codehaus.plj.utils.classloaders.ClassStoreException;
import org.codehaus.plj.utils.classloaders.PLJClassLoader;



/**
 * @author Laszlo Hornyak
 */
//TODO: edit comments for RemoveJar
public class RemoveJar extends BasicPrivilegedJSProc {

	/**
	 * @param je
	 */
	protected RemoveJar(JavaExecutor je) {
		super(je);
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#getName()
	 */
	public String getName() {
		return "remove_jar";
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#perform(org.pgj.messages.CallRequest)
	 */
	public Object perform(CallRequest call) throws Exception {
		String jar = (String) ((Field) call.getParams().get(0))
				.get(String.class);
		return doRemoveJar(javaExecutor.classloader, jar);
	}

	static Object doRemoveJar(PLJClassLoader classLoader, String jar) throws ClassStoreException {
		classLoader.removeJar(jar);
		return null;
	}

}