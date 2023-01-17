package com.pennant.pff.excess.model;

import java.math.BigDecimal;

import com.pennant.pff.upload.model.UploadDetails;

public class ExcessTransferUpload extends UploadDetails {
	private static final long serialVersionUID = 2L;

	private String transferFromType;
	private String transferToType;
	private BigDecimal transferAmount;

	public ExcessTransferUpload() {
		super();
	}

	public String getTransferFromType() {
		return transferFromType;
	}

	public String getTransferToType() {
		return transferToType;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferFromType(String transferFromType) {
		this.transferFromType = transferFromType;
	}

	public void setTransferToType(String transferToType) {
		this.transferToType = transferToType;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

}
