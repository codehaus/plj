/*
 * Created on Oct 13, 2004
 */

package org.pgj.bsf;

import java.io.File;
import java.util.HashMap;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.pgj.Client;
import org.pgj.Executor;
import org.pgj.TriggerExecutor;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Error;
import org.pgj.messages.Message;
import org.pgj.messages.TriggerCallRequest;
import org.pgj.tools.classloaders.PGJClassLoaderAdapter;
import org.pgj.tools.classloaders.PLJClassLoader;
import org.pgj.tools.jdbc.JDBCConfigurator;
import org.pgj.tools.utils.ClientUtils;
import org.pgj.typemapping.MappingException;


/**
 * BSF executor component.
 * 
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="bsf-executor" lifestyle="singleton"
 * @avalon.service type="org.pgj.Executor"
 * @avalon.service type="org.pgj.TriggerExecutor"
 * 
 * @dna.component
 * @dna.service type="org.pgj.Executor"
 * @dna.service type="org.pgj.TriggerExecutor"
 * 
 */
public class BSFExecutor
		implements
			Executor,
			Configurable,
			Serviceable,
			Initializable,
			TriggerExecutor {

	HashMap managerMap = new HashMap();
	ScriptRepository loader = null;
	String tempDir = null;
	PLJClassLoader classLoader = null;
	JDBCConfigurator jdbcConf = null;

	/* (non-Javadoc)
	 * @see org.pgj.Executor#execute(org.pgj.messages.CallRequest)
	 */
	public Message execute(CallRequest call) {
		Message result = null;
		try {
			Client cli = call.getClient();
			BSFManager manager = (BSFManager) managerMap.get(cli);
			if(manager == null){
				manager = new BSFManager();
				managerMap.put(cli, manager);
			}
			Script script = loader.loadScript(call.getMethodname());
			Object ret = manager.eval(script.getLanguage(), script.getSource(),
					0, 0, script.getSource());
			cli.getTypeMapper().backMap(ret);
		} catch (ScriptNotFoundException e) {
			result = throwableToErrorMsg(e);
		} catch (BSFException e) {
			result = throwableToErrorMsg(e);
		} catch (MappingException e) {
			result = throwableToErrorMsg(e);
		} catch (ScriptStoreException e) {
			result = throwableToErrorMsg(e);
		} finally {
		}

		return result;
	}

	private Error throwableToErrorMsg(Throwable t) {
		Error error = new Error();
		error.setExceptionClassName("");
		error.setMessage(t.getClass().getName() + " " + t.getMessage());
		return error;
	}

	/* (non-Javadoc)
	 * @see org.pgj.Executor#initClientSession(org.pgj.Client)
	 */
	public void initClientSession(Client sessionClient) {
		BSFManager manager = new BSFManager();
		manager.setClassLoader(new PGJClassLoaderAdapter(classLoader));
		managerMap.put(sessionClient, manager);
		ClientUtils.setClientforThread(sessionClient);
		Configuration conf = jdbcConf.getJDBCConfiguration();
		org.pgj.tools.utils.JDBCUtil.setConfiguration(conf);
	}

	/* (non-Javadoc)
	 * @see org.pgj.Executor#destroyClientSession(org.pgj.Client)
	 */
	public void destroyClientSession(Client sessionClient) {
		managerMap.remove(sessionClient);
		ClientUtils.setClientforThread(null);
		org.pgj.tools.utils.JDBCUtil.setConfiguration(null);
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		tempDir = arg0.getChild("tempDir").getValue();
		Configuration[] seConfs = arg0.getChildren("installEngine");
		for (int i = 0; i < seConfs.length; i++) {
			BSFManager.registerScriptingEngine(seConfs[i].getChild("name")
					.getValue(), seConfs[i].getChild("class").getValue(),
					new String[]{});
		}
	}

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 * 
	 * @avalon.dependency key="classloader" type="org.pgj.tools.classloaders.PLJClassLoader"
	 * @avalon.dependency key="jdbc-configurator"
	 *                    type="org.pgj.tools.jdbc.JDBCConfigurator"
	 * @avalon.dependency key="scriptloader" type="org.pgj.bsf.ScriptRepository"	
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		loader = (ScriptRepository) arg0.lookup("scriptloader");
		classLoader = (PLJClassLoader) arg0.lookup("classloader");
		jdbcConf = (JDBCConfigurator)arg0.lookup("jdbc-configurator");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		File f = new File(tempDir);
		f.mkdirs();
	}

	/* (non-Javadoc)
	 * @see org.pgj.TriggerExecutor#executeTrigger(org.pgj.messages.TriggerCallRequest)
	 */
	public Message executeTrigger(TriggerCallRequest trigger) {
		Message result = null;
		try {
			Client cli = trigger.getClient();
			BSFManager manager = (BSFManager) managerMap.get(cli);
			Script script = loader.loadScript(trigger.getMethodname());
			Object ret = manager.eval(script.getLanguage(), script.getSource(),
					0, 0, null);
			cli.getTypeMapper().backMap(ret);
		} catch (ScriptNotFoundException e) {
			result = throwableToErrorMsg(e);
		} catch (BSFException e) {
			result = throwableToErrorMsg(e);
		} catch (MappingException e) {
			result = throwableToErrorMsg(e);
		} catch (ScriptStoreException e) {
			result = throwableToErrorMsg(e);
		}

		return result;
	}

}