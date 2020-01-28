package com.pennant.backend.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class CustomerTaxDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private long requestId;
	private long customerId;
	private String loanrefeNo;
	private String orderStatus;
	private long transactionId;
	private String requestedBy;
	private Timestamp requestDate;

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getLoanrefeNo() {
		return loanrefeNo;
	}

	public void setLoanrefeNo(String loanrefeNo) {
		this.loanrefeNo = loanrefeNo;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public Timestamp getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Timestamp requestDate) {
		this.requestDate = requestDate;
	}

}
