package org.pgj.glue;

import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * @author Laszlo Hornyak
 *
 * Factory for GLUE workers. This class is for adopting factoring to excalibur pool.
 */
class GlueWorkerFactory implements ObjectFactory {

	private Logger logger = null;
	private GlueConfiguration gConf = null;

	/**
	 * @see ObjectFactory#decommission(Object)
	 */
	public void decommission(Object arg0) throws Exception {
	}

	/**
	 * @see ObjectFactory#getCreatedClass()
	 */
	public Class getCreatedClass() {
		return GlueWorker.class;
	}

	/**
	 * @see ObjectFactory#newInstance()
	 */
	public Object newInstance() throws Exception {
		
		GlueWorker gw = new GlueWorker();
		gw.enableLogging(logger);
		gw.setGConf(gConf);
		return gw;
	}

	public void setGConf(GlueConfiguration conf) {
		gConf = conf;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}