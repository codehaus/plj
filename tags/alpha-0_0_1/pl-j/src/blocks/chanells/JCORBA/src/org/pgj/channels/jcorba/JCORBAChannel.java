/*
 * Created on Sep 2, 2003
 */
package org.pgj.channels.jcorba;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.avalon.excalibur.collections.PriorityQueue;
import org.apache.avalon.excalibur.collections.SynchronizedPriorityQueue;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.CommunicationException;
import org.pgj.messages.Message;

/**
 * @author Laszlo Hornyak
 * @version 0.1
 * 
 * Java CORBA Channel implementation. This should replace that crazy native corba channel.
 * You need at least jdk 1.4.0 to make this fly.
 * Configuration:
 * <pre>
 * 	&lt;block&gt;
 * 			&lt;corba-options&gt;-ORBInitialPort 1099&lt;/corba-options&gt;
 * 			&lt;name&gt;&namelt;/name&gt;
 * 	&lt;/block&gt;
 * </pre>
 * 
 * <!--
 * Es egy par uveg sor utan mindenki elfelejti
 * Hogy ugy lett ahogy pont nem akarta senki...
 * -->
 * 
 * @phoenix:block
 * @phoenix:service name="org.pgj.Channel"
 * 
 */
public class JCORBAChannel
	implements Channel, Configurable, Startable, Initializable, Serviceable, LogEnabled{

	ORB orb = null;
	POA poa = null;
	NamingContextExt rootcontext = null;
	CallServerImpl servant = null;
	
	PriorityQueue clientQue = null;

	String[] orbconfiguration = null;
	String name = null;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		Configuration corba_options = arg0.getChild("corba-options");
		Configuration name_conf = arg0.getChild("name");
		
		this.name = name_conf.getValue();
		
		StringTokenizer tokenizer = new StringTokenizer(corba_options.getValue(), " ");  
		Vector v = new Vector();
		while(tokenizer.hasMoreElements()){
			v.add(tokenizer.nextToken());
		}
		this.orbconfiguration = new String[v.size()];
		for(int i = 0; i< orbconfiguration.length; i++){
			orbconfiguration[i] = (String)v.get(i);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Startable#start()
	 */
	public void start() throws Exception {
		logger.debug("registering servant.");
		
		org.omg.CORBA.Object ref = poa.servant_to_reference(servant);
		
		logger.debug("starting ORB thread...");
		Thread thrd = new Thread(){
			public void run(){
				orb.run();
			}
		};
		thrd.start();
		logger.debug("done");
		logger.debug("registering in nameservice...");
		NameComponent path[] = rootcontext.to_name(name);
		rootcontext.bind(path,ref);
		logger.debug("done");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Startable#stop()
	 */
	public void stop() throws Exception {
		logger.debug("stopping ORB thread...");
		orb.shutdown(true);
		logger.debug("done");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		clientQue = new SynchronizedPriorityQueue(null);
		logger.debug("initializing ORB...");
		orb = ORB.init(orbconfiguration, null);
		poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		poa.the_POAManager().activate();
		servant = new CallServerImpl(this);
		servant.setLogger(logger);
		
		org.omg.CORBA.Object ncref = orb.resolve_initial_references("NameService");
		rootcontext = NamingContextExtHelper.narrow(ncref);
		
		logger.debug("done");
	}

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 * 
	 */
	//@phoenix:dependency name="org.apache.avalon.cornerstone.services.datasources.DataSourceSelector"
	public void service(ServiceManager arg0) throws ServiceException {
		logger.debug("servicing");
	}

	/* (non-Javadoc)
	 * @see org.pgj.Channel#getConnection(int)
	 */
	public Client getConnection(int timeout) {
		// TODO Auto-generated method stub
		logger.debug("getConnection: "+timeout);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.Channel#receiveFromRDBMS(org.pgj.Client)
	 */
	public Message receiveFromRDBMS(Client client)
		throws CommunicationException {
		// TODO Auto-generated method stub
		logger.debug("receiveFromRDBMS:"+client);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.Channel#sendToRDBMS(org.pgj.messages.Message)
	 */
	public void sendToRDBMS(Message msg) throws CommunicationException {
		// TODO Auto-generated method stub
		logger.debug("sendToRDBMS:"+msg);
	}

	private Logger logger = null;

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

}
