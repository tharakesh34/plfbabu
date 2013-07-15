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
 * FileName    		:  FinanceCampaign.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-12-2011    														*
 *                                                                  						*
 * Modified Date    :  30-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.solutionfactory;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>FinanceCampaign table</b>.<br>
 *
 */
public class FinanceCampaign implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String fCCode = null;
	private String fCDesc;
	private String fCFinType;
	private boolean fCIsAlwMD;
	private boolean fCIsAlwGrace;
	private boolean fCOrgPrfUnchanged;
	private String fCRateType;
	private String fCBaseRate;
	private String fCSplRate;
	private BigDecimal fCIntRate;
	private String fCDftIntFrq;
	private boolean fCIsIntCpz;
	private String fCCpzFrq;
	private boolean fCIsRvwAlw;
	private String fCRvwFrq;
	private String fCGrcRateType;
	private String fCGrcBaseRate;
	private String fCGrcSplRate;
	private BigDecimal fCGrcIntRate;
	private String fCGrcDftIntFrq;
	private boolean fCGrcIsIntCpz;
	private String fCGrcCpzFrq;
	private boolean fCGrcIsRvwAlw;
	private String fCGrcRvwFrq;
	private BigDecimal fCMinTerm;
	private BigDecimal fCMaxTerm;
	private BigDecimal fCDftTerms;
	private String fCRpyFrq;
	private String fCRepayMethod;
	private boolean fCIsAlwPartialRpy;
	private boolean fCIsAlwDifferment;
	private BigDecimal fCMaxDifferment;
	private boolean fCIsAlwFrqDifferment;
	private BigDecimal fCMaxFrqDifferment;
	private boolean fCIsAlwEarlyRpy;
	private boolean fCIsAlwEarlySettle;
	private boolean fCIsDwPayRequired;
	private String fCRvwRateApplFor;
	private boolean fCAlwRateChangeAnyDate;
	private String fCGrcRvwRateApplFor;
	private boolean fCIsIntCpzAtGrcEnd;
	private boolean fCGrcAlwRateChgAnyDate;
	private BigDecimal fCMinDownPayAmount;
	private String fCSchCalCodeOnRvw;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinanceCampaign befImage;
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

	public FinanceCampaign() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("FinanceCampaign");
	}

	public FinanceCampaign(String id) {
		this.setId(id);
	}

	//Getter and Setter methods
	
	public String getId() {
		return fCCode;
	}
	
	public void setId (String id) {
		this.fCCode = id;
	}
	
	public String getFCCode() {
		return fCCode;
	}
	public void setFCCode(String fCCode) {
		this.fCCode = fCCode;
	}
	
	
		
	
	public String getFCDesc() {
		return fCDesc;
	}
	public void setFCDesc(String fCDesc) {
		this.fCDesc = fCDesc;
	}
	
	
		
	
	public String getFCFinType() {
		return fCFinType;
	}
	public void setFCFinType(String fCFinType) {
		this.fCFinType = fCFinType;
	}
	
	
		
	
	public boolean isFCIsAlwMD() {
		return fCIsAlwMD;
	}
	public void setFCIsAlwMD(boolean fCIsAlwMD) {
		this.fCIsAlwMD = fCIsAlwMD;
	}
	
	
		
	
	public boolean isFCIsAlwGrace() {
		return fCIsAlwGrace;
	}
	public void setFCIsAlwGrace(boolean fCIsAlwGrace) {
		this.fCIsAlwGrace = fCIsAlwGrace;
	}
	
	
		
	
	public boolean isFCOrgPrfUnchanged() {
		return fCOrgPrfUnchanged;
	}
	public void setFCOrgPrfUnchanged(boolean fCOrgPrfUnchanged) {
		this.fCOrgPrfUnchanged = fCOrgPrfUnchanged;
	}
	
	
		
	
	public String getFCRateType() {
		return fCRateType;
	}
	public void setFCRateType(String fCRateType) {
		this.fCRateType = fCRateType;
	}
	
	
		
	
	public String getFCBaseRate() {
		return fCBaseRate;
	}
	public void setFCBaseRate(String fCBaseRate) {
		this.fCBaseRate = fCBaseRate;
	}
	
	
		
	
	public String getFCSplRate() {
		return fCSplRate;
	}
	public void setFCSplRate(String fCSplRate) {
		this.fCSplRate = fCSplRate;
	}
	
	
		
	
	public BigDecimal getFCIntRate() {
		return fCIntRate;
	}
	public void setFCIntRate(BigDecimal fCIntRate) {
		this.fCIntRate = fCIntRate;
	}
	
	
		
	
	public String getFCDftIntFrq() {
		return fCDftIntFrq;
	}
	public void setFCDftIntFrq(String fCDftIntFrq) {
		this.fCDftIntFrq = fCDftIntFrq;
	}
	
	
		
	
	public boolean isFCIsIntCpz() {
		return fCIsIntCpz;
	}
	public void setFCIsIntCpz(boolean fCIsIntCpz) {
		this.fCIsIntCpz = fCIsIntCpz;
	}
	
	
		
	
	public String getFCCpzFrq() {
		return fCCpzFrq;
	}
	public void setFCCpzFrq(String fCCpzFrq) {
		this.fCCpzFrq = fCCpzFrq;
	}
	
	
		
	
	public boolean isFCIsRvwAlw() {
		return fCIsRvwAlw;
	}
	public void setFCIsRvwAlw(boolean fCIsRvwAlw) {
		this.fCIsRvwAlw = fCIsRvwAlw;
	}
	
	
		
	
	public String getFCRvwFrq() {
		return fCRvwFrq;
	}
	public void setFCRvwFrq(String fCRvwFrq) {
		this.fCRvwFrq = fCRvwFrq;
	}
	
	
		
	
	public String getFCGrcRateType() {
		return fCGrcRateType;
	}
	public void setFCGrcRateType(String fCGrcRateType) {
		this.fCGrcRateType = fCGrcRateType;
	}
	
	
		
	
	public String getFCGrcBaseRate() {
		return fCGrcBaseRate;
	}
	public void setFCGrcBaseRate(String fCGrcBaseRate) {
		this.fCGrcBaseRate = fCGrcBaseRate;
	}
	
	
		
	
	public String getFCGrcSplRate() {
		return fCGrcSplRate;
	}
	public void setFCGrcSplRate(String fCGrcSplRate) {
		this.fCGrcSplRate = fCGrcSplRate;
	}
	
	
		
	
	public BigDecimal getFCGrcIntRate() {
		return fCGrcIntRate;
	}
	public void setFCGrcIntRate(BigDecimal fCGrcIntRate) {
		this.fCGrcIntRate = fCGrcIntRate;
	}
	
	
		
	
	public String getFCGrcDftIntFrq() {
		return fCGrcDftIntFrq;
	}
	public void setFCGrcDftIntFrq(String fCGrcDftIntFrq) {
		this.fCGrcDftIntFrq = fCGrcDftIntFrq;
	}
	
	
		
	
	public boolean isFCGrcIsIntCpz() {
		return fCGrcIsIntCpz;
	}
	public void setFCGrcIsIntCpz(boolean fCGrcIsIntCpz) {
		this.fCGrcIsIntCpz = fCGrcIsIntCpz;
	}
	
	
		
	
	public String getFCGrcCpzFrq() {
		return fCGrcCpzFrq;
	}
	public void setFCGrcCpzFrq(String fCGrcCpzFrq) {
		this.fCGrcCpzFrq = fCGrcCpzFrq;
	}
	
	
		
	
	public boolean isFCGrcIsRvwAlw() {
		return fCGrcIsRvwAlw;
	}
	public void setFCGrcIsRvwAlw(boolean fCGrcIsRvwAlw) {
		this.fCGrcIsRvwAlw = fCGrcIsRvwAlw;
	}
	
	
		
	
	public String getFCGrcRvwFrq() {
		return fCGrcRvwFrq;
	}
	public void setFCGrcRvwFrq(String fCGrcRvwFrq) {
		this.fCGrcRvwFrq = fCGrcRvwFrq;
	}
	
	
		
	
	public BigDecimal getFCMinTerm() {
		return fCMinTerm;
	}
	public void setFCMinTerm(BigDecimal fCMinTerm) {
		this.fCMinTerm = fCMinTerm;
	}
	
	
		
	
	public BigDecimal getFCMaxTerm() {
		return fCMaxTerm;
	}
	public void setFCMaxTerm(BigDecimal fCMaxTerm) {
		this.fCMaxTerm = fCMaxTerm;
	}
	
	
		
	
	public BigDecimal getFCDftTerms() {
		return fCDftTerms;
	}
	public void setFCDftTerms(BigDecimal fCDftTerms) {
		this.fCDftTerms = fCDftTerms;
	}
	
	
		
	
	public String getFCRpyFrq() {
		return fCRpyFrq;
	}
	public void setFCRpyFrq(String fCRpyFrq) {
		this.fCRpyFrq = fCRpyFrq;
	}
	
	
		
	
	public String getFCRepayMethod() {
		return fCRepayMethod;
	}
	public void setFCRepayMethod(String fCRepayMethod) {
		this.fCRepayMethod = fCRepayMethod;
	}
	
	
		
	
	public boolean isFCIsAlwPartialRpy() {
		return fCIsAlwPartialRpy;
	}
	public void setFCIsAlwPartialRpy(boolean fCIsAlwPartialRpy) {
		this.fCIsAlwPartialRpy = fCIsAlwPartialRpy;
	}
	
	
		
	
	public boolean isFCIsAlwDifferment() {
		return fCIsAlwDifferment;
	}
	public void setFCIsAlwDifferment(boolean fCIsAlwDifferment) {
		this.fCIsAlwDifferment = fCIsAlwDifferment;
	}
	
	
		
	
	public BigDecimal getFCMaxDifferment() {
		return fCMaxDifferment;
	}
	public void setFCMaxDifferment(BigDecimal fCMaxDifferment) {
		this.fCMaxDifferment = fCMaxDifferment;
	}
	
	
		
	
	public boolean isFCIsAlwFrqDifferment() {
		return fCIsAlwFrqDifferment;
	}
	public void setFCIsAlwFrqDifferment(boolean fCIsAlwFrqDifferment) {
		this.fCIsAlwFrqDifferment = fCIsAlwFrqDifferment;
	}
	
	
		
	
	public BigDecimal getFCMaxFrqDifferment() {
		return fCMaxFrqDifferment;
	}
	public void setFCMaxFrqDifferment(BigDecimal fCMaxFrqDifferment) {
		this.fCMaxFrqDifferment = fCMaxFrqDifferment;
	}
	
	
		
	
	public boolean isFCIsAlwEarlyRpy() {
		return fCIsAlwEarlyRpy;
	}
	public void setFCIsAlwEarlyRpy(boolean fCIsAlwEarlyRpy) {
		this.fCIsAlwEarlyRpy = fCIsAlwEarlyRpy;
	}
	
	
		
	
	public boolean isFCIsAlwEarlySettle() {
		return fCIsAlwEarlySettle;
	}
	public void setFCIsAlwEarlySettle(boolean fCIsAlwEarlySettle) {
		this.fCIsAlwEarlySettle = fCIsAlwEarlySettle;
	}
	
	
		
	
	public boolean isFCIsDwPayRequired() {
		return fCIsDwPayRequired;
	}
	public void setFCIsDwPayRequired(boolean fCIsDwPayRequired) {
		this.fCIsDwPayRequired = fCIsDwPayRequired;
	}
	
	
		
	
	public String getFCRvwRateApplFor() {
		return fCRvwRateApplFor;
	}
	public void setFCRvwRateApplFor(String fCRvwRateApplFor) {
		this.fCRvwRateApplFor = fCRvwRateApplFor;
	}
	
	
		
	
	public boolean isFCAlwRateChangeAnyDate() {
		return fCAlwRateChangeAnyDate;
	}
	public void setFCAlwRateChangeAnyDate(boolean fCAlwRateChangeAnyDate) {
		this.fCAlwRateChangeAnyDate = fCAlwRateChangeAnyDate;
	}
	
	
		
	
	public String getFCGrcRvwRateApplFor() {
		return fCGrcRvwRateApplFor;
	}
	public void setFCGrcRvwRateApplFor(String fCGrcRvwRateApplFor) {
		this.fCGrcRvwRateApplFor = fCGrcRvwRateApplFor;
	}
	
	
		
	
	public boolean isFCIsIntCpzAtGrcEnd() {
		return fCIsIntCpzAtGrcEnd;
	}
	public void setFCIsIntCpzAtGrcEnd(boolean fCIsIntCpzAtGrcEnd) {
		this.fCIsIntCpzAtGrcEnd = fCIsIntCpzAtGrcEnd;
	}
	
	
		
	
	public boolean isFCGrcAlwRateChgAnyDate() {
		return fCGrcAlwRateChgAnyDate;
	}
	public void setFCGrcAlwRateChgAnyDate(boolean fCGrcAlwRateChgAnyDate) {
		this.fCGrcAlwRateChgAnyDate = fCGrcAlwRateChgAnyDate;
	}
	
	
		
	
	public BigDecimal getFCMinDownPayAmount() {
		return fCMinDownPayAmount;
	}
	public void setFCMinDownPayAmount(BigDecimal fCMinDownPayAmount) {
		this.fCMinDownPayAmount = fCMinDownPayAmount;
	}
	
	
		
	
	public String getFCSchCalCodeOnRvw() {
		return fCSchCalCodeOnRvw;
	}
	public void setFCSchCalCodeOnRvw(String fCSchCalCodeOnRvw) {
		this.fCSchCalCodeOnRvw = fCSchCalCodeOnRvw;
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

	public FinanceCampaign getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(FinanceCampaign beforeImage){
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
	public boolean equals(FinanceCampaign financeCampaign) {
		return getId() == financeCampaign.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinanceCampaign) {
			FinanceCampaign financeCampaign = (FinanceCampaign) obj;
			return equals(financeCampaign);
		}
		return false;
	}
}
