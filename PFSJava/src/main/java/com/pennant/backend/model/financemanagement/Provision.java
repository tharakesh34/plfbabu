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
 * FileName    		:  Provision.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

	private boolean newRecord = false;
	private Provision befImage;
	private Provision oldProvision;
	private LoggedInUser userDetails;

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

		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
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
}
