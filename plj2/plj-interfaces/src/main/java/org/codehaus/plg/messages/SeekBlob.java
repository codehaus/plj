/*
 * Created on May 26, 2005
 */
package org.codehaus.plg.messages;


/**
 * @author Laszlo Hornyak
 */
public class SeekBlob extends SQL {
	private long blobId = -1;
	private long position = -1;
	private boolean relative = false;
	public long getBlobId() {
		return blobId;
	}
	public void setBlobId(long blobId) {
		this.blobId = blobId;
	}
	public long getPosition() {
		return position;
	}
	public void setPosition(long position) {
		this.position = position;
	}
	public boolean isRelative() {
		return relative;
	}
	public void setRelative(boolean relative) {
		this.relative = relative;
	}
}
