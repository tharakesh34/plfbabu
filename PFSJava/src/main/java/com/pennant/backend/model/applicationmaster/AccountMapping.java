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
 * FileName    		:  AccountMapping.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>AccountMapping table</b>.<br>
 *
 */
public class AccountMapping extends AbstractWorkflowEntity {
	private static final long					serialVersionUID	= 1L;

	private String								account;
	private String								hostAccount;
	private String								finType;
	private boolean								newRecord			= false;
	private String								lovValue;
	private AccountMapping						befImage;
	private LoggedInUser						userDetails;

	private long								profitCenterID;
	private long								costCenterID;
	private String								profitCenterDesc;
	private String								costCenterDesc;
	private String								costCenterCode;
	private String								profitCenterCode;
	private String								accountType;
	private String								accountTypeDesc;
	private String								finTypeDesc;

	private List<AccountMapping>				accountMappingList	= new ArrayList<AccountMapping>();
	private HashMap<String, List<AuditDetail>>	auditDetailMap		= new HashMap<String, List<AuditDetail>>();
	private String								tranType			= "";

	public boolean isNew() {
		return isNewRecord();
	}

	public AccountMapping() {
		super();
	}

	public AccountMapping(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("profitCenterDesc");
		excludeFields.add("costCenterDesc");
		excludeFields.add("costCenterCode");
		excludeFields.add("profitCenterCode");
		excludeFields.add("accountTypeDesc");
		excludeFields.add("finTypeDesc");
		excludeFields.add("tranType");
		return excludeFields;
	}

	public String getId() {
		return account;
	}

	public void setId(String id) {
		this.account = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getHostAccount() {
		return hostAccount;
	}

	public void setHostAccount(String hostAccount) {
		this.hostAccount = hostAccount;
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

	public AccountMapping getBefImage() {
		return this.befImage;
	}

	public void setBefImage(AccountMapping beforeImage) {
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

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getProfitCenterID() {
		return profitCenterID;
	}

	public void setProfitCenterID(long profitCenterID) {
		this.profitCenterID = profitCenterID;
	}

	public long getCostCenterID() {
		return costCenterID;
	}

	public void setCostCenterID(long costCenterID) {
		this.costCenterID = costCenterID;
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

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountTypeDesc() {
		return accountTypeDesc;
	}

	public void setAccountTypeDesc(String accountTypeDesc) {
		this.accountTypeDesc = accountTypeDesc;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public List<AccountMapping> getAccountMappingList() {
		return accountMappingList;
	}

	public void setAccountMappingList(List<AccountMapping> accountMappingList) {
		this.accountMappingList = accountMappingList;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
}
