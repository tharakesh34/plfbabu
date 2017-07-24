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

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Collateral table</b>.<br>
 *
 */
@XmlType(propOrder = { "feeCategory", "schdDate", "feeTypeCode", "actualAmount", "waivedAmount", "paidAmount",
		"feeScheduleMethod", "terms", "remainingFee" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinFeeDetail extends AbstractWorkflowEntity implements Entity {
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
	@XmlElement(name = "waiverAmount")
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal paidAmount = BigDecimal.ZERO;
	@XmlElement(name= "scheduleTerms")
	private int terms = 0;
	@XmlElement(name= "feeBalance")
	private BigDecimal remainingFee = BigDecimal.ZERO;
	@XmlElement
	private String feeCategory;
	private String paymentRef;
	
	private int feeOrder;
	private String finEventDesc;
	@XmlElement(name= "feeCode")
	private String feeTypeCode;
	private String feeTypeDesc;
	@XmlElement(name="feeMethod")
	private String feeScheduleMethod;
	private String calculationType;
	private String ruleCode;
	private BigDecimal fixedAmount = BigDecimal.ZERO;
	private BigDecimal percentage = BigDecimal.ZERO;
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
	private boolean newRecord=false;
	private String lovValue;
	private FinFeeDetail befImage;
	private LoggedInUser userDetails;
	
	@SuppressWarnings("unused")
	private FinFeeDetail validateFinFeeDetail = this;
	
	private List<FinFeeScheduleDetail> finFeeScheduleDetailList = new ArrayList<FinFeeScheduleDetail>(1);
	

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
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finEventDesc");
		excludeFields.add("feeTypeCode");
		excludeFields.add("feeTypeDesc");
		excludeFields.add("dataModified");
		excludeFields.add("rcdVisible");
		excludeFields.add("validateFinFeeDetail");
		excludeFields.add("feeCategory");
		excludeFields.add("schdDate");
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

	public FinFeeDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinFeeDetail beforeImage){
		this.befImage=beforeImage;
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

	@Override
	public long getId() {
		return this.feeID;
	}

	@Override
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

}

