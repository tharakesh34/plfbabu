package com.pennant.backend.model.reports;

public class CashierReceipt {

	private String userName;
	private String userBranch;
	private String finReference;
	private String custName;
	private String receiptAmount;
	private String receiptAmountInWords;
	private String appDate;
	private String receiptDate;
	private String receiptNo;
	private String fundingAc;
	private String paymentMode;

	private byte[] clientLogo = null;

	public CashierReceipt() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserBranch() {
		return userBranch;
	}

	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getReceiptAmountInWords() {
		return receiptAmountInWords;
	}

	public void setReceiptAmountInWords(String receiptAmountInWords) {
		this.receiptAmountInWords = receiptAmountInWords;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getFundingAc() {
		return fundingAc;
	}

	public void setFundingAc(String fundingAc) {
		this.fundingAc = fundingAc;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public byte[] getClientLogo() {
		return clientLogo;
	}

	public void setClientLogo(byte[] clientLogo) {
		this.clientLogo = clientLogo;
	}

}
