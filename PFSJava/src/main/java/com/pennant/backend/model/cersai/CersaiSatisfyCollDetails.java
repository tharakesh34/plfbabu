package com.pennant.backend.model.cersai;

import java.util.Date;

public class CersaiSatisfyCollDetails {

	private long batchId;
	private String rowType;
	private long serialNumber;
	private Long siId;
	private Long assetId;
	private Date satisfactionDate;
	private String reasonCode;
	private String reasonOthers;
	private String reasonForDelay;
	private String batchRefNumber;

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public String getRowType() {
		return rowType;
	}

	public void setRowType(String rowType) {
		this.rowType = rowType;
	}

	public long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Long getSiId() {
		return siId;
	}

	public void setSiId(Long siId) {
		this.siId = siId;
	}

	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	public Date getSatisfactionDate() {
		return satisfactionDate;
	}

	public void setSatisfactionDate(Date satisfactionDate) {
		this.satisfactionDate = satisfactionDate;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getReasonOthers() {
		return reasonOthers;
	}

	public void setReasonOthers(String reasonOthers) {
		this.reasonOthers = reasonOthers;
	}

	public String getReasonForDelay() {
		return reasonForDelay;
	}

	public void setReasonForDelay(String reasonForDelay) {
		this.reasonForDelay = reasonForDelay;
	}

	public String getBatchRefNumber() {
		return batchRefNumber;
	}

	public void setBatchRefNumber(String batchRefNumber) {
		this.batchRefNumber = batchRefNumber;
	}

}
