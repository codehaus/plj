/*
 * Created on Mar 12, 2004
 */

package org.pgj.tools.log4j;

import java.util.Properties;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
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
public class Log4jInitComponent implements Configurable, LogEnabled {

	Logger logger = null;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		Configuration propConfigs = arg0.getChild("properties");
		if (propConfigs != null) {
			Properties props = new Properties();
			Configuration[] configs = propConfigs.getChildren("property");
			for (int i = 0; i < configs.length; i++) {
				props.put(configs[i].getAttribute("name"), configs[i]
						.getAttribute("value"));
			}
			PropertyConfigurator.configure(props);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

}
