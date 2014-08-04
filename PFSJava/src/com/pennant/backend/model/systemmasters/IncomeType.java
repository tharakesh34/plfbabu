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
 * FileName    		:  IncomeType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>IncomeType table</b>.<br>
 * 
 */
public class IncomeType implements java.io.Serializable {

	private static final long serialVersionUID = -260562868574383176L;
	
	private String incomeExpense;
	private String category;
	private String lovDescCategoryName;
	private String incomeTypeCode;
	private String incomeTypeDesc;
	private BigDecimal margin;
	private boolean incomeTypeIsActive;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private IncomeType befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public IncomeType() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("IncomeType");
	}

	public IncomeType(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getId() {
		return incomeTypeCode;
	}
	public void setId(String id) {
		this.incomeTypeCode = id;
	}

	public String getIncomeTypeCode() {
		return incomeTypeCode;
	}
	public void setIncomeTypeCode(String incomeTypeCode) {
		this.incomeTypeCode = incomeTypeCode;
	}

	public String getIncomeTypeDesc() {
		return incomeTypeDesc;
	}
	public void setIncomeTypeDesc(String incomeTypeDesc) {
		this.incomeTypeDesc = incomeTypeDesc;
	}

	public boolean isIncomeTypeIsActive() {
		return incomeTypeIsActive;
	}
	public void setIncomeTypeIsActive(boolean incomeTypeIsActive) {
		this.incomeTypeIsActive = incomeTypeIsActive;
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

	public IncomeType getBefImage() {
		return this.befImage;
	}
	public void setBefImage(IncomeType beforeImage) {
		this.befImage = beforeImage;
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
		if (this.workflowId == 0) {
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
	public boolean equals(IncomeType incomeType) {
		return getId() == incomeType.getId();
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

		if (obj instanceof IncomeType) {
			IncomeType incomeType = (IncomeType) obj;
			return equals(incomeType);
		}
		return false;
	}

	public void setIncomeExpense(String incomeExpense) {
	    this.incomeExpense = incomeExpense;
    }

	public String getIncomeExpense() {
	    return incomeExpense;
    }

	public void setCategory(String category) {
	    this.category = category;
    }

	public String getCategory() {
	    return category;
    }

	public void setMargin(BigDecimal margin) {
	    this.margin = margin;
    }

	public BigDecimal getMargin() {
	    return margin;
    }

	public void setLovDescCategoryName(String lovDescCategoryName) {
	    this.lovDescCategoryName = lovDescCategoryName;
    }

	public String getLovDescCategoryName() {
	    return lovDescCategoryName;
    }
}
