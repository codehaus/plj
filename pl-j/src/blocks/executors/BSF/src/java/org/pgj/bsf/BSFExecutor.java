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
import org.apache.bsf.util.BSFEngineImpl;
import org.pgj.Client;
import org.pgj.Executor;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Error;
import org.pgj.messages.Message;
import org.pgj.tools.classloaders.PLJClassLoader;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.TypeMapper;


/**
 * BSF executor component.
 * 
 * @author Laszlo Hornyak
 */
public class BSFExecutor implements Executor, Configurable, Serviceable, Initializable{

	HashMap managerMap = new HashMap();
	ScriptRepository loader = null;
	String tempDir = null;
	PLJClassLoader classLoader = null;

	/* (non-Javadoc)
	 * @see org.pgj.Executor#execute(org.pgj.messages.CallRequest)
	 */
	public Message execute(CallRequest call) {
		// TODO Auto-generated method stub
		
		Message result = null;
		try {
			Client cli = call.getClient();
			BSFManager manager = (BSFManager)managerMap.get(cli);
			Script script = loader.loadScript(call.getMethodname());
			Object ret = manager.eval(script.getLanguage(), script.getSource(), 0, 0, null);
			cli.getTypeMapper().backMap(ret);
		} catch (ScriptNotFoundException e) {
			result = throwableToErrorMsg(e);
		} catch (BSFException e) {
			result = throwableToErrorMsg(e);
		} catch (MappingException e) {
			result = throwableToErrorMsg(e);
		}
		
		return result;
	}

	private Error throwableToErrorMsg(Throwable t){
		Error error = new Error();
		error.setMessage(t.getClass().getName() + " " + t.getMessage());
		return error;
	}
	
	/* (non-Javadoc)
	 * @see org.pgj.Executor#initClientSession(org.pgj.Client)
	 */
	public void initClientSession(Client sessionClient) {
		managerMap.put(sessionClient, new BSFManager());
	}

	/* (non-Javadoc)
	 * @see org.pgj.Executor#destroyClientSession(org.pgj.Client)
	 */
	public void destroyClientSession(Client sessionClient) {
		managerMap.remove(sessionClient);
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		tempDir = arg0.getChild("tempDir").getValue();
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		loader = (ScriptRepository)arg0.lookup("scriptloader");
		classLoader = (PLJClassLoader)arg0.lookup("classloader");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		File f = new File(tempDir);
		f.mkdirs();
	}

}
