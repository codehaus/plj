/*
 * Created on Oct 11, 2003
 */
package org.pgj.channels.jcorba;

import org.omg.CORBA.Any;
import org.pgj.messages.Message;

/**
 * @author Laszlo Hornyak
 * @version 0.1
 * Interface for message structure producers.
 */
public interface Producer {
	
	Message create(Any any);
	
}
