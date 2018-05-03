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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

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
	private String				acType;
	private String 				acTypeName;
	private String 				fileName;
	private boolean				alwFileDownload;
	private int					inFavourLength;
	private boolean				active;
	private boolean				alwDisb;
	private boolean				alwPayment;
	private boolean				alwReceipt;

	private boolean				newRecord;
	private String				lovValue;
	private PartnerBank			befImage;
	private LoggedInUser		userDetails;
	private String				hostGLCode;
	private String				profitCenterID;
	private String				costCenterID;
	private String              entity;
	private String              entityDesc;
	
	private List<PartnerBankModes>partnerBankModesList = new ArrayList<PartnerBankModes>();
	private List<PartnerBranchModes>partnerBranchModesList = new ArrayList<PartnerBranchModes>();

	public boolean isNew() {
		return isNewRecord();
	}

	public PartnerBank() {
		super();
	}

	public PartnerBank(long partnerBankId) {
		super();
		this.setPartnerBankId(partnerBankId);
	}
	

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("bankCodeName");
		excludeFields.add("bankBranchCodeName");
		excludeFields.add("acTypeName");
		excludeFields.add("entityDesc");
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

	public int getInFavourLength() {
		return inFavourLength;
	}

	public void setInFavourLength(int inFavourLength) {
		this.inFavourLength = inFavourLength;
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

	public List<PartnerBankModes> getPartnerBankModesList() {
		return partnerBankModesList;
	}

	public void setPartnerBankModesList(List<PartnerBankModes> partnerBankModesList) {
		this.partnerBankModesList = partnerBankModesList;
	}

	public boolean isAlwDisb() {
		return alwDisb;
	}

	public void setAlwDisb(boolean alwDisb) {
		this.alwDisb = alwDisb;
	}

	public boolean isAlwPayment() {
		return alwPayment;
	}

	public void setAlwPayment(boolean alwPayment) {
		this.alwPayment = alwPayment;
	}

	public boolean isAlwReceipt() {
		return alwReceipt;
	}

	public void setAlwReceipt(boolean alwReceipt) {
		this.alwReceipt = alwReceipt;
	}
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
	public String getHostGLCode() {
		return hostGLCode;
	}

	public void setHostGLCode(String hostGLCode) {
		this.hostGLCode = hostGLCode;
	}

	public List<PartnerBranchModes> getPartnerBranchModesList() {
		return partnerBranchModesList;
	}

	public void setPartnerBranchModesList(List<PartnerBranchModes> partnerBranchModesList) {
		this.partnerBranchModesList = partnerBranchModesList;
	}

	public String getProfitCenterID() {
		return profitCenterID;
	}

	public void setProfitCenterID(String profitCenterID) {
		this.profitCenterID = profitCenterID;
	}

	public String getCostCenterID() {
		return costCenterID;
	}

	public void setCostCenterID(String costCenterID) {
		this.costCenterID = costCenterID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}



}
