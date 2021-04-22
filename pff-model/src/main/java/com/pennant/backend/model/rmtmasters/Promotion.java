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
	private BigDecimal actualInterestRate = BigDecimal.ZERO;
	private String finBaseRate;
	private String finBaseRateName;
	private String finSplRate;
	private String finSplRateName;
	private BigDecimal finMargin = BigDecimal.ZERO;;
	private boolean applyRpyPricing;
	private long rpyPricingMethod;
	private int finMinTerm;
	private int finMaxTerm;
	private BigDecimal finMinAmount = BigDecimal.ZERO;
	private BigDecimal finMaxAmount = BigDecimal.ZERO;
	private BigDecimal finMinRate = BigDecimal.ZERO;
	private BigDecimal finMaxRate = BigDecimal.ZERO;
	private boolean active = true;
	private boolean newRecord = false;
	private Promotion befImage;
	private LoggedInUser userDetails;
	private String productCategory;

	private String finTypeDesc;
	private String finCcy;
	private String downPayRuleCode;
	private String downPayRuleDesc;
	private String rpyPricingCode;
	private String rpyPricingDesc;

	//Consumer Durables
	private long referenceID = Long.MIN_VALUE;
	private boolean openBalOnPV = false;
	private int tenor = 0;
	private int advEMITerms = 0;
	private String pftDaysBasis = "";
	private BigDecimal subventionRate = BigDecimal.ZERO;
	private boolean taxApplicable = false;
	private int cashBackFromDealer = 0;
	private int cashBackToCustomer = 0;
	private boolean specialScheme;
	private String remarks;
	private int cbFrmMnf;
	private int mnfCbToCust;
	private int dlrCbToCust;
	private String cbPyt;
	private boolean dbd;
	private boolean mbd;
	private BigDecimal dbdPerc = BigDecimal.ZERO;
	private String dbdPercCal;
	private boolean dbdRtnd;
	private boolean mbdRtnd;
	private boolean knckOffDueAmt;
	private long dbdFeeTypId;
	private long mbdFeeTypId;
	private long dbdAndMbdFeeTypId;
	private boolean isCDLoan = false;

	private String lovValue;

	private List<FinTypeFees> finTypeFeesList = new ArrayList<FinTypeFees>();
	private List<FinTypeInsurances> finTypeInsurancesList = new ArrayList<FinTypeInsurances>();
	private List<FinTypeAccounting> finTypeAccountingList = new ArrayList<FinTypeAccounting>();

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
		excludeFields.add("productCategory");
		excludeFields.add("isCDLoan");

		return excludeFields;
	}

	public Promotion copyEntity() {
		Promotion entity = new Promotion();
		entity.setPromotionId(this.promotionId);
		entity.setPromotionCode(this.promotionCode);
		entity.setPromotionDesc(this.promotionDesc);
		entity.setFinType(this.finType);
		entity.setStartDate(this.startDate);
		entity.setEndDate(this.endDate);
		entity.setFinIsDwPayRequired(this.finIsDwPayRequired);
		entity.setDownPayRule(this.downPayRule);
		entity.setActualInterestRate(this.actualInterestRate);
		entity.setFinBaseRate(this.finBaseRate);
		entity.setFinBaseRateName(this.finBaseRateName);
		entity.setFinSplRate(this.finSplRate);
		entity.setFinSplRateName(this.finSplRateName);
		entity.setFinMargin(this.finMargin);
		entity.setApplyRpyPricing(this.applyRpyPricing);
		entity.setRpyPricingMethod(this.rpyPricingMethod);
		entity.setFinMinTerm(this.finMinTerm);
		entity.setFinMaxTerm(this.finMaxTerm);
		entity.setFinMinAmount(this.finMinAmount);
		entity.setFinMaxAmount(this.finMaxAmount);
		entity.setFinMinRate(this.finMinRate);
		entity.setFinMaxRate(this.finMaxRate);
		entity.setActive(this.active);
		entity.setNewRecord(this.newRecord);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setProductCategory(this.productCategory);
		entity.setFinTypeDesc(this.finTypeDesc);
		entity.setFinCcy(this.finCcy);
		entity.setDownPayRuleCode(this.downPayRuleCode);
		entity.setDownPayRuleDesc(this.downPayRuleDesc);
		entity.setRpyPricingCode(this.rpyPricingCode);
		entity.setRpyPricingDesc(this.rpyPricingDesc);
		entity.setReferenceID(this.referenceID);
		entity.setOpenBalOnPV(this.openBalOnPV);
		entity.setTenor(this.tenor);
		entity.setAdvEMITerms(this.advEMITerms);
		entity.setPftDaysBasis(this.pftDaysBasis);
		entity.setSubventionRate(this.subventionRate);
		entity.setTaxApplicable(this.taxApplicable);
		entity.setCashBackFromDealer(this.cashBackFromDealer);
		entity.setCashBackToCustomer(this.cashBackToCustomer);
		entity.setSpecialScheme(this.specialScheme);
		entity.setRemarks(this.remarks);
		entity.setCbFrmMnf(this.cbFrmMnf);
		entity.setMnfCbToCust(this.mnfCbToCust);
		entity.setDlrCbToCust(this.dlrCbToCust);
		entity.setCbPyt(this.cbPyt);
		entity.setDbd(this.dbd);
		entity.setMbd(this.mbd);
		entity.setDbdPerc(this.dbdPerc);
		entity.setDbdPercCal(this.dbdPercCal);
		entity.setDbdRtnd(this.dbdRtnd);
		entity.setMbdRtnd(this.mbdRtnd);
		entity.setKnckOffDueAmt(this.knckOffDueAmt);
		entity.setDbdFeeTypId(this.dbdFeeTypId);
		entity.setMbdFeeTypId(this.mbdFeeTypId);
		entity.setDbdAndMbdFeeTypId(this.dbdAndMbdFeeTypId);
		entity.setCDLoan(this.isCDLoan);
		entity.setLovValue(this.lovValue);
		this.finTypeFeesList.stream().forEach(e -> entity.getFinTypeFeesList().add(e == null ? null : e.copyEntity()));
		this.finTypeInsurancesList.stream()
				.forEach(e -> entity.getFinTypeInsurancesList().add(e == null ? null : e.copyEntity()));
		this.finTypeAccountingList.stream()
				.forEach(e -> entity.getFinTypeAccountingList().add(e == null ? null : e.copyEntity()));
		this.auditDetailMap.entrySet().stream().forEach(e -> {
			List<AuditDetail> newList = new ArrayList<AuditDetail>();
			if (e.getValue() != null) {
				e.getValue().forEach(
						auditDetail -> newList.add(auditDetail == null ? null : auditDetail.getNewCopyInstance()));
				entity.getAuditDetailMap().put(e.getKey(), newList);
			} else
				entity.getAuditDetailMap().put(e.getKey(), null);
		});

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

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public long getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(long referenceID) {
		this.referenceID = referenceID;
	}

	public boolean isOpenBalOnPV() {
		return openBalOnPV;
	}

	public void setOpenBalOnPV(boolean openBalOnPV) {
		this.openBalOnPV = openBalOnPV;
	}

	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	public int getAdvEMITerms() {
		return advEMITerms;
	}

	public void setAdvEMITerms(int advEMITerms) {
		this.advEMITerms = advEMITerms;
	}

	public String getPftDaysBasis() {
		return pftDaysBasis;
	}

	public void setPftDaysBasis(String pftDaysBasis) {
		this.pftDaysBasis = pftDaysBasis;
	}

	public BigDecimal getSubventionRate() {
		return subventionRate;
	}

	public void setSubventionRate(BigDecimal subventionRate) {
		this.subventionRate = subventionRate;
	}

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public int getCashBackFromDealer() {
		return cashBackFromDealer;
	}

	public void setCashBackFromDealer(int cashBackFromDealer) {
		this.cashBackFromDealer = cashBackFromDealer;
	}

	public int getCashBackToCustomer() {
		return cashBackToCustomer;
	}

	public void setCashBackToCustomer(int cashBackToCustomer) {
		this.cashBackToCustomer = cashBackToCustomer;
	}

	public boolean isSpecialScheme() {
		return specialScheme;
	}

	public void setSpecialScheme(boolean specialScheme) {
		this.specialScheme = specialScheme;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getCbFrmMnf() {
		return cbFrmMnf;
	}

	public void setCbFrmMnf(int cbFrmMnf) {
		this.cbFrmMnf = cbFrmMnf;
	}

	public int getMnfCbToCust() {
		return mnfCbToCust;
	}

	public void setMnfCbToCust(int mnfCbToCust) {
		this.mnfCbToCust = mnfCbToCust;
	}

	public int getDlrCbToCust() {
		return dlrCbToCust;
	}

	public void setDlrCbToCust(int dlrCbToCust) {
		this.dlrCbToCust = dlrCbToCust;
	}

	public String getCbPyt() {
		return cbPyt;
	}

	public void setCbPyt(String cbPyt) {
		this.cbPyt = cbPyt;
	}

	public boolean isDbd() {
		return dbd;
	}

	public void setDbd(boolean dbd) {
		this.dbd = dbd;
	}

	public boolean isMbd() {
		return mbd;
	}

	public void setMbd(boolean mbd) {
		this.mbd = mbd;
	}

	public BigDecimal getDbdPerc() {
		return dbdPerc;
	}

	public void setDbdPerc(BigDecimal dbdPerc) {
		this.dbdPerc = dbdPerc;
	}

	public String getDbdPercCal() {
		return dbdPercCal;
	}

	public void setDbdPercCal(String dbdPercCal) {
		this.dbdPercCal = dbdPercCal;
	}

	public boolean isDbdRtnd() {
		return dbdRtnd;
	}

	public void setDbdRtnd(boolean dbdRtnd) {
		this.dbdRtnd = dbdRtnd;
	}

	public boolean isMbdRtnd() {
		return mbdRtnd;
	}

	public void setMbdRtnd(boolean mbdRtnd) {
		this.mbdRtnd = mbdRtnd;
	}

	public boolean isKnckOffDueAmt() {
		return knckOffDueAmt;
	}

	public void setKnckOffDueAmt(boolean knckOffDueAmt) {
		this.knckOffDueAmt = knckOffDueAmt;
	}

	public long getDbdFeeTypId() {
		return dbdFeeTypId;
	}

	public void setDbdFeeTypId(long dbdFeeTypId) {
		this.dbdFeeTypId = dbdFeeTypId;
	}

	public long getMbdFeeTypId() {
		return mbdFeeTypId;
	}

	public void setMbdFeeTypId(long mbdFeeTypId) {
		this.mbdFeeTypId = mbdFeeTypId;
	}

	public long getDbdAndMbdFeeTypId() {
		return dbdAndMbdFeeTypId;
	}

	public void setDbdAndMbdFeeTypId(long dbdAndMbdFeeTypId) {
		this.dbdAndMbdFeeTypId = dbdAndMbdFeeTypId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isCDLoan() {
		return isCDLoan;
	}

	public void setCDLoan(boolean isCDLoan) {
		this.isCDLoan = isCDLoan;
	}

}