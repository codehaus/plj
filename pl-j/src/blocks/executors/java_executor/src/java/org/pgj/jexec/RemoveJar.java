/*
 * Created on Oct 17, 2004
 */

package org.pgj.jexec;

import org.pgj.messages.CallRequest;
import org.pgj.tools.classloaders.ClassStoreException;
import org.pgj.tools.classloaders.PLJClassLoader;
import org.pgj.typemapping.Field;


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