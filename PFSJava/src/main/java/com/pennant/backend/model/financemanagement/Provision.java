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
 * * FileName : Provision.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * * Modified Date :
 * 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Provision table</b>.<br>
 * 
 */
public class Provision extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long provisionId = Long.MIN_VALUE;
	private long finID;
	private String finReference;
	private BigDecimal closingBalance;
	private BigDecimal outStandPrincipal;
	private BigDecimal outStandProfit;
	private BigDecimal profitAccruedAndDue;
	private BigDecimal profitAccruedAndNotDue;
	private BigDecimal collateralValue = BigDecimal.ZERO;
	private Date dueFromDate;
	private Date lastFullyPaidDate;
	private int dueDays;
	private int currBucket;
	private int dpd;
	private Date provisionDate;
	private BigDecimal provisionedAmt;
	private BigDecimal provisionRate = BigDecimal.ZERO;
	private String assetCode;
	private int assetStageOrder;
	private boolean npa;
	private boolean manualProvision;
	private long linkedTranId = Long.MIN_VALUE;
	private long chgLinkedTranId = Long.MIN_VALUE;

	private List<ProvisionAmount> provisionAmounts = new ArrayList<>();
	private String finBranch;
	private String finType;
	private boolean finIsActive;
	private long custID;
	private String custCIF;
	private String custShrtName;
	private boolean assetFwdMov;
	private boolean assetBkwMov;
	private FinanceDetail financeDetail;
	private NPAProvisionHeader npaHeader;
	private String finCcy;
	private Provision befImage;
	private Provision oldProvision;
	private LoggedInUser userDetails;
	private Long ruleId;
	private String custCtgCode;
	private boolean secured;
	private String product;
	private BigDecimal insuranceAmount;
	private boolean insuranceComponent;
	private String propertyType;
	private NPAProvisionHeader npaIntHeader;
	private NPAProvisionHeader npaRegHeader;
	private Long npaTemplateId;
	private String npaTemplateCode;
	private String npaTemplateDesc;
	private BigDecimal overDuePrincipal = BigDecimal.ZERO;
	private BigDecimal overDueProfit = BigDecimal.ZERO;
	private BigDecimal futureRpyPri = BigDecimal.ZERO;
	private BigDecimal unDisbursedAmount = BigDecimal.ZERO;

	public Provision() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("provisionAmounts");
		excludeFields.add("provisionId");
		excludeFields.add("finBranch");
		excludeFields.add("custID");
		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("assetFwdMov");
		excludeFields.add("assetBkwMov");
		excludeFields.add("finCcy");
		excludeFields.add("npaHeader");
		excludeFields.add("oldProvision");
		excludeFields.add("financeDetail");
		excludeFields.add("finType");
		excludeFields.add("provisionRate");
		excludeFields.add("finIsActive");
		excludeFields.add("ruleId");
		excludeFields.add("custCtgCode");
		excludeFields.add("secured");
		excludeFields.add("product");
		excludeFields.add("insuranceAmount");
		excludeFields.add("insuranceComponent");
		excludeFields.add("propertyType");
		excludeFields.add("npaIntHeader");
		excludeFields.add("npaRegHeader");
		excludeFields.add("npaTemplateCode");
		excludeFields.add("npaTemplateDesc");
		excludeFields.add("overDuePrincipal");
		excludeFields.add("overDueProfit");
		excludeFields.add("futureRpyPri");
		excludeFields.add("unDisbursedAmount");

		return excludeFields;
	}

	public Provision copyEntity() {
		Provision entity = new Provision();
		entity.setId(this.id);
		entity.setProvisionId(this.provisionId);
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setClosingBalance(this.closingBalance);
		entity.setOutStandPrincipal(this.outStandPrincipal);
		entity.setOutStandProfit(this.outStandProfit);
		entity.setProfitAccruedAndDue(this.profitAccruedAndDue);
		entity.setProfitAccruedAndNotDue(this.profitAccruedAndNotDue);
		entity.setCollateralValue(this.collateralValue);
		entity.setDueFromDate(this.dueFromDate);
		entity.setLastFullyPaidDate(this.lastFullyPaidDate);
		entity.setDueDays(this.dueDays);
		entity.setCurrBucket(this.currBucket);
		entity.setDpd(this.dpd);
		entity.setProvisionDate(this.provisionDate);
		entity.setProvisionedAmt(this.provisionedAmt);
		entity.setProvisionRate(this.provisionRate);
		entity.setAssetCode(this.assetCode);
		entity.setAssetStageOrder(this.assetStageOrder);
		entity.setNpa(this.npa);
		entity.setManualProvision(this.manualProvision);
		entity.setLinkedTranId(this.linkedTranId);
		entity.setChgLinkedTranId(this.chgLinkedTranId);
		this.provisionAmounts.stream()
				.forEach(e -> entity.getProvisionAmounts().add(e == null ? null : e.copyEntity()));
		entity.setFinBranch(this.finBranch);
		entity.setFinType(this.finType);
		entity.setFinIsActive(this.finIsActive);
		entity.setCustID(this.custID);
		entity.setCustCIF(this.custCIF);
		entity.setCustShrtName(this.custShrtName);
		entity.setAssetFwdMov(this.assetFwdMov);
		entity.setAssetBkwMov(this.assetBkwMov);
		entity.setFinanceDetail(this.financeDetail);
		entity.setNpaHeader(this.npaHeader == null ? null : this.npaHeader.copyEntity());
		entity.setFinCcy(this.finCcy);
		entity.setNewRecord(super.isNewRecord());
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setOldProvision(this.oldProvision == null ? null : this.oldProvision.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setRuleId(this.ruleId);
		entity.setCustCtgCode(this.custCtgCode);
		entity.setSecured(this.secured);
		entity.setProduct(this.product);
		entity.setInsuranceAmount(this.insuranceAmount);
		entity.setInsuranceComponent(this.insuranceComponent);
		entity.setPropertyType(this.propertyType);
		entity.setNpaIntHeader(this.npaIntHeader == null ? null : this.npaIntHeader.copyEntity());
		entity.setNpaRegHeader(this.npaRegHeader == null ? null : this.npaRegHeader.copyEntity());
		entity.setNpaTemplateId(this.npaTemplateId);
		entity.setNpaTemplateCode(this.npaTemplateCode);
		entity.setNpaTemplateDesc(this.npaTemplateDesc);
		entity.setOverDuePrincipal(this.overDuePrincipal);
		entity.setOverDueProfit(this.overDueProfit);
		entity.setFutureRpyPri(this.futureRpyPri);
		entity.setUnDisbursedAmount(this.unDisbursedAmount);
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProvisionId() {
		return provisionId;
	}

	public void setProvisionId(long provisionId) {
		this.provisionId = provisionId;
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

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public BigDecimal getOutStandPrincipal() {
		return outStandPrincipal;
	}

	public void setOutStandPrincipal(BigDecimal outStandPrincipal) {
		this.outStandPrincipal = outStandPrincipal;
	}

	public BigDecimal getOutStandProfit() {
		return outStandProfit;
	}

	public void setOutStandProfit(BigDecimal outStandProfit) {
		this.outStandProfit = outStandProfit;
	}

	public BigDecimal getProfitAccruedAndDue() {
		return profitAccruedAndDue;
	}

	public void setProfitAccruedAndDue(BigDecimal profitAccruedAndDue) {
		this.profitAccruedAndDue = profitAccruedAndDue;
	}

	public BigDecimal getProfitAccruedAndNotDue() {
		return profitAccruedAndNotDue;
	}

	public void setProfitAccruedAndNotDue(BigDecimal profitAccruedAndNotDue) {
		this.profitAccruedAndNotDue = profitAccruedAndNotDue;
	}

	public Date getDueFromDate() {
		return dueFromDate;
	}

	public void setDueFromDate(Date dueFromDate) {
		this.dueFromDate = dueFromDate;
	}

	public Date getLastFullyPaidDate() {
		return lastFullyPaidDate;
	}

	public void setLastFullyPaidDate(Date lastFullyPaidDate) {
		this.lastFullyPaidDate = lastFullyPaidDate;
	}

	public int getDueDays() {
		return dueDays;
	}

	public void setDueDays(int dueDays) {
		this.dueDays = dueDays;
	}

	public int getCurrBucket() {
		return currBucket;
	}

	public void setCurrBucket(int currBucket) {
		this.currBucket = currBucket;
	}

	public int getDpd() {
		return dpd;
	}

	public void setDpd(int dpd) {
		this.dpd = dpd;
	}

	public Date getProvisionDate() {
		return provisionDate;
	}

	public void setProvisionDate(Date provisionDate) {
		this.provisionDate = provisionDate;
	}

	public BigDecimal getProvisionedAmt() {
		return provisionedAmt;
	}

	public void setProvisionedAmt(BigDecimal provisionedAmt) {
		this.provisionedAmt = provisionedAmt;
	}

	public String getAssetCode() {
		return assetCode;
	}

	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	public int getAssetStageOrder() {
		return assetStageOrder;
	}

	public void setAssetStageOrder(int assetStageOrder) {
		this.assetStageOrder = assetStageOrder;
	}

	public boolean isNpa() {
		return npa;
	}

	public void setNpa(boolean npa) {
		this.npa = npa;
	}

	public boolean isManualProvision() {
		return manualProvision;
	}

	public void setManualProvision(boolean manualProvision) {
		this.manualProvision = manualProvision;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public long getChgLinkedTranId() {
		return chgLinkedTranId;
	}

	public void setChgLinkedTranId(long chgLinkedTranId) {
		this.chgLinkedTranId = chgLinkedTranId;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public boolean isAssetFwdMov() {
		return assetFwdMov;
	}

	public void setAssetFwdMov(boolean assetFwdMov) {
		this.assetFwdMov = assetFwdMov;
	}

	public boolean isAssetBkwMov() {
		return assetBkwMov;
	}

	public void setAssetBkwMov(boolean assetBkwMov) {
		this.assetBkwMov = assetBkwMov;
	}

	public List<ProvisionAmount> getProvisionAmounts() {
		return provisionAmounts;
	}

	public void setProvisionAmounts(List<ProvisionAmount> provisionAmounts) {
		this.provisionAmounts = provisionAmounts;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public Provision getBefImage() {
		return befImage;
	}

	public void setBefImage(Provision befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Provision getOldProvision() {
		return oldProvision;
	}

	public void setOldProvision(Provision oldProvision) {
		this.oldProvision = oldProvision;
	}

	public NPAProvisionHeader getNpaHeader() {
		return npaHeader;
	}

	public void setNpaHeader(NPAProvisionHeader npaHeader) {
		this.npaHeader = npaHeader;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public BigDecimal getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(BigDecimal collateralValue) {
		this.collateralValue = collateralValue;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public BigDecimal getProvisionRate() {
		return provisionRate;
	}

	public void setProvisionRate(BigDecimal provisionRate) {
		this.provisionRate = provisionRate;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public BigDecimal getInsuranceAmount() {
		return insuranceAmount;
	}

	public void setInsuranceAmount(BigDecimal insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public NPAProvisionHeader getNpaIntHeader() {
		return npaIntHeader;
	}

	public void setNpaIntHeader(NPAProvisionHeader npaIntHeader) {
		this.npaIntHeader = npaIntHeader;
	}

	public NPAProvisionHeader getNpaRegHeader() {
		return npaRegHeader;
	}

	public void setNpaRegHeader(NPAProvisionHeader npaRegHeader) {
		this.npaRegHeader = npaRegHeader;
	}

	public Long getNpaTemplateId() {
		return npaTemplateId;
	}

	public void setNpaTemplateId(Long npaTemplateId) {
		this.npaTemplateId = npaTemplateId;
	}

	public String getNpaTemplateCode() {
		return npaTemplateCode;
	}

	public void setNpaTemplateCode(String npaTemplateCode) {
		this.npaTemplateCode = npaTemplateCode;
	}

	public String getNpaTemplateDesc() {
		return npaTemplateDesc;
	}

	public void setNpaTemplateDesc(String npaTemplateDesc) {
		this.npaTemplateDesc = npaTemplateDesc;
	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public boolean isInsuranceComponent() {
		return insuranceComponent;
	}

	public void setInsuranceComponent(boolean insuranceComponent) {
		this.insuranceComponent = insuranceComponent;
	}

	public BigDecimal getOverDuePrincipal() {
		return overDuePrincipal;
	}

	public void setOverDuePrincipal(BigDecimal overDuePrincipal) {
		this.overDuePrincipal = overDuePrincipal;
	}

	public BigDecimal getOverDueProfit() {
		return overDueProfit;
	}

	public void setOverDueProfit(BigDecimal overDueProfit) {
		this.overDueProfit = overDueProfit;
	}

	public BigDecimal getFutureRpyPri() {
		return futureRpyPri;
	}

	public void setFutureRpyPri(BigDecimal futureRpyPri) {
		this.futureRpyPri = futureRpyPri;
	}

	public BigDecimal getUnDisbursedAmount() {
		return unDisbursedAmount;
	}

	public void setUnDisbursedAmount(BigDecimal unDisbursedAmount) {
		this.unDisbursedAmount = unDisbursedAmount;
	}

}
