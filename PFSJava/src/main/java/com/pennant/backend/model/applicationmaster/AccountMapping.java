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
 * * FileName : AccountMapping.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * * Modified Date
 * : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.RequestSource;

/**
 * Model class for the <b>AccountMapping table</b>.<br>
 *
 */
public class AccountMapping extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String account;
	private String hostAccount;
	private String finType;
	private String lovValue;
	private AccountMapping befImage;
	private LoggedInUser userDetails;

	private Long profitCenterID;
	private Long costCenterID;
	private String profitCenterDesc;
	private String costCenterDesc;
	private String costCenterCode;
	private String profitCenterCode;
	private String accountType;
	private String accountTypeDesc;
	private String finTypeDesc;
	private Date openedDate;
	private Date closedDate;
	private String status;
	private String allowedManualEntry;
	private long createdBy;
	private Timestamp createdOn;
	private Long approvedBy;
	private Timestamp approvedOn;
	private String gLDescription;
	private String accountTypeGroup;
	private RequestSource requestSource = RequestSource.UI;

	private List<AccountMapping> accountMappingList = new ArrayList<AccountMapping>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private String tranType = "";

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
		excludeFields.add("gLDescription");
		excludeFields.add("requestSource");
		excludeFields.add("accountTypeGroup");
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

	public Long getProfitCenterID() {
		return profitCenterID;
	}

	public void setProfitCenterID(Long profitCenterID) {
		this.profitCenterID = profitCenterID;
	}

	public Long getCostCenterID() {
		return costCenterID;
	}

	public void setCostCenterID(Long costCenterID) {
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

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
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

	public Date getOpenedDate() {
		return openedDate;
	}

	public void setOpenedDate(Date openedDate) {
		this.openedDate = openedDate;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAllowedManualEntry() {
		return allowedManualEntry;
	}

	public void setAllowedManualEntry(String allowedManualEntry) {
		this.allowedManualEntry = allowedManualEntry;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getGLDescription() {
		return gLDescription;
	}

	public void setGLDescription(String gLDescription) {
		this.gLDescription = gLDescription;
	}

	public RequestSource getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(RequestSource requestSource) {
		this.requestSource = requestSource;
	}

	public String getAccountTypeGroup() {
		return accountTypeGroup;
	}

	public void setAccountTypeGroup(String accountTypeGroup) {
		this.accountTypeGroup = accountTypeGroup;
	}

}
