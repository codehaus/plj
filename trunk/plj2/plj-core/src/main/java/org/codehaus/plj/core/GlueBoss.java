package org.codehaus.plj.core;

import org.apache.log4j.Logger;
import org.codehaus.plj.Channel;
import org.codehaus.plj.Client;
import org.codehaus.plj.Runner;
import org.codehaus.plj.TriggerExecutor;

import com.sun.corba.se.internal.orbutil.ThreadPool;
import com.sun.jndi.ldap.pool.Pool;

/**
 * The logic that wakes up glue workers.
 * This logic runs as in a separate thread and listens the chanell for inclmming connections.
 * For all connections, it allocates a worker that will deal with the connection.
 * 
 * @author Laszlo Hornyak
 */
public class GlueBoss implements Runnable {

	/** Reference ot the chanell we are dealing with. */
	private Channel chanell = null;
	/** Logger for the Glue component. */
	private Logger logger = null;
	/** flag to show if we are terminating */
	private boolean terminating = false;
	/** the thread pool to use for workers */
	private ThreadPool threadPool = null;
	/** Worker pool */
	private Pool workerPool = null;
	/** Reference to the executor component. */
	private Runner executor;
	/** Reference to the executor component. */
	private TriggerExecutor triggerExecutor;

	/**
	 * Constructor for GlueBoss.
	 */
	public GlueBoss() {
		super();
	}

	/**
	 * @see Runnable#run()
	 */
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			logger.fatal("execution", e);
		}
	}

	/**
	 * @see Executable#execute()
	 */
	public void execute() throws Exception {
		try{
			if(logger.isInfoEnabled())
				logger.debug("Client session acceptor thread starting.");
			while (!terminating) {
				Client cli = null;
				do {
					cli = chanell.getConnection(-1);
					if(logger.isInfoEnabled())
						logger.debug("New client session:"+cli);

					if (terminating)
						return;
				} while (cli == null);

				GlueWorker worker = //(GlueWorker) workerPool.get();
					new GlueWorker();
				worker.setExecutor(executor);
				worker.setChanell(chanell);
				worker.setClient(cli);

				new Thread(worker).run();
//				threadPool.execute(worker);

			}
		} finally {
			if(logger.isInfoEnabled())
				logger.debug("Client session acceptor thread stoping.");
		}
	}

	/**
	 * Gets the chanell.
	 * @return Returns a Channel
	 */
	protected Channel getChanell() {
		return chanell;
	}

	/**
	 * Sets the chanell.
	 * @param chanell The chanell to set
	 */
	protected void setChanell(Channel chanell) {
		this.chanell = chanell;
	}

	/**
	 * @see LogEnabled#enableLogging(Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/**
	 * @see Startable#start()
	 */
	public void start() throws Exception {
	}

	/**
	 * @see Startable#stop()
	 */
	public void stop() throws Exception {
		terminating = true;
	}

	/**
	 * Gets the threadPool.
	 * @return Returns a ThreadPool
	 */
	protected ThreadPool getThreadPool() {
		return threadPool;
	}

	/**
	 * Sets the threadPool.
	 * @param threadPool The threadPool to set
	 */
	protected void setThreadPool(ThreadPool threadPool) {
		this.threadPool = threadPool;
	}

	/**
	 * Gets the workerPool.
	 * @return Returns a Pool
	 */
	protected Pool getWorkerPool() {
		return workerPool;
	}

	/**
	 * Sets the workerPool.
	 * @param workerPool The workerPool to set
	 */
	protected void setWorkerPool(Pool workerPool) {
		this.workerPool = workerPool;
	}

	/**
	 * Gets the executor.
	 * @return Returns a Executor
	 */
	protected Runner getExecutor() {
		return executor;
	}

	/**
	 * Sets the executor.
	 * @param executor The executor to set
	 */
	protected void setExecutor(Runner executor) {
		this.executor = executor;
	}

	/**
	 * @return Returns the triggerExecutor.
	 */
	protected TriggerExecutor getTriggerExecutor() {
		return triggerExecutor;
	}
	/**
	 * @param triggerExecutor The triggerExecutor to set.
	 */
	protected void setTriggerExecutor(TriggerExecutor triggerExecutor) {
		this.triggerExecutor = triggerExecutor;
	}
}