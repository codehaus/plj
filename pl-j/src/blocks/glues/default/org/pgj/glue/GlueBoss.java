package org.pgj.glue;

import org.pgj.*;
import org.apache.avalon.framework.activity.*;
import org.apache.avalon.framework.logger.*;
import org.apache.avalon.excalibur.pool.*;
import org.apache.avalon.excalibur.thread.*;

/**
 * @author bitfakk
 *
 * The logic that wakes up glue workers.
 * This logic runs as in a separate thread and listens the chanell for inclmming connections.
 * For all connections, it allocates a worker that will deal with the connection.
 */
public class GlueBoss implements Executable, LogEnabled, Startable {

	/** Reference ot the chanell we are dealing with. */
	Channel chanell = null;
	/** Logger for the Glue component. */
	Logger logger = null;
	/** flag to show if we are terminating */
	boolean terminating = false;
	/** the thread pool to use for workers */
	ThreadPool threadPool = null;
	/** Worker pool */
	Pool workerPool = null;
	/** Reference to the executor component. */
	Executor executor;

	/**
	 * Constructor for GlueBoss.
	 */
	public GlueBoss() {
		super();
	}

	/**
	 * @see Runnable#run()
	 */
	public void run() throws Exception{
		execute();
	}

	/**
	 * @see Executable#execute()
	 */
	public void execute() throws Exception {
		try {
			while (!terminating) {
				Client cli = null;
				do {
					cli = chanell.getConnection(-1);
					logger.debug("I HAVE A CIENT:"+cli);
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

}