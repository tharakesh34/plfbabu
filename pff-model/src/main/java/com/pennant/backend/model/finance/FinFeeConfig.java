package com.pennant.backend.model.finance;

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
 * * FileName : FinTypeFees.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-12-2013 * * Modified Date :
 * 04-12-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-12-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class FinFeeConfig extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private boolean originationFee;
	private String finEvent;
	private long feeTypeID = Long.MIN_VALUE;
	private long percRuleId = Long.MIN_VALUE;
	private int feeOrder;
	private String feeScheduleMethod;
	private String calculationType;
	private String ruleCode;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal percentage = BigDecimal.ZERO;
	private String calculateOn;
	private boolean alwDeviation;
	private boolean alwModifyFeeSchdMthd;
	private boolean alwModifyFee;
	private BigDecimal maxWaiverPerc = BigDecimal.ZERO;
	private int moduleId;
	private long referenceId;
	private long finTypeFeeId = Long.MIN_VALUE;
	private boolean alwPreIncomization;
	private String percType;
	private String percRule;

	private String feeTypeCode;
	private String feeTypeDesc;
	private String finEventDesc;

	private boolean taxApplicable;
	private String taxComponent;
	private boolean intervalFeeReq;

	public FinFeeConfig() {
		super();
	}

	public FinFeeConfig(String finReference) {
		super();
		this.setFinReference(finReference);
	}

	public Set<String> getExcludeFields() {

		Set<String> excludeFields = new HashSet<String>();
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

	public boolean isAlwPreIncomization() {
		return alwPreIncomization;
	}

	public void setAlwPreIncomization(boolean alwPreIncomization) {
		this.alwPreIncomization = alwPreIncomization;
	}

	public String getPercType() {
		return percType;
	}

	public void setPercType(String percType) {
		this.percType = percType;
	}

	public String getPercRule() {
		return percRule;
	}

	public void setPercRule(String percRule) {
		this.percRule = percRule;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public long getFinTypeFeeId() {
		return finTypeFeeId;
	}

	public void setFinTypeFeeId(long finTypeFeeId) {
		this.finTypeFeeId = finTypeFeeId;
	}

	public long getId() {
		return 0;
	}

	public void setId(long id) {

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

	public boolean isIntervalFeeReq() {
		return intervalFeeReq;
	}

	public void setIntervalFeeReq(boolean intervalFeeReq) {
		this.intervalFeeReq = intervalFeeReq;
	}

	public long getPercRuleId() {
		return percRuleId;
	}

	public void setPercRuleId(long percRuleId) {
		this.percRuleId = percRuleId;
	}
}
