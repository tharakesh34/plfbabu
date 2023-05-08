package com.pennant.backend.model.limit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class LimitReferenceMapping implements Serializable {
	private static final long serialVersionUID = 1L;
	private long referenceId = Long.MIN_VALUE;
	private String referenceCode;
	private String referenceNumber;
	private String limitLine;
	private String finCcy;
	private String loanStatus;
	private long headerId;

	private BigDecimal loanAmount;
	private BigDecimal outstandingAmount;
	private boolean newRecord = false;
	private boolean proceeed = true;

	public LimitReferenceMapping() {
	    super();
	}

	public LimitReferenceMapping(String id) {
		this.setReferenceCode(id);
	}

	public Set<String> getExcludeFields() {

		return new HashSet<String>();
	}

	public long getId() {
		return referenceId;
	}

	public void setId(long id) {
		this.referenceId = id;

	}

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getLimitLine() {
		return limitLine;
	}

	public void setLimitLine(String limitLine) {
		this.limitLine = limitLine;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public BigDecimal getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(BigDecimal outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public boolean isProceeed() {
		return proceeed;
	}

	public void setProceeed(boolean proceeed) {
		this.proceeed = proceeed;
	}

}
