package org.pgj.glue;

import org.apache.avalon.excalibur.pool.DefaultPool;
import org.apache.avalon.excalibur.pool.Pool;
import org.apache.excalibur.thread.ThreadPool;
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
import org.pgj.Channel;
import org.pgj.Executor;

/**
 * Glue is the glue component, containing worker threads. 
 * @avalon.component name="glue" lifestyle="singleton"
 */
public class Glue
		implements
			Configurable,
			Initializable,
			Serviceable,
			Startable,
			LogEnabled {

	/** A Thread pool to enable pooling capabilities for workers. */
	private ThreadPool threadPool = null;
	/** the configured capacity of the Thread pool */
	private int configThreadPoolCapacity = 10;
	/** Workers are pooled here. */
	private Pool workerPool = null;
	/** Factory that creates workers */
	private GlueWorkerFactory gwfactory = null;
	/** flag to show if the component is terminating */
	private boolean terminating = false;
	/** The glue boss */
	private GlueBoss gb;
	/** The chanell we are dealing with */
	Channel chanell = null;
	/** The executor we are dealing with */
	Executor executor = null;

	//
	//from Configurable
	//
	public void configure(Configuration conf) throws ConfigurationException {
		logger.debug("configured");
	}

	public void initialize() throws Exception {
		logger.debug("initializing");
		threadPool = new DefaultThreadPool("Glue pool", 10, 5);
		((DefaultThreadPool) threadPool).enableLogging(logger);
		gwfactory = new GlueWorkerFactory();
		gwfactory.enableLogging(logger);
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
	 * @see Serviceable#service(ServiceManager) @avalon.dependency
	 *      key="channel" type="org.pgj.Channel" @avalon.dependency
	 *      key="executor" type="org.pgj.Executor"
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		chanell = (Channel) arg0.lookup("channel");
		executor = (Executor) arg0.lookup("executor");
		logger.debug("serviced");
	}
}