
package org.codehaus.plj.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.plj.Client;
import org.codehaus.plj.ExecutionCancelException;
import org.codehaus.plj.Runner;
import org.codehaus.plj.TriggerExecutor;
import org.codehaus.plj.TupleMapper;
import org.codehaus.plj.java.method.MethodFinder;
import org.codehaus.plj.messages.CallRequest;
import org.codehaus.plj.messages.Message;
import org.codehaus.plj.messages.Result;
import org.codehaus.plj.messages.TriggerCallRequest;
import org.codehaus.plj.messages.TupleResult;
import org.codehaus.plj.typemapping.Tuple;
import org.codehaus.plj.typemapping.TypeMapper;
import org.codehaus.plj.utils.ClientUtils;
import org.codehaus.plj.utils.JDBCUtil;
import org.codehaus.plj.utils.classloaders.PGJClassLoaderAdapter;
import org.codehaus.plj.utils.classloaders.PLJClassLoader;

/**
 * Executes java code as UDF or trigger.
 * 
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="java-executor"
 * @avalon.service type="org.pgj.Executor"
 * @avalon.service type="org.pgj.TriggerExecutor"
 * 
 * @dna.component
 * @dna.service type="org.pgj.Executor"
 * @dna.service type="org.pgj.TriggerExecutor"
 * 
 */
public class JavaExecutor extends ClassLoader
		implements
			Runner,
			TriggerExecutor {

	private Map privilegedCalls = new HashMap();
	private Map jdbcConfiguration = new HashMap();

	/** avalon logger object */
	private final static Logger logger = Logger.getLogger(JavaExecutor.class);

	/** class loader block */
	protected PLJClassLoader classloader = null;

	/** reference to the type mapper block */
	private TypeMapper typemapper = null;

	private TupleMapper tupleMapper = null;

	private MethodFinder methodFinder = null;

	public static final String FN_UTILITIES_CLASS = "org.pgj.jexec.Utils";

//	/** A block that helps configuring JDBC. */
//	private JDBCConfigurator jdbcConfigurator = null;

	public Message execute(CallRequest c) {

		Object obj = null;
		try {

			//privileged call execution
			if ("#privileged-class".equals(c.getClassname().trim())) {
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
			Result ret = typemapper.createResult(obj, c.getExpect(), false);
			logger.debug("---->created result");
			ret.setClient(c.getClient());
			return ret;

		} catch (InvocationTargetException t) {
			Throwable cause = t.getTargetException();
			if (cause instanceof ExecutionCancelException) {
				logger.debug("ExecutionCancelException");
				throw (ExecutionCancelException) cause;
			}
			org.codehaus.plj.messages.Error exc = createException(t.getCause());
			return exc;
		} catch (Throwable t) {
			org.codehaus.plj.messages.Error exc = createException(t);
			return exc;
		}

		finally {
			logger.debug("execution done.");
		}
	}

	private org.codehaus.plj.messages.Error createException(Throwable t) {
		org.codehaus.plj.messages.Error exc = new org.codehaus.plj.messages.Error();

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
		JDBCUtil.setConfiguration(this.jdbcConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Executor#destroyClientSession(org.pgj.Client)
	 */
	public void destroyClientSession(Client sessionClient) {
		ClientUtils.setClientforThread(null);
		JDBCUtil.setConfiguration(null);
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		privilegedCalls.put("install_jar", new InstallJar(this));
		privilegedCalls.put("replace_jar", new ReplaceJar(this));
		privilegedCalls.put("remove_jar", new RemoveJar(this));
		privilegedCalls.put("get_default_mapping",
				new GetDefaultMappingForClass(this));
		privilegedCalls.put("alter_java_path", null);
	}

	public Map getJdbcConfiguration() {
		return jdbcConfiguration;
	}

	public void setJdbcConfiguration(Map jdbcConfiguration) {
		this.jdbcConfiguration = jdbcConfiguration;
	}

}