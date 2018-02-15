package com.pennant.backend.model.rmtmasters;

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
import java.util.HashSet;
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
@XmlType(propOrder = { "feeTypeCode", "feeTypeDesc", "finEvent", "alwDeviation", "maxWaiverPerc" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinTypeFees extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String finType = null;
	private boolean originationFee;
	@XmlElement(name="feeEvent")
	private String finEvent;
	private String finEventDesc;
	private long feeTypeID = Long.MIN_VALUE;
	@XmlElement(name = "feeCode")
	private String feeTypeCode;
	@XmlElement(name="feeDesc")
	private String feeTypeDesc;
	private int feeOrder;
	private String feeScheduleMethod;
	private String calculationType;
	private String ruleCode;
	private String ruleDesc;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal percentage = BigDecimal.ZERO;
	private String calculateOn;
	@XmlElement(name ="allowWaiver")
	private boolean alwDeviation;
	@XmlElement(name="maxWaiverPerc")
	private BigDecimal maxWaiverPerc = BigDecimal.ZERO;
	private boolean alwModifyFee;
	private boolean alwModifyFeeSchdMthd;
	private boolean active;
	
	private int moduleId;
	
	private boolean newRecord=false;
	private String lovValue;
	private FinTypeFees befImage;
	
	private LoggedInUser userDetails;
	
	//GST
	private boolean taxApplicable;
	private String	taxComponent;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinTypeFees() {
		super();
	}

	public FinTypeFees(String id) {
		super();
		this.setId(id);
	}
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("feeTypeCode");
		excludeFields.add("feeTypeDesc");
		excludeFields.add("finEventDesc");
		excludeFields.add("ruleDesc");
		excludeFields.add("taxApplicable");
		excludeFields.add("taxComponent");
		
		return excludeFields;
	}
	//Getter and Setter methods
	
	public String getId() {
		return finType;
	}
	
	public void setId (String id) {
		this.finType = id;
	}
		
	
	public String getFinType() {
    	return finType;
    }

	public void setFinType(String finType) {
    	this.finType = finType;
    }

	public String getFinEvent() {
		return finEvent;
	}
	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
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

	public boolean isOriginationFee() {
		return originationFee;
	}
	public void setOriginationFee(boolean originationFee) {
		this.originationFee = originationFee;
	}

	public int getFeeOrder() {
		return feeOrder;
	}
	public void setFeeOrder(int feeOrder) {
		this.feeOrder = feeOrder;
	}

	public String getFeeScheduleMethod() {
		return feeScheduleMethod;
	}
	public void setFeeScheduleMethod(String feeScheduleMethod) {
		this.feeScheduleMethod = feeScheduleMethod;
	}

	public String getCalculationType() {
		return calculationType;
	}
	public void setCalculationType(String calculationType) {
		this.calculationType = calculationType;
	}

	public String getRuleCode() {
		return ruleCode;
	}
	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleDesc() {
		return ruleDesc;
	}
	public void setRuleDesc(String ruleDesc) {
		this.ruleDesc = ruleDesc;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}
	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public String getCalculateOn() {
		return calculateOn;
	}
	public void setCalculateOn(String calculateOn) {
		this.calculateOn = calculateOn;
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

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
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

	public FinTypeFees getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinTypeFees beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
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
}

