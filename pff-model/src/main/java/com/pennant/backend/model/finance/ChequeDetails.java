package com.pennant.backend.model.finance;

public class ChequeDetails {

	private String finBranchName;
	private String appDate;

	private String custName;
	private String finReference;
	private String repayAmount;
	private String repayAmountinWords;

	public ChequeDetails() {
	    super();
	}

	public String getFinBranchName() {
		return finBranchName;
	}

	public void setFinBranchName(String finBranchName) {
		this.finBranchName = finBranchName;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(String repayAmount) {
		this.repayAmount = repayAmount;
	}

	public String getRepayAmountinWords() {
		return repayAmountinWords;
	}

	public void setRepayAmountinWords(String repayAmountinWords) {
		this.repayAmountinWords = repayAmountinWords;
	}

}
