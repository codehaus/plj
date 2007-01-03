/*
 * Created on Oct 17, 2004
 */

package org.codehaus.plj.java;

import java.io.IOException;

import org.codehaus.plj.messages.CallRequest;
import org.codehaus.plj.typemapping.Field;
import org.codehaus.plj.utils.classloaders.ClassStoreException;
import org.codehaus.plj.utils.classloaders.PLJClassLoader;



/**
 * @author Laszlo Hornyak
 */
//TODO: edit comments for ReplaceJar
public class ReplaceJar extends BasicPrivilegedJSProc {

	/**
	 * @param je
	 */
	protected ReplaceJar(JavaExecutor je) {
		super(je);
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#getName()
	 */
	public String getName() {
		return "replace_jar";
	}

	/* (non-Javadoc)
	 * @see org.pgj.jexec.PrivilegedJSProc#perform(org.pgj.messages.CallRequest)
	 */
	public Object perform(CallRequest call) throws Exception {
		String jarFile = (String) ((Field) call.getParams().get(0))
				.get(String.class);
		String jarName = (String) ((Field) call.getParams().get(1))
				.get(String.class);
		return doReplaceJar(javaExecutor.classloader, jarName, jarFile);
	}

	static Object doReplaceJar(PLJClassLoader classLoader, String jarName,
			String jarFile) throws IOException, ClassStoreException {
		RemoveJar.doRemoveJar(classLoader, jarName);
		InstallJar.doInstallJar(classLoader, jarName, jarFile);
		return null;
	}
}