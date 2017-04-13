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
 * FileName    		:  PartnerBank.java                                                   * 	  
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

package com.pennant.backend.model.partnerbank;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>PartnerBank table</b>.<br>
 *
 */
public class PartnerBank extends AbstractWorkflowEntity implements Entity{
	private static final long	serialVersionUID	= 1L;

	private long 				partnerBankId = Long.MIN_VALUE;
	private String				partnerBankCode;
	private String				partnerBankName;
	private String				bankCode;
	private String				bankCodeName;
	private String				bankBranchCode;
	private String				bankBranchCodeName;
	private String				branchMICRCode;
	private String				branchIFSCCode;
	private String				branchCity;
	private String				utilityCode;
	private String				accountNo;
	private String				usage;
	private String				acType;
	private String 				acTypeName;
	private boolean				alwFileDownload;
	private boolean				disbDownload;
	private int					inFavourLength;
	private String				accountCategory;
	private boolean				active;

	private boolean				newRecord;
	private String				lovValue;
	private PartnerBank			befImage;
	private LoggedInUser		userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public PartnerBank() {
		super();
	}


	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("bankCodeName");
		excludeFields.add("bankBranchCodeName");
		excludeFields.add("acTypeName");
		return excludeFields;
	}



	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
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
	public PartnerBank getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PartnerBank beforeImage) {
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

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public boolean isAlwFileDownload() {
		return alwFileDownload;
	}

	public void setAlwFileDownload(boolean alwFileDownload) {
		this.alwFileDownload = alwFileDownload;
	}

	public boolean isDisbDownload() {
		return disbDownload;
	}

	public void setDisbDownload(boolean disbDownload) {
		this.disbDownload = disbDownload;
	}

	public int getInFavourLength() {
		return inFavourLength;
	}

	public void setInFavourLength(int inFavourLength) {
		this.inFavourLength = inFavourLength;
	}

	public String getAccountCategory() {
		return accountCategory;
	}

	public void setAccountCategory(String accountCategory) {
		this.accountCategory = accountCategory;
	}

	public String getAcTypeName() {
		return acTypeName;
	}

	public void setAcTypeName(String acTypeName) {
		this.acTypeName = acTypeName;
	}

	@Override
	public void setId(long id) {
		this.partnerBankId = id;
		
	}

	@Override
	public long getId() {
		return partnerBankId;
	}
	
	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

}
