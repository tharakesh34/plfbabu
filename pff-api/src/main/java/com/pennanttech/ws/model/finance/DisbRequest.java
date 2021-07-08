package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finReference", "type", "clearingDate", "rejectReason", "transactionRef", "status", "chequeNo",
		"disbDate" })
@XmlAccessorType(XmlAccessType.FIELD)
public class DisbRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private String type;
	private Date clearingDate;
	private String rejectReason;
	private String transactionRef;
	private String status;
	private String chequeNo;
	private Date disbDate;
	private long paymentId;
	private String disbType;

	public DisbRequest() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getClearingDate() {
		return clearingDate;
	}

	public void setClearingDate(Date clearingDate) {
		this.clearingDate = clearingDate;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Date getDisbDate() {
		return disbDate;
	}

	public void setDisbDate(Date disbDate) {
		this.disbDate = disbDate;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	public String getDisbType() {
		return disbType;
	}

	public void setDisbType(String disbType) {
		this.disbType = disbType;
	}

}
