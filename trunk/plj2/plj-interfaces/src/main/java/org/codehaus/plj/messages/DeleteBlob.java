/*
 * Created on May 26, 2005
 */
package org.codehaus.plj.messages;


/**
 * @author Laszlo Hornyak
 */
//TODO: edit comments for DeleteBlob
public class DeleteBlob extends SQL {
	long blobId = -1;
	public long getBlobId() {
		return blobId;
	}
	public void setBlobId(long blobId) {
		this.blobId = blobId;
	}
}
