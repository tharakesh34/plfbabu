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
 * * FileName : PresentmentDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * * Modified
 * Date : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.pff.presentment.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "id", "finReference", "custCif", "finType", "schDate", "presentmentAmt", "presentmentRef",
		"batchReference", "presentmentId", "status", "mandateType", })
@XmlAccessorType(XmlAccessType.NONE)
public class PresentmentDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "presentmentId")
	private long id = Long.MIN_VALUE;
	@XmlElement(name = "presentmentHeaderId")
	private long headerId = Long.MIN_VALUE;
	private long responseId;
	@XmlElement
	private String batchReference;

	@XmlElement(name = "uniqueReference")
	private String presentmentRef;
	private long finID;
	@XmlElement
	private String finReference;
	private String hostReference;
	@XmlElement(name = "instDate")
	private Date schDate;
	private Date defSchdDate;
	private Date originalSchDate;
	private long mandateId;
	private BigDecimal schAmtDue;
	private BigDecimal schPriDue;
	private BigDecimal schPftDue;
	private BigDecimal schFeeDue;
	private BigDecimal schInsDue;
	private BigDecimal schPenaltyDue;
	private BigDecimal advanceAmt;
	private BigDecimal tDSAmount;
	private long excessID;
	private BigDecimal adviseAmt;
	private int excludeReason;
	@XmlElement(name = "amount")
	private BigDecimal presentmentAmt;
	private int emiNo;
	private int schSeq;
	private long bounceID;
	private String bounceCode;
	private String bounceRemarks;
	private Long manualAdviseId;
	@XmlElement(name = "presentmentStatus")
	private String status;
	private String customerName;
	@XmlElement(name = "cif")
	private String custCif;

	@XmlElement
	private String finType;
	private String finTypeDesc;
	@XmlElement(name = "paymentMode")
	private String mandateType;
	private String mandateStatus;
	private Date mandateExpiryDate;
	private String finCcy;
	private String ecsReturn;
	private long receiptID;

	private String accountNo;
	private String acType;
	private String errorCode;
	private String errorDesc;
	private String entityCode;
	private long partnerBankId;

	private FinanceDetail financeDetail;

	private String grcAdvType;
	private String advType;
	private String advStage;
	private Date grcPeriodEndDate;
	private BigDecimal advAdjusted = BigDecimal.ZERO;
	private Date appDate;
	private String bpiOrHoliday;
	private String bpiTreatment;
	private FinExcessAmount excessAmount;
	private FinExcessAmount excessAmountReversal;
	private List<PresentmentDetail> presements = new ArrayList<>();
	private String bankCode;
	private FinExcessAmount emiInAdvance;
	private Long linkedTranId;
	private String presentmentType;
	private String utrNumber;
	private String clearingStatus;
	private boolean finisActive;
	private Date representmentDate;
	private String productCategory;
	private BigDecimal charges = BigDecimal.ZERO;
	private List<PresentmentCharge> presentmentCharges = new ArrayList<>();
	private BigDecimal lppAmount = BigDecimal.ZERO;
	private BigDecimal bounceAmount = BigDecimal.ZERO;
	private String bankName;
	private Date approvedDate;
	private Date dueDate;

	public PresentmentDetail() {
		super();
	}

	public String getMandateStatus() {
		return mandateStatus;
	}

	public void setMandateStatus(String mandateStatus) {
		this.mandateStatus = mandateStatus;
	}

	public PresentmentDetail copyEntity() {
		PresentmentDetail entity = new PresentmentDetail();
		entity.setId(this.id);
		entity.setHeaderId(this.headerId);
		entity.setPresentmentRef(this.presentmentRef);
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setHostReference(this.hostReference);
		entity.setSchDate(this.schDate);
		entity.setDefSchdDate(this.defSchdDate);
		entity.setOriginalSchDate(this.originalSchDate);
		entity.setMandateId(this.mandateId);
		entity.setSchAmtDue(this.schAmtDue);
		entity.setSchPriDue(this.schPriDue);
		entity.setSchPftDue(this.schPftDue);
		entity.setSchFeeDue(this.schFeeDue);
		entity.setSchInsDue(this.schInsDue);
		entity.setSchPenaltyDue(this.schPenaltyDue);
		entity.setAdvanceAmt(this.advanceAmt);
		entity.settDSAmount(this.tDSAmount);
		entity.setExcessID(this.excessID);
		entity.setAdviseAmt(this.adviseAmt);
		entity.setExcludeReason(this.excludeReason);
		entity.setPresentmentAmt(this.presentmentAmt);
		entity.setEmiNo(this.emiNo);
		entity.setSchSeq(this.schSeq);
		entity.setBounceID(this.bounceID);
		entity.setBounceCode(this.bounceCode);
		entity.setBounceRemarks(this.bounceRemarks);
		entity.setManualAdviseId(this.manualAdviseId);
		entity.setStatus(this.status);
		entity.setNewRecord(super.isNewRecord());
		entity.setCustomerName(this.customerName);
		entity.setFinType(this.finType);
		entity.setFinTypeDesc(this.finTypeDesc);
		entity.setMandateType(this.mandateType);
		entity.setMandateStatus(this.mandateStatus);
		entity.setMandateExpiryDate(this.mandateExpiryDate);
		entity.setFinCcy(this.finCcy);
		entity.setEcsReturn(this.ecsReturn);
		entity.setReceiptID(this.receiptID);
		entity.setAccountNo(this.accountNo);
		entity.setAcType(this.acType);
		entity.setErrorCode(this.errorCode);
		entity.setErrorDesc(this.errorDesc);
		entity.setEntityCode(this.entityCode);
		entity.setPartnerBankId(this.partnerBankId);
		entity.setFinanceDetail(this.financeDetail);
		entity.setGrcAdvType(this.grcAdvType);
		entity.setAdvType(this.advType);
		entity.setAdvStage(this.advStage);
		entity.setGrcPeriodEndDate(this.grcPeriodEndDate);
		entity.setAdvAdjusted(this.advAdjusted);
		entity.setAppDate(this.appDate);
		entity.setBpiOrHoliday(this.bpiOrHoliday);
		entity.setBpiTreatment(this.bpiTreatment);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setPresentmentType(this.presentmentType);
		entity.setUtrNumber(this.utrNumber);
		entity.setClearingStatus(this.clearingStatus);
		entity.setFinisActive(this.finisActive);
		entity.setRepresentmentDate(this.representmentDate);
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
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("accountNo");
		excludeFields.add("acType");
		excludeFields.add("bounceReason");
		excludeFields.add("financeDetail");
		excludeFields.add("grcAdvType");
		excludeFields.add("advType");
		excludeFields.add("grcPeriodEndDate");
		excludeFields.add("advAdjusted");
		excludeFields.add("advStage");
		excludeFields.add("partnerBankId");
		excludeFields.add("bpiOrHoliday");
		excludeFields.add("bpiTreatment");
		excludeFields.add("excessAmount");
		excludeFields.add("excessAmountReversal");
		excludeFields.add("bankCode");
		excludeFields.add("emiInAdvance");
		excludeFields.add("linkedTranId");
		excludeFields.add("presentmentType");
		excludeFields.add("hostReference");
		excludeFields.add("representmentDate");

		return excludeFields;
	}

	@XmlTransient
	private PresentmentDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String bounceReason;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public long getResponseId() {
		return responseId;
	}

	public void setResponseId(long responseId) {
		this.responseId = responseId;
	}

	public String getBatchReference() {
		return batchReference;
	}

	public void setBatchReference(String batchReference) {
		this.batchReference = batchReference;
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

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public Date getDefSchdDate() {
		return defSchdDate;
	}

	public void setDefSchdDate(Date defSchdDate) {
		this.defSchdDate = defSchdDate;
	}

	public Date getOriginalSchDate() {
		return originalSchDate;
	}

	public void setOriginalSchDate(Date originalSchDate) {
		this.originalSchDate = originalSchDate;
	}

	public BigDecimal getSchAmtDue() {
		return schAmtDue;
	}

	public void setSchAmtDue(BigDecimal schAmtDue) {
		this.schAmtDue = schAmtDue;
	}

	public BigDecimal getSchPriDue() {
		return schPriDue;
	}

	public void setSchPriDue(BigDecimal schPriDue) {
		this.schPriDue = schPriDue;
	}

	public BigDecimal getSchPftDue() {
		return schPftDue;
	}

	public void setSchPftDue(BigDecimal schPftDue) {
		this.schPftDue = schPftDue;
	}

	public BigDecimal getSchFeeDue() {
		return schFeeDue;
	}

	public void setSchFeeDue(BigDecimal schFeeDue) {
		this.schFeeDue = schFeeDue;
	}

	public BigDecimal getSchInsDue() {
		return schInsDue;
	}

	public void setSchInsDue(BigDecimal schInsDue) {
		this.schInsDue = schInsDue;
	}

	public BigDecimal getSchPenaltyDue() {
		return schPenaltyDue;
	}

	public void setSchPenaltyDue(BigDecimal schPenaltyDue) {
		this.schPenaltyDue = schPenaltyDue;
	}

	public BigDecimal getAdvanceAmt() {
		return advanceAmt;
	}

	public void setAdvanceAmt(BigDecimal advanceAmt) {
		this.advanceAmt = advanceAmt;
	}

	public BigDecimal getAdviseAmt() {
		return adviseAmt;
	}

	public void setAdviseAmt(BigDecimal adviseAmt) {
		this.adviseAmt = adviseAmt;
	}

	public int getExcludeReason() {
		return excludeReason;
	}

	public void setExcludeReason(int excludeReason) {
		this.excludeReason = excludeReason;
	}

	public BigDecimal getPresentmentAmt() {
		return presentmentAmt;
	}

	public void setPresentmentAmt(BigDecimal presentmentAmt) {
		this.presentmentAmt = presentmentAmt;
	}

	public int getEmiNo() {
		return emiNo;
	}

	public void setEmiNo(int emiNo) {
		this.emiNo = emiNo;
	}

	public int getSchSeq() {
		return schSeq;
	}

	public void setSchSeq(int schSeq) {
		this.schSeq = schSeq;
	}

	public long getBounceID() {
		return bounceID;
	}

	public void setBounceID(long bounceID) {
		this.bounceID = bounceID;
	}

	public String getBounceCode() {
		return bounceCode;
	}

	public void setBounceCode(String bounceCode) {
		this.bounceCode = bounceCode;
	}

	public String getBounceRemarks() {
		return bounceRemarks;
	}

	public void setBounceRemarks(String bounceRemarks) {
		this.bounceRemarks = bounceRemarks;
	}

	public BigDecimal gettDSAmount() {
		return tDSAmount;
	}

	public void settDSAmount(BigDecimal tDSAmount) {
		this.tDSAmount = tDSAmount;
	}

	public long getExcessID() {
		return excessID;
	}

	public void setExcessID(long excessID) {
		this.excessID = excessID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public PresentmentDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(PresentmentDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}

	public Date getMandateExpiryDate() {
		return mandateExpiryDate;
	}

	public void setMandateExpiryDate(Date mandateExpiryDate) {
		this.mandateExpiryDate = mandateExpiryDate;
	}

	public long getMandateId() {
		return mandateId;
	}

	public void setMandateId(long mandateId) {
		this.mandateId = mandateId;
	}

	public String getPresentmentRef() {
		return presentmentRef;
	}

	public void setPresentmentRef(String presentmentRef) {
		this.presentmentRef = presentmentRef;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getEcsReturn() {
		return ecsReturn;
	}

	public void setEcsReturn(String ecsReturn) {
		this.ecsReturn = ecsReturn;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public String getBounceReason() {
		return bounceReason;
	}

	public void setBounceReason(String bounceReason) {
		this.bounceReason = bounceReason;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public Long getManualAdviseId() {
		return manualAdviseId;
	}

	public void setManualAdviseId(Long manualAdviseId) {
		this.manualAdviseId = manualAdviseId;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	/**
	 * @return the financeDetail
	 */
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	/**
	 * @param financeDetail the financeDetail to set
	 */
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public String getGrcAdvType() {
		return grcAdvType;
	}

	public void setGrcAdvType(String grcAdvType) {
		this.grcAdvType = grcAdvType;
	}

	public String getAdvType() {
		return advType;
	}

	public void setAdvType(String advType) {
		this.advType = advType;
	}

	public Date getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}

	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	public BigDecimal getAdvAdjusted() {
		return advAdjusted;
	}

	public void setAdvAdjusted(BigDecimal advAdjusted) {
		this.advAdjusted = advAdjusted;
	}

	public String getAdvStage() {
		return advStage;
	}

	public void setAdvStage(String advStage) {
		this.advStage = advStage;
	}

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getBpiOrHoliday() {
		return bpiOrHoliday;
	}

	public void setBpiOrHoliday(String bpiOrHoliday) {
		this.bpiOrHoliday = bpiOrHoliday;
	}

	public String getBpiTreatment() {
		return bpiTreatment;
	}

	public void setBpiTreatment(String bpiTreatment) {
		this.bpiTreatment = bpiTreatment;
	}

	public FinExcessAmount getExcessAmount() {
		return excessAmount;
	}

	public void setExcessAmount(FinExcessAmount excessAmount) {
		this.excessAmount = excessAmount;
	}

	public FinExcessAmount getExcessAmountReversal() {
		return excessAmountReversal;
	}

	public void setExcessAmountReversal(FinExcessAmount excessAmountReversal) {
		this.excessAmountReversal = excessAmountReversal;
	}

	public List<PresentmentDetail> getPresements() {
		return presements;
	}

	public void setPresements(List<PresentmentDetail> presements) {
		this.presements = presements;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public FinExcessAmount getEmiInAdvance() {
		return emiInAdvance;
	}

	public void setEmiInAdvance(FinExcessAmount emiInAdvance) {
		this.emiInAdvance = emiInAdvance;
	}

	public Long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(Long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public String getPresentmentType() {
		return presentmentType;
	}

	public void setPresentmentType(String presentmentType) {
		this.presentmentType = presentmentType;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

	public String getClearingStatus() {
		return clearingStatus;
	}

	public void setClearingStatus(String clearingStatus) {
		this.clearingStatus = clearingStatus;
	}

	public boolean isFinisActive() {
		return finisActive;
	}

	public void setFinisActive(boolean finisActive) {
		this.finisActive = finisActive;
	}

	public String getHostReference() {
		return hostReference;
	}

	public void setHostReference(String hostReference) {
		this.hostReference = hostReference;
	}

	public Date getRepresentmentDate() {
		return representmentDate;
	}

	public void setRepresentmentDate(Date representmentDate) {
		this.representmentDate = representmentDate;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public BigDecimal getCharges() {
		return charges;
	}

	public void setCharges(BigDecimal charges) {
		this.charges = charges;
	}

	public List<PresentmentCharge> getPresentmentCharges() {
		return presentmentCharges;
	}

	public void setPresentmentCharges(List<PresentmentCharge> presentmentCharges) {
		this.presentmentCharges = presentmentCharges;
	}

	public BigDecimal getLppAmount() {
		return lppAmount;
	}

	public void setLppAmount(BigDecimal lppAmount) {
		this.lppAmount = lppAmount;
	}

	public BigDecimal getBounceAmount() {
		return bounceAmount;
	}

	public void setBounceAmount(BigDecimal bounceAmount) {
		this.bounceAmount = bounceAmount;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

}