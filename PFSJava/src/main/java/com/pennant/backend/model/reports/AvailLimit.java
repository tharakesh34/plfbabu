package com.pennant.backend.model.reports;

public class AvailLimit {

	private String limitExpiry = "";
	private String riskAmount = "";
	private String limitAmount = "";
	private String limitAvailAmt = "";
	private String limitCcy = "";
	private int limitCcyEdit;

	private String currentExposureLimit = "";
	private String newExposure = "";
	private String availableLimit = "";
	private String pastdueAmount = "";
	private String actualDate = "";
	private String dueDays = "";
	private String limitRemarks = "";

	public AvailLimit() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getLimitExpiry() {
		return limitExpiry;
	}

	public void setLimitExpiry(String limitExpiry) {
		this.limitExpiry = limitExpiry;
	}

	public String getRiskAmount() {
		return riskAmount;
	}

	public void setRiskAmount(String riskAmount) {
		this.riskAmount = riskAmount;
	}

	public String getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(String limitAmount) {
		this.limitAmount = limitAmount;
	}

	public String getLimitAvailAmt() {
		return limitAvailAmt;
	}

	public void setLimitAvailAmt(String limitAvailAmt) {
		this.limitAvailAmt = limitAvailAmt;
	}

	public String getLimitCcy() {
		return limitCcy;
	}

	public void setLimitCcy(String limitCcy) {
		this.limitCcy = limitCcy;
	}

	public String getCurrentExposureLimit() {
		return currentExposureLimit;
	}

	public void setCurrentExposureLimit(String currentExposureLimit) {
		this.currentExposureLimit = currentExposureLimit;
	}

	public String getNewExposure() {
		return newExposure;
	}

	public void setNewExposure(String newExposure) {
		this.newExposure = newExposure;
	}

	public String getAvailableLimit() {
		return availableLimit;
	}

	public void setAvailableLimit(String availableLimit) {
		this.availableLimit = availableLimit;
	}

	public String getPastdueAmount() {
		return pastdueAmount;
	}

	public void setPastdueAmount(String pastdueAmount) {
		this.pastdueAmount = pastdueAmount;
	}

	public String getActualDate() {
		return actualDate;
	}

	public void setActualDate(String actualDate) {
		this.actualDate = actualDate;
	}

	public String getDueDays() {
		return dueDays;
	}

	public void setDueDays(String dueDays) {
		this.dueDays = dueDays;
	}

	public String getLimitRemarks() {
		return limitRemarks;
	}

	public void setLimitRemarks(String limitRemarks) {
		this.limitRemarks = limitRemarks;
	}

	public int getLimitCcyEdit() {
		return limitCcyEdit;
	}

	public void setLimitCcyEdit(int limitCcyEdit) {
		this.limitCcyEdit = limitCcyEdit;
	}

}
