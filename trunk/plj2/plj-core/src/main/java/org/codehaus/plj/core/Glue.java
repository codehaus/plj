package org.codehaus.plj.core;


import org.apache.log4j.Logger;
import org.codehaus.plj.Channel;
import org.codehaus.plj.JTAAdapter;
import org.codehaus.plj.Runner;
import org.codehaus.plj.TriggerExecutor;


/**
 * Glue is the glue component, containing worker threads.
 * 
 * @avalon.component name="glue" lifestyle="singleton"
 * 
 * @dna.component
 * 
 */
public class Glue {

	private final static Logger logger = Logger.getLogger(Glue.class);

	
	/**
	 * Glue configuration object.
	 */
	private GlueConfiguration gConf = null;

//	/** Factory that creates workers */
//	private GlueWorkerFactory gwfactory = null;

	/** JTA Adapter. May be null, as it is an optional resource. */
	private JTAAdapter jtaAdatper = null;
	
	/** flag to show if the component is terminating */
	private boolean terminating = false;

	/** The glue boss */
	private GlueBoss gb;

	/** The chanell we are dealing with */
	private Channel channel = null;

	/** The executor we are dealing with */
	private Runner executor = null;

	/** The trigger executor, if it differs from the call executor */
	private TriggerExecutor trigexecutor = null;

	public void initialize() throws Exception {
		logger.debug("initializing");
//		threadPool = new DefaultThreadPool("Glue pool", 10, 5);
//		((DefaultThreadPool) threadPool).enableLogging(logger);
//		gwfactory = new GlueWorkerFactory();
//		gwfactory.setLogger(logger);
//		gwfactory.setGConf(gConf);
//		workerPool = new DefaultPool(gwfactory);
//		((DefaultPool) workerPool).enableLogging(logger);
	}

	public void start() throws Exception {
			gb = new GlueBoss();
			gb.setChanell(channel);
			gb.setExecutor(executor);
			gb.setTriggerExecutor(trigexecutor);

			new Thread(gb).run();

			logger.debug("started");
	}

	public void stop() throws Exception {
		terminating = true;
		logger.debug("asked to stop");
		synchronized (gb) {
			gb.stop();
			gb.notify();
		}
	}


	public Channel getChanell() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Runner getExecutor() {
		return executor;
	}

	public void setExecutor(Runner executor) {
		this.executor = executor;
	}

	public TriggerExecutor getTrigexecutor() {
		return trigexecutor;
	}

	public void setTrigexecutor(TriggerExecutor trigexecutor) {
		this.trigexecutor = trigexecutor;
	}

//	/**
//	 * @see Serviceable#service(ServiceManager)
//	 * @avalon.dependency key="channel" type="org.pgj.Channel"
//	 * @avalon.dependency key="executor" type="org.pgj.Executor"
//	 * @avalon.dependency key="triggerexecutor" type="org.pgj.TriggerExecutor" optional="true"
//	 * @avalon.dependency key="jta-adapter" type="org.pgj.tools.transactions.JTAAdapter" optional="true"
//	 * 
//	 * @dna.dependency key="channel" type="org.pgj.Channel"
//	 * @dna.dependency key="executor" type="org.pgj.Executor"
//	 * @dna.dependency key="triggerexecutor" type="org.pgj.TriggerExecutor" optional="true"
//	 * @dna.dependency key="jta-adapter" type="org.pgj.tools.transactions.JTAAdapter" optional="true"
//	 * 
//	 */
//	public void service(ServiceManager arg0) throws ServiceException {
//		chanell = (Channel) arg0.lookup("channel");
//		executor = (Executor) arg0.lookup("executor");
//		try {
//			trigexecutor = (TriggerExecutor) arg0.lookup("triggerexecutor");
//		} catch (ServiceException e) {
//			if (!(executor instanceof TriggerExecutor))
//				throw new ServiceException(
//						"triggerexecutor should be provided, or executor must implement TriggerExecutor",
//						"");
//			trigexecutor = (TriggerExecutor) executor;
//		}
//		logger.debug("serviced");
//	}
	
}