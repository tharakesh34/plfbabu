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
 * FileName    		:  Accounts.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-01-2012    														*
 *                                                                  						*
 * Modified Date    :  02-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-01-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.accounts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Accounts table</b>.<br>
 *
 */
public class Accounts implements java.io.Serializable {

	private static final long serialVersionUID = -1673137129792916291L;
	private String accountId;
	private String acCcy;
	private String acType;
	private String acBranch;
	private long acCustId;
	private String acFullName;
	private String acShortName;
	private String acPurpose;
	private boolean internalAc;
	private boolean custSysAc;
	private BigDecimal acPrvDayBal= BigDecimal.ZERO;
	private BigDecimal acTodayDr= BigDecimal.ZERO;
	private BigDecimal acTodayCr= BigDecimal.ZERO;
	private BigDecimal acTodayNet= BigDecimal.ZERO;
	private BigDecimal acAccrualBal= BigDecimal.ZERO;
	private BigDecimal acTodayBal= BigDecimal.ZERO;
	private Date acOpenDate;
	private Date acCloseDate;
	private Date acLastCustTrnDate;
	private Date acLastSysTrnDate;
	private boolean acActive;
	private boolean acBlocked;
	private boolean acClosed;

	private String hostAcNumber;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private Accounts befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private int    lovDescFinFormatter;
	private String lovDescCustCIF;
	private String lovDescAccTypeDesc;
	private String lovDescCurrency;
	private String lovDescBranchCodeName;
	private String lovDescAcHeadCode;
	private String lovDescCcyNumber;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public boolean isNew() {
		return isNewRecord();
	}

	public Accounts() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Accounts");
	}

	public Accounts(String accountId){
		super();
		this.accountId=accountId;
	}

	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAcCcy() {
		return acCcy;
	}
	public void setAcCcy(String acCcy) {
		this.acCcy = acCcy;
	}

	public String getAcType() {
		return acType;
	}
	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getAcBranch() {
		return acBranch;
	}
	public void setAcBranch(String acBranch) {
		this.acBranch = acBranch;
	}
	
	public long getAcCustId() {
		return acCustId;
	}
	public void setAcCustId(long acCustId) {
		this.acCustId = acCustId;
	}

	public String getAcFullName() {
		return acFullName;
	}
	public void setAcFullName(String acFullName) {
		this.acFullName = acFullName;
	}

	public String getAcShortName() {
		return acShortName;
	}
	public void setAcShortName(String acShortName) {
		this.acShortName = acShortName;
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

	public BigDecimal getAcPrvDayBal() {
		return acPrvDayBal;
	}
	public void setAcPrvDayBal(BigDecimal acPrvDayBal) {
		this.acPrvDayBal = acPrvDayBal;
	}

	public BigDecimal getAcTodayDr() {
		return acTodayDr;
	}
	public void setAcTodayDr(BigDecimal acTodayDr) {
		this.acTodayDr = acTodayDr;
	}

	public BigDecimal getAcTodayCr() {
		return acTodayCr;
	}
	public void setAcTodayCr(BigDecimal acTodayCr) {
		this.acTodayCr = acTodayCr;
	}

	public BigDecimal getAcTodayNet() {
		return acTodayNet;
	}
	public void setAcTodayNet(BigDecimal acTodayNet) {
		this.acTodayNet = acTodayNet;
	}

	public BigDecimal getAcAccrualBal() {
		return acAccrualBal;
	}
	public void setAcAccrualBal(BigDecimal acAccrualBal) {
		this.acAccrualBal = acAccrualBal;
	}

	public BigDecimal getAcTodayBal() {
		return acTodayBal;
	}
	public void setAcTodayBal(BigDecimal acTodayBal) {
		this.acTodayBal = acTodayBal;
	}

	public boolean isAcActive() {
		return acActive;
	}
	public void setAcActive(boolean acActive) {
		this.acActive = acActive;
	}

	public boolean isAcBlocked() {
		return acBlocked;
	}
	public void setAcBlocked(boolean acBlocked) {
		this.acBlocked = acBlocked;
	}

	public boolean isAcClosed() {
		return acClosed;
	}
	public void setAcClosed(boolean acClosed) {
		this.acClosed = acClosed;
	}

	public String getHostAcNumber() {
		return hostAcNumber;
	}
	public void setHostAcNumber(String hostAcNumber) {
		this.hostAcNumber = hostAcNumber;
	}

	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public Accounts getBefImage(){
		return this.befImage;
	}
	public void setBefImage(Accounts beforeImage){
		this.befImage=beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	// Overridden Equals method to handle the comparison
	public boolean equals(Accounts accounts) {
		return getAccountId() == accounts.getAccountId();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Accounts) {
			Accounts acounts = (Accounts) obj;
			return equals(acounts);
		}
		return false;
	}

	public void setAcPurpose(String acPurpose) {
		this.acPurpose = acPurpose;
	}
	public String getAcPurpose() {
		return acPurpose;
	}

	public int getLovDescFinFormatter() {
    	return lovDescFinFormatter;
    }
	public void setLovDescFinFormatter(int lovDescFinFormatter) {
    	this.lovDescFinFormatter = lovDescFinFormatter;
    }

	public void setLovDescAcHeadCode(String lovDescAcHeadCode) {
		this.lovDescAcHeadCode = lovDescAcHeadCode;
	}
	public String getLovDescAcHeadCode() {
		return lovDescAcHeadCode;
	}

	public void setLovDescCcyNumber(String lovDescCcyNumber) {
		this.lovDescCcyNumber = lovDescCcyNumber;
	}
	public String getLovDescCcyNumber() {
		return lovDescCcyNumber;
	}
	
	public Date getAcOpenDate() {
		return acOpenDate;
	}
	public void setAcOpenDate(Date acOpenDate) {
		this.acOpenDate = acOpenDate;
	}

	public Date getAcLastCustTrnDate() {
		return acLastCustTrnDate;
	}
	public void setAcLastCustTrnDate(Date acLastCustTrnDate) {
		this.acLastCustTrnDate = acLastCustTrnDate;
	}

	public Date getAcLastSysTrnDate() {
		return acLastSysTrnDate;
	}
	public void setAcLastSysTrnDate(Date acLastSysTrnDate) {
		this.acLastSysTrnDate = acLastSysTrnDate;
	}

	public void setAcCloseDate(Date acCloseDate) {
		this.acCloseDate = acCloseDate;
	}
	public Date getAcCloseDate() {
		return acCloseDate;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescAccTypeDesc() {
		return lovDescAccTypeDesc;
	}
	public void setLovDescAccTypeDesc(String lovDescAccTypeDesc) {
		this.lovDescAccTypeDesc = lovDescAccTypeDesc;
	}

	public String getLovDescCurrency() {
		return lovDescCurrency;
	}
	public void setLovDescCurrency(String lovDescCurrency) {
		this.lovDescCurrency = lovDescCurrency;
	}

	public String getLovDescBranchCodeName() {
		return lovDescBranchCodeName;
	}
	public void setLovDescBranchCodeName(String lovDescBranchCodeName) {
		this.lovDescBranchCodeName = lovDescBranchCodeName;
	}

}
