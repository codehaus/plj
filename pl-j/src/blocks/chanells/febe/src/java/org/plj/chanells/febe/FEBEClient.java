/*
 * Created on Jan 25, 2004
 */
package org.plj.chanells.febe;

import org.pgj.Channel;
import org.pgj.Client;
import org.pgj.typemapping.TypeMapper;
import org.plj.chanells.febe.core.Encoding;
import org.plj.chanells.febe.core.PGStream;

/**
 * Represents a client of the FE/BE chanell.
 * 
 * @author Laszlo Hornyak
 * @version 0.1
 */
final class FEBEClient implements Client {
	private Encoding encoding = null;

	private PGStream stream = null;

	private TypeMapper typeMapper = null;

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

	/**
	 * @return
	 */
	public Encoding getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 */
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	private Channel channel = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Client#getChannel()
	 */
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pgj.Client#getTypeMapper()
	 */
	public TypeMapper getTypeMapper() {
		return typeMapper;
	}

	public void setTypeMapper(TypeMapper typeMapper) {
		this.typeMapper = typeMapper;
	}

}