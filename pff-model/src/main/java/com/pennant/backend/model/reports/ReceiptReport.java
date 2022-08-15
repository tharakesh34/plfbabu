/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : ReceiptReport.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.reports;

import java.io.Serializable;

public class ReceiptReport implements Serializable {
	private static final long serialVersionUID = 1L;
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
	private String subReceiptMode;
	private String transactionRef;

	private String panNumber;
	private String mobileNo;
	private String finType;
	private String finTypeDesc;
	private String bankCode;
	private String bankName;
	private String branchCode;
	private String branchName;

	// Allocation Details
	private String priPaid = "0";
	private String pftPaid = "0";
	private String lppAmount = "0";
	private String lpiAmount = "0";
	private String bounceCharges = "0";
	private String feeCharges = "0";
	private String others = "0";

	// User Branch Address Details
	private String branchAddrLine1;
	private String branchAddrLine2;
	private String branchAddrHNbr;
	private String branchAddrFlatNo;
	private String branchAddrStreet;
	private String branchAddrCountry;
	private String branchAddrCity;
	private String branchAddrProvince;
	private String branchAddrDistrict;
	private String branchAddrPincode;
	private String branchPhone;

	public ReceiptReport() {
		super();
	}

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

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBounceCharges() {
		return bounceCharges;
	}

	public void setBounceCharges(String bounceCharges) {
		this.bounceCharges = bounceCharges;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public String getSubReceiptMode() {
		return subReceiptMode;
	}

	public void setSubReceiptMode(String subReceiptMode) {
		this.subReceiptMode = subReceiptMode;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getBranchAddrLine1() {
		return branchAddrLine1;
	}

	public void setBranchAddrLine1(String branchAddrLine1) {
		this.branchAddrLine1 = branchAddrLine1;
	}

	public String getBranchAddrLine2() {
		return branchAddrLine2;
	}

	public void setBranchAddrLine2(String branchAddrLine2) {
		this.branchAddrLine2 = branchAddrLine2;
	}

	public String getBranchAddrHNbr() {
		return branchAddrHNbr;
	}

	public void setBranchAddrHNbr(String branchAddrHNbr) {
		this.branchAddrHNbr = branchAddrHNbr;
	}

	public String getBranchAddrFlatNo() {
		return branchAddrFlatNo;
	}

	public void setBranchAddrFlatNo(String branchAddrFlatNo) {
		this.branchAddrFlatNo = branchAddrFlatNo;
	}

	public String getBranchAddrStreet() {
		return branchAddrStreet;
	}

	public void setBranchAddrStreet(String branchAddrStreet) {
		this.branchAddrStreet = branchAddrStreet;
	}

	public String getBranchAddrCountry() {
		return branchAddrCountry;
	}

	public void setBranchAddrCountry(String branchAddrCountry) {
		this.branchAddrCountry = branchAddrCountry;
	}

	public String getBranchAddrCity() {
		return branchAddrCity;
	}

	public void setBranchAddrCity(String branchAddrCity) {
		this.branchAddrCity = branchAddrCity;
	}

	public String getBranchAddrProvince() {
		return branchAddrProvince;
	}

	public void setBranchAddrProvince(String branchAddrProvince) {
		this.branchAddrProvince = branchAddrProvince;
	}

	public String getBranchAddrDistrict() {
		return branchAddrDistrict;
	}

	public void setBranchAddrDistrict(String branchAddrDistrict) {
		this.branchAddrDistrict = branchAddrDistrict;
	}

	public String getBranchAddrPincode() {
		return branchAddrPincode;
	}

	public void setBranchAddrPincode(String branchAddrPincode) {
		this.branchAddrPincode = branchAddrPincode;
	}

	public String getBranchPhone() {
		return branchPhone;
	}

	public void setBranchPhone(String branchPhone) {
		this.branchPhone = branchPhone;
	}

	public String getPriPaid() {
		return priPaid;
	}

	public void setPriPaid(String priPaid) {
		this.priPaid = priPaid;
	}

	public String getPftPaid() {
		return pftPaid;
	}

	public void setPftPaid(String pftPaid) {
		this.pftPaid = pftPaid;
	}

	public String getLppAmount() {
		return lppAmount;
	}

	public void setLppAmount(String lppAmount) {
		this.lppAmount = lppAmount;
	}

	public String getLpiAmount() {
		return lpiAmount;
	}

	public void setLpiAmount(String lpiAmount) {
		this.lpiAmount = lpiAmount;
	}

	public String getFeeCharges() {
		return feeCharges;
	}

	public void setFeeCharges(String feeCharges) {
		this.feeCharges = feeCharges;
	}

}
