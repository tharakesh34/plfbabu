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
 * * FileName : FinAdvancePayments.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-10-2011 * * Modified
 * Date : 13-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinAdvancePayments table</b>.<br>
 * 
 */
@XmlType(propOrder = { "paymentDetail", "paymentType", "llDate", "amtToBeReleased", "branchBankCode", "branchBankName",
		"branchCode", "iFSC", "custShrtName", "beneficiaryAccNo", "beneficiaryName", "linkedTranId", "partnerBankID",
		"partnerbankCode", "phoneCountryCode", "phoneAreaCode", "phoneNumber", "remarks", "bankCode",
		"liabilityHoldName", "payableLoc", "printingLoc", "valueDate", "llReferenceNo" })

@XmlAccessorType(XmlAccessType.NONE)
public class FinAdvancePayments extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -6234931333270161797L;
	@XmlElement(name = "disbInstId")
	private long paymentId = Long.MIN_VALUE;
	private long finID;
	@XmlElement
	private String finReference;
	private int paymentSeq;
	private int disbSeq;
	private String serviceReqNo;

	@XmlElement(name = "disbParty")
	private String paymentDetail;

	@XmlElement(name = "disbAmount")
	private BigDecimal amtToBeReleased = BigDecimal.ZERO;

	@XmlElement(name = "favourName")
	private String liabilityHoldName;

	@XmlElement(name = "acHolderName")
	private String beneficiaryName;

	@XmlElement(name = "accountNo")
	private String beneficiaryAccNo;
	private String reEnterBeneficiaryAccNo;
	private String description;

	@XmlElement(name = "disbType")
	private String paymentType;

	@XmlElement(name = "chequeNo")
	private String llReferenceNo;

	@XmlElement(name = "disbDate")
	private Date llDate;
	private BigDecimal custContribution = BigDecimal.ZERO;
	private BigDecimal sellerContribution = BigDecimal.ZERO;

	@XmlElement
	private String remarks;

	@XmlElement(name = "issueBank")
	private String bankCode;
	private String bankName;

	@XmlElement(name = "bankCode")
	private String branchBankCode;
	@XmlElement(name = "bankName")
	private String branchBankName;

	@XmlElement
	private String branchCode;
	private String branchDesc;
	private String city;

	@XmlElement(name = "ifsc")
	private String iFSC;

	@XmlElement
	private String payableLoc;

	@XmlElement
	private String printingLoc;

	private String printingLocDesc;

	@XmlElement
	private Date valueDate;
	private long bankBranchID;

	@XmlElement
	private String phoneCountryCode;

	@XmlElement
	private String phoneAreaCode;

	@XmlElement
	private String phoneNumber;
	private Date clearingDate;
	@XmlElement(name = "DisbStatus")
	private String status;
	private String clearingStatus;
	private boolean active;
	private Date inputDate;
	private String disbCCy;
	private boolean pOIssued;
	private String lovValue;
	private FinAdvancePayments befImage;
	private LoggedInUser userDetails;
	@XmlElement(name = "partnerBankId")
	private long partnerBankID;
	@XmlElement
	private String partnerbankCode;
	private String partnerBankName;
	@XmlElement
	private String finType;
	private List<FinanceDisbursement> financeDisbursements;

	@XmlElement
	private String custShrtName;
	private long linkedTranId;
	private String partnerBankAcType;
	private String transactionRef;
	private String rejectReason;
	private String partnerBankAc;
	private boolean alwFileDownload;
	private String fileNamePrefix;
	@XmlElement
	private String channel;
	@XmlElement
	private String entityCode;
	private String vasReference;
	private Long providerId;
	private boolean paymentProcReq;
	private int tempSeq = 0;
	private String tempReference = null;

	// Auto Disbursement Download
	private String configName;
	private String fileName;
	private String autoDisbStatus;
	private String errorCode;
	private String pickUpBatchId;
	private long custID;
	private Date reversedDate;
	private Date downloadedon;
	private Date realizationDate;
	private boolean onlineProcReq;

	@XmlElement
	private boolean holdDisbursement = false;

	private Date postDate;
	private String dealerShortCode;
	private String productShortCode;
	private boolean postingQdp = false;

	@XmlElement
	private Date fromDate;
	@XmlElement
	private Date toDate;
	@XmlElement(name = "NetDisbursementAmount")
	private BigDecimal netDisbAmt;
	@XmlElement(name = "docName")
	private String documentName;
	@XmlElement(name = "docContent")
	private byte[] docImage;
	@XmlElement(name = "docFormat")
	private String docType;
	@XmlElement
	private String vasProductCode;

	private Long disbResponseBatchId;
	private long userId;
	private String downloadType;
	@XmlElement
	private String lei;

	@XmlElement
	private String micr;

	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public FinAdvancePayments() {
		super();
	}

	public FinAdvancePayments copyEntity() {
		FinAdvancePayments entity = new FinAdvancePayments();
		entity.setPaymentId(this.paymentId);
		entity.setPaymentId(this.finID);
		entity.setFinReference(this.finReference);
		entity.setPaymentSeq(this.paymentSeq);
		entity.setDisbSeq(this.disbSeq);
		entity.setServiceReqNo(this.serviceReqNo);
		entity.setPaymentDetail(this.paymentDetail);
		entity.setAmtToBeReleased(this.amtToBeReleased);
		entity.setLiabilityHoldName(this.liabilityHoldName);
		entity.setBeneficiaryName(this.beneficiaryName);
		entity.setBeneficiaryAccNo(this.beneficiaryAccNo);
		entity.setReEnterBeneficiaryAccNo(this.reEnterBeneficiaryAccNo);
		entity.setDescription(this.description);
		entity.setPaymentType(this.paymentType);
		entity.setLLReferenceNo(this.llReferenceNo);
		entity.setLLDate(this.llDate);
		entity.setCustContribution(this.custContribution);
		entity.setSellerContribution(this.sellerContribution);
		entity.setRemarks(this.remarks);
		entity.setBankCode(this.bankCode);
		entity.setBankName(this.bankName);
		entity.setBranchBankCode(this.branchBankCode);
		entity.setBranchBankName(this.branchBankName);
		entity.setBranchCode(this.branchCode);
		entity.setBranchDesc(this.branchDesc);
		entity.setCity(this.city);
		entity.setiFSC(this.iFSC);
		entity.setPayableLoc(this.payableLoc);
		entity.setPrintingLoc(this.printingLoc);
		entity.setPrintingLocDesc(this.printingLocDesc);
		entity.setValueDate(this.valueDate);
		entity.setBankBranchID(this.bankBranchID);
		entity.setPhoneCountryCode(this.phoneCountryCode);
		entity.setPhoneAreaCode(this.phoneAreaCode);
		entity.setPhoneNumber(this.phoneNumber);
		entity.setClearingDate(this.clearingDate);
		entity.setStatus(this.status);
		entity.setClearingStatus(this.clearingStatus);
		entity.setActive(this.active);
		entity.setInputDate(this.inputDate);
		entity.setDisbCCy(this.disbCCy);
		entity.setpOIssued(this.pOIssued);
		entity.setNewRecord(super.isNewRecord());
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setPartnerBankID(this.partnerBankID);
		entity.setPartnerbankCode(this.partnerbankCode);
		entity.setPartnerBankName(this.partnerBankName);
		entity.setFinType(this.finType);
		if (financeDisbursements != null) {
			entity.setFinanceDisbursements(new ArrayList<>());
			this.financeDisbursements.stream()
					.forEach(e -> entity.getFinanceDisbursements().add(e == null ? null : e.copyEntity()));
		}
		entity.setCustShrtName(this.custShrtName);
		entity.setLinkedTranId(this.linkedTranId);
		entity.setPartnerBankAcType(this.partnerBankAcType);
		entity.setTransactionRef(this.transactionRef);
		entity.setRejectReason(this.rejectReason);
		entity.setPartnerBankAc(this.partnerBankAc);
		entity.setAlwFileDownload(this.alwFileDownload);
		entity.setFileNamePrefix(this.fileNamePrefix);
		entity.setChannel(this.channel);
		entity.setEntityCode(this.entityCode);
		entity.setVasReference(this.vasReference);
		entity.setProviderId(this.providerId);
		entity.setPaymentProcReq(this.paymentProcReq);
		entity.setTempSeq(this.tempSeq);
		entity.setTempReference(this.tempReference);
		entity.setConfigName(this.configName);
		entity.setFileName(this.fileName);
		entity.setAutoDisbStatus(this.autoDisbStatus);
		entity.setErrorCode(this.errorCode);
		entity.setPickUpBatchId(this.pickUpBatchId);
		entity.setCustID(this.custID);
		entity.setReversedDate(this.reversedDate);
		entity.setDownloadedon(this.downloadedon);
		entity.setRealizationDate(this.realizationDate);
		entity.setOnlineProcReq(this.onlineProcReq);
		entity.setHoldDisbursement(this.holdDisbursement);
		entity.setDownloadType(this.downloadType);
		entity.setLei(this.lei);
		entity.setPostDate(this.postDate);
		entity.setDealerShortCode(this.dealerShortCode);
		entity.setProductShortCode(this.productShortCode);
		entity.setPostingQdp(this.postingQdp);
		entity.setFromDate(this.fromDate);
		entity.setToDate(this.toDate);
		entity.setDocumentName(this.documentName);
		entity.setDocImage(this.docImage);
		entity.setDocType(this.docType);
		entity.setVasProductCode(this.vasProductCode);
		entity.setDisbResponseBatchId(this.disbResponseBatchId);
		entity.setUserId(this.userId);
		entity.setMicr(this.micr);
		entity.setNetDisbAmt(this.netDisbAmt);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
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
		excludeFields.add("serviceReqNo");
		excludeFields.add("providerId");
		excludeFields.add("printingLocDesc");
		excludeFields.add("financeDisbursements");
		excludeFields.add("paymentProcReq");
		excludeFields.add("tempSeq");
		excludeFields.add("tempReference");
		excludeFields.add("configName");
		excludeFields.add("fileName");
		excludeFields.add("autoDisbStatus");
		excludeFields.add("errorCode");
		excludeFields.add("pickUpBatchId");
		excludeFields.add("custID");
		excludeFields.add("downloadedon");
		excludeFields.add("reversedDate");
		excludeFields.add("onlineProcReq");
		excludeFields.add("holdDisbursement");
		excludeFields.add("postDate");
		excludeFields.add("dealerShortCode");
		excludeFields.add("productShortCode");
		excludeFields.add("postingQdp");
		excludeFields.add("fromDate");
		excludeFields.add("toDate");
		excludeFields.add("documentName");
		excludeFields.add("docImage");
		excludeFields.add("docType");
		excludeFields.add("vasProductCode");
		excludeFields.add("userId");
		excludeFields.add("disbResponseBatchId");
		excludeFields.add("clearingStatus");
		excludeFields.add("downloadType");
		excludeFields.add("micr");
		excludeFields.add("netDisbAmt");
		return excludeFields;
	}

	public String getServiceReqNo() {
		return serviceReqNo;
	}

	public void setServiceReqNo(String serviceReqNo) {
		this.serviceReqNo = serviceReqNo;
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

	public String getClearingStatus() {
		return clearingStatus;
	}

	public void setClearingStatus(String clearingStatus) {
		this.clearingStatus = clearingStatus;
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

	public String getVasReference() {
		return vasReference;
	}

	public void setVasReference(String vasReference) {
		this.vasReference = vasReference;
	}

	public String getReEnterBeneficiaryAccNo() {
		return reEnterBeneficiaryAccNo;
	}

	public void setReEnterBeneficiaryAccNo(String reEnterBeneficiaryAccNo) {
		this.reEnterBeneficiaryAccNo = reEnterBeneficiaryAccNo;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public String getPrintingLocDesc() {
		return printingLocDesc;
	}

	public void setPrintingLocDesc(String printingLocDesc) {
		this.printingLocDesc = printingLocDesc;
	}

	public List<FinanceDisbursement> getFinanceDisbursements() {
		return financeDisbursements;
	}

	public void setFinanceDisbursements(List<FinanceDisbursement> financeDisbursements) {
		this.financeDisbursements = financeDisbursements;
	}

	public boolean isPaymentProcReq() {
		return paymentProcReq;
	}

	public void setPaymentProcReq(boolean paymentProcReq) {
		this.paymentProcReq = paymentProcReq;
	}

	public int getTempSeq() {
		return tempSeq;
	}

	public void setTempSeq(int tempSeq) {
		this.tempSeq = tempSeq;
	}

	public String getTempReference() {
		return tempReference;
	}

	public void setTempReference(String tempReference) {
		this.tempReference = tempReference;
	}

	public Date getDownloadedon() {
		return downloadedon;
	}

	public void setDownloadedon(Date downloadedon) {
		this.downloadedon = downloadedon;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAutoDisbStatus() {
		return autoDisbStatus;
	}

	public void setAutoDisbStatus(String autoDisbStatus) {
		this.autoDisbStatus = autoDisbStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getPickUpBatchId() {
		return pickUpBatchId;
	}

	public void setPickUpBatchId(String pickUpBatchId) {
		this.pickUpBatchId = pickUpBatchId;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public Date getReversedDate() {
		return reversedDate;
	}

	public void setReversedDate(Date reversedDate) {
		this.reversedDate = reversedDate;
	}

	public boolean isOnlineProcReq() {
		return onlineProcReq;
	}

	public void setOnlineProcReq(boolean onlineProcReq) {
		this.onlineProcReq = onlineProcReq;
	}

	public boolean isHoldDisbursement() {
		return holdDisbursement;
	}

	public void setHoldDisbursement(boolean holdDisbursement) {
		this.holdDisbursement = holdDisbursement;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public String getDealerShortCode() {
		return dealerShortCode;
	}

	public void setDealerShortCode(String dealerShortCode) {
		this.dealerShortCode = dealerShortCode;
	}

	public String getProductShortCode() {
		return productShortCode;
	}

	public void setProductShortCode(String productShortCode) {
		this.productShortCode = productShortCode;
	}

	public boolean isPostingQdp() {
		return postingQdp;
	}

	public void setPostingQdp(boolean postingQdp) {
		this.postingQdp = postingQdp;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getVasProductCode() {
		return vasProductCode;
	}

	public void setVasProductCode(String vasProductCode) {
		this.vasProductCode = vasProductCode;
	}

	public Long getDisbResponseBatchId() {
		return disbResponseBatchId;
	}

	public void setDisbResponseBatchId(Long disbResponseBatchId) {
		this.disbResponseBatchId = disbResponseBatchId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getDownloadType() {
		return downloadType;
	}

	public void setDownloadType(String downloadType) {
		this.downloadType = downloadType;
	}

	public String getLei() {
		return lei;
	}

	public void setLei(String lei) {
		this.lei = lei;
	}

	public String getMicr() {
		return micr;
	}

	public void setMicr(String micr) {
		this.micr = micr;
	}

	public BigDecimal getNetDisbAmt() {
		return netDisbAmt;
	}

	public void setNetDisbAmt(BigDecimal netDisbAmt) {
		this.netDisbAmt = netDisbAmt;
	}
}
