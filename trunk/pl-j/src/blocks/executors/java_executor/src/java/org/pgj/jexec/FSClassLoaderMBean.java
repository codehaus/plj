/*
 * Created on Jun 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.pgj.jexec;

/**
 * @author Laszlo Hornyak
 * 
 * @phoenix:mx-topic name="plj"
 * 
 */
public interface FSClassLoaderMBean {
	
	void reloadClass(String fqn) throws ClassNotFoundException;
	public void flushCache();
}
