package org.pgj.glue;

import org.apache.avalon.excalibur.pool.Pool;
import org.apache.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.Executor;
import org.pgj.TriggerExecutor;

/**
 * The logic that wakes up glue workers.
 * This logic runs as in a separate thread and listens the chanell for inclmming connections.
 * For all connections, it allocates a worker that will deal with the connection.
 * 
 * @author Laszlo Hornyak
 */
public class GlueBoss implements Runnable, LogEnabled, Startable {

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
	private Executor executor;
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
			logger.fatalError("execution", e);
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

				GlueWorker worker = (GlueWorker) workerPool.get();
				worker.setExecutor(executor);
				worker.setChanell(chanell);
				worker.setClient(cli);

				threadPool.execute(worker);

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
	protected Executor getExecutor() {
		return executor;
	}

	/**
	 * Sets the executor.
	 * @param executor The executor to set
	 */
	protected void setExecutor(Executor executor) {
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