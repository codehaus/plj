package org.pgj.jexec;

import java.lang.reflect.InvocationTargetException;
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
import org.pgj.Client;
import org.pgj.ExecutionCancelException;
import org.pgj.Executor;
import org.pgj.TriggerExecutor;
import org.pgj.messages.CallRequest;
import org.pgj.messages.Message;
import org.pgj.messages.Result;
import org.pgj.messages.TriggerCallRequest;
import org.pgj.messages.TupleResult;
import org.pgj.tools.channelutil.ClientUtils;
import org.pgj.tools.classloaders.PLJClassLoader;
import org.pgj.tools.jdbc.JDBCConfigurator;
import org.pgj.tools.tuplemapper.TupleMapper;
import org.pgj.typemapping.Tuple;
import org.pgj.typemapping.TypeMapper;

/**
 * Executes java code as UDF or trigger.
 * 
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="java-executor"
 * @avalon.service type="org.pgj.Executor"
 * @avalon.service type="org.pgj.TriggerExecutor"
 */
public class JavaExecutor extends ClassLoader implements Executor,
		TriggerExecutor, Configurable, Serviceable, LogEnabled {

	/** avalon logger object */
	private Logger logger = null;

	/** class loader block */
	private PLJClassLoader classloader = null;

	/** reference to the type mapper block */
	private TypeMapper typemapper = null;

	private TupleMapper tupleMapper = null;

	public static final String FN_UTILITIES_CLASS = "org.pgj.jexec.Utils";

	/** A block that helps configuring JDBC. */
	private JDBCConfigurator jdbcConfigurator = null;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void configure(Configuration conf) throws ConfigurationException {
		logger.debug("configured");
	}

	public Message execute(CallRequest c) {

		Object obj;
		try {
			Class callclass = classloader.load(c.getClassname());

			Vector paramvector = c.getParams();
			org.pgj.typemapping.Field[] params = new org.pgj.typemapping.Field[paramvector
					.size()];

			Class[] paramclasses = new Class[params.length];

			for (int i = 0; i < params.length; i++) {
				params[i] = (org.pgj.typemapping.Field) paramvector.get(i);
				paramclasses[i] = params[i].getPreferredClass();
			}

			Method callm = null;

			callm = callclass.getMethod(c.getMethodname(), paramclasses);
			Object[] paramobjs = new Object[params.length];
			Object callobj = callclass.newInstance();

			for (int i = 0; i < params.length; i++) {
				paramobjs[i] = params[i].defaultGet();
			}
			Thread.currentThread().setContextClassLoader(
					new PGJClassLoaderAdapter(this.classloader));
			obj = callm.invoke(callobj, paramobjs);
			Thread.currentThread().setContextClassLoader(null);
			logger.debug("<----creating result");
			Result ret = typemapper.createResult(obj);
			logger.debug("---->created result");
			ret.setClient(c.getClient());
			return ret;
		} catch (InvocationTargetException t) {
			Throwable cause = t.getTargetException();
			if (cause instanceof ExecutionCancelException) {
				logger.debug("ExecutionCancelException");
				throw (ExecutionCancelException) cause;
			}
			org.pgj.messages.Error exc = createException(t.getCause());
			return exc;
		} catch (Throwable t) {
			org.pgj.messages.Error exc = createException(t);
			return exc;
		}

		finally {
			logger.debug("execution done.");
		}
	}

	private org.pgj.messages.Error createException(Throwable t) {
		org.pgj.messages.Error exc = new org.pgj.messages.Error();

		logger.debug("exception!", t);

		exc.setExceptionClassName(t.getClass().getName());
		exc.setMessage(t.getMessage());
		StringBuffer buf = new StringBuffer("<---- java executor stack trace\n");
		StackTraceElement[] trace = t.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			StackTraceElement el = trace[i];
			buf.append(el.getClassName()).append('.')
					.append(el.getMethodName()).append(" at ").append(
							el.getFileName()).append(":").append(
							el.getLineNumber()).append('\n');
		}
		buf.append("java executor stack trace --->\n");
		exc.setStackTrace(buf.toString());

		return exc;
	}

	/**
	 * @see Serviceable#service(ServiceManager)
	 * @avalon.dependency key="classloader"
	 *                    type="org.pgj.tools.classloaders.PLJClassLoader"
	 * @avalon.dependency key="type-mapper"
	 *                    type="org.pgj.typemapping.TypeMapper"
	 * @avalon.dependency key="tuple-mapper"
	 *                    type="org.pgj.tools.tuplemapper.TupleMapper"
	 *                    optional="true"
	 * @avalon.dependency key="jdbc-configurator"
	 *                    type="org.pgj.tools.jdbc.JDBCConfigurator"
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		classloader = (PLJClassLoader) arg0.lookup("classloader");
		typemapper = (TypeMapper) arg0.lookup("type-mapper");
		try {
			tupleMapper = (TupleMapper) arg0.lookup("tuple-mapper");
		} catch (ServiceException e) {
			logger
					.warn("I got no tuplemapper, i won`t be able to run triggers.");
		}
		jdbcConfigurator = (JDBCConfigurator) arg0.lookup("jdbc-configurator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.TriggerExecutor#executeTrigger(org.pgj.messages.TriggerCallRequest)
	 */
	public Message executeTrigger(TriggerCallRequest trigger) {
		try {
			logger.debug("executing trigger --> ");

			Tuple tpl = trigger.getReason() == TriggerCallRequest.TRIGGER_REASON_DELETE ? trigger
					.getOld()
					: trigger.getNew();
			Class pcl = tupleMapper.getMappedClass(tpl);
			logger.debug("class" + pcl);
			Class[] paramClasses = null;

			if (trigger.getRowmode() == TriggerCallRequest.TRIGGER_ROWMODE_ROW) {
				switch (trigger.getReason()) {
				case TriggerCallRequest.TRIGGER_REASON_UPDATE:
					paramClasses = new Class[2];
					paramClasses[0] = pcl;
					paramClasses[1] = pcl;
					break;
				case TriggerCallRequest.TRIGGER_REASON_DELETE:
				case TriggerCallRequest.TRIGGER_REASON_INSERT:
					paramClasses = new Class[1];
					paramClasses[0] = pcl;
					break;
				}
			} else {
				logger.debug("statement trigger");
				paramClasses = new Class[0];
			}

			Class triggerClass = classloader.load(trigger.getClassname());
			Method triggerMethod = triggerClass.getDeclaredMethod(trigger
					.getMethodname(), paramClasses);
			Object triggerObj = triggerClass.newInstance();
			Object[] paramObjects = null;

			if (trigger.getRowmode() == TriggerCallRequest.TRIGGER_ROWMODE_ROW) {
				switch (trigger.getReason()) {
				case TriggerCallRequest.TRIGGER_REASON_UPDATE:
					paramObjects = new Object[2];
					paramObjects[0] = tupleMapper.mapTuple(trigger.getNew());
					paramObjects[1] = tupleMapper.mapTuple(trigger.getOld());
					break;
				case TriggerCallRequest.TRIGGER_REASON_DELETE:
					paramObjects = new Object[1];
					paramObjects[0] = tupleMapper.mapTuple(trigger.getOld());
					break;
				case TriggerCallRequest.TRIGGER_REASON_INSERT:
					paramObjects = new Object[1];
					paramObjects[0] = tupleMapper.mapTuple(trigger.getNew());
					break;
				}
			} else {
				paramObjects = new Object[0];
			}

			Object retObj = triggerMethod.invoke(triggerObj, paramObjects);
			Tuple t = tupleMapper.backMap(retObj, typemapper);

			TupleResult res = new TupleResult();
			res.setTuple(t);
			res.setClient(trigger.getClient());
			return res;
		} catch (InvocationTargetException e) {
			return createException(e);
		} catch (Throwable t) {
			//TODO it is very dangerous to catch all throwables here...
			return createException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Executor#initClientSession(org.pgj.Client)
	 */
	public void initClientSession(Client sessionClient) {
		/*
		 * if the client is not set, so this is a new call from a client we must
		 * set the Client object for this thread, and unset it after the call is
		 * done. (see finally block)
		 */
		ClientUtils.setClientforThread(sessionClient);
		Configuration conf = jdbcConfigurator.getJDBCConfiguration();
		org.pgj.tools.utils.JDBCUtil.setConfiguration(conf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Executor#destroyClientSession(org.pgj.Client)
	 */
	public void destroyClientSession(Client sessionClient) {
		ClientUtils.setClientforThread(null);
		org.pgj.tools.utils.JDBCUtil.setConfiguration(null);
	}

}