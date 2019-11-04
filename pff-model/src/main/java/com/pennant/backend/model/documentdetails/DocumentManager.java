package com.pennant.backend.model.documentdetails;

import com.pennant.backend.model.Entity;

public class DocumentManager implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;

	long id = Long.MIN_VALUE;
	byte[] docImage;

	public DocumentManager() {
		super();
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
}