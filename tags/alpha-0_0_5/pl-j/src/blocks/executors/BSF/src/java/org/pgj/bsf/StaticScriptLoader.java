/*
 * Created on Oct 16, 2004
 */
package org.pgj.bsf;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * A static Script repository that loads scripts from the configuration and does not store it.
 * @author Laszlo Hornyak
 */
public class StaticScriptLoader implements ScriptRepository, Configurable {

	private Map scriptMap = new HashMap();

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptRepository#loadScript(java.lang.String)
	 */
	public Script loadScript(String name) throws ScriptNotFoundException {
		Script scr = (Script)scriptMap.get(name);
		if(scr == null)
			throw new ScriptNotFoundException("Script "+name+ " not found");
		return scr;
	}

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptRepository#storeScript(org.pgj.bsf.Script)
	 */
	public void storeScript(Script script) throws ScriptStoreException {
		throw new ScriptStoreException("Cannot delete from config");
	}

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptRepository#deleteScript(java.lang.String)
	 */
	public void deleteScript(String name) throws ScriptStoreException, ScriptNotFoundException {
		throw new ScriptStoreException("Cannot delete from config");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		Configuration[] scripts = arg0.getChildren("JSProc");
		for(int i=0; i<scripts.length; i++){
			String lang = scripts[i].getAttribute("language");
			String src = scripts[i].getValue();
			String name = scripts[i].getAttribute("name");
			Script script = new Script(lang, src);
			scriptMap.put(name, script);
		}
	}

}
