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
 * FileName    		:  DeferementHeader.java                                                   * 	  
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
import java.util.Date;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>DeferementHeader table</b>.<br>
 *
 */
public class DefermentHeader implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private Date deferedSchdDate;
	private long logKey;
	private BigDecimal defSchdProfit = BigDecimal.ZERO;
	private BigDecimal defSchdPrincipal = BigDecimal.ZERO;
	private String defRecalType = "";
	private Date defTillDate = null;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private DefermentHeader befImage;
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

	public DefermentHeader() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("DeferementHeader");
	}

	public DefermentHeader(String id) {
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
	
	public Date getDeferedSchdDate() {
		return deferedSchdDate;
	}
	public void setDeferedSchdDate(Date deferedSchdDate) {
		this.deferedSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(deferedSchdDate,
				PennantConstants.dateFormat));
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
	
	
		
	
	public String getDefRecalType() {
		return defRecalType;
	}
	public void setDefRecalType(String defRecalType) {
		this.defRecalType = defRecalType;
	}
	
	
		
	
	public Date getDefTillDate() {
		return defTillDate;
	}
	public void setDefTillDate(Date defTillDate) {
		this.defTillDate = defTillDate;
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

	public DefermentHeader getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(DefermentHeader beforeImage){
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
	public boolean equals(DefermentHeader deferementHeader) {
		return getId() == deferementHeader.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof DefermentHeader) {
			DefermentHeader deferementHeader = (DefermentHeader) obj;
			return equals(deferementHeader);
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
