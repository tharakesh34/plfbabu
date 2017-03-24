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
 * FileName    		:  SponsorBank.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-03-2017    														*
 *                                                                  						*
 * Modified Date    :  09-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.model.sponsorbank;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>SponsorBank table</b>.<br>
 *
 */
public class SponsorBank extends AbstractWorkflowEntity implements java.io.Serializable {
	private static final long	serialVersionUID	= 1L;

	private String				sponsorBankCode;
	private String				sponsorBankName;
	private String				bankCode;
	private String				bankCodeName;
	private String				bankBranchCode;
	private String				bankBranchCodeName;
	private String				branchMICRCode;
	private String				branchIFSCCode;
	private String				branchCity;
	private String				utilityCode;
	private String				accountNo;
	private String				accountType;
	private boolean				active;

	private boolean				newRecord;
	private String				lovValue;
	private SponsorBank			befImage;
	private LoggedInUser		userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public SponsorBank() {
		super();
	}

	public SponsorBank(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("bankCodeName");
		excludeFields.add("bankBranchCodeName");
		return excludeFields;
	}

	public String getId() {
		return sponsorBankCode;
	}

	public void setId(String id) {
		this.sponsorBankCode = id;
	}

	public String getSponsorBankCode() {
		return sponsorBankCode;
	}

	public void setSponsorBankCode(String sponsorBankCode) {
		this.sponsorBankCode = sponsorBankCode;
	}

	public String getSponsorBankName() {
		return sponsorBankName;
	}

	public void setSponsorBankName(String sponsorBankName) {
		this.sponsorBankName = sponsorBankName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankCodeName() {
		return this.bankCodeName;
	}

	public void setBankCodeName(String bankCodeName) {
		this.bankCodeName = bankCodeName;
	}

	public String getBankBranchCode() {
		return bankBranchCode;
	}

	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
	}

	public String getBankBranchCodeName() {
		return this.bankBranchCodeName;
	}

	public void setBankBranchCodeName(String bankBranchCodeName) {
		this.bankBranchCodeName = bankBranchCodeName;
	}

	public String getBranchMICRCode() {
		return branchMICRCode;
	}

	public void setBranchMICRCode(String branchMICRCode) {
		this.branchMICRCode = branchMICRCode;
	}

	public String getBranchIFSCCode() {
		return branchIFSCCode;
	}

	public void setBranchIFSCCode(String branchIFSCCode) {
		this.branchIFSCCode = branchIFSCCode;
	}

	public String getBranchCity() {
		return branchCity;
	}

	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}

	public String getUtilityCode() {
		return utilityCode;
	}

	public void setUtilityCode(String utilityCode) {
		this.utilityCode = utilityCode;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public SponsorBank getBefImage() {
		return this.befImage;
	}

	public void setBefImage(SponsorBank beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
