package com.pennant.backend.model.perfios;

import java.io.Serializable;

public class PerfiousDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long detailId;
	private String transactionId;
	private long docID;
	private String fileID;
	private String statusCode;
	private String statusDesc;
	/* Below fields are excluded */
	private byte[] docImg;
	private String docName;
	private String password;

	public long getDetailId() {
		return detailId;
	}

	public void setDetailId(long detailId) {
		this.detailId = detailId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public long getDocID() {
		return docID;
	}

	public void setDocID(long docID) {
		this.docID = docID;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public byte[] getDocImg() {
		return docImg;
	}

	public void setDocImg(byte[] docImg) {
		this.docImg = docImg;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
