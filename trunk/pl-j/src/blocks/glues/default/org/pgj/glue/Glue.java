package org.pgj.glue;

import org.apache.avalon.excalibur.thread.*;
import org.apache.avalon.excalibur.thread.impl.DefaultThreadPool;
import org.apache.avalon.framework.activity.*;
import org.apache.avalon.framework.service.*;
import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.avalon.framework.configuration.*;
import org.apache.avalon.framework.logger.*;
import org.apache.avalon.excalibur.pool.*;

import org.pgj.*;

/**
 * Glue is the glue component, containing worker threads.
 */
public class Glue
	implements Configurable, Initializable, Serviceable, Startable, LogEnabled {

	/** A Thread pool to enable pooling capabilities for workers. */
	ThreadPool threadPool = null;
	/** the configured capacity of the Thread pool */
	int configThreadPoolCapacity = 10;
	/** Workers are pooled here. */
	Pool workerPool = null;
	/** Factory that creates workers */
	GlueWorkerFactory gwfactory = null;
	/** flag to show if the component is terminating */
	boolean terminating = false;
	/** The glue boss */
	GlueBoss gb;

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

	//
	//from Initializable
	//
	public void initialize() throws Exception {
		try {
			logger.debug("initializing");

			ThreadContext ctx = ThreadContext.getThreadContext();

			threadPool = new DefaultThreadPool("Glue pool", 10, ctx);

			((DefaultThreadPool) threadPool).enableLogging(logger);

			gwfactory = new GlueWorkerFactory();
			gwfactory.enableLogging(logger);

			workerPool = new DefaultPool(gwfactory);
			((DefaultPool) workerPool).enableLogging(logger);
		} catch (Exception t) {
			t.printStackTrace();
			//throw t;
		} catch (Throwable t) {
			logger.debug("hmmm...");
			logger.debug("error:", t);
		} finally {
			logger.debug("done");
		}
	}

	//
	//from Startable
	//
	public void start() throws Exception {
		try {

			gb = new GlueBoss();
			gb.enableLogging(logger);
			gb.setChanell(chanell);
			gb.setThreadPool(threadPool);
			gb.setWorkerPool(workerPool);
			gb.setExecutor(executor);

			threadPool.execute(gb);

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			logger.debug("started");
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
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		chanell = (Channel) arg0.lookup("org.pgj.Channel");
		executor = (Executor) arg0.lookup("org.pgj.Executor");

		logger.debug("serviced");
	}

}