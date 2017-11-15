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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  Rule.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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

package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Rule table</b>.<br>
 *
 */
public class Rule extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 522289325946000330L;

	private long ruleId = Long.MIN_VALUE;
	private long groupId = 0;

	private String ruleCode;
	private String ruleModule;
 	private String ruleEvent;
	private String ruleCodeDesc;
	private String waiverDecider;
	private String lovDescGroupName;
	private String sQLRule;
	private String actualBlock;
	private String deviationType;
	private String returnType;
	private String feeToFinance;
	private String fixedOrVariableLimit = "F";//FIXME: How to use constant-LimitConstants.LIMIT_RULE_FIXED;
	private String fields;
	private String lovValue;
	private Long feeTypeID;
	private String feeTypeCode;
	private String feeTypeDesc;

	private boolean waiver;
	private boolean allowDeviation;
	private boolean calFeeModify;
	private boolean revolving = true;
	private boolean active;
	private boolean newRecord;

	private BigDecimal waiverPerc;

	private int seqOrder;

	private Rule befImage;
	private LoggedInUser userDetails;

	/**
	 * default constructor.<br>
	 */
	public Rule() {
		super();
	}

	public Rule(long id) {
		super();
		this.setRuleId(id);
	}

	public boolean isNew() {
		return isNewRecord();
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		excludeFields.add("feeTypeCode");
		excludeFields.add("feeTypeDesc");
		
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return ruleId;
	}

	public void setId(long id) {
		this.ruleId = id;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleModule() {
		return ruleModule;
	}

	public void setRuleModule(String ruleModule) {
		this.ruleModule = ruleModule;
	}

	public String getRuleEvent() {
		return ruleEvent;
	}

	public void setRuleEvent(String ruleEvent) {
		this.ruleEvent = ruleEvent;
	}

	public String getRuleCodeDesc() {
		return ruleCodeDesc;
	}

	public void setRuleCodeDesc(String ruleCodeDesc) {
		this.ruleCodeDesc = ruleCodeDesc;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setLovDescGroupName(String lovDescGroupName) {
		this.lovDescGroupName = lovDescGroupName;
	}

	public String getLovDescGroupName() {
		return lovDescGroupName;
	}

	public String getWaiverDecider() {
		return waiverDecider;
	}

	public void setWaiverDecider(String waiverDecider) {
		this.waiverDecider = waiverDecider;
	}

	public boolean isWaiver() {
		return waiver;
	}

	public void setWaiver(boolean waiver) {
		this.waiver = waiver;
	}

	public BigDecimal getWaiverPerc() {
		return waiverPerc;
	}

	public void setWaiverPerc(BigDecimal waiverPerc) {
		this.waiverPerc = waiverPerc;
	}

	public String getSQLRule() {
		return sQLRule;
	}

	public void setSQLRule(String sQLRule) {
		this.sQLRule = sQLRule;
	}

	public String getActualBlock() {
		return actualBlock;
	}

	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
	}

	public void setSeqOrder(int seqOrder) {
		this.seqOrder = seqOrder;
	}

	public int getSeqOrder() {
		return seqOrder;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getDeviationType() {
		return deviationType;
	}

	public void setDeviationType(String deviationType) {
		this.deviationType = deviationType;
	}

	public String getReturnType() {
		return returnType;
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

	public Rule getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Rule beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isAllowDeviation() {
		return allowDeviation;
	}

	public void setAllowDeviation(boolean allowDeviation) {
		this.allowDeviation = allowDeviation;
	}

	public boolean isCalFeeModify() {
		return calFeeModify;
	}

	public void setCalFeeModify(boolean calFeeModify) {
		this.calFeeModify = calFeeModify;
	}

	public String getFeeToFinance() {
		return feeToFinance;
	}

	public void setFeeToFinance(String feeToFinance) {
		this.feeToFinance = feeToFinance;
	}

	public boolean isRevolving() {
		return revolving;
	}

	public void setRevolving(boolean revolving) {
		this.revolving = revolving;
	}

	public String getFixedOrVariableLimit() {
		return fixedOrVariableLimit;
	}

	public void setFixedOrVariableLimit(String fixedOrVariableLimit) {
		this.fixedOrVariableLimit = fixedOrVariableLimit;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public Long getFeeTypeID() {
		return feeTypeID;
	}

	public void setFeeTypeID(Long feeTypeID) {
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
}