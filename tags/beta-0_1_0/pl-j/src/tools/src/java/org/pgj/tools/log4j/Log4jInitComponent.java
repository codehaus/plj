/*
 * Created on Mar 12, 2004
 */

package org.pgj.tools.log4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.log4j.PropertyConfigurator;
import org.pgj.tools.classloaders.ClassStoreException;
import org.pgj.tools.classloaders.PLJClassLoader;

/**
 * A bloody hack.
 * 
 * This is a hack. The log4j logging system in servlet environment 
 * is initialized by a servlet, that doesn`t run after. This is very similar
 * but in Avalon service. After the configuration happened, this object goes
 * to garbage.
 * 
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="log4j-init" lifestyle="singleton"
 * 
 * @dna.component name="log4j-init"
 */
public class Log4jInitComponent implements Configurable, LogEnabled, Serviceable {

	private Logger logger = null;
	private PLJClassLoader cl = null;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		logger.info("Seting up log4j logging for client code");
		Configuration propConfigs = arg0.getChild("properties");
		if (propConfigs != null) {
			Properties props = new Properties();
			Configuration[] configs = propConfigs.getChildren("property");
			for (int i = 0; i < configs.length; i++) {
				props.put(configs[i].getAttribute("name"), configs[i]
						.getAttribute("value"));
			}
//			PropertyConfigurator.configure(props);
			try {
				Class c = cl.load("org.apache.log4j.PropertyConfigurator");
				Object i = c.newInstance();
				Method m = c.getMethod("configure",new Class[]{Properties.class});
				m.invoke(i, new Object[] {props});
			} catch (SecurityException e) {
				throw new ConfigurationException("Reflection problem. ", e);
			} catch (IllegalArgumentException e) {
				throw new ConfigurationException("Reflection problem. ", e);
			} catch (ClassNotFoundException e) {
				throw new ConfigurationException("Reflection problem. ", e);
			} catch (ClassStoreException e) {
				throw new ConfigurationException("Reflection problem. ", e);
			} catch (InstantiationException e) {
				throw new ConfigurationException("Reflection problem. ", e);
			} catch (IllegalAccessException e) {
				throw new ConfigurationException("Reflection problem. ", e);
			} catch (NoSuchMethodException e) {
				throw new ConfigurationException("Reflection problem. ", e);
			} catch (InvocationTargetException e) {
				throw new ConfigurationException("Reflection problem. ", e);
			}
		}
		logger.info("Log4j setup done");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/**
	 * @dna.dependency key="classloader" type="org.pgj.tools.classloaders.PLJClassLoader"
	 * 
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		cl = (PLJClassLoader)arg0.lookup("classloader");
	}

}
