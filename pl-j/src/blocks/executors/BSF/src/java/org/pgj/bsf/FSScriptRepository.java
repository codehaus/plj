/*
 * Created on Oct 16, 2004
 */
package org.pgj.bsf;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;


/**
 * File system script store.
 * 
 * @author Laszlo Hornyak
 */
public class FSScriptRepository
		implements
			ScriptRepository,
			LogEnabled,
			Initializable,
			Configurable {

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptRepository#loadScript(java.lang.String)
	 */
	public Script loadScript(String name) throws ScriptNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptRepository#storeScript(org.pgj.bsf.Script)
	 */
	public void storeScript(Script script) throws ScriptStoreException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptRepository#deleteScript(java.lang.String)
	 */
	public void deleteScript(String name) throws ScriptStoreException,
			ScriptNotFoundException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		// TODO Auto-generated method stub

	}

}
