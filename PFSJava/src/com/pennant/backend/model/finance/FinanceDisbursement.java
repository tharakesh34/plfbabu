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
 * FileName    		:  FinanceDisbursement.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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
import java.util.Date;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>FinanceDisbursement table</b>.<br>
 *
 */
public class FinanceDisbursement implements java.io.Serializable {

    private static final long serialVersionUID = -5230263039482884873L;
    
	private String finReference = null;
	private Date disbDate = null;
	private int disbSeq;
	private long logKey;
	private String disbType;
	private String disbDesc;
	private long disbExpType;
	private String lovDescDisbExpType;
	private long disbBeneficiary = Long.MIN_VALUE;
	private String lovdescDisbBenificiary;
	private String lovDescDisbBenfShrtName;;
	private BigDecimal disbRetPerc = BigDecimal.ZERO;
	private BigDecimal disbRetAmount = BigDecimal.ZERO;
	private BigDecimal disbRetPaid = BigDecimal.ZERO;
	private Date retPaidDate = null;
	private boolean autoDisb;
	private String disbAccountId;
	private BigDecimal disbAmount= BigDecimal.ZERO;
	private Date disbReqDate = null;
	private BigDecimal disbClaim = BigDecimal.ZERO;
	private boolean disbDisbursed;
	private BigDecimal feeChargeAmt= BigDecimal.ZERO;
	private boolean disbIsActive;
	private String disbRemarks;
	private BigDecimal netAdvDue= BigDecimal.ZERO;
	private BigDecimal netRetDue= BigDecimal.ZERO;
	private long linkedTranId;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinanceDisbursement befImage;
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

	public FinanceDisbursement() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("FinanceDisbursement");
	}

	public FinanceDisbursement(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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
	
	public Date getDisbDate() {
		return disbDate;
	}
	public void setDisbDate(Date disbDate) {
		this.disbDate = DateUtility.getDate(DateUtility.formatUtilDate(disbDate, PennantConstants.dateFormat));
	}
	
	public String getDisbAccountId() {
		return disbAccountId;
	}

	public void setDisbAccountId(String disbAccountId) {
		this.disbAccountId = disbAccountId;
	}
	
	public String getDisbType() {
    	return disbType;
    }
	public void setDisbType(String disbType) {
    	this.disbType = disbType;
    }

	public long getDisbExpType() {
    	return disbExpType;
    }
	public void setDisbExpType(long disbExpType) {
    	this.disbExpType = disbExpType;
    }

	public long getDisbBeneficiary() {
    	return disbBeneficiary;
    }
	public void setDisbBeneficiary(long disbBeneficiary) {
    	this.disbBeneficiary = disbBeneficiary;
    }
	
	public String getLovDescDisbExpType() {
    	return lovDescDisbExpType;
    }
	public void setLovDescDisbExpType(String lovDescDisbExpType) {
    	this.lovDescDisbExpType = lovDescDisbExpType;
    }

	public void setLovdescDisbBenificiary(String lovdescDisbBenificiary) {
	    this.lovdescDisbBenificiary = lovdescDisbBenificiary;
    }
	public String getLovdescDisbBenificiary() {
	    return lovdescDisbBenificiary;
    }

	public void setLovDescDisbBenfShrtName(String lovDescDisbBenfShrtName) {
	    this.lovDescDisbBenfShrtName = lovDescDisbBenfShrtName;
    }
	public String getLovDescDisbBenfShrtName() {
	    return lovDescDisbBenfShrtName;
    }

	public BigDecimal getDisbRetPerc() {
    	return disbRetPerc;
    }
	public void setDisbRetPerc(BigDecimal disbRetPerc) {
    	this.disbRetPerc = disbRetPerc;
    }

	public BigDecimal getDisbRetAmount() {
    	return disbRetAmount;
    }
	public void setDisbRetAmount(BigDecimal disbRetAmount) {
    	this.disbRetAmount = disbRetAmount;
    }
	
	public void setDisbRetPaid(BigDecimal disbRetPaid) {
	    this.disbRetPaid = disbRetPaid;
    }
	public BigDecimal getDisbRetPaid() {
	    return disbRetPaid;
    }

	public void setRetPaidDate(Date retPaidDate) {
	    this.retPaidDate = retPaidDate;
    }
	public Date getRetPaidDate() {
	    return retPaidDate;
    }

	public boolean isAutoDisb() {
    	return autoDisb;
    }
	public void setAutoDisb(boolean autoDisb) {
    	this.autoDisb = autoDisb;
    }
	
	public int getDisbSeq() {
		return disbSeq;
	}
	public void setDisbSeq(int disbSeq) {
		this.disbSeq = disbSeq;
	}
	
	public String getDisbDesc() {
		return disbDesc;
	}
	public void setDisbDesc(String disbDesc) {
		this.disbDesc = disbDesc;
	}
	
	public BigDecimal getDisbAmount() {
		return disbAmount;
	}
	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}
	
	public Date getDisbReqDate() {
		return disbReqDate;
	}
	public void setDisbReqDate(Date disbReqDate) {
		this.disbReqDate = disbReqDate;
	}

	public boolean isDisbDisbursed() {
		return disbDisbursed;
	}
	public void setDisbDisbursed(boolean disbDisbursed) {
		this.disbDisbursed = disbDisbursed;
	}
	
	public void setDisbClaim(BigDecimal disbClaim) {
	    this.disbClaim = disbClaim;
    }
	public BigDecimal getDisbClaim() {
	    return disbClaim;
    }
	
	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
	    this.feeChargeAmt = feeChargeAmt;
    }
	public BigDecimal getFeeChargeAmt() {
	    return feeChargeAmt;
    }

	public boolean isDisbIsActive() {
		return disbIsActive;
	}
	public void setDisbIsActive(boolean disbIsActive) {
		this.disbIsActive = disbIsActive;
	}
	
	public String getDisbRemarks() {
		return disbRemarks;
	}
	public void setDisbRemarks(String disbRemarks) {
		this.disbRemarks = disbRemarks;
	}
	
	public long getLinkedTranId() {
    	return linkedTranId;
    }
	public void setLinkedTranId(long linkedTranId) {
    	this.linkedTranId = linkedTranId;
    }
	
	public void setNetAdvDue(BigDecimal netAdvDue) {
	    this.netAdvDue = netAdvDue;
    }
	public BigDecimal getNetAdvDue() {
	    return netAdvDue;
    }

	public void setNetRetDue(BigDecimal netRetDue) {
	    this.netRetDue = netRetDue;
    }
	public BigDecimal getNetRetDue() {
	    return netRetDue;
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

	public FinanceDisbursement getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinanceDisbursement beforeImage){
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
	public boolean equals(FinanceDisbursement financeDisbursement) {
		return getId() == financeDisbursement.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinanceDisbursement) {
			FinanceDisbursement financeDisbursement = (FinanceDisbursement) obj;
			return equals(financeDisbursement);
		}
		return false;
	}

	public void setLogKey(long logKey) {
	    this.logKey = logKey;
    }

	public long getLogKey() {
	    return logKey;
    }

}
