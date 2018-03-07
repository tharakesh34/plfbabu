/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  EtihadCreditBureauDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>EtihadCreditBureauDetail table</b>.<br>
 * 
 */
@XmlType(propOrder = { "paymentDetail", "paymentType", "llDate", "amtToBeReleased", "remarks", "bankCode",
		"liabilityHoldName", "payableLoc", "printingLoc", "valueDate", "llReferenceNo", "branchBankCode", "branchCode",
		"iFSC", "beneficiaryAccNo", "beneficiaryName", "partnerBankID", "phoneCountryCode", "phoneAreaCode",
		"phoneNumber" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinAdvancePayments extends AbstractWorkflowEntity implements Entity {

	private static final long	serialVersionUID	= -6234931333270161797L;

	private long				paymentId			= Long.MIN_VALUE;
	private String				finReference;
	private int					paymentSeq;
	private int					disbSeq;

	@XmlElement(name = "disbParty")
	private String				paymentDetail;

	@XmlElement(name = "disbAmount")
	private BigDecimal			amtToBeReleased		= BigDecimal.ZERO;

	@XmlElement(name = "favourName")
	private String				liabilityHoldName;

	@XmlElement(name = "acHolderName")
	private String				beneficiaryName;

	@XmlElement(name = "accountNo")
	private String				beneficiaryAccNo;
	private String				description;

	@XmlElement(name = "disbType")
	private String				paymentType;

	@XmlElement(name = "chequeNo")
	private String				llReferenceNo;

	@XmlElement(name = "disbDate")
	private Date				llDate;
	private BigDecimal			custContribution	= BigDecimal.ZERO;
	private BigDecimal			sellerContribution	= BigDecimal.ZERO;

	@XmlElement
	private String				remarks;

	@XmlElement(name = "issueBank")
	private String				bankCode;
	private String				bankName;

	@XmlElement(name = "bankCode")
	private String				branchBankCode;
	private String				branchBankName;

	@XmlElement
	private String				branchCode;
	private String				branchDesc;
	private String				city;

	@XmlElement(name = "ifsc")
	private String				iFSC;

	@XmlElement
	private String				payableLoc;

	@XmlElement
	private String				printingLoc;

	@XmlElement
	private Date				valueDate;
	private long				bankBranchID;

	@XmlElement
	private String				phoneCountryCode;

	@XmlElement
	private String				phoneAreaCode;

	@XmlElement
	private String				phoneNumber;
	private Date				clearingDate;
	private String				status;
	private boolean				active;
	private Date				inputDate;
	private String				disbCCy;
	private boolean				pOIssued;

	private boolean				newRecord			= false;
	private String				lovValue;
	private FinAdvancePayments	befImage;
	private LoggedInUser		userDetails;
	@XmlElement(name = "partnerBankId")
	private long				partnerBankID;
	private String				partnerbankCode;
	private String				partnerBankName;
	private String				finType;
	private String				custShrtName;
	private long				linkedTranId;
	private String				partnerBankAcType;
	private String				transactionRef;
	private String				rejectReason;

	private String				partnerBankAc;
	private boolean				alwFileDownload;
	private String				fileNamePrefix;
	private String				channel;
	private String              entityCode;
	
	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public FinAdvancePayments() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("branchCode");
		excludeFields.add("branchDesc");
		excludeFields.add("bankName");
		excludeFields.add("city");
		excludeFields.add("iFSC");
		excludeFields.add("branchBankCode");
		excludeFields.add("branchBankName");
		excludeFields.add("partnerbankCode");
		excludeFields.add("partnerBankName");
		excludeFields.add("finType");
		excludeFields.add("custShrtName");
		excludeFields.add("partnerBankAcType");
		excludeFields.add("rejectReason");
		excludeFields.add("partnerBankAc");
		excludeFields.add("alwFileDownload");
		excludeFields.add("fileNamePrefix");
		excludeFields.add("channel");
		excludeFields.add("entityCode");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@Override
	public long getId() {

		return paymentId;

	}

	@Override
	public void setId(long paymentId) {
		this.paymentId = paymentId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public int getPaymentSeq() {
		return paymentSeq;
	}

	public void setPaymentSeq(int paymentSeq) {
		this.paymentSeq = paymentSeq;
	}

	public String getPaymentDetail() {
		return paymentDetail;
	}

	public void setPaymentDetail(String paymentDetail) {
		this.paymentDetail = paymentDetail;
	}

	public BigDecimal getAmtToBeReleased() {
		return amtToBeReleased;
	}

	public void setAmtToBeReleased(BigDecimal amtToBeReleased) {
		this.amtToBeReleased = amtToBeReleased;
	}

	public String getLiabilityHoldName() {
		return liabilityHoldName;
	}

	public void setLiabilityHoldName(String liabilityHoldName) {
		this.liabilityHoldName = liabilityHoldName;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getBeneficiaryAccNo() {
		return beneficiaryAccNo;
	}

	public void setBeneficiaryAccNo(String beneficiaryAccNo) {
		this.beneficiaryAccNo = beneficiaryAccNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getLlReferenceNo() {
		return llReferenceNo;
	}

	public void setLLReferenceNo(String llReferenceNo) {
		this.llReferenceNo = llReferenceNo;
	}

	public Date getLlDate() {
		return llDate;
	}

	public void setLLDate(Date llDate) {
		this.llDate = llDate;
	}

	public BigDecimal getCustContribution() {
		return custContribution;
	}

	public void setCustContribution(BigDecimal custContribution) {
		this.custContribution = custContribution;
	}

	public BigDecimal getSellerContribution() {
		return sellerContribution;
	}

	public void setSellerContribution(BigDecimal sellerContribution) {
		this.sellerContribution = sellerContribution;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinAdvancePayments getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinAdvancePayments beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
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

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getPhoneAreaCode() {
		return phoneAreaCode;
	}

	public void setPhoneAreaCode(String phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPOStatus() {
		return status;
	}

	public void setPOStatus(String status) {
		this.status = status;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getDisbCCy() {
		return disbCCy;
	}

	public void setDisbCCy(String disbCCy) {
		this.disbCCy = disbCCy;
	}

	public Date getInputDate() {
		return inputDate;
	}

	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getiFSC() {
		return iFSC;
	}

	public void setiFSC(String iFSC) {
		this.iFSC = iFSC;
	}

	public long getBankBranchID() {
		return bankBranchID;
	}

	public void setBankBranchID(long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public Date getClearingDate() {
		return clearingDate;
	}

	public void setClearingDate(Date clearingDate) {
		this.clearingDate = clearingDate;
	}

	public String getBranchBankCode() {
		return branchBankCode;
	}

	public void setBranchBankCode(String branchBankCode) {
		this.branchBankCode = branchBankCode;
	}

	public String getBranchBankName() {
		return branchBankName;
	}

	public void setBranchBankName(String branchBankName) {
		this.branchBankName = branchBankName;
	}

	public boolean ispOIssued() {
		return pOIssued;
	}

	public void setpOIssued(boolean pOIssued) {
		this.pOIssued = pOIssued;
	}

	public int getDisbSeq() {
		return disbSeq;
	}

	public void setDisbSeq(int disbSeq) {
		this.disbSeq = disbSeq;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	public long getPartnerBankID() {
		return partnerBankID;
	}

	public void setPartnerBankID(long partnerBankID) {
		this.partnerBankID = partnerBankID;
	}

	public String getPartnerbankCode() {
		return partnerbankCode;
	}

	public void setPartnerbankCode(String partnerbankCode) {
		this.partnerbankCode = partnerbankCode;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
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

	public boolean isAlwFileDownload() {
		return alwFileDownload;
	}

	public void setAlwFileDownload(boolean alwFileDownload) {
		this.alwFileDownload = alwFileDownload;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

}
