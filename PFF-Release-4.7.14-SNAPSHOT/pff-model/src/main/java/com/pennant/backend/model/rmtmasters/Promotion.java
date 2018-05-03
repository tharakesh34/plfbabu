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
 * FileName    		:  Promotion.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.model.rmtmasters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Promotion table</b>.<br>
 * 
 */
public class Promotion extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long promotionId = Long.MIN_VALUE;
	private String promotionCode;
	private String promotionDesc;
	private String finType;
	private Date startDate;
	private Date endDate;
	private boolean finIsDwPayRequired;
	private long downPayRule;
	private BigDecimal actualInterestRate;
	private String finBaseRate;
	private String finBaseRateName;
	private String finSplRate;
	private String finSplRateName;
	private BigDecimal finMargin = BigDecimal.ZERO;;
	private boolean applyRpyPricing;
	private long rpyPricingMethod;
	private int finMinTerm;
	private int finMaxTerm;
	private BigDecimal finMinAmount;
	private BigDecimal finMaxAmount;
	private BigDecimal finMinRate;
	private BigDecimal finMaxRate;
	private boolean active = true;
	private boolean newRecord = false;
	private Promotion befImage;
	private LoggedInUser userDetails;

	private String finTypeDesc;
	private String finCcy;
	private String downPayRuleCode;
	private String downPayRuleDesc;
	private String rpyPricingCode;
	private String rpyPricingDesc;
	
	private String lovValue;
	
	private List<FinTypeFees> finTypeFeesList = new ArrayList<FinTypeFees>();
	private List<FinTypeInsurances> finTypeInsurancesList = new ArrayList<FinTypeInsurances>();
	private List<FinTypeAccounting>	finTypeAccountingList = new ArrayList<FinTypeAccounting>();
	
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

	public Promotion() {
		super();
	}

	public Promotion(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("finTypeDesc");
		excludeFields.add("finCcy");
		excludeFields.add("downPayRuleCode");
		excludeFields.add("downPayRuleDesc");
		excludeFields.add("rpyPricingCode");
		excludeFields.add("rpyPricingDesc");
		excludeFields.add("finBaseRateName");
		excludeFields.add("finSplRateName");

		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return promotionId;
	}

	public void setId(long promotionId) {
		this.promotionId = promotionId;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getPromotionDesc() {
		return promotionDesc;
	}

	public void setPromotionDesc(String promotionDesc) {
		this.promotionDesc = promotionDesc;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isFinIsDwPayRequired() {
		return finIsDwPayRequired;
	}

	public void setFinIsDwPayRequired(boolean finIsDwPayRequired) {
		this.finIsDwPayRequired = finIsDwPayRequired;
	}

	public long getDownPayRule() {
		return downPayRule;
	}

	public void setDownPayRule(long downPayRule) {
		this.downPayRule = downPayRule;
	}

	public BigDecimal getActualInterestRate() {
		return actualInterestRate;
	}

	public void setActualInterestRate(BigDecimal actualInterestRate) {
		this.actualInterestRate = actualInterestRate;
	}

	public String getFinBaseRate() {
		return finBaseRate;
	}

	public void setFinBaseRate(String finBaseRate) {
		this.finBaseRate = finBaseRate;
	}

	public String getFinBaseRateName() {
		return this.finBaseRateName;
	}

	public void setFinBaseRateName(String finBaseRateName) {
		this.finBaseRateName = finBaseRateName;
	}

	public String getFinSplRate() {
		return finSplRate;
	}

	public void setFinSplRate(String finSplRate) {
		this.finSplRate = finSplRate;
	}

	public String getFinSplRateName() {
		return this.finSplRateName;
	}

	public void setFinSplRateName(String finSplRateName) {
		this.finSplRateName = finSplRateName;
	}

	public BigDecimal getFinMargin() {
		return finMargin;
	}

	public void setFinMargin(BigDecimal finMargin) {
		this.finMargin = finMargin;
	}

	public boolean isApplyRpyPricing() {
		return applyRpyPricing;
	}

	public void setApplyRpyPricing(boolean applyRpyPricing) {
		this.applyRpyPricing = applyRpyPricing;
	}

	public long getRpyPricingMethod() {
		return rpyPricingMethod;
	}

	public void setRpyPricingMethod(long rpyPricingMethod) {
		this.rpyPricingMethod = rpyPricingMethod;
	}

	public int getFinMinTerm() {
		return finMinTerm;
	}

	public void setFinMinTerm(int finMinTerm) {
		this.finMinTerm = finMinTerm;
	}

	public int getFinMaxTerm() {
		return finMaxTerm;
	}

	public void setFinMaxTerm(int finMaxTerm) {
		this.finMaxTerm = finMaxTerm;
	}

	public BigDecimal getFinMinAmount() {
		return finMinAmount;
	}

	public void setFinMinAmount(BigDecimal finMinAmount) {
		this.finMinAmount = finMinAmount;
	}

	public BigDecimal getFinMaxAmount() {
		return finMaxAmount;
	}

	public void setFinMaxAmount(BigDecimal finMaxAmount) {
		this.finMaxAmount = finMaxAmount;
	}

	public BigDecimal getFinMinRate() {
		return finMinRate;
	}

	public void setFinMinRate(BigDecimal finMinRate) {
		this.finMinRate = finMinRate;
	}

	public BigDecimal getFinMaxRate() {
		return finMaxRate;
	}

	public void setFinMaxRate(BigDecimal finMaxRate) {
		this.finMaxRate = finMaxRate;
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

	public Promotion getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Promotion beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getDownPayRuleCode() {
		return downPayRuleCode;
	}

	public void setDownPayRuleCode(String downPayRuleCode) {
		this.downPayRuleCode = downPayRuleCode;
	}

	public String getDownPayRuleDesc() {
		return downPayRuleDesc;
	}

	public void setDownPayRuleDesc(String downPayRuleDesc) {
		this.downPayRuleDesc = downPayRuleDesc;
	}

	public String getRpyPricingCode() {
		return rpyPricingCode;
	}

	public void setRpyPricingCode(String rpyPricingCode) {
		this.rpyPricingCode = rpyPricingCode;
	}

	public String getRpyPricingDesc() {
		return rpyPricingDesc;
	}

	public void setRpyPricingDesc(String rpyPricingDesc) {
		this.rpyPricingDesc = rpyPricingDesc;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public List<FinTypeFees> getFinTypeFeesList() {
		return finTypeFeesList;
	}

	public void setFinTypeFeesList(List<FinTypeFees> finTypeFeesList) {
		this.finTypeFeesList = finTypeFeesList;
	}

	public List<FinTypeInsurances> getFinTypeInsurancesList() {
		return finTypeInsurancesList;
	}

	public void setFinTypeInsurancesList(List<FinTypeInsurances> finTypeInsurancesList) {
		this.finTypeInsurancesList = finTypeInsurancesList;
	}

	public List<FinTypeAccounting> getFinTypeAccountingList() {
		return finTypeAccountingList;
	}

	public void setFinTypeAccountingList(List<FinTypeAccounting> finTypeAccountingList) {
		this.finTypeAccountingList = finTypeAccountingList;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(long promotionId) {
		this.promotionId = promotionId;
	}

}
