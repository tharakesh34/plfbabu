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
package com.pennant.backend.model.payment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennanttech.model.adapter.DateFormatterAdapter;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PaymentInstruction table</b>.<br>
 * 
 */
@XmlType(propOrder = { "paymentInstructionId", "paymentId", "paymentType", "paymentAmount", "remarks", "bankCode",
		"favourName", "favourNumber", "payableLoc", "printingLoc", "valueDate", "postDate", "bankBranchId",
		"acctHolderName", "accountNo", "phoneCountryCode", "phoneNumber", "clearingdate", "active", "paymentCCy" })
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentInstruction extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long paymentInstructionId = Long.MIN_VALUE;
	private long paymentId;

	private String paymentType;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
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
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
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

	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date clearingdate;
	private String active;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private PaymentInstruction befImage;
	@XmlTransient
	private LoggedInUser userDetails;

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

	public PaymentInstruction() {
		super();
	}

	public PaymentInstruction(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("partnerBankCode");
		excludeFields.add("partnerBankName");
		excludeFields.add("bankBranchIFSC");
		excludeFields.add("bankBranchCode");
		excludeFields.add("issuingBankName");
		excludeFields.add("pCCityName");
		excludeFields.add("branchDesc");
		excludeFields.add("bankName");
		return excludeFields;
	}

	public Map<String, Object> getDeclaredFieldValues(Map<String, Object> paymentInstMap) {

		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				paymentInstMap.put("pi_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {

			}
		}
		return paymentInstMap;
	}

	public long getId() {
		return paymentInstructionId;
	}

	public void setId(long id) {
		this.paymentInstructionId = id;
	}

	public long getPaymentInstructionId() {
		return paymentInstructionId;
	}

	public void setPaymentInstructionId(long paymentInstructionId) {
		this.paymentInstructionId = paymentInstructionId;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
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

	public Date getClearingdate() {
		return clearingdate;
	}

	public void setClearingdate(Date clearingdate) {
		this.clearingdate = clearingdate;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
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

	public PaymentInstruction getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PaymentInstruction beforeImage) {
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

}
