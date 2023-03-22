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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
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
	private Long headerId = Long.MIN_VALUE;
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
	private Long mandateId;
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
	private Long partnerBankId;
	private String grcAdvType;
	private String advType;
	private String advStage;
	private Date grcPeriodEndDate;
	private BigDecimal advAdjusted = BigDecimal.ZERO;
	private Date appDate;
	private String bpiOrHoliday;
	private String bpiTreatment;
	private String bankCode;
	private Long linkedTranId;
	private String presentmentType;
	private String utrNumber;
	private String clearingStatus;
	private boolean finisActive;
	private Date representmentDate;
	private String productCategory;
	private BigDecimal charges = BigDecimal.ZERO;
	private BigDecimal lppAmount = BigDecimal.ZERO;
	private BigDecimal bounceAmount = BigDecimal.ZERO;
	private String bankName;
	private Date approvedDate;
	private Date dueDate;
	private String finBranch;
	private int instNumber;
	private BigDecimal profitSchd = BigDecimal.ZERO;
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private BigDecimal feeSchd = BigDecimal.ZERO;
	private BigDecimal schdPftPaid = BigDecimal.ZERO;
	private BigDecimal schdPriPaid = BigDecimal.ZERO;
	private BigDecimal schdFeePaid = BigDecimal.ZERO;
	private BigDecimal tdsPaid = BigDecimal.ZERO;
	private String emandateSource;
	private Long chequeId;
	private String chequeType;
	private String chequeStatus;
	private Date chequeDate;
	private BigDecimal chequeAmount = BigDecimal.ZERO;;
	private String branchCode;
	private String bounceReason;
	private FinExcessAmount advInExcess;
	private FinExcessAmount emiInAdv;
	private boolean dueExist;
	private boolean bpiPaidOnInstDate;
	private String instrumentType;
	private int schdVersion;
	private String fateCorrection;
	private String employeeNo;
	private Long employerId;
	private String employerName;
	private Long rePresentUploadID;

	@XmlTransient
	private PresentmentDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private FinanceDetail financeDetail;
	private FinExcessAmount excessAmount;
	private List<FinExcessAmount> excessAmountReversal = new ArrayList<>();
	private List<PresentmentDetail> presements = new ArrayList<>();
	private FinExcessAmount emiInAdvance;
	private List<PresentmentCharge> presentmentCharges = new ArrayList<>();
	private EventProperties eventProperties = new EventProperties();

	private List<FinExcessMovement> excessMovements = new ArrayList<>();

	public PresentmentDetail() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(Long headerId) {
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

	public String getPresentmentRef() {
		return presentmentRef;
	}

	public void setPresentmentRef(String presentmentRef) {
		this.presentmentRef = presentmentRef;
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

	public String getHostReference() {
		return hostReference;
	}

	public void setHostReference(String hostReference) {
		this.hostReference = hostReference;
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

	public Long getMandateId() {
		return mandateId;
	}

	public void setMandateId(Long mandateId) {
		this.mandateId = mandateId;
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

	public Long getManualAdviseId() {
		return manualAdviseId;
	}

	public void setManualAdviseId(Long manualAdviseId) {
		this.manualAdviseId = manualAdviseId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}

	public String getMandateStatus() {
		return mandateStatus;
	}

	public void setMandateStatus(String mandateStatus) {
		this.mandateStatus = mandateStatus;
	}

	public Date getMandateExpiryDate() {
		return mandateExpiryDate;
	}

	public void setMandateExpiryDate(Date mandateExpiryDate) {
		this.mandateExpiryDate = mandateExpiryDate;
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

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public Long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(Long partnerBankId) {
		this.partnerBankId = partnerBankId;
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

	public String getAdvStage() {
		return advStage;
	}

	public void setAdvStage(String advStage) {
		this.advStage = advStage;
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

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
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

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public int getInstNumber() {
		return instNumber;
	}

	public void setInstNumber(int instNumber) {
		this.instNumber = instNumber;
	}

	public BigDecimal getProfitSchd() {
		return profitSchd;
	}

	public void setProfitSchd(BigDecimal profitSchd) {
		this.profitSchd = profitSchd;
	}

	public BigDecimal getPrincipalSchd() {
		return principalSchd;
	}

	public void setPrincipalSchd(BigDecimal principalSchd) {
		this.principalSchd = principalSchd;
	}

	public BigDecimal getFeeSchd() {
		return feeSchd;
	}

	public void setFeeSchd(BigDecimal feeSchd) {
		this.feeSchd = feeSchd;
	}

	public BigDecimal getSchdPftPaid() {
		return schdPftPaid;
	}

	public void setSchdPftPaid(BigDecimal schdPftPaid) {
		this.schdPftPaid = schdPftPaid;
	}

	public BigDecimal getSchdPriPaid() {
		return schdPriPaid;
	}

	public void setSchdPriPaid(BigDecimal schdPriPaid) {
		this.schdPriPaid = schdPriPaid;
	}

	public BigDecimal getSchdFeePaid() {
		return schdFeePaid;
	}

	public void setSchdFeePaid(BigDecimal schdFeePaid) {
		this.schdFeePaid = schdFeePaid;
	}

	public BigDecimal getTdsPaid() {
		return tdsPaid;
	}

	public void setTdsPaid(BigDecimal tdsPaid) {
		this.tdsPaid = tdsPaid;
	}

	public String getEmandateSource() {
		return emandateSource;
	}

	public void setEmandateSource(String emandateSource) {
		this.emandateSource = emandateSource;
	}

	public Long getChequeId() {
		return chequeId;
	}

	public void setChequeId(Long chequeId) {
		this.chequeId = chequeId;
	}

	public String getChequeType() {
		return chequeType;
	}

	public void setChequeType(String chequeType) {
		this.chequeType = chequeType;
	}

	public String getChequeStatus() {
		return chequeStatus;
	}

	public void setChequeStatus(String chequeStatus) {
		this.chequeStatus = chequeStatus;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public BigDecimal getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(BigDecimal chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBounceReason() {
		return bounceReason;
	}

	public void setBounceReason(String bounceReason) {
		this.bounceReason = bounceReason;
	}

	public FinExcessAmount getAdvInExcess() {
		return advInExcess;
	}

	public void setAdvInExcess(FinExcessAmount advInExcess) {
		this.advInExcess = advInExcess;
	}

	public FinExcessAmount getEmiInAdv() {
		return emiInAdv;
	}

	public void setEmiInAdv(FinExcessAmount emiInAdv) {
		this.emiInAdv = emiInAdv;
	}

	public boolean isDueExist() {
		return dueExist;
	}

	public void setDueExist(boolean dueExist) {
		this.dueExist = dueExist;
	}

	public boolean isBpiPaidOnInstDate() {
		return bpiPaidOnInstDate;
	}

	public void setBpiPaidOnInstDate(boolean bpiPaidOnInstDate) {
		this.bpiPaidOnInstDate = bpiPaidOnInstDate;
	}

	public String getInstrumentType() {
		return instrumentType;
	}

	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
	}

	public int getSchdVersion() {
		return schdVersion;
	}

	public void setSchdVersion(int schdVersion) {
		this.schdVersion = schdVersion;
	}

	public String getFateCorrection() {
		return fateCorrection;
	}

	public void setFateCorrection(String fateCorrection) {
		this.fateCorrection = fateCorrection;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public Long getEmployerId() {
		return employerId;
	}

	public void setEmployerId(Long employerId) {
		this.employerId = employerId;
	}

	public String getEmployerName() {
		return employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}

	public Long getRePresentUploadID() {
		return rePresentUploadID;
	}

	public void setRePresentUploadID(Long rePresentUploadID) {
		this.rePresentUploadID = rePresentUploadID;
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

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinExcessAmount getExcessAmount() {
		return excessAmount;
	}

	public void setExcessAmount(FinExcessAmount excessAmount) {
		this.excessAmount = excessAmount;
	}

	public List<FinExcessAmount> getExcessAmountReversal() {
		return excessAmountReversal;
	}

	public void setExcessAmountReversal(List<FinExcessAmount> excessAmountReversal) {
		this.excessAmountReversal = excessAmountReversal;
	}

	public List<PresentmentDetail> getPresements() {
		return presements;
	}

	public void setPresements(List<PresentmentDetail> presements) {
		this.presements = presements;
	}

	public FinExcessAmount getEmiInAdvance() {
		return emiInAdvance;
	}

	public void setEmiInAdvance(FinExcessAmount emiInAdvance) {
		this.emiInAdvance = emiInAdvance;
	}

	public List<PresentmentCharge> getPresentmentCharges() {
		return presentmentCharges;
	}

	public void setPresentmentCharges(List<PresentmentCharge> presentmentCharges) {
		this.presentmentCharges = presentmentCharges;
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

	public List<FinExcessMovement> getExcessMovements() {
		return excessMovements;
	}

	public void setExcessMovements(List<FinExcessMovement> excessMovements) {
		this.excessMovements = excessMovements;
	}
}
