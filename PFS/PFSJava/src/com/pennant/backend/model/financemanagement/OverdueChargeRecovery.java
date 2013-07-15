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
 * FileName    		:  OverdueChargeRecovery.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>OverdueChargeRecovery table</b>.<br>
 *
 */
public class OverdueChargeRecovery implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private Date finSchdDate;
	private String finODFor;
	private String finBranch;
	private String finType;
	private long finCustId;
	private String finCcy;
	private Date finODDate;
	private BigDecimal finODPri = new BigDecimal(0);
	private BigDecimal finODPft = new BigDecimal(0);
	private BigDecimal finODTot = new BigDecimal(0);
	private String finODCRuleCode;
	private String finODCPLAc;
	private String finODCCAc;
	private BigDecimal finODCPLShare = new BigDecimal(0);
	private boolean finODCSweep;
	private String finODCCustCtg;
	private String finODCType;
	private String finODCOn;
	private BigDecimal finODC = new BigDecimal(0);
	private int finODCGraceDays;
	private boolean finODCAlwWaiver;
	private BigDecimal finODCMaxWaiver = new BigDecimal(0);
	private BigDecimal finODCPenalty = new BigDecimal(0);
	private BigDecimal finODCWaived = new BigDecimal(0);
	private BigDecimal finODCPLPenalty = new BigDecimal(0);
	private BigDecimal finODCCPenalty = new BigDecimal(0);
	private BigDecimal finODCPaid = new BigDecimal(0);
	private BigDecimal finODCWaiverPaid = new BigDecimal(0);
	private Date finODCLastPaidDate;
	private String finODCRecoverySts;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private OverdueChargeRecovery befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private int lovDescFinFormatter;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private Date lovDescFinStartDate;
	private Date lovDescMaturityDate;
	private BigDecimal lovDescFinAmount = new BigDecimal(0);
	private BigDecimal lovDescCurFinAmt = new BigDecimal(0);
	private BigDecimal lovDescCurSchPriDue = new BigDecimal(0);
	private BigDecimal lovDescCurSchPftDue = new BigDecimal(0);
	private BigDecimal lovDescTotOvrDueChrg = new BigDecimal(0);
	private BigDecimal lovDescTotOvrDueChrgWaived = new BigDecimal(0);
	private BigDecimal lovDescTotOvrDueChrgPaid = new BigDecimal(0);
	private BigDecimal lovDescTotOvrDueChrgBal = new BigDecimal(0);
	private BigDecimal pendingODC = new BigDecimal(0);
	
	public boolean isNew() {
		return isNewRecord();
	}

	public OverdueChargeRecovery() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("OverdueChargeRecovery");
	}

	public OverdueChargeRecovery(String id) {
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
	
	
		
	
	public Date getFinSchdDate() {
		return finSchdDate;
	}
	public void setFinSchdDate(Date finSchdDate) {
		this.finSchdDate = finSchdDate;
	}
	
	public String getFinODFor() {
		return finODFor;
	}
	public void setFinODFor(String finODFor) {
		this.finODFor = finODFor;
	}
	
	public String getFinBranch() {
		return finBranch;
	}
	public void setFinBranch(String finBrnm) {
		this.finBranch = finBrnm;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public long getFinCustId() {
		return finCustId;
	}
	public void setFinCustId(long finCustId) {
		this.finCustId = finCustId;
	}
	
	public String getFinCcy() {
		return finCcy;
	}
	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public Date getFinODDate() {
		return finODDate;
	}
	public void setFinODDate(Date finODDate) {
		this.finODDate = finODDate;
	}
	
	public BigDecimal getFinODPri() {
		return finODPri;
	}
	public void setFinODPri(BigDecimal finODPri) {
		this.finODPri = finODPri;
	}
	
	public BigDecimal getFinODPft() {
		return finODPft;
	}
	public void setFinODPft(BigDecimal finODPft) {
		this.finODPft = finODPft;
	}
	
	public BigDecimal getFinODTot() {
		return finODTot;
	}
	public void setFinODTot(BigDecimal finODTot) {
		this.finODTot = finODTot;
	}
	
	public String getFinODCRuleCode() {
		return finODCRuleCode;
	}
	public void setFinODCRuleCode(String finODCRuleCode) {
		this.finODCRuleCode = finODCRuleCode;
	}
	
	public String getFinODCPLAc() {
		return finODCPLAc;
	}
	public void setFinODCPLAc(String finODCPLAc) {
		this.finODCPLAc = finODCPLAc;
	}
	
	public String getFinODCCAc() {
		return finODCCAc;
	}
	public void setFinODCCAc(String finODCCAc) {
		this.finODCCAc = finODCCAc;
	}
	
	public BigDecimal getFinODCPLShare() {
		return finODCPLShare;
	}
	public void setFinODCPLShare(BigDecimal finODCPLShare) {
		this.finODCPLShare = finODCPLShare;
	}
	
	public boolean isFinODCSweep() {
		return finODCSweep;
	}
	public void setFinODCSweep(boolean finODCSweep) {
		this.finODCSweep = finODCSweep;
	}
	
	public String getFinODCCustCtg() {
		return finODCCustCtg;
	}
	public void setFinODCCustCtg(String finODCCustCtg) {
		this.finODCCustCtg = finODCCustCtg;
	}
	
	public String getFinODCType() {
		return finODCType;
	}
	public void setFinODCType(String finODCType) {
		this.finODCType = finODCType;
	}
	
	public String getFinODCOn() {
		return finODCOn;
	}
	public void setFinODCOn(String finODCOn) {
		this.finODCOn = finODCOn;
	}
	
	public BigDecimal getFinODC() {
		return finODC;
	}
	public void setFinODC(BigDecimal finODC) {
		this.finODC = finODC;
	}
	
	public int getFinODCGraceDays() {
		return finODCGraceDays;
	}
	public void setFinODCGraceDays(int finODCGraceDays) {
		this.finODCGraceDays = finODCGraceDays;
	}
	
	public boolean isFinODCAlwWaiver() {
		return finODCAlwWaiver;
	}
	public void setFinODCAlwWaiver(boolean finODCAlwWaiver) {
		this.finODCAlwWaiver = finODCAlwWaiver;
	}
	
	public BigDecimal getFinODCMaxWaiver() {
		return finODCMaxWaiver;
	}
	public void setFinODCMaxWaiver(BigDecimal finODCMaxWaiver) {
		this.finODCMaxWaiver = finODCMaxWaiver;
	}
	
	public BigDecimal getFinODCPenalty() {
		return finODCPenalty;
	}
	public void setFinODCPenalty(BigDecimal finODCPenalty) {
		this.finODCPenalty = finODCPenalty;
	}
	
	public BigDecimal getFinODCWaived() {
		return finODCWaived;
	}
	public void setFinODCWaived(BigDecimal finODCWaived) {
		this.finODCWaived = finODCWaived;
	}
	
	public BigDecimal getFinODCPLPenalty() {
		return finODCPLPenalty;
	}
	public void setFinODCPLPenalty(BigDecimal finODCPLPenalty) {
		this.finODCPLPenalty = finODCPLPenalty;
	}
	
	public BigDecimal getFinODCCPenalty() {
		return finODCCPenalty;
	}
	public void setFinODCCPenalty(BigDecimal finODCCPenalty) {
		this.finODCCPenalty = finODCCPenalty;
	}
	
	public BigDecimal getFinODCPaid() {
		return finODCPaid;
	}
	public void setFinODCPaid(BigDecimal finODCPaid) {
		this.finODCPaid = finODCPaid;
	}
	
	public BigDecimal getFinODCWaiverPaid() {
    	return finODCWaiverPaid;
    }

	public void setFinODCWaiverPaid(BigDecimal finODCWaiverPaid) {
    	this.finODCWaiverPaid = finODCWaiverPaid;
    }

	public Date getFinODCLastPaidDate() {
		return finODCLastPaidDate;
	}
	public void setFinODCLastPaidDate(Date finODCLastPaidDate) {
		this.finODCLastPaidDate = finODCLastPaidDate;
	}
	
	public String getFinODCRecoverySts() {
		return finODCRecoverySts;
	}
	public void setFinODCRecoverySts(String finODCRecoverySts) {
		this.finODCRecoverySts = finODCRecoverySts;
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

	public OverdueChargeRecovery getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(OverdueChargeRecovery beforeImage){
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
	
	public int getLovDescFinFormatter() {
		return lovDescFinFormatter;
	}

	public void setLovDescFinFormatter(int lovDescFinFormatter) {
		this.lovDescFinFormatter = lovDescFinFormatter;
	}
	
	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public Date getLovDescFinStartDate() {
		return lovDescFinStartDate;
	}

	public void setLovDescFinStartDate(Date lovDescFinStartDate) {
		this.lovDescFinStartDate = lovDescFinStartDate;
	}

	public Date getLovDescMaturityDate() {
		return lovDescMaturityDate;
	}

	public void setLovDescMaturityDate(Date lovDescMaturityDate) {
		this.lovDescMaturityDate = lovDescMaturityDate;
	}

	public BigDecimal getLovDescFinAmount() {
		return lovDescFinAmount;
	}

	public void setLovDescFinAmount(BigDecimal lovDescFinAmount) {
		this.lovDescFinAmount = lovDescFinAmount;
	}

	public BigDecimal getLovDescCurFinAmt() {
		return lovDescCurFinAmt;
	}

	public void setLovDescCurFinAmt(BigDecimal lovDescCurFinAmt) {
		this.lovDescCurFinAmt = lovDescCurFinAmt;
	}

	public BigDecimal getLovDescCurSchPriDue() {
		return lovDescCurSchPriDue;
	}
	public void setLovDescCurSchPriDue(BigDecimal lovDescCurSchPriDue) {
		this.lovDescCurSchPriDue = lovDescCurSchPriDue;
	}

	public BigDecimal getLovDescCurSchPftDue() {
		return lovDescCurSchPftDue;
	}
	public void setLovDescCurSchPftDue(BigDecimal lovDescCurSchPftDue) {
		this.lovDescCurSchPftDue = lovDescCurSchPftDue;
	}

	public BigDecimal getLovDescTotOvrDueChrg() {
		return lovDescTotOvrDueChrg;
	}
	public void setLovDescTotOvrDueChrg(BigDecimal lovDescTotOvrDueChrg) {
		this.lovDescTotOvrDueChrg = lovDescTotOvrDueChrg;
	}

	public BigDecimal getLovDescTotOvrDueChrgWaived() {
		return lovDescTotOvrDueChrgWaived;
	}
	public void setLovDescTotOvrDueChrgWaived(BigDecimal lovDescTotOvrDueChrgWaived) {
		this.lovDescTotOvrDueChrgWaived = lovDescTotOvrDueChrgWaived;
	}

	public BigDecimal getLovDescTotOvrDueChrgPaid() {
		return lovDescTotOvrDueChrgPaid;
	}
	public void setLovDescTotOvrDueChrgPaid(BigDecimal lovDescTotOvrDueChrgPaid) {
		this.lovDescTotOvrDueChrgPaid = lovDescTotOvrDueChrgPaid;
	}

	public BigDecimal getLovDescTotOvrDueChrgBal() {
		return lovDescTotOvrDueChrgBal;
	}
	public void setLovDescTotOvrDueChrgBal(BigDecimal lovDescTotOvrDueChrgBal) {
		this.lovDescTotOvrDueChrgBal = lovDescTotOvrDueChrgBal;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(OverdueChargeRecovery overdueChargeRecovery) {
		return getId() == overdueChargeRecovery.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof OverdueChargeRecovery) {
			OverdueChargeRecovery overdueChargeRecovery = (OverdueChargeRecovery) obj;
			return equals(overdueChargeRecovery);
		}
		return false;
	}

	public BigDecimal getPendingODC() {
		return pendingODC;
	}

	public void setPendingODC(BigDecimal pendingODC) {
		this.pendingODC = pendingODC;
	}
	
}
