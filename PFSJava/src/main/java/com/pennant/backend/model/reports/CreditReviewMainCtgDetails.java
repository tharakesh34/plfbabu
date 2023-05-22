package com.pennant.backend.model.reports;

public class CreditReviewMainCtgDetails {

	private String customerId = "";
	private String bankName = "";
	private String auditors = "";
	private String consolOrUnConsol = "";
	private String location = "";
	private String auditedDate = "";
	private String conversionRate = "";
	private String auditYear = "";
	private String noOfShares = "";
	private String marketPrice = "";
	private String auditPeriod = "";

	private String custCIF = "";
	private String toYear = "";

	private String allowBankName = "False";

	public CreditReviewMainCtgDetails() {
	    super();
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAuditors() {
		return auditors;
	}

	public void setAuditors(String auditors) {
		this.auditors = auditors;
	}

	public String getConsolOrUnConsol() {
		return consolOrUnConsol;
	}

	public void setConsolOrUnConsol(String consolOrUnConsol) {
		this.consolOrUnConsol = consolOrUnConsol;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAuditedDate() {
		return auditedDate;
	}

	public void setAuditedDate(String auditedDate) {
		this.auditedDate = auditedDate;
	}

	public String getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(String conversionRate) {
		this.conversionRate = conversionRate;
	}

	public String getAuditYear() {
		return auditYear;
	}

	public void setAuditYear(String auditYear) {
		this.auditYear = auditYear;
	}

	public String getNoOfShares() {
		return noOfShares;
	}

	public void setNoOfShares(String noOfShares) {
		this.noOfShares = noOfShares;
	}

	public String getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(String marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getAuditPeriod() {
		return auditPeriod;
	}

	public void setAuditPeriod(String auditPeriod) {
		this.auditPeriod = auditPeriod;
	}

	/*
	 * For Inquiry Screen
	 */

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getToYear() {
		return toYear;
	}

	public void setToYear(String toYear) {
		this.toYear = toYear;
	}

	public String getAllowBankName() {
		return allowBankName;
	}

	public void setAllowBankName(String allowBankName) {
		this.allowBankName = allowBankName;
	}
}
