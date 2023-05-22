package com.pennant.backend.model.reports;

public class AvailCustomer {

	private String appDate;
	private String appTime;

	private String custCIF = "";
	private String branch = "";
	private String currency = "";
	private String amount = "";
	private String limitCcy = "";
	private String amountInLimitCcy = "";
	private String claimNat = "";
	private String riskCountry = "";
	private String industry = "";
	private String custType = "";
	private String custStatus = "";
	private String custGroup = "";
	private String custSatisfactory = "";
	private String externalRating = "";
	private String clientRequest = "";
	private String paymentInstruction = "";
	private String tolerance = "";

	private String custActualBal = "";
	private String custBlockedBal = "";
	private String custDeposit = "";
	private String custBlockedDeposit = "";
	private String totalCustBal = "";
	private String totalCustBlockedBal = "";

	private String cashMargin = "0.000";
	private String creditDeptComment = "";

	private String cmtFlag = "F";
	private String offBSAcFlag = "F";
	private String acRcvblFlag = "F";
	private String acPayblFlag = "F";
	private String coltrlFlag = "F";
	private String limitFlag = "F";

	public AvailCustomer() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getAppTime() {
		return appTime;
	}

	public void setAppTime(String appTime) {
		this.appTime = appTime;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getClaimNat() {
		return claimNat;
	}

	public void setClaimNat(String claimNat) {
		this.claimNat = claimNat;
	}

	public String getRiskCountry() {
		return riskCountry;
	}

	public void setRiskCountry(String riskCountry) {
		this.riskCountry = riskCountry;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getCustStatus() {
		return custStatus;
	}

	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}

	public String getCustGroup() {
		return custGroup;
	}

	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}

	public String getCustSatisfactory() {
		return custSatisfactory;
	}

	public void setCustSatisfactory(String custSatisfactory) {
		this.custSatisfactory = custSatisfactory;
	}

	public String getExternalRating() {
		return externalRating;
	}

	public void setExternalRating(String externalRating) {
		this.externalRating = externalRating;
	}

	public String getClientRequest() {
		return clientRequest;
	}

	public void setClientRequest(String clientRequest) {
		this.clientRequest = clientRequest;
	}

	public String getPaymentInstruction() {
		return paymentInstruction;
	}

	public void setPaymentInstruction(String paymentInstruction) {
		this.paymentInstruction = paymentInstruction;
	}

	public String getTolerance() {
		return tolerance;
	}

	public void setTolerance(String tolerance) {
		this.tolerance = tolerance;
	}

	public String getCustActualBal() {
		return custActualBal;
	}

	public void setCustActualBal(String custActualBal) {
		this.custActualBal = custActualBal;
	}

	public String getCustBlockedBal() {
		return custBlockedBal;
	}

	public void setCustBlockedBal(String custBlockedBal) {
		this.custBlockedBal = custBlockedBal;
	}

	public String getCustDeposit() {
		return custDeposit;
	}

	public void setCustDeposit(String custDeposit) {
		this.custDeposit = custDeposit;
	}

	public String getCustBlockedDeposit() {
		return custBlockedDeposit;
	}

	public void setCustBlockedDeposit(String custBlockedDeposit) {
		this.custBlockedDeposit = custBlockedDeposit;
	}

	public String getTotalCustBal() {
		return totalCustBal;
	}

	public void setTotalCustBal(String totalCustBal) {
		this.totalCustBal = totalCustBal;
	}

	public String getTotalCustBlockedBal() {
		return totalCustBlockedBal;
	}

	public void setTotalCustBlockedBal(String totalCustBlockedBal) {
		this.totalCustBlockedBal = totalCustBlockedBal;
	}

	public String getCreditDeptComment() {
		return creditDeptComment;
	}

	public void setCreditDeptComment(String creditDeptComment) {
		this.creditDeptComment = creditDeptComment;
	}

	public void setCashMargin(String cashMargin) {
		this.cashMargin = cashMargin;
	}

	public String getCashMargin() {
		return cashMargin;
	}

	public void setLimitFlag(String limitFlag) {
		this.limitFlag = limitFlag;
	}

	public String getLimitFlag() {
		return limitFlag;
	}

	public String getAmountInLimitCcy() {
		return amountInLimitCcy;
	}

	public void setAmountInLimitCcy(String amountInLimitCcy) {
		this.amountInLimitCcy = amountInLimitCcy;
	}

	public String getLimitCcy() {
		return limitCcy;
	}

	public void setLimitCcy(String limitCcy) {
		this.limitCcy = limitCcy;
	}

	public String getCmtFlag() {
		return cmtFlag;
	}

	public void setCmtFlag(String cmtFlag) {
		this.cmtFlag = cmtFlag;
	}

	public String getOffBSAcFlag() {
		return offBSAcFlag;
	}

	public void setOffBSAcFlag(String offBSAcFlag) {
		this.offBSAcFlag = offBSAcFlag;
	}

	public String getAcRcvblFlag() {
		return acRcvblFlag;
	}

	public void setAcRcvblFlag(String acRcvblFlag) {
		this.acRcvblFlag = acRcvblFlag;
	}

	public String getAcPayblFlag() {
		return acPayblFlag;
	}

	public void setAcPayblFlag(String acPayblFlag) {
		this.acPayblFlag = acPayblFlag;
	}

	public String getColtrlFlag() {
		return coltrlFlag;
	}

	public void setColtrlFlag(String coltrlFlag) {
		this.coltrlFlag = coltrlFlag;
	}

}
