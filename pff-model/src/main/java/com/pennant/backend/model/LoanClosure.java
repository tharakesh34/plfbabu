package com.pennant.backend.model;

import java.math.BigDecimal;

import com.pennant.pff.upload.model.UploadDetails;

public class LoanClosure extends UploadDetails {

	private static final long serialVersionUID = -58727889587717168L;
	private long receiptID = 0;
	private String remarks;
	private Long reasonCode;
	private String closureType;
	private String source;
	private BigDecimal principal_W;
	private BigDecimal interest_W;
	private BigDecimal bounce_W;
	private BigDecimal lpp_W;
	private BigDecimal ftInterest_W;
	private BigDecimal ftPrincipal_W;
	private String feeTypeCode;

	public LoanClosure() {
		super();
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(Long reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getClosureType() {
		return closureType;
	}

	public void setClosureType(String closureType) {
		this.closureType = closureType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public BigDecimal getPrincipal_W() {
		return principal_W;
	}

	public void setPrincipal_W(BigDecimal principal_W) {
		this.principal_W = principal_W;
	}

	public BigDecimal getInterest_W() {
		return interest_W;
	}

	public void setInterest_W(BigDecimal interest_W) {
		this.interest_W = interest_W;
	}

	public BigDecimal getBounce_W() {
		return bounce_W;
	}

	public void setBounce_W(BigDecimal bounce_W) {
		this.bounce_W = bounce_W;
	}

	public BigDecimal getLpp_W() {
		return lpp_W;
	}

	public void setLpp_W(BigDecimal lpp_W) {
		this.lpp_W = lpp_W;
	}

	public BigDecimal getFtPrincipal_W() {
		return ftPrincipal_W;
	}

	public void setFtPrincipal_W(BigDecimal ftPrincipal_W) {
		this.ftPrincipal_W = ftPrincipal_W;
	}

	public BigDecimal getFtInterest_W() {
		return ftInterest_W;
	}

	public void setFtInterest_W(BigDecimal ftInterest_W) {
		this.ftInterest_W = ftInterest_W;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

}
