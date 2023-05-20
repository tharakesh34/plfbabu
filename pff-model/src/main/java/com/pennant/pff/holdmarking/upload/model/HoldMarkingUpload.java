package com.pennant.pff.holdmarking.upload.model;

import java.math.BigDecimal;

import com.pennant.pff.upload.model.UploadDetails;

public class HoldMarkingUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String type;
	private String accountNumber;
	private BigDecimal amount = BigDecimal.ZERO;
	private String reference;
	private String remarks;

	public HoldMarkingUpload() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}