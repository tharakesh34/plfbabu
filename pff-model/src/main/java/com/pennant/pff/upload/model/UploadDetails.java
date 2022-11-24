package com.pennant.pff.upload.model;

import java.io.Serializable;

public class UploadDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long headerId;
	private Long referenceID;
	private String reference;
	private int progress;
	private String remarks;

	public UploadDetails() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public Long getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(Long referenceID) {
		this.referenceID = referenceID;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
