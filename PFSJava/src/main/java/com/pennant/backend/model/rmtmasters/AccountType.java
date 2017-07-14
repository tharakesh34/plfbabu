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
 * FileName    		:  AccountType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>AccountType table</b>.<br>
 * 
 */
public class AccountType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -6862602123712610264L;

	private long acTypeGrpId;
	private String acType;
	private String acTypeDesc;
	private String acPurpose;
	private String acHeadCode;
	private boolean internalAc;
	private boolean custSysAc;
	private String acLmtCategory;
	private String assertOrLiability;
	private boolean onBalanceSheet;
	private boolean allowOverDraw;
	private boolean acTypeIsActive;
	private boolean newRecord;
	private String lovValue;
	private AccountType befImage;
	private LoggedInUser userDetails;
	private String groupCode;
	private String groupDescription;
	private int acctTypeLevel;
	private long profitCenterID;
	private Long costCenterID;
	private String profitCenterDesc;
	private String costCenterDesc;
	private String costCenterCode;
	private String profitCenterCode;
	private boolean taxApplicable;
	private String  aCCADDLVAR1;
	private String  aCCADDLVAR2;
	private boolean aCCADDLCHAR1;
	
	private String  extractionType;

	public boolean isNew() {
		return isNewRecord();
	}

	public AccountType() {
		super();
	}

	public AccountType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return acType;
	}
	public void setId(String id) {
		this.acType = id;
	}

	public String getAcType() {
		return acType;
	}
	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getAcTypeDesc() {
		return acTypeDesc;
	}
	public void setAcTypeDesc(String acTypeDesc) {
		this.acTypeDesc = acTypeDesc;
	}

	public String getAcPurpose() {
		return acPurpose;
	}
	public void setAcPurpose(String acPurpose) {
		this.acPurpose = acPurpose;
	}

	public String getAcHeadCode() {
		return acHeadCode;
	}
	public void setAcHeadCode(String acHeadCode) {
		this.acHeadCode = acHeadCode;
	}

	public boolean isInternalAc() {
		return internalAc;
	}

	public void setInternalAc(boolean internalAc) {
		this.internalAc = internalAc;
	}

	public boolean isCustSysAc() {
		return custSysAc;
	}

	public void setCustSysAc(boolean custSysAc) {
		this.custSysAc = custSysAc;
	}
	
	public String getAcLmtCategory() {
    	return acLmtCategory;
    }

	public void setAcLmtCategory(String acLmtCategory) {
    	this.acLmtCategory = acLmtCategory;
    }

	public boolean isAcTypeIsActive() {
		return acTypeIsActive;
	}
	public void setAcTypeIsActive(boolean acTypeIsActive) {
		this.acTypeIsActive = acTypeIsActive;
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

	public AccountType getBefImage() {
		return this.befImage;
	}
	public void setBefImage(AccountType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getAssertOrLiability() {
		return assertOrLiability;
	}

	public void setAssertOrLiability(String assertOrLiability) {
		this.assertOrLiability = assertOrLiability;
	}

	public boolean isOnBalanceSheet() {
		return onBalanceSheet;
	}

	public void setOnBalanceSheet(boolean onBalanceSheet) {
		this.onBalanceSheet = onBalanceSheet;
	}

	public boolean isAllowOverDraw() {
		return allowOverDraw;
	}

	public void setAllowOverDraw(boolean allowOverDraw) {
		this.allowOverDraw = allowOverDraw;
	}

	public long getAcTypeGrpId() {
		return acTypeGrpId;
	}

	public void setAcTypeGrpId(long acTypeGrpId) {
		this.acTypeGrpId = acTypeGrpId;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("groupCode");
		excludeFields.add("groupDescription");
		excludeFields.add("acctTypeLevel");
		excludeFields.add("profitCenterDesc");
		excludeFields.add("costCenterDesc");
		excludeFields.add("costCenterCode");
		excludeFields.add("profitCenterCode");
		return excludeFields;
	}

	public int getAcctTypeLevel() {
		return acctTypeLevel;
	}

	public void setAcctTypeLevel(int acctTypeLevel) {
		this.acctTypeLevel = acctTypeLevel;
	}

	public String getProfitCenterDesc() {
		return profitCenterDesc;
	}

	public void setProfitCenterDesc(String profitCenterDesc) {
		this.profitCenterDesc = profitCenterDesc;
	}

	public String getCostCenterDesc() {
		return costCenterDesc;
	}

	public void setCostCenterDesc(String costCenterDesc) {
		this.costCenterDesc = costCenterDesc;
	}

	public String getCostCenterCode() {
		return costCenterCode;
	}

	public void setCostCenterCode(String costCenterCode) {
		this.costCenterCode = costCenterCode;
	}

	public String getProfitCenterCode() {
		return profitCenterCode;
	}

	public void setProfitCenterCode(String profitCenterCode) {
		this.profitCenterCode = profitCenterCode;
	}

	public long getProfitCenterID() {
		return profitCenterID;
	}

	public void setProfitCenterID(long profitCenterID) {
		this.profitCenterID = profitCenterID;
	}

	public Long getCostCenterID() {
		return costCenterID;
	}

	public void setCostCenterID(Long costCenterID) {
		this.costCenterID = costCenterID;
	}

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public String getaCCADDLVAR1() {
		return aCCADDLVAR1;
	}

	public void setaCCADDLVAR1(String aCCADDLVAR1) {
		this.aCCADDLVAR1 = aCCADDLVAR1;
	}

	public String getaCCADDLVAR2() {
		return aCCADDLVAR2;
	}

	public void setaCCADDLVAR2(String aCCADDLVAR2) {
		this.aCCADDLVAR2 = aCCADDLVAR2;
	}

	public boolean isaCCADDLCHAR1() {
		return aCCADDLCHAR1;
	}

	public void setaCCADDLCHAR1(boolean aCCADDLCHAR1) {
		this.aCCADDLCHAR1 = aCCADDLCHAR1;
	}

	public String getExtractionType() {
		return extractionType;
	}

	public void setExtractionType(String extractionType) {
		this.extractionType = extractionType;
	}

}
