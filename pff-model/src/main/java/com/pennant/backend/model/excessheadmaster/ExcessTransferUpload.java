package com.pennant.backend.model.excessheadmaster;

import java.math.BigDecimal;

import com.pennant.pff.upload.model.UploadDetails;

public class ExcessTransferUpload extends UploadDetails {

	private static final long serialVersionUID = 2L;

	private int finId;
	private String finReference;
	private String transferFromType;
	private int transferFromId;
	private String transferToType;
	private int transferToId;
	private BigDecimal transferAmount;

	public ExcessTransferUpload() {
		super();
	}

	public int getFinId() {
		return finId;
	}

	public void setFinId(int finId) {
		this.finId = finId;
	}

	public String getFinReference() {
		return finReference;
	}

	public String getTransferFromType() {
		return transferFromType;
	}

	public int getTransferFromId() {
		return transferFromId;
	}

	public String getTransferToType() {
		return transferToType;
	}

	public int getTransferToId() {
		return transferToId;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public void setTransferFromType(String transferFromType) {
		this.transferFromType = transferFromType;
	}

	public void setTransferFromId(int transferFromId) {
		this.transferFromId = transferFromId;
	}

	public void setTransferToType(String transferToType) {
		this.transferToType = transferToType;
	}

	public void setTransferToId(int transferToId) {
		this.transferToId = transferToId;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

}
