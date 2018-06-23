package com.pennanttech.model.dms;

import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractEntity;

public class DMSDocumentDetails extends AbstractEntity {
	private static final long serialVersionUID = 625346919968624685L;
	private String finReference;
	private String docModule;
	private long docRefId;
	private String state;
	private String status;
	private long id;
	private Timestamp createdOn;
	private String referenceId;
	private String customerCif;
	private String docUri;
	private long docId;
	private String docCategory;
	private int retryCount;
	private String errorDesc;
	private String docDesc;
	private String docExt;

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getDocModule() {
		return docModule;
	}

	public void setDocModule(String docModule) {
		this.docModule = docModule;
	}

	public long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(long docRefId) {
		this.docRefId = docRefId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getCustomerCif() {
		return customerCif;
	}

	public void setCustomerCif(String customerCif) {
		this.customerCif = customerCif;
	}

	public String getDocUri() {
		return docUri;
	}

	public void setDocUri(String docUri) {
		this.docUri = docUri;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getDocDesc() {
		return docDesc;
	}

	public void setDocDesc(String docDesc) {
		this.docDesc = docDesc;
	}

	public String getDocExt() {
		return docExt;
	}

	public void setDocExt(String docExt) {
		this.docExt = docExt;
	}
}
