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
 * FileName    		:  Branch.java                                                   * 	  
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
package com.pennanttech.bajaj.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Branch table</b>.<br>
 *
 */
public class Branch extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 5702329578156631687L;

	private String branchCode;
	private String branchDesc;
	private String branchAddrLine1;
	private String branchAddrLine2;
	private String branchPOBox;
	private String branchCity;
	private String lovDescBranchCityName;
	private String branchProvince;
	private String lovDescBranchProvinceName;
	private String branchCountry;
	private String lovDescBranchCountryName;
	private String branchFax;
	private String branchTel;
	private String branchSwiftBankCde;
	private String branchSwiftCountry;
	private String lovDescBranchSwiftCountryName;
	private String branchSwiftLocCode;
	private String branchSwiftBrnCde;
	private String branchSortCode;
	private boolean branchIsActive;
	private String newBranchCode;
	private String newBranchDesc;
	private boolean miniBranch;
	private String branchType;
	private String parentBranch;
	private String region;
	private String parentBranchDesc;
	private String bankRefNo;
	private String branchAddrHNbr;
	private String branchFlatNbr;
	private String branchAddrStreet;

	private boolean newRecord;
	private String lovValue;
	private Branch befImage;
	private LoggedInUser userDetails;
    private String pinCode;
    private String pinAreaDesc;

	public boolean isNew() {
		return isNewRecord();
	}

	public Branch() {
		super();
	}

	public Branch(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("newBranchDesc");
		excludeFields.add("parentBranchDesc");
		excludeFields.add("pinAreaDesc");
		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return branchCode;
	}
	public void setId (String id) {
		this.branchCode = id;
	}

	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchDesc() {
		return branchDesc;
	}
	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getBranchAddrLine1() {
		return branchAddrLine1;
	}
	public void setBranchAddrLine1(String branchAddrLine1) {
		this.branchAddrLine1 = branchAddrLine1;
	}

	public String getBranchAddrLine2() {
		return branchAddrLine2;
	}
	public void setBranchAddrLine2(String branchAddrLine2) {
		this.branchAddrLine2 = branchAddrLine2;
	}

	public String getBranchPOBox() {
		return branchPOBox;
	}
	public void setBranchPOBox(String branchPOBox) {
		this.branchPOBox = branchPOBox;
	}

	public String getBranchCity() {
		return branchCity;
	}
	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}

	public String getLovDescBranchCityName() {
		return this.lovDescBranchCityName;
	}

	public void setLovDescBranchCityName(String lovDescBranchCityName) {
		this.lovDescBranchCityName = lovDescBranchCityName;
	}

	public String getBranchProvince() {
		return branchProvince;
	}
	public void setBranchProvince(String branchProvince) {
		this.branchProvince = branchProvince;
	}	

	public String getLovDescBranchProvinceName() {
		return this.lovDescBranchProvinceName;
	}
	public void setLovDescBranchProvinceName(String lovDescBranchProvinceName) {
		this.lovDescBranchProvinceName = lovDescBranchProvinceName;
	}

	public String getBranchCountry() {
		return branchCountry;
	}
	public void setBranchCountry(String branchCountry) {
		this.branchCountry = branchCountry;
	}	

	public String getLovDescBranchCountryName() {
		return this.lovDescBranchCountryName;
	}
	public void setLovDescBranchCountryName(String lovDescBranchCountryName) {
		this.lovDescBranchCountryName = lovDescBranchCountryName;
	}

	public String getBranchFax() {
		return branchFax;
	}
	public void setBranchFax(String branchFax) {
		this.branchFax = branchFax;
	}

	public String getBranchTel() {
		return branchTel;
	}
	public void setBranchTel(String branchTel) {
		this.branchTel = branchTel;
	}

	public String getBranchSwiftBankCde() {
		return branchSwiftBankCde;
	}
	public void setBranchSwiftBankCde(String branchSwiftBankCde) {
		this.branchSwiftBankCde = branchSwiftBankCde;
	}

	public String getBranchSwiftCountry() {
		return branchSwiftCountry;
	}
	public void setBranchSwiftCountry(String branchSwiftCountry) {
		this.branchSwiftCountry = branchSwiftCountry;
	}

	public String getLovDescBranchSwiftCountryName() {
		return lovDescBranchSwiftCountryName;
	}
	public void setLovDescBranchSwiftCountryName(
			String lovDescBranchSwiftCountryName) {
		this.lovDescBranchSwiftCountryName = lovDescBranchSwiftCountryName;
	}

	public String getBranchSwiftLocCode() {
		return branchSwiftLocCode;
	}
	public void setBranchSwiftLocCode(String branchSwiftLocCode) {
		this.branchSwiftLocCode = branchSwiftLocCode;
	}

	public String getBranchSwiftBrnCde() {
		return branchSwiftBrnCde;
	}
	public void setBranchSwiftBrnCde(String branchSwiftBrnCde) {
		this.branchSwiftBrnCde = branchSwiftBrnCde;
	}

	public String getBranchSortCode() {
		return branchSortCode;
	}
	public void setBranchSortCode(String branchSortCode) {
		this.branchSortCode = branchSortCode;
	}

	public boolean isBranchIsActive() {
		return branchIsActive;
	}
	public void setBranchIsActive(boolean branchIsActive) {
		this.branchIsActive = branchIsActive;
	}

	public String getNewBranchCode() {
		return newBranchCode;
	}
	public void setNewBranchCode(String newBranchCode) {
		this.newBranchCode = newBranchCode;
	}

	public String getNewBranchDesc() {
		return newBranchDesc;
	}
	public void setNewBranchDesc(String newBranchDesc) {
		this.newBranchDesc = newBranchDesc;
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

	public Branch getBefImage(){
		return this.befImage;
	}
	public void setBefImage(Branch beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isMiniBranch() {
		return miniBranch;
	}

	public void setMiniBranch(boolean miniBranch) {
		this.miniBranch = miniBranch;
	}

	public String getBranchType() {
		return branchType;
	}

	public void setBranchType(String branchType) {
		this.branchType = branchType;
	}

	public String getParentBranch() {
		return parentBranch;
	}

	public void setParentBranch(String parentBranch) {
		this.parentBranch = parentBranch;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getParentBranchDesc() {
		return parentBranchDesc;
	}

	public void setParentBranchDesc(String parentBranchDesc) {
		this.parentBranchDesc = parentBranchDesc;
	}

	public String getBankRefNo() {
		return bankRefNo;
	}

	public void setBankRefNo(String bankRefNo) {
		this.bankRefNo = bankRefNo;
	}

	public String getBranchAddrHNbr() {
		return branchAddrHNbr;
	}

	public void setBranchAddrHNbr(String branchAddrHNbr) {
		this.branchAddrHNbr = branchAddrHNbr;
	}

	public String getBranchFlatNbr() {
		return branchFlatNbr;
	}

	public void setBranchFlatNbr(String branchFlatNbr) {
		this.branchFlatNbr = branchFlatNbr;
	}

	public String getBranchAddrStreet() {
		return branchAddrStreet;
	}

	public void setBranchAddrStreet(String branchAddrStreet) {
		this.branchAddrStreet = branchAddrStreet;
	}
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getPinAreaDesc() {
		return pinAreaDesc;
	}

	public void setPinAreaDesc(String pinAreaDesc) {
		this.pinAreaDesc = pinAreaDesc;
	}

}
