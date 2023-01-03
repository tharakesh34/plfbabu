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
 * * FileName : PaymentInstruction.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified
 * Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.feerefund;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PaymentInstruction table</b>.<br>
 * 
 */
public class FeeRefundInstruction extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long feeRefundInstId = Long.MIN_VALUE;
	private long feeRefundId;

	private String paymentType;
	private Date postDate;
	private BigDecimal paymentAmount = BigDecimal.ZERO;
	private long partnerBankId;
	private String partnerBankCode;
	private String partnerBankName;

	private String remarks;
	private long bankBranchId;
	private String bankBranchIFSC;
	private String bankBranchCode;
	private String issuingBank;
	private String issuingBankName;
	private String favourName;
	private String payableLoc;
	private String printingLoc;
	private Date valueDate;
	private String favourNumber;// Cheque/DD Number
	private String accountNo;// beneficiaryAccNo
	private String acctHolderName;
	private String phoneCountryCode;// Need to add
	private String phoneNumber;
	private String paymentCCy;

	private String pCCityName;
	private String branchDesc;
	private String bankName;
	private String status;
	private String clearingStatus;

	private Date realizationDate;
	private boolean active;

	private long finID;
	private String finReference;
	private Date clearingDate;
	private String transactionRef;
	private String rejectReason;
	private String partnerBankAc;
	private String partnerBankAcType;
	private long linkedTranId;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private FeeRefundInstruction befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private boolean apiRequest = false; // for Refund Upload
	private boolean paymentProcReq = false;
	private String branchBankCode;
	private String lei;

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public FeeRefundInstruction() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("partnerBankCode");
		excludeFields.add("partnerBankName");
		excludeFields.add("bankBranchIFSC");
		excludeFields.add("bankBranchCode");
		excludeFields.add("issuingBankName");
		excludeFields.add("pCCityName");
		excludeFields.add("branchDesc");
		excludeFields.add("bankName");
		excludeFields.add("finID");
		excludeFields.add("finReference");
		excludeFields.add("clearingDate");
		excludeFields.add("transactionRef");
		excludeFields.add("rejectReason");
		excludeFields.add("partnerBankAc");
		excludeFields.add("partnerBankAcType");
		excludeFields.add("linkedTranId");
		excludeFields.add("apiRequest");
		excludeFields.add("realizationDate");
		excludeFields.add("paymentProcReq");
		excludeFields.add("branchBankCode");
		excludeFields.add("clearingStatus");
		return excludeFields;
	}

	public long getFeeRefundInstId() {
		return feeRefundInstId;
	}

	public void setFeeRefundInstId(long feeRefundInstId) {
		this.feeRefundInstId = feeRefundInstId;
	}

	public long getFeeRefundId() {
		return feeRefundId;
	}

	public void setFeeRefundId(long feeRefundId) {
		this.feeRefundId = feeRefundId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getFavourName() {
		return favourName;
	}

	public void setFavourName(String favourName) {
		this.favourName = favourName;
	}

	public String getFavourNumber() {
		return favourNumber;
	}

	public void setFavourNumber(String favourNumber) {
		this.favourNumber = favourNumber;
	}

	public String getPayableLoc() {
		return payableLoc;
	}

	public void setPayableLoc(String payableLoc) {
		this.payableLoc = payableLoc;
	}

	public String getPrintingLoc() {
		return printingLoc;
	}

	public void setPrintingLoc(String printingLoc) {
		this.printingLoc = printingLoc;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public long getBankBranchId() {
		return bankBranchId;
	}

	public void setBankBranchId(long bankBranchId) {
		this.bankBranchId = bankBranchId;
	}

	public String getBankBranchCode() {
		return bankBranchCode;
	}

	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
	}

	public String getAcctHolderName() {
		return acctHolderName;
	}

	public void setAcctHolderName(String acctHolderName) {
		this.acctHolderName = acctHolderName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getPaymentCCy() {
		return paymentCCy;
	}

	public void setPaymentCCy(String paymentCCy) {
		this.paymentCCy = paymentCCy;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FeeRefundInstruction getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FeeRefundInstruction beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getIssuingBank() {
		return issuingBank;
	}

	public void setIssuingBank(String issuingBank) {
		this.issuingBank = issuingBank;
	}

	public String getIssuingBankName() {
		return issuingBankName;
	}

	public void setIssuingBankName(String issuingBankName) {
		this.issuingBankName = issuingBankName;
	}

	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

	public String getBankBranchIFSC() {
		return bankBranchIFSC;
	}

	public void setBankBranchIFSC(String bankBranchIFSC) {
		this.bankBranchIFSC = bankBranchIFSC;
	}

	public String getpCCityName() {
		return pCCityName;
	}

	public void setpCCityName(String pCCityName) {
		this.pCCityName = pCCityName;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getClearingStatus() {
		return clearingStatus;
	}

	public void setClearingStatus(String clearingStatus) {
		this.clearingStatus = clearingStatus;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getClearingDate() {
		return clearingDate;
	}

	public void setClearingDate(Date clearingDate) {
		this.clearingDate = clearingDate;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getPartnerBankAc() {
		return partnerBankAc;
	}

	public void setPartnerBankAc(String partnerBankAc) {
		this.partnerBankAc = partnerBankAc;
	}

	public String getPartnerBankAcType() {
		return partnerBankAcType;
	}

	public void setPartnerBankAcType(String partnerBankAcType) {
		this.partnerBankAcType = partnerBankAcType;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public boolean isApiRequest() {
		return apiRequest;
	}

	public void setApiRequest(boolean apiRequest) {
		this.apiRequest = apiRequest;
	}

	public boolean isPaymentProcReq() {
		return paymentProcReq;
	}

	public void setPaymentProcReq(boolean paymentProcReq) {
		this.paymentProcReq = paymentProcReq;
	}

	public String getBranchBankCode() {
		return branchBankCode;
	}

	public void setBranchBankCode(String branchBankCode) {
		this.branchBankCode = branchBankCode;
	}

	public String getLei() {
		return lei;
	}

	public void setLei(String lei) {
		this.lei = lei;
	}
}
