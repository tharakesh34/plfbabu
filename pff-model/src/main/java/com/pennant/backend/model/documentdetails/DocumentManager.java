package com.pennant.backend.model.documentdetails;

public class DocumentManager implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	long id = Long.MIN_VALUE;
	byte[] docImage;
	private String docURI;
	private Long custId;

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

	public String getDocURI() {
		return docURI;
	}

	public void setDocURI(String docURI) {
		this.docURI = docURI;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

}