package com.pennanttech.external.gst.model;

import java.math.BigDecimal;
import java.util.Date;

public class GSTInvoiceDetail {
	private String customerName;
	private String customerAddress;
	private String currentGstin;
	private String loanBranchAddress;
	private String gstin;
	private Date transactionDate;
	private String invoiceNumber;
	private String chargeDescription;
	private BigDecimal chargeAmount;
	private BigDecimal cgstRate;
	private BigDecimal cgstAmount;
	private BigDecimal sgstRate;
	private BigDecimal sgstAmount;
	private BigDecimal igstRate;
	private BigDecimal igstAmount;
	private BigDecimal ugstRate;
	private BigDecimal ugstAmount;
	private BigDecimal cessAmount;
	private String pop;
	private String pos;
	private String cin;
	private String pan;
	private String sac;
	private String websiteAddress;
	private String emailId;
	private String regBankAddress;
	private String disclaimer;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCurrentGstin() {
		return currentGstin;
	}

	public void setCurrentGstin(String currentGstin) {
		this.currentGstin = currentGstin;
	}

	public String getLoanBranchAddress() {
		return loanBranchAddress;
	}

	public void setLoanBranchAddress(String loanBranchAddress) {
		this.loanBranchAddress = loanBranchAddress;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getChargeDescription() {
		return chargeDescription;
	}

	public void setChargeDescription(String chargeDescription) {
		this.chargeDescription = chargeDescription;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public BigDecimal getCgstRate() {
		return cgstRate;
	}

	public void setCgstRate(BigDecimal cgstRate) {
		this.cgstRate = cgstRate;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getSgstRate() {
		return sgstRate;
	}

	public void setSgstRate(BigDecimal sgstRate) {
		this.sgstRate = sgstRate;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getIgstRate() {
		return igstRate;
	}

	public void setIgstRate(BigDecimal igstRate) {
		this.igstRate = igstRate;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public BigDecimal getUgstRate() {
		return ugstRate;
	}

	public void setUgstRate(BigDecimal ugstRate) {
		this.ugstRate = ugstRate;
	}

	public BigDecimal getUgstAmount() {
		return ugstAmount;
	}

	public void setUgstAmount(BigDecimal ugstAmount) {
		this.ugstAmount = ugstAmount;
	}

	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

	public String getPop() {
		return pop;
	}

	public void setPop(String pop) {
		this.pop = pop;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getSac() {
		return sac;
	}

	public void setSac(String sac) {
		this.sac = sac;
	}

	public String getWebsiteAddress() {
		return websiteAddress;
	}

	public void setWebsiteAddress(String websiteAddress) {
		this.websiteAddress = websiteAddress;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getRegBankAddress() {
		return regBankAddress;
	}

	public void setRegBankAddress(String regBankAddress) {
		this.regBankAddress = regBankAddress;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

}
