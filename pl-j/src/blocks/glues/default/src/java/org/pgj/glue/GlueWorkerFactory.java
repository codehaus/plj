package org.pgj.glue;

import org.apache.avalon.excalibur.pool.*;
import org.apache.avalon.framework.logger.*;

/**
 * @author bitfakk
 *
 * I was too lazy to edit the comments.
 */
class GlueWorkerFactory implements ObjectFactory, LogEnabled {

	Logger logger = null;

	public GlueWorkerFactory() {
	}

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
		return gw;
	}

	/**
	 * @see LogEnabled#enableLogging(Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}
}