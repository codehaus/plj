
package org.pgj.jexec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.activity.Initializable;
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
import org.pgj.tools.classloaders.PLJClassLoader;
import org.pgj.tools.jdbc.JDBCConfigurator;
import org.pgj.tools.methodfinder.MethodFinder;
import org.pgj.tools.tuplemapper.TupleMapper;
import org.pgj.tools.utils.ClientUtils;
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
public class JavaExecutor extends ClassLoader
		implements
			Executor,
			TriggerExecutor,
			Configurable,
			Serviceable,
			LogEnabled,
			Initializable {

	private Map privilegedCalls = new HashMap();

	/** avalon logger object */
	private Logger logger = null;

	/** class loader block */
	protected PLJClassLoader classloader = null;

	/** reference to the type mapper block */
	private TypeMapper typemapper = null;

	private TupleMapper tupleMapper = null;

	private MethodFinder methodFinder = null;

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

		Object obj = null;
		try {

			//privileged call execution
			if ("#privileged-class".equals(c.getClassname())) {
				PrivilegedJSProc proc = (PrivilegedJSProc) this.privilegedCalls
						.get(c.getMethodname());
				if (proc == null)
					throw new NoSuchMethodError(
							"privileged call not supported:"
									+ c.getMethodname());
				proc.perform(c);
				obj = "OK";
			} else {
				Class callclass = classloader.load(c.getClassname());

				Method callm = methodFinder.findMethod(c, callclass);
				Object[] paramobjs = methodFinder.createParameters(c, callm);
				Thread.currentThread().setContextClassLoader(
						new PGJClassLoaderAdapter(this.classloader));
				obj = callm.invoke(null, paramobjs);

				Thread.currentThread().setContextClassLoader(null);
			}
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
	 * @avalon.dependency key="method-finder"
	 *                    type="org.pgj.tools.methodfinder.MethodFinder"
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		classloader = (PLJClassLoader) arg0.lookup("classloader");
		typemapper = (TypeMapper) arg0.lookup("type-mapper");
		methodFinder = (MethodFinder) arg0.lookup("method-finder");
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

			Class triggerClass = classloader.load(trigger.getClassname());
			Method triggerMethod = methodFinder.findMethod(trigger,
					triggerClass);
			Object[] paramObjects = methodFinder.createParameters(trigger,
					triggerMethod);

			Object retObj = triggerMethod.invoke(null, paramObjects);
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

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		privilegedCalls.put("install_jar", null);
		privilegedCalls.put("replace_jar", null);
		privilegedCalls.put("remove_jar", null);
		privilegedCalls.put("alter_java_path", null);
	}

}