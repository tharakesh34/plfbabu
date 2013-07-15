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
 * FileName    		:  Currency.java                                                   * 	  
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

package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Currency table</b>.<br>
 *
 */
public class Currency implements java.io.Serializable {

	private static final long serialVersionUID = -7893835195187974710L;
	
	private String ccyCode = null;
	private String ccyNumber;
	private String ccyDesc;
	private String ccySwiftCode;
	private int ccyEditField;
	private BigDecimal ccyMinorCcyUnits;
	private String ccyDrRateBasisCode;
	private String lovDescCcyDrRateBasisCodeName;
	private String ccyCrRateBasisCode;
	private String lovDescCcyCrRateBasisCodeName;
	private String ccySymbol;
	private String ccyMinorCcyDesc;
	private boolean ccyIsIntRounding;
	private BigDecimal ccySpotRate;
	private boolean ccyIsReceprocal;
	private BigDecimal ccyUserRateBuy;
	private BigDecimal ccyUserRateSell;
	private boolean ccyIsMember;
	private boolean ccyIsGroup;
	private boolean ccyIsAlwForLoans;
	private boolean ccyIsAlwForDepo;
	private boolean ccyIsAlwForAc;
	private boolean ccyIsActive;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private Currency befImage;
	private LoginUserDetails userDetails;

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

	public Currency() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Currency");
	}

	public Currency(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getId() {
		return ccyCode;
	}	
	public void setId (String id) {
		this.ccyCode = id;
	}
	
	public String getCcyCode() {
		return ccyCode;
	}
	public void setCcyCode(String ccyCode) {
		this.ccyCode = ccyCode;
	}
	
	public String getCcyNumber() {
		return ccyNumber;
	}
	public void setCcyNumber(String ccyNumber) {
		this.ccyNumber = ccyNumber;
	}
	
	public String getCcyDesc() {
		return ccyDesc;
	}
	public void setCcyDesc(String ccyDesc) {
		this.ccyDesc = ccyDesc;
	}
		
	public String getCcySwiftCode() {
		return ccySwiftCode;
	}
	public void setCcySwiftCode(String ccySwiftCode) {
		this.ccySwiftCode = ccySwiftCode;
	}
		
	public int getCcyEditField() {
		return ccyEditField;
	}
	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}
		
	public BigDecimal getCcyMinorCcyUnits() {
		return ccyMinorCcyUnits;
	}
	public void setCcyMinorCcyUnits(BigDecimal ccyMinorCcyUnits) {
		this.ccyMinorCcyUnits = ccyMinorCcyUnits;
	}
		
	public String getCcyDrRateBasisCode() {
		return ccyDrRateBasisCode;
	}
	public void setCcyDrRateBasisCode(String ccyDrRateBasisCode) {
		this.ccyDrRateBasisCode = ccyDrRateBasisCode;
	}
	
	public String getLovDescCcyDrRateBasisCodeName() {
		return this.lovDescCcyDrRateBasisCodeName;
	}
	public void setLovDescCcyDrRateBasisCodeName(String lovDescCcyDrRateBasisCodeName) {
		this.lovDescCcyDrRateBasisCodeName = lovDescCcyDrRateBasisCodeName;
	}
	
	public String getCcyCrRateBasisCode() {
		return ccyCrRateBasisCode;
	}
	public void setCcyCrRateBasisCode(String ccyCrRateBasisCode) {
		this.ccyCrRateBasisCode = ccyCrRateBasisCode;
	}

	public String getLovDescCcyCrRateBasisCodeName() {
		return this.lovDescCcyCrRateBasisCodeName;
	}
	public void setLovDescCcyCrRateBasisCodeName(String lovDescCcyCrRateBasisCodeName) {
		this.lovDescCcyCrRateBasisCodeName = lovDescCcyCrRateBasisCodeName;
	}
	
	public String getCcySymbol() {
		return ccySymbol;
	}
	public void setCcySymbol(String ccySymbol) {
		this.ccySymbol = ccySymbol;
	}

	public String getCcyMinorCcyDesc() {
		return ccyMinorCcyDesc;
	}
	public void setCcyMinorCcyDesc(String ccyMinorCcyDesc) {
		this.ccyMinorCcyDesc = ccyMinorCcyDesc;
	}

	public boolean isCcyIsIntRounding() {
		return ccyIsIntRounding;
	}
	public void setCcyIsIntRounding(boolean ccyIsIntRounding) {
		this.ccyIsIntRounding = ccyIsIntRounding;
	}
		
	public BigDecimal getCcySpotRate() {
		return ccySpotRate;
	}
	public void setCcySpotRate(BigDecimal ccySpotRate) {
		this.ccySpotRate = ccySpotRate;
	}
		
	public boolean isCcyIsReceprocal() {
		return ccyIsReceprocal;
	}
	public void setCcyIsReceprocal(boolean ccyIsReceprocal) {
		this.ccyIsReceprocal = ccyIsReceprocal;
	}
		
	public BigDecimal getCcyUserRateBuy() {
		return ccyUserRateBuy;
	}
	public void setCcyUserRateBuy(BigDecimal ccyUserRateBuy) {
		this.ccyUserRateBuy = ccyUserRateBuy;
	}
		
	public BigDecimal getCcyUserRateSell() {
		return ccyUserRateSell;
	}
	public void setCcyUserRateSell(BigDecimal ccyUserRateSell) {
		this.ccyUserRateSell = ccyUserRateSell;
	}
		
	public boolean isCcyIsMember() {
		return ccyIsMember;
	}
	public void setCcyIsMember(boolean ccyIsMember) {
		this.ccyIsMember = ccyIsMember;
	}
		
	public boolean isCcyIsGroup() {
		return ccyIsGroup;
	}
	public void setCcyIsGroup(boolean ccyIsGroup) {
		this.ccyIsGroup = ccyIsGroup;
	}
		
	public boolean isCcyIsAlwForLoans() {
		return ccyIsAlwForLoans;
	}
	public void setCcyIsAlwForLoans(boolean ccyIsAlwForLoans) {
		this.ccyIsAlwForLoans = ccyIsAlwForLoans;
	}
		
	public boolean isCcyIsAlwForDepo() {
		return ccyIsAlwForDepo;
	}
	public void setCcyIsAlwForDepo(boolean ccyIsAlwForDepo) {
		this.ccyIsAlwForDepo = ccyIsAlwForDepo;
	}
	
	public boolean isCcyIsAlwForAc() {
		return ccyIsAlwForAc;
	}
	public void setCcyIsAlwForAc(boolean ccyIsAlwForAc) {
		this.ccyIsAlwForAc = ccyIsAlwForAc;
	}
		
	public boolean isCcyIsActive() {
		return ccyIsActive;
	}
	public void setCcyIsActive(boolean ccyIsActive) {
		this.ccyIsActive = ccyIsActive;
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

	public Currency getBefImage(){
		return this.befImage;
	}	
	public void setBefImage(Currency beforeImage){
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
	public boolean equals(Currency currency) {
		return getId() == currency.getId();
	}
	
	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Currency) {
			Currency currency = (Currency) obj;
			return equals(currency);
		}
		return false;
	}
}
