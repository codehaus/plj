package org.pgj.glue;

import org.apache.avalon.excalibur.pool.DefaultPool;
import org.apache.avalon.excalibur.pool.Pool;
import org.apache.avalon.excalibur.thread.impl.DefaultThreadPool;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.thread.ThreadPool;
import org.pgj.Channel;
import org.pgj.Executor;
import org.pgj.TriggerExecutor;
import org.pgj.tools.transactions.JTAAdapter;

/**
 * Glue is the glue component, containing worker threads.
 * 
 * @avalon.component name="glue" lifestyle="singleton"
 * 
 * @dna.component
 * 
 */
public class Glue implements Configurable, Initializable, Serviceable,
		Startable, LogEnabled {

	/** A Thread pool to enable pooling capabilities for workers. */
	private ThreadPool threadPool = null;
	
	/**
	 * Glue configuration object.
	 */
	private GlueConfiguration gConf = null;

	/** the configured capacity of the Thread pool */
	private int configThreadPoolCapacity = 10;

	/** Workers are pooled here. */
	private Pool workerPool = null;

	/** Factory that creates workers */
	private GlueWorkerFactory gwfactory = null;

	/** JTA Adapter. May be null, as it is an optional resource. */
	private JTAAdapter jtaAdatper = null;
	
	/** flag to show if the component is terminating */
	private boolean terminating = false;

	/** The glue boss */
	private GlueBoss gb;

	/** The chanell we are dealing with */
	private Channel chanell = null;

	/** The executor we are dealing with */
	private Executor executor = null;

	/** The trigger executor, if it differs from the call executor */
	private TriggerExecutor trigexecutor = null;

	//
	//from Configurable
	//
	public void configure(Configuration conf) throws ConfigurationException {
		gConf = new GlueConfiguration();
		gConf.setErrorRecoverable(conf.getChild("errorRecoverable").getValueAsBoolean());
		logger.debug("configured");
	}

	public void initialize() throws Exception {
		logger.debug("initializing");
		threadPool = new DefaultThreadPool("Glue pool", 10, 5);
		((DefaultThreadPool) threadPool).enableLogging(logger);
		gwfactory = new GlueWorkerFactory();
		gwfactory.setLogger(logger);
		gwfactory.setGConf(gConf);
		workerPool = new DefaultPool(gwfactory);
		((DefaultPool) workerPool).enableLogging(logger);
	}

	public void start() throws Exception {
		try {
			gb = new GlueBoss();
			gb.enableLogging(logger);
			gb.setChanell(chanell);
			gb.setThreadPool(threadPool);
			gb.setWorkerPool(workerPool);
			gb.setExecutor(executor);
			gb.setTriggerExecutor(trigexecutor);

			threadPool.execute(gb);

			logger.debug("started");
		} catch (Throwable t) {
			logger.fatalError("glue threads cannot start", t);
		}
	}

	public void stop() throws Exception {
		terminating = true;
		logger.debug("asked to stop");
		synchronized (gb) {
			gb.stop();
			gb.notify();
		}
	}

	//
	//from LogEnabled
	//
	Logger logger = null;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @see Serviceable#service(ServiceManager)
	 * @avalon.dependency key="channel" type="org.pgj.Channel"
	 * @avalon.dependency key="executor" type="org.pgj.Executor"
	 * @avalon.dependency key="triggerexecutor" type="org.pgj.TriggerExecutor" optional="true"
	 * @avalon.dependency key="jta-adapter" type="org.pgj.tools.transactions.JTAAdapter" optional="true"
	 * 
	 * @dna.dependency key="channel" type="org.pgj.Channel"
	 * @dna.dependency key="executor" type="org.pgj.Executor"
	 * @dna.dependency key="triggerexecutor" type="org.pgj.TriggerExecutor" optional="true"
	 * @dna.dependency key="jta-adapter" type="org.pgj.tools.transactions.JTAAdapter" optional="true"
	 * 
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		chanell = (Channel) arg0.lookup("channel");
		executor = (Executor) arg0.lookup("executor");
		try {
			trigexecutor = (TriggerExecutor) arg0.lookup("triggerexecutor");
		} catch (ServiceException e) {
			if (!(executor instanceof TriggerExecutor))
				throw new ServiceException(
						"triggerexecutor should be provided, or executor must implement TriggerExecutor",
						"");
			trigexecutor = (TriggerExecutor) executor;
		}
		logger.debug("serviced");
	}
	
}