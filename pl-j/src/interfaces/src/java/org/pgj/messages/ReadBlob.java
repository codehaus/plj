/*
 * Created on May 26, 2005
 */
package org.pgj.messages;


/**
 * Read blob message.
 * @author Laszlo Hornyak
 */
public class ReadBlob extends SQL {
	private long blobId = -1;
	private int read_max;
	private boolean strictRead;
	public long getBlobId() {
		return blobId;
	}
	public void setBlobId(long blobId) {
		this.blobId = blobId;
	}
	public int getRead_max() {
		return read_max;
	}
	public void setRead_max(int read_max) {
		this.read_max = read_max;
	}
	public boolean isStrictRead() {
		return strictRead;
	}
	public void setStrictRead(boolean strictRead) {
		this.strictRead = strictRead;
	}
}
