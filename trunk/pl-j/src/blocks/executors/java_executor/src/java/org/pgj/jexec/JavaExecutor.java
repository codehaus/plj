package org.pgj.jexec;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Vector;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.pgj.Executor;
import org.pgj.classloaders.pgjClassLoader;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Message;
import org.pgj.messages.Result;
import org.pgj.typemapping.TypeMapper;

/**
 * @author Laszlo Hornyak
 * 
 * Executes java code. 
 * 
 * @avalon.component name="java-executor"
 * @avalon.service type="org.pgj.Executor"
 */
public class JavaExecutor
	extends ClassLoader
	implements Executor, Configurable, Serviceable, LogEnabled {

	/** avalon logger object */
	Logger logger = null;

	/** class loader block */
	pgjClassLoader classloader = null;

	/** reference to the type mapper block */
	TypeMapper typemapper = null;

	public static final String FN_UTILITIES_CLASS = "org.pgj.jexec.Utils";

	String tempDirectory = null;
	File tempDirectoryFile = null;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void configure(Configuration conf) throws ConfigurationException {
		tempDirectory = conf.getChild("tempDir").getValue();
		tempDirectoryFile = new File(tempDirectory);
		if (!tempDirectoryFile.exists())
			tempDirectoryFile.mkdirs();
		logger.debug("configured");
	}

	public Message execute(CallRequest c) {

		try {

			//log
			logger.debug(c.getClassname());
			logger.debug(c.getMethodname());
			logger.debug(c.getExpect());

			//---
			// find class
			//---
			Class callclass = classloader.load(c.getClassname());

			//---
			// find a method
			//---
			Vector paramvector = c.getParams();
			org.pgj.typemapping.Field[] params =
				new org.pgj.typemapping.Field[paramvector.size()];

			Class[] paramclasses = new Class[params.length];

			//in the first step, try to use the preferred types.
			for (int i = 0; i < params.length; i++) {
				params[i] = (org.pgj.typemapping.Field) paramvector.get(i);
				paramclasses[i] = params[i].getPreferredClass();
			}

			Method callm = null;

			callm = callclass.getMethod(c.getMethodname(), paramclasses);
			Object[] paramobjs = new Object[params.length];
			Object callobj = callclass.newInstance();

			//---
			// invoke method
			//---
			Object obj;
			try {
				obj = callm.invoke(callobj, paramobjs);
			} catch (Throwable t) {
				org.pgj.messages.Exception exc = createException(t);
				return exc;
			}

			logger.debug("<----creating result");
			Result ret = typemapper.createResult(obj);
			logger.debug("---->created result");
			// TODO this shoul not be here!
			ret.setSid(c.getSid());
			ret.setClient(c.getClient());

			return ret;
		} catch (java.lang.Exception e){
			org.pgj.messages.Exception exc = createException(e);
			return exc;
			
		} finally {
			logger.debug("execution done.");
		}
	}

	private org.pgj.messages.Exception createException(Throwable t) {
		org.pgj.messages.Exception exc = new org.pgj.messages.Exception();

		logger.debug("exception!");

		exc.setExceptionClassName(t.getClass().getName());
		exc.setMessage(t.getMessage());
		StringBuffer buf =
			new StringBuffer("<---- java executor stack trace\n");
		StackTraceElement[] trace = t.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			StackTraceElement el = trace[i];
			buf
				.append(el.getClassName())
				.append('.')
				.append(el.getMethodName())
				.append(" at ")
				.append(el.getFileName())
				.append(":")
				.append(el.getLineNumber())
				.append('\n');
		}
		buf.append("java executor stack trace --->\n");
		exc.setStackTrace(buf.toString());

		return exc;
	}

	/**
	 * @see Serviceable#service(ServiceManager)
	 * @avalon.dependency key="classloader" type="org.pgj.classloaders.pgjClassLoader"
	 * @avalon.dependency key="type-mapper" type="org.pgj.typemapping.TypeMapper"
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		classloader =
			(pgjClassLoader) arg0.lookup("classloader");
		typemapper = (TypeMapper) arg0.lookup("type-mapper");
	}

	private String compile(String text) {

		return null;
	}

	private void compile_and_store(String[] text) {

	}

	private void compile_and_store(String text) {

	}

	private void store(String name, byte[] classdata) {
		classloader.store(name, classdata);
	}

}