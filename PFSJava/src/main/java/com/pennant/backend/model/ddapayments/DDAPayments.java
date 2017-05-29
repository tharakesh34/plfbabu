package com.pennant.backend.model.ddapayments;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class DDAPayments implements Serializable, Entity {

	private static final long serialVersionUID = 4523508108989522685L;

	private long ddaSeqId = Long.MIN_VALUE;
	private String finReference;
	private BigDecimal finRepaymentAmount = BigDecimal.ZERO;
	private String dDAReferenceNo;
	private String repayAccountId;
	private String custCIF;
	private Date schDate;

	// Used for Ext tables
	private String dDARefNo;
	private String directDebitRefNo;
	private String status;
	private String reason;
	private String pFFData;
	private String t24Data;

	public DDAPayments() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getId() {
		return ddaSeqId;
	}

	@Override
	public void setId(long id) {
		this.ddaSeqId = id;
	}

	public long getDdaSeqId() {
		return ddaSeqId;
	}

	public void setDdaSeqId(long ddaSeqId) {
		this.ddaSeqId = ddaSeqId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getFinRepaymentAmount() {
		return finRepaymentAmount;
	}

	public void setFinRepaymentAmount(BigDecimal finRepaymentAmount) {
		this.finRepaymentAmount = finRepaymentAmount;
	}

	public String getdDAReferenceNo() {
		return dDAReferenceNo;
	}

	public void setdDAReferenceNo(String dDAReferenceNo) {
		this.dDAReferenceNo = dDAReferenceNo;
	}

	public String getDirectDebitRefNo() {
		return directDebitRefNo;
	}

	public void setDirectDebitRefNo(String directDebitRefNo) {
		this.directDebitRefNo = directDebitRefNo;
	}
	
	public String getRepayAccountId() {
		return repayAccountId;
	}

	public void setRepayAccountId(String repayAccountId) {
		this.repayAccountId = repayAccountId;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public String getdDARefNo() {
		return dDARefNo;
	}

	public void setdDARefNo(String dDARefNo) {
		this.dDARefNo = dDARefNo;
	}

	public String getpFFData() {
		return pFFData;
	}

	public void setpFFData(String pFFData) {
		this.pFFData = pFFData;
	}

	public String getT24Data() {
		return t24Data;
	}

	public void setT24Data(String t24Data) {
		this.t24Data = t24Data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
