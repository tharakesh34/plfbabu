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
 * FileName    		:  TransactionEntry.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>TransactionEntry table</b>.<br>
 *
 */
public class TransactionEntry implements java.io.Serializable, Entity {

	private static final long serialVersionUID = -477902982694204735L;
	private long accountSetid;
	private int transOrder;
	private String transDesc;
	private String debitcredit;
	private boolean shadowPosting;
	private String account;
	private String accountType;
	private String accountBranch;
	private String lovDescAccountTypeName;
	private String lovDescAccountBranchName;	
	private String accountSubHeadRule;
	private String lovDescAccountSubHeadRuleName;	
	private String transcationCode;
	private String lovDescTranscationCodeName;	
	private String rvsTransactionCode;
	private String lovDescRvsTransactionCodeName;
	private String amountRule;
	private String chargeType;
	
	private String feeCode;
	//private String ruleDecider;
	private String lovDescFeeCodeName;
	private boolean entryByInvestment;
	private boolean openNewFinAc;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private TransactionEntry befImage;
	private LoginUserDetails userDetails;
	
	private String lovDescEventCodeName;
	private String lovDescEventCodeDesc;
	private String lovDescAccSetCodeName;
	private String lovDescAccSetCodeDesc;
	private String lovDescSysInAcTypeName;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public TransactionEntry() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("TransactionEntry");
	}

	public TransactionEntry(long id) {
		this.setId(id);
	}

	//Getter and Setter methods
	
	public long getId() {
		return accountSetid;
	}
	
	public void setId (long id) {
		this.accountSetid = id;
	}
	
	public long getAccountSetid() {
		return accountSetid;
	}
	public void setAccountSetid(long accountSetid) {
		this.accountSetid = accountSetid;
	}
	
	
		
	
	public int getTransOrder() {
		return transOrder;
	}
	public void setTransOrder(int transOrder) {
		this.transOrder = transOrder;
	}
	
	
		
	
	public String getTransDesc() {
		return transDesc;
	}
	public void setTransDesc(String transDesc) {
		this.transDesc = transDesc;
	}
	
	
		
	
	public String getDebitcredit() {
		return debitcredit;
	}
	public void setDebitcredit(String debitcredit) {
		this.debitcredit = debitcredit;
	}
			
	
	/**
	 * @return the shadowPosting
	 */
	public boolean isShadowPosting() {
		return shadowPosting;
	}

	/**
	 * @param shadowPosting the shadowPosting to set
	 */
	public void setShadowPosting(boolean shadowPosting) {
		this.shadowPosting = shadowPosting;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	

	/**
	 * @return the accountBranch
	 */
	public String getAccountBranch() {
		return accountBranch;
	}

	/**
	 * @param accountBranch the accountBranch to set
	 */
	public void setAccountBranch(String accountBranch) {
		this.accountBranch = accountBranch;
	}

	public String getLovDescAccountTypeName() {
		return this.lovDescAccountTypeName;
	}

	public void setLovDescAccountTypeName (String lovDescAccountTypeName) {
		this.lovDescAccountTypeName = lovDescAccountTypeName;
	}
	
		
	
	public String getAccountSubHeadRule() {
		return accountSubHeadRule;
	}
	public void setAccountSubHeadRule(String accountSubHeadRule) {
		this.accountSubHeadRule = accountSubHeadRule;
	}
	

	public String getLovDescAccountSubHeadRuleName() {
		return this.lovDescAccountSubHeadRuleName;
	}

	public void setLovDescAccountSubHeadRuleName (String lovDescAccountSubHeadRuleName) {
		this.lovDescAccountSubHeadRuleName = lovDescAccountSubHeadRuleName;
	}
	
		
	
	public String getTranscationCode() {
		return transcationCode;
	}
	public void setTranscationCode(String transcationCode) {
		this.transcationCode = transcationCode;
	}
	

	public String getLovDescTranscationCodeName() {
		return this.lovDescTranscationCodeName;
	}

	public void setLovDescTranscationCodeName (String lovDescTranscationCodeName) {
		this.lovDescTranscationCodeName = lovDescTranscationCodeName;
	}
	
		
	
	public String getRvsTransactionCode() {
		return rvsTransactionCode;
	}
	public void setRvsTransactionCode(String rvsTransactionCode) {
		this.rvsTransactionCode = rvsTransactionCode;
	}
	

	public String getLovDescRvsTransactionCodeName() {
		return this.lovDescRvsTransactionCodeName;
	}

	public void setLovDescRvsTransactionCodeName (String lovDescRvsTransactionCodeName) {
		this.lovDescRvsTransactionCodeName = lovDescRvsTransactionCodeName;
	}
	
		
	
	public String getAmountRule() {
		return amountRule;
	}
	public void setAmountRule(String amountRule) {
		this.amountRule = amountRule;
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

	public TransactionEntry getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(TransactionEntry beforeImage){
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

	// Overidden Equals method to handle the comparision
	public boolean equals(TransactionEntry transactionEntry) {
		return getId() == transactionEntry.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof TransactionEntry) {
			TransactionEntry transactionEntry = (TransactionEntry) obj;
			return equals(transactionEntry);
		}
		return false;
	}

	public void setLovDescEventCodeName(String lovDescEventCodeName) {
		this.lovDescEventCodeName = lovDescEventCodeName;
	}

	public String getLovDescEventCodeName() {
		return lovDescEventCodeName;
	}

	public void setLovDescAccSetCodeName(String lovDescAccSetCodeName) {
		this.lovDescAccSetCodeName = lovDescAccSetCodeName;
	}

	public String getLovDescAccSetCodeName() {
		return lovDescAccSetCodeName;
	}

	public void setLovDescAccSetCodeDesc(String lovDescAccSetCodeDesc) {
		this.lovDescAccSetCodeDesc = lovDescAccSetCodeDesc;
	}

	public String getLovDescAccSetCodeDesc() {
		return lovDescAccSetCodeDesc;
	}

	/**
	 * @return the lovDescAccountBranchName
	 */
	public String getLovDescAccountBranchName() {
		return lovDescAccountBranchName;
	}

	/**
	 * @param lovDescAccountBranchName the lovDescAccountBranchName to set
	 */
	public void setLovDescAccountBranchName(String lovDescAccountBranchName) {
		this.lovDescAccountBranchName = lovDescAccountBranchName;
	}



	public String getLovDescEventCodeDesc() {
		return lovDescEventCodeDesc;
	}

	public void setLovDescEventCodeDesc(String lovDescEventCodeDesc) {
		this.lovDescEventCodeDesc = lovDescEventCodeDesc;
	}

	public void setLovDescSysInAcTypeName(String lovDescSysInAcTypeName) {
		this.lovDescSysInAcTypeName = lovDescSysInAcTypeName;
	}

	public String getLovDescSysInAcTypeName() {
		return lovDescSysInAcTypeName;
	}

	public String getFeeCode() {
		return feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	/*public String getRuleDecider() {
		return ruleDecider;
	}*/

	public String getLovDescFeeCodeName() {
		return lovDescFeeCodeName;
	}

	public void setLovDescFeeCodeName(String lovDescFeeCodeName) {
		this.lovDescFeeCodeName = lovDescFeeCodeName;
	}

	/*public void setRuleDecider(String ruleDecider) {
		this.ruleDecider = ruleDecider;
	}*/

	public void setChargeType(String chargeType) {
	    this.chargeType = chargeType;
    }

	public String getChargeType() {
	    return chargeType;
    }

	public void setEntryByInvestment(boolean entryByInvestment) {
	    this.entryByInvestment = entryByInvestment;
    }

	public boolean isEntryByInvestment() {
	    return entryByInvestment;
    }

	public void setOpenNewFinAc(boolean openNewFinAc) {
	    this.openNewFinAc = openNewFinAc;
    }

	public boolean isOpenNewFinAc() {
	    return openNewFinAc;
    }
	
}
