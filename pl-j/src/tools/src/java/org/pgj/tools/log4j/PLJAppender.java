/*
 * Created on Mar 12, 2004
 */

package org.pgj.tools.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.CommunicationException;
import org.pgj.messages.Log;
import org.pgj.tools.utils.ClientUtils;


/**
 * Log4J appender implementation that uses Log messages to 
 * send log back to the RDBMS.
 * 
 * @author Laszlo Hornyak
 * 
 * @todo finish PLJAppender
 */
public class PLJAppender extends AppenderSkeleton {

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	protected void append(LoggingEvent arg0) {
		Client clnt = ClientUtils.getClientforThread();

		//TODO: in a good configuration this should never happen, so lets log it.
		if(clnt == null)
			return;
		Log log = new Log();
		log.setClient(clnt);
		log.setCategory(arg0.getLoggerName());
		int level = Log.LEVEL_DEBUG;
		switch (arg0.getLevel().toInt()) {
			case Level.WARN_INT :
				level = Log.LEVEL_WARN;
				break;
			case Level.FATAL_INT :
				level = Log.LEVEL_FATAL;
				break;
			case Level.INFO_INT :
				level = Log.LEVEL_INFO;
				break;
			case Level.ERROR_INT :
				level = Log.LEVEL_ERROR;
				break;
			default :
		}
		log.setLevel(level);
		log.setMessage(arg0.getMessage() == null ? "(null)" : arg0.getMessage()
				.toString());
		try {
			if(clnt == null)
				return;
			Channel channel = clnt.getChannel();
			if(channel != null){
				channel.sendToRDBMS(log);
			}
		} catch (CommunicationException e) {
			//TODO: what shall here happen?
			System.err.println("gebasz!");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#close()
	 */
	public void close() {
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
		return false;
	}
}
