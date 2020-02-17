package com.pennant.backend.model.finance;

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
 * FileName    		:  FinTypeFees.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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
 * Model class for the <b>Collateral table</b>.<br>
 *
 */
@XmlType(propOrder = { "feeCategory", "schdDate", "feeTypeCode", "actualAmount", "waivedAmount", "paidAmount",
		"feeScheduleMethod", "terms", "remainingFee" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinFeeDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long feeID = Long.MIN_VALUE;
	private int feeSeq;
	private String finReference;
	private boolean originationFee;
	private String finEvent;
	private long feeTypeID = Long.MIN_VALUE;
	private BigDecimal calculatedAmount = BigDecimal.ZERO;
	@XmlElement(name = "feeAmount")
	private BigDecimal actualAmount = BigDecimal.ZERO;
	private BigDecimal actualAmountOriginal = BigDecimal.ZERO;
	private BigDecimal actualAmountGST = BigDecimal.ZERO;
	@XmlElement(name = "waiverAmount")
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private BigDecimal paidAmountOriginal = BigDecimal.ZERO;
	private BigDecimal paidAmountGST = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal netAmountOriginal = BigDecimal.ZERO;
	private BigDecimal netAmountGST = BigDecimal.ZERO;
	private BigDecimal netAmount = BigDecimal.ZERO;
	@XmlElement(name = "scheduleTerms")
	private int terms = 0;
	private BigDecimal remainingFeeOriginal = BigDecimal.ZERO;
	private BigDecimal remainingFeeGST = BigDecimal.ZERO;
	@XmlElement(name = "feeBalance")
	private BigDecimal remainingFee = BigDecimal.ZERO;
	private BigDecimal taxPercent = BigDecimal.ZERO;
	@XmlElement
	private String feeCategory;
	private String paymentRef;
	private int feeOrder;
	private String finEventDesc;
	@XmlElement(name = "feeCode")
	private String feeTypeCode;
	private String feeTypeDesc;
	@XmlElement(name = "feeMethod")
	private String feeScheduleMethod;
	private String calculationType;
	private String ruleCode;
	private BigDecimal fixedAmount = BigDecimal.ZERO;
	private BigDecimal percentage = BigDecimal.ZERO;
	private BigDecimal actPercentage = BigDecimal.ZERO;
	private String calculateOn;
	private boolean alwDeviation;
	private BigDecimal maxWaiverPerc = BigDecimal.ZERO;
	private boolean alwModifyFee;
	private boolean alwModifyFeeSchdMthd;
	private String vasReference;
	private String status;
	private boolean dataModified = false;
	private boolean rcdVisible = true;
	@XmlElement
	private Date schdDate;
	private boolean newRecord = false;
	private String lovValue;
	private FinFeeDetail befImage;
	private LoggedInUser userDetails;
	private Date postDate;
	private String transactionId;
	private BigDecimal calculatedOn = BigDecimal.ZERO;
	private boolean refundable;
	private boolean alwPreIncomization;
	@SuppressWarnings("unused")
	private FinFeeDetail validateFinFeeDetail = this;
	private transient List<FinFeeScheduleDetail> finFeeScheduleDetailList = new ArrayList<>(1);
	private FinTaxDetails finTaxDetails = new FinTaxDetails();
	private boolean taxApplicable;
	private String taxComponent;
	private BigDecimal feeAmz;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal ugst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	@XmlElement
	private List<FinFeeReceipt> finFeeReceipts = new ArrayList<>(1);
	private boolean feeModified = false;
	private Long instructionUID;
	private String moduleDefiner;
	private BigDecimal actualOldAmount = BigDecimal.ZERO;
	// TDS
	private BigDecimal netTDS = BigDecimal.ZERO;;
	private BigDecimal paidTDS = BigDecimal.ZERO;
	private BigDecimal remTDS = BigDecimal.ZERO;

	private long referenceId = 0; // For Receipt Fees

	// Waivers Gst values
	private BigDecimal waivedGST = BigDecimal.ZERO;
	private long taxHeaderId = 0;
	private TaxHeader taxHeader;

	public FinFeeDetail() {
		super();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public FinFeeDetail(String finReference) {
		super();
		this.setFinReference(finReference);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("finEventDesc");
		excludeFields.add("feeTypeCode");
		excludeFields.add("feeTypeDesc");
		excludeFields.add("dataModified");
		excludeFields.add("rcdVisible");
		excludeFields.add("validateFinFeeDetail");
		excludeFields.add("feeCategory");
		excludeFields.add("schdDate");
		excludeFields.add("taxPercent");

		excludeFields.add("finTaxDetails");
		excludeFields.add("igst");
		excludeFields.add("ugst");
		excludeFields.add("sgst");
		excludeFields.add("cgst");
		excludeFields.add("feeModified");
		excludeFields.add("alwPreIncomization");
		excludeFields.add("finFeeReceipts");
		excludeFields.add("calculatedOn");
		excludeFields.add("taxHeader");
		excludeFields.add("moduleDefiner");
		excludeFields.add("actualOldAmount");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public boolean isOriginationFee() {
		return originationFee;
	}

	public void setOriginationFee(boolean originationFee) {
		this.originationFee = originationFee;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public int getFeeOrder() {
		return feeOrder;
	}

	public void setFeeOrder(int feeOrder) {
		this.feeOrder = feeOrder;
	}

	public String getFinEventDesc() {
		return finEventDesc;
	}

	public void setFinEventDesc(String finEventDesc) {
		this.finEventDesc = finEventDesc;
	}

	public long getFeeTypeID() {
		return feeTypeID;
	}

	public void setFeeTypeID(long feeTypeID) {
		this.feeTypeID = feeTypeID;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}

	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}

	public BigDecimal getCalculatedAmount() {
		return calculatedAmount;
	}

	public void setCalculatedAmount(BigDecimal calculatedAmount) {
		this.calculatedAmount = calculatedAmount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public int getTerms() {
		return terms;
	}

	public void setTerms(int terms) {
		this.terms = terms;
	}

	public BigDecimal getRemainingFee() {
		return remainingFee;
	}

	public void setRemainingFee(BigDecimal remainingFee) {
		this.remainingFee = remainingFee;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public String getFeeScheduleMethod() {
		return feeScheduleMethod;
	}

	public void setFeeScheduleMethod(String feeScheduleMethod) {
		this.feeScheduleMethod = feeScheduleMethod;
	}

	public boolean isAlwModifyFee() {
		return alwModifyFee;
	}

	public void setAlwModifyFee(boolean alwModifyFee) {
		this.alwModifyFee = alwModifyFee;
	}

	public boolean isAlwModifyFeeSchdMthd() {
		return alwModifyFeeSchdMthd;
	}

	public void setAlwModifyFeeSchdMthd(boolean alwModifyFeeSchdMthd) {
		this.alwModifyFeeSchdMthd = alwModifyFeeSchdMthd;
	}

	public boolean isAlwDeviation() {
		return alwDeviation;
	}

	public void setAlwDeviation(boolean alwDeviation) {
		this.alwDeviation = alwDeviation;
	}

	public BigDecimal getMaxWaiverPerc() {
		return maxWaiverPerc;
	}

	public void setMaxWaiverPerc(BigDecimal maxWaiverPerc) {
		this.maxWaiverPerc = maxWaiverPerc;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public BigDecimal getFixedAmount() {
		return fixedAmount;
	}

	public void setFixedAmount(BigDecimal fixedAmount) {
		this.fixedAmount = fixedAmount;
	}

	public String getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(String calculationType) {
		this.calculationType = calculationType;
	}

	public String getCalculateOn() {
		return calculateOn;
	}

	public void setCalculateOn(String calculateOn) {
		this.calculateOn = calculateOn;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
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

	public FinFeeDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinFeeDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isDataModified() {
		return dataModified;
	}

	public void setDataModified(boolean dataModified) {
		this.dataModified = dataModified;
	}

	public boolean isRcdVisible() {
		return rcdVisible;
	}

	public void setRcdVisible(boolean rcdVisible) {
		this.rcdVisible = rcdVisible;
	}

	public long getFeeID() {
		return feeID;
	}

	public void setFeeID(long feeID) {
		this.feeID = feeID;
	}

	public long getId() {
		return this.feeID;
	}

	public void setId(long id) {
		this.feeID = id;
	}

	public int getFeeSeq() {
		return feeSeq;
	}

	public void setFeeSeq(int feeSeq) {
		this.feeSeq = feeSeq;
	}

	public List<FinFeeScheduleDetail> getFinFeeScheduleDetailList() {
		return finFeeScheduleDetailList;
	}

	public void setFinFeeScheduleDetailList(List<FinFeeScheduleDetail> finFeeScheduleDetailList) {
		this.finFeeScheduleDetailList = finFeeScheduleDetailList;
	}

	public String getVasReference() {
		return vasReference;
	}

	public void setVasReference(String vasReference) {
		this.vasReference = vasReference;
	}

	public String getFeeCategory() {
		return feeCategory;
	}

	public void setFeeCategory(String feeCategory) {
		this.feeCategory = feeCategory;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getSchdDate() {
		return schdDate;
	}

	public void setSchdDate(Date schdDate) {
		this.schdDate = schdDate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public FinTaxDetails getFinTaxDetails() {
		return finTaxDetails;
	}

	public void setFinTaxDetails(FinTaxDetails finTaxDetails) {
		this.finTaxDetails = finTaxDetails;
	}

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public String getTaxComponent() {
		return taxComponent;
	}

	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}

	public BigDecimal getFeeAmz() {
		return feeAmz;
	}

	public void setFeeAmz(BigDecimal feeAmz) {
		this.feeAmz = feeAmz;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getActualAmountOriginal() {
		return actualAmountOriginal;
	}

	public void setActualAmountOriginal(BigDecimal actualAmountOriginal) {
		this.actualAmountOriginal = actualAmountOriginal;
	}

	public BigDecimal getActualAmountGST() {
		return actualAmountGST;
	}

	public void setActualAmountGST(BigDecimal actualAmountGST) {
		this.actualAmountGST = actualAmountGST;
	}

	public BigDecimal getPaidAmountOriginal() {
		return paidAmountOriginal;
	}

	public void setPaidAmountOriginal(BigDecimal paidAmountOriginal) {
		this.paidAmountOriginal = paidAmountOriginal;
	}

	public BigDecimal getPaidAmountGST() {
		return paidAmountGST;
	}

	public void setPaidAmountGST(BigDecimal paidAmountGST) {
		this.paidAmountGST = paidAmountGST;
	}

	public BigDecimal getNetAmountOriginal() {
		return netAmountOriginal;
	}

	public void setNetAmountOriginal(BigDecimal netAmountOriginal) {
		this.netAmountOriginal = netAmountOriginal;
	}

	public BigDecimal getNetAmountGST() {
		return netAmountGST;
	}

	public void setNetAmountGST(BigDecimal netAmountGST) {
		this.netAmountGST = netAmountGST;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public BigDecimal getRemainingFeeOriginal() {
		return remainingFeeOriginal;
	}

	public void setRemainingFeeOriginal(BigDecimal remainingFeeOriginal) {
		this.remainingFeeOriginal = remainingFeeOriginal;
	}

	public BigDecimal getRemainingFeeGST() {
		return remainingFeeGST;
	}

	public void setRemainingFeeGST(BigDecimal remainingFeeGST) {
		this.remainingFeeGST = remainingFeeGST;
	}

	public BigDecimal getUgst() {
		return ugst;
	}

	public void setUgst(BigDecimal ugst) {
		this.ugst = ugst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getTaxPercent() {
		return taxPercent;
	}

	public void setTaxPercent(BigDecimal taxPercent) {
		this.taxPercent = taxPercent;
	}

	public boolean isFeeModified() {
		return feeModified;
	}

	public void setFeeModified(boolean feeModified) {
		this.feeModified = feeModified;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public List<FinFeeReceipt> getFinFeeReceipts() {
		return finFeeReceipts;
	}

	public void setFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts) {
		this.finFeeReceipts = finFeeReceipts;
	}

	public boolean isAlwPreIncomization() {
		return alwPreIncomization;
	}

	public void setAlwPreIncomization(boolean alwPreIncomization) {
		this.alwPreIncomization = alwPreIncomization;
	}

	public boolean isRefundable() {
		return refundable;
	}

	public void setRefundable(boolean refundable) {
		this.refundable = refundable;
	}

	public Long getInstructionUID() {
		return instructionUID;
	}

	public void setInstructionUID(Long instructionUID) {
		this.instructionUID = instructionUID;
	}

	public BigDecimal getCalculatedOn() {
		return calculatedOn;
	}

	public void setCalculatedOn(BigDecimal calculatedOn) {
		this.calculatedOn = calculatedOn;
	}

	public BigDecimal getActPercentage() {
		return actPercentage;
	}

	public void setActPercentage(BigDecimal actPercentage) {
		this.actPercentage = actPercentage;
	}

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public BigDecimal getWaivedGST() {
		return waivedGST;
	}

	public void setWaivedGST(BigDecimal waivedGST) {
		this.waivedGST = waivedGST;
	}

	public TaxHeader getTaxHeader() {
		return taxHeader;
	}

	public void setTaxHeader(TaxHeader taxHeader) {
		this.taxHeader = taxHeader;
	}

	public long getTaxHeaderId() {
		return taxHeaderId;
	}

	public void setTaxHeaderId(long taxHeaderId) {
		this.taxHeaderId = taxHeaderId;
	}

	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
	}

	public BigDecimal getNetTDS() {
		return netTDS;
	}

	public void setNetTDS(BigDecimal netTDS) {
		this.netTDS = netTDS;
	}

	public BigDecimal getPaidTDS() {
		return paidTDS;
	}

	public void setPaidTDS(BigDecimal paidTDS) {
		this.paidTDS = paidTDS;
	}

	public BigDecimal getRemTDS() {
		return remTDS;
	}

	public void setRemTDS(BigDecimal remTDS) {
		this.remTDS = remTDS;
	}

	public BigDecimal getActualOldAmount() {
		return actualOldAmount;
	}

	public void setActualOldAmount(BigDecimal actualOldAmount) {
		this.actualOldAmount = actualOldAmount;
	}

}
