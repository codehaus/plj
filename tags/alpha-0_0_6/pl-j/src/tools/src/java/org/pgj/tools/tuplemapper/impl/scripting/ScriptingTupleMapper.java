/*
 * Created on Apr 29, 2004
 */
package org.pgj.tools.tuplemapper.impl.scripting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.pgj.tools.tuplemapper.TupleMapper;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.Tuple;
import org.pgj.typemapping.TypeMapper;


/**
 * A tuple mapper that enables scripting. - Unfinished, untested!
 * 
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="scriptingtuplemapper" lifestyle="singleton"
 * @avalon.service type="org.pgj.tools.tuplemapper.TupleMapper"
 * 
 * @dna.component name="scriptingtuplemapper"
 * @dna.service type="org.pgj.tools.tuplemapper.TupleMapper"
 * 
 */
public class ScriptingTupleMapper implements Configurable, TupleMapper, LogEnabled{

	private String script = null;
	private String language = null;
	String cls = null;
	
	/**
	 * 
	 */
	public ScriptingTupleMapper() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		script = arg0.getChild("script").getValue();
		language = arg0.getChild("script").getAttribute("language");
		cls = arg0.getChild("script").getAttribute("returns");
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#mapTuple(org.pgj.typemapping.Tuple)
	 */
	public Object mapTuple(Tuple tuple) throws MappingException {
		BSFManager manager = new BSFManager();
		manager.registerBean("tuple", tuple);
		try {
			return manager.eval(language, script, 1, 1, null);
		} catch (BSFException e) {
			throw new MappingException("script error in configuration!", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#getMappedClass(org.pgj.typemapping.Tuple)
	 */
	public Class getMappedClass(Tuple tuple) {
		try {
			return Class.forName(cls);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	Logger logger = null;
	
	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#backMap(java.lang.Object)
	 */
	public Tuple backMap(Object obj, TypeMapper typeMapper) throws MappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
