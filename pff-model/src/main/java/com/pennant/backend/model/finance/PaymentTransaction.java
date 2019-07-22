package com.pennant.backend.model.finance;

import java.util.Date;

public class PaymentTransaction {

	private long transactionId;
	private String tranModule;
	private String tranReference;
	private String tranBatch;
	private String tranStatus;
	private String respReference;
	private String statusCode;
	private String statusDesc;
	private Date transctionInitOn;
	private Date statusUpdateOn;

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getTranModule() {
		return tranModule;
	}

	public void setTranModule(String tranModule) {
		this.tranModule = tranModule;
	}

	public String getTranReference() {
		return tranReference;
	}

	public void setTranReference(String tranReference) {
		this.tranReference = tranReference;
	}

	public String getTranBatch() {
		return tranBatch;
	}

	public void setTranBatch(String tranBatch) {
		this.tranBatch = tranBatch;
	}

	public String getTranStatus() {
		return tranStatus;
	}

	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}

	public String getRespReference() {
		return respReference;
	}

	public void setRespReference(String respReference) {
		this.respReference = respReference;
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

	public Date getTransctionInitOn() {
		return transctionInitOn;
	}

	public void setTransctionInitOn(Date transctionInitOn) {
		this.transctionInitOn = transctionInitOn;
	}

	public Date getStatusUpdateOn() {
		return statusUpdateOn;
	}

	public void setStatusUpdateOn(Date statusUpdateOn) {
		this.statusUpdateOn = statusUpdateOn;
	}

}
