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
 * FileName    		:  DeferementDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>DeferementDetail table</b>.<br>
 *
 */
public class DeferementDetail implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private Timestamp deferedSchdDate;
	private BigDecimal defSchdProfit;
	private BigDecimal defSchdPrincipal;
	private Timestamp deferedRpyDate;
	private BigDecimal defRpySchdPft;
	private BigDecimal defRpySchdPri;
	private BigDecimal defRpySchdPftBal;
	private BigDecimal defRpySchdPriBal;
	private BigDecimal defPaidPftTillDate;
	private BigDecimal defPaidPriTillDate;
	private BigDecimal defPftBalance;
	private BigDecimal defPriBalance;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private DeferementDetail befImage;
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

	public DeferementDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("DeferementDetail");
	}

	public DeferementDetail(String id) {
		this.setId(id);
	}

	//Getter and Setter methods
	
	public String getId() {
		return finReference;
	}
	
	public void setId (String id) {
		this.finReference = id;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	
	
	public Timestamp getDeferedSchdDate() {
		return deferedSchdDate;
	}
	public void setDeferedSchdDate(Timestamp deferedSchdDate) {
		this.deferedSchdDate = deferedSchdDate;
	}
	
	
		
	
	public BigDecimal getDefSchdProfit() {
		return defSchdProfit;
	}
	public void setDefSchdProfit(BigDecimal defSchdProfit) {
		this.defSchdProfit = defSchdProfit;
	}
	
	
		
	
	public BigDecimal getDefSchdPrincipal() {
		return defSchdPrincipal;
	}
	public void setDefSchdPrincipal(BigDecimal defSchdPrincipal) {
		this.defSchdPrincipal = defSchdPrincipal;
	}
	
	
		
	
	public Timestamp getDeferedRpyDate() {
		return deferedRpyDate;
	}
	public void setDeferedRpyDate(Timestamp deferedRpyDate) {
		this.deferedRpyDate = deferedRpyDate;
	}
	
	
		
	
	public BigDecimal getDefRpySchdPft() {
		return defRpySchdPft;
	}
	public void setDefRpySchdPft(BigDecimal defRpySchdPft) {
		this.defRpySchdPft = defRpySchdPft;
	}
	
	
		
	
	public BigDecimal getDefRpySchdPri() {
		return defRpySchdPri;
	}
	public void setDefRpySchdPri(BigDecimal defRpySchdPri) {
		this.defRpySchdPri = defRpySchdPri;
	}
	
	
		
	
	public BigDecimal getDefRpySchdPftBal() {
		return defRpySchdPftBal;
	}
	public void setDefRpySchdPftBal(BigDecimal defRpySchdPftBal) {
		this.defRpySchdPftBal = defRpySchdPftBal;
	}
	
	
		
	
	public BigDecimal getDefRpySchdPriBal() {
		return defRpySchdPriBal;
	}
	public void setDefRpySchdPriBal(BigDecimal defRpySchdPriBal) {
		this.defRpySchdPriBal = defRpySchdPriBal;
	}
	
	
		
	
	public BigDecimal getDefPaidPftTillDate() {
		return defPaidPftTillDate;
	}
	public void setDefPaidPftTillDate(BigDecimal defPaidPftTillDate) {
		this.defPaidPftTillDate = defPaidPftTillDate;
	}
	
	
		
	
	public BigDecimal getDefPaidPriTillDate() {
		return defPaidPriTillDate;
	}
	public void setDefPaidPriTillDate(BigDecimal defPaidPriTillDate) {
		this.defPaidPriTillDate = defPaidPriTillDate;
	}
	
	
		
	
	public BigDecimal getDefPftBalance() {
		return defPftBalance;
	}
	public void setDefPftBalance(BigDecimal defPftBalance) {
		this.defPftBalance = defPftBalance;
	}
	
	
		
	
	public BigDecimal getDefPriBalance() {
		return defPriBalance;
	}
	public void setDefPriBalance(BigDecimal defPriBalance) {
		this.defPriBalance = defPriBalance;
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

	public DeferementDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(DeferementDetail beforeImage){
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
	public boolean equals(DeferementDetail deferementDetail) {
		return getId() == deferementDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof DeferementDetail) {
			DeferementDetail deferementDetail = (DeferementDetail) obj;
			return equals(deferementDetail);
		}
		return false;
	}
}
