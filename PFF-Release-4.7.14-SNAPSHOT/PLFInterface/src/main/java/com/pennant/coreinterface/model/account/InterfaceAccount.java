package com.pennant.coreinterface.model.account;

import java.io.Serializable;

public class InterfaceAccount implements Serializable {
	
	private static final long serialVersionUID = 3988443874861414927L;

	public InterfaceAccount() {
		super();
	}
	
	private String referenceNum;
	private String custCIF;
	private String branchCode;
	private String customerType;
	private String productCode;
	private String currency;
	private String accountName;
	private String accountOfficer;
	private String jointHolder;
	private String jointHolderID;
	private String jointRelationCode;
	private String relationNotes;
	private String modeOfOperation;
	private int minNoOfSignatory;
	private String introducer;
	private String powerOfAttorneyFlag;
	private String powerOfAttorneyCIF;
	private String shoppingCardIssue;
	private String accountNumber;
	private String iban;
	private String cin;
	private String uin;
	private long timeStamp;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountOfficer() {
		return accountOfficer;
	}

	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
	}

	public String getJointHolder() {
		return jointHolder;
	}

	public void setJointHolder(String jointHolder) {
		this.jointHolder = jointHolder;
	}

	public String getJointHolderID() {
		return jointHolderID;
	}

	public void setJointHolderID(String jointHolderID) {
		this.jointHolderID = jointHolderID;
	}

	public String getJointRelationCode() {
		return jointRelationCode;
	}

	public void setJointRelationCode(String jointRelationCode) {
		this.jointRelationCode = jointRelationCode;
	}

	public String getRelationNotes() {
		return relationNotes;
	}

	public void setRelationNotes(String relationNotes) {
		this.relationNotes = relationNotes;
	}

	public String getModeOfOperation() {
		return modeOfOperation;
	}

	public void setModeOfOperation(String modeOfOperation) {
		this.modeOfOperation = modeOfOperation;
	}

	public int getMinNoOfSignatory() {
		return minNoOfSignatory;
	}

	public void setMinNoOfSignatory(int minNoOfSignatory) {
		this.minNoOfSignatory = minNoOfSignatory;
	}

	public String getIntroducer() {
		return introducer;
	}

	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}

	public String getPowerOfAttorneyFlag() {
		return powerOfAttorneyFlag;
	}

	public void setPowerOfAttorneyFlag(String powerOfAttorneyFlag) {
		this.powerOfAttorneyFlag = powerOfAttorneyFlag;
	}

	public String getPowerOfAttorneyCIF() {
		return powerOfAttorneyCIF;
	}

	public void setPowerOfAttorneyCIF(String powerOfAttorneyCIF) {
		this.powerOfAttorneyCIF = powerOfAttorneyCIF;
	}

	public String getShoppingCardIssue() {
		return shoppingCardIssue;
	}

	public void setShoppingCardIssue(String shoppingCardIssue) {
		this.shoppingCardIssue = shoppingCardIssue;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}

}
