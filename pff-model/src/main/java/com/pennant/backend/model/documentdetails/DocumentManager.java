package com.pennant.backend.model.documentdetails;

import org.apache.log4j.Logger;

import com.pennant.backend.model.Entity;

public class DocumentManager implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DocumentManager.class);
	
	long id = Long.MIN_VALUE;
	byte[] docImage;

	public DocumentManager() {
		logger.info("DocumentManager = "+this.hashCode()+", "+this);
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public byte[] getDocImage() {
		return docImage;
	}
	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}
	@Override
	//This method is not used, only dummy implementation is provided
	public boolean isNew() {
		return false;
	}
	@Override
	public String toString() {
		return "DocumentManager [id=" + id + ", docImage=" + docImage + "]";
	}
}