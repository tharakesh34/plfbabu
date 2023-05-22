package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.sql.Timestamp;

public class DealerResponse implements Serializable {
	private static final long serialVersionUID = -5364509511869836239L;

	private long dealerResponseId = Long.MIN_VALUE;
	private long dealerId;
	private long finID;
	private String finReference;
	private String UniqueReference;
	private String attachmentName;
	private String reqUserRole;
	private long reqUserid;
	private String status;
	private Timestamp requestDate;
	private Timestamp responseDate;
	private String responseRef;
	private boolean processed;

	public DealerResponse() {
	    super();
	}

	public long getDealerResponseId() {
		return dealerResponseId;
	}

	public void setDealerResponseId(long dealerResponseId) {
		this.dealerResponseId = dealerResponseId;
	}

	public long getDealerId() {
		return dealerId;
	}

	public void setDealerId(long dealerId) {
		this.dealerId = dealerId;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getUniqueReference() {
		return UniqueReference;
	}

	public void setUniqueReference(String uniqueReference) {
		UniqueReference = uniqueReference;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public String getReqUserRole() {
		return reqUserRole;
	}

	public void setReqUserRole(String reqUserRole) {
		this.reqUserRole = reqUserRole;
	}

	public long getReqUserid() {
		return reqUserid;
	}

	public void setReqUserid(long reqUserid) {
		this.reqUserid = reqUserid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseRef() {
		return responseRef;
	}

	public void setResponseRef(String responseRef) {
		this.responseRef = responseRef;
	}

	public Timestamp getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Timestamp requestDate) {
		this.requestDate = requestDate;
	}

	public Timestamp getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Timestamp responseDate) {
		this.responseDate = responseDate;
	}

	public long getId() {
		return dealerResponseId;

	}

	public void setId(long id) {
		this.dealerResponseId = id;

	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

}
