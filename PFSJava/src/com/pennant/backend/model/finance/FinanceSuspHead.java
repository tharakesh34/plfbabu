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
 * FileName    		:  FinanceSuspHead.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-02-2012    														*
 *                                                                  						*
 * Modified Date    :  04-02-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-02-2012       Pennant	                 0.1                                            * 
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.WorkFlowUtil;


/**
 * Model class for the <b>FinSuspHead table</b>.<br>
 *
 */
public class FinanceSuspHead implements Serializable {

    private static final long serialVersionUID = -7731584953589841445L;
    
	private String finReference;
	private String finBranch;
	private String finType;
	private long custId = Long.MIN_VALUE;
	private int finSuspSeq;
	private boolean finIsInSusp=false;
	private boolean manualSusp=false;
	private Date finSuspDate;
	private Date finSuspTrfDate;
	private BigDecimal finSuspAmt = BigDecimal.ZERO;
	private BigDecimal finCurSuspAmt = BigDecimal.ZERO;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinanceSuspHead befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;	
	
	private String lovDescCustCIFName;
	private int lovDescFinFormatter;
	private String lovDescCustShrtName;
	
	private List<FinanceSuspDetails> suspDetailsList = new ArrayList<FinanceSuspDetails>();
	private List<ReturnDataSet> suspPostingsList = new ArrayList<ReturnDataSet>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceSuspHead() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("FinanceSuspHead");
	}

	public FinanceSuspHead(String id) {
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
	
	public String getFinBranch() {
		return finBranch;
	}
	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	
	public int getFinSuspSeq() {
		return finSuspSeq;
	}
	public void setFinSuspSeq(int finSuspSeq) {
		this.finSuspSeq = finSuspSeq;
	}
	
	public boolean isFinIsInSusp() {
		return finIsInSusp;
	}
	public void setFinIsInSusp(boolean finIsInSusp) {
		this.finIsInSusp = finIsInSusp;
	}
	
	public boolean isManualSusp() {
    	return manualSusp;
    }
	public void setManualSusp(boolean manualSusp) {
    	this.manualSusp = manualSusp;
    }
	
	public Date getFinSuspDate() {
		return finSuspDate;
	}
	public void setFinSuspDate(Date finSuspDate) {
		this.finSuspDate = finSuspDate;
	}
	
	public Date getFinSuspTrfDate() {
	    return finSuspTrfDate;
    }
	public void setFinSuspTrfDate(Date finSuspTrfDate) {
	    this.finSuspTrfDate = finSuspTrfDate;
    }
	
	public BigDecimal getFinSuspAmt() {
		return finSuspAmt;
	}
	public void setFinSuspAmt(BigDecimal finSuspAmt) {
		this.finSuspAmt = finSuspAmt;
	}
	
	public BigDecimal getFinCurSuspAmt() {
		return finCurSuspAmt;
	}
	public void setFinCurSuspAmt(BigDecimal finCurSuspAmt) {
		this.finCurSuspAmt = finCurSuspAmt;
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

	public FinanceSuspHead getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinanceSuspHead beforeImage){
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
	
	public void setLovDescCustCIFName(String lovDescCustCIFName) {
	    this.lovDescCustCIFName = lovDescCustCIFName;
    }
	public String getLovDescCustCIFName() {
	    return lovDescCustCIFName;
    }

	public void setLovDescFinFormatter(int lovDescFinFormatter) {
	    this.lovDescFinFormatter = lovDescFinFormatter;
    }
	public int getLovDescFinFormatter() {
	    return lovDescFinFormatter;
    }
	
	public void setSuspDetailsList(List<FinanceSuspDetails> suspDetailsList) {
	    this.suspDetailsList = suspDetailsList;
    }
	public List<FinanceSuspDetails> getSuspDetailsList() {
	    return suspDetailsList;
    }
	
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
	    this.lovDescCustShrtName = lovDescCustShrtName;
    }
	public String getLovDescCustShrtName() {
	    return lovDescCustShrtName;
    }
	
	public void setSuspPostingsList(List<ReturnDataSet> suspPostingsList) {
	    this.suspPostingsList = suspPostingsList;
    }
	public List<ReturnDataSet> getSuspPostingsList() {
	    return suspPostingsList;
    }
	
	// Overridden Equals method to handle the comparison
	public boolean equals(FinanceSuspHead financeSuspHead) {
		return getFinReference() == financeSuspHead.getFinReference();
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

		if (obj instanceof FinanceSuspHead) {
			FinanceSuspHead financeSuspHead = (FinanceSuspHead) obj;
			return equals(financeSuspHead);
		}
		return false;
	}

}
