/*
 * Created on Jan 25, 2004
 */
package org.plj.chanells.febe;

import org.pgj.Client;
import org.plj.chanells.febe.core.PGStream;

/**
 * Represents a client of the FE/BE chanell.
 * @author Laszlo Hornyak
 * @version 0.1
 */
public final class FEBEClient implements Client {
	private PGStream stream = null;
	/**
	 * @return
	 */
	public PGStream getStream() {
		return stream;
	}

	/**
	 * @param stream
	 */
	public void setStream(PGStream stream) {
		this.stream = stream;
	}

}
