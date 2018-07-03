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
 * FileName    		:  PSLDetail.java                                                   	* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-06-2018    														*
 *                                                                  						*
 * Modified Date    :  20-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.finance.psl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PSLDetail table</b>.<br>
 *
 */
public class PSLDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private String categoryCode;
	private String categoryCodeName;
	private String weakerSection;
	private String weakerSectionName;
	private String landHolding;
	private String landHoldingName;
	private String landArea;
	private String landAreaName;
	private String sector;
	private String sectorName;
	private double amount;
	private String subCategory;
	private String subCategoryName;
	private String purpose;
	private String purposeName;
	private String endUse;
	private String endUseName;
	private boolean newRecord = false;
	private String lovValue;
	private PSLDetail befImage;
	private LoggedInUser userDetails;
	private String loanPurpose;
	private String loanPurposeName;
	private BigDecimal eligibleAmount = BigDecimal.ZERO;


	public boolean isNew() {
		return isNewRecord();
	}

	public PSLDetail() {
		super();
	}

	public PSLDetail(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("weakerSectionName");
		excludeFields.add("landHoldingName");
		excludeFields.add("purposeName");
		excludeFields.add("endUseName");
		excludeFields.add("landAreaName");
		excludeFields.add("sectorName");
		excludeFields.add("subCategoryName");
		excludeFields.add("categoryCodeName");
		excludeFields.add("loanPurposeName");
		return excludeFields;
	}

	public String getId() {
		return finReference;
	}

	public void setId(String id) {
		this.finReference = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryCodeName() {
		return this.categoryCodeName;
	}

	public void setCategoryCodeName(String categoryCodeName) {
		this.categoryCodeName = categoryCodeName;
	}

	public String getWeakerSection() {
		return weakerSection;
	}

	public void setWeakerSection(String weakerSection) {
		this.weakerSection = weakerSection;
	}

	public String getWeakerSectionName() {
		return this.weakerSectionName;
	}

	public void setWeakerSectionName(String weakerSectionName) {
		this.weakerSectionName = weakerSectionName;
	}

	public String getLandHolding() {
		return landHolding;
	}

	public void setLandHolding(String landHolding) {
		this.landHolding = landHolding;
	}

	public String getLandHoldingName() {
		return this.landHoldingName;
	}

	public void setLandHoldingName(String landHoldingName) {
		this.landHoldingName = landHoldingName;
	}

	public String getLandArea() {
		return landArea;
	}

	public void setLandArea(String landArea) {
		this.landArea = landArea;
	}

	public String getLandAreaName() {
		return this.landAreaName;
	}

	public void setLandAreaName(String landAreaName) {
		this.landAreaName = landAreaName;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getSectorName() {
		return this.sectorName;
	}

	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getSubCategoryName() {
		return this.subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getPurposeName() {
		return this.purposeName;
	}

	public void setPurposeName(String purposeName) {
		this.purposeName = purposeName;
	}

	public String getEndUse() {
		return endUse;
	}

	public void setEndUse(String endUse) {
		this.endUse = endUse;
	}

	public String getEndUseName() {
		return this.endUseName;
	}

	public void setEndUseName(String endUseName) {
		this.endUseName = endUseName;
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

	public PSLDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PSLDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getLoanPurpose() {
		return loanPurpose;
	}

	public void setLoanPurpose(String loanPurpose) {
		this.loanPurpose = loanPurpose;
	}


	public String getLoanPurposeName() {
		return loanPurposeName;
	}

	public void setLoanPurposeName(String loanPurposeName) {
		this.loanPurposeName = loanPurposeName;
	}


	public BigDecimal getEligibleAmount() {
		return eligibleAmount;
	}

	public void setEligibleAmount(BigDecimal eligibleAmount) {
		this.eligibleAmount = eligibleAmount;
	}

}
