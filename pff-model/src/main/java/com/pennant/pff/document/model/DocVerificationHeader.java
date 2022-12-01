package com.pennant.pff.document.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class DocVerificationHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String custCif;
	private String docType;
	private String docNumber;
	private boolean verified;
	private Timestamp verifiedOn;
	private Timestamp prevVerifiedOn;
	private String docReference;
	private String clientId;
	private String docRequest;
	private String docResponse;
	private String status;
	private DocVerificationDetail docVerificationDetail;

	public DocVerificationHeader() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public Timestamp getVerifiedOn() {
		return verifiedOn;
	}

	public void setVerifiedOn(Timestamp verifiedOn) {
		this.verifiedOn = verifiedOn;
	}

	public String getDocReference() {
		return docReference;
	}

	public void setDocReference(String docReference) {
		this.docReference = docReference;
	}

	public DocVerificationDetail getDocVerificationDetail() {
		return docVerificationDetail;
	}

	public void setDocVerificationDetail(DocVerificationDetail docVerificationDetail) {
		this.docVerificationDetail = docVerificationDetail;
	}

	public String getDocRequest() {
		return docRequest;
	}

	public void setDocRequest(String docRequest) {
		this.docRequest = docRequest;
	}

	public String getDocResponse() {
		return docResponse;
	}

	public void setDocResponse(String docResponse) {
		this.docResponse = docResponse;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Timestamp getPrevVerifiedOn() {
		return prevVerifiedOn;
	}

	public void setPrevVerifiedOn(Timestamp prevVerifiedOn) {
		this.prevVerifiedOn = prevVerifiedOn;
	}
}