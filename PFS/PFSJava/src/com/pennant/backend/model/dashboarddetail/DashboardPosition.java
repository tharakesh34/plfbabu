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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  DashboardDetails.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  08-06-2011    
 *                                                                  
 * Modified Date    :  08-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-06-2011       Pennant	                 0.1                                         * 
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

package com.pennant.backend.model.dashboarddetail;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>DashboardDetails table</b>.<br>
 *
 */
/**
 * @author S026
 *
 */
public class DashboardPosition implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String dashboardRef ;
	private long UsrId;
	private int dashboardCol;
	private int dashboardRow;
	private String DashboardDesc;
	 
	private int version=0;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
 	private DashboardPosition befImage;
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
	
	public DashboardPosition() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("DashboardDetails");
	}

	public DashboardPosition(String id) {
		this.setDashboardRef(id);
	}

	//Getter and Setter methods
	
	public void setId(String Id) {
		this.dashboardRef = Id;
	}
	
	public String getId() {
		return this.dashboardRef ;
	}	
	
	/**
	 * @return the dashboardId
	 */
	public String getDashboardRef() {
		return dashboardRef;
	}

	/**
	 * @param dashboardId the dashboardId to set
	 */
	public void setDashboardRef(String dashboardRef) {
		this.dashboardRef = dashboardRef;
	}

	/**
	 * @return the userId
	 */
	public long getUsrId() {
		return UsrId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUsrId(long usrId) {
		UsrId = usrId;
	}

	/**
	 * @return the dashboardCol
	 */
	public int getDashboardCol() {
		return dashboardCol;
	}

	/**
	 * @param dashboardCol the dashboardCol to set
	 */
	public void setDashboardCol(int dashboardCol) {
		this.dashboardCol = dashboardCol;
	}

	/**
	 * @return the dashboardRow
	 */
	public int getDashboardRow() {
		return dashboardRow;
	}

	/**
	 * @param dashboardRow the dashboardRow to set
	 */
	public void setDashboardRow(int dashboardRow) {
		this.dashboardRow = dashboardRow;
	}

	/**
	 * @return the dashboardDesc
	 */
	public String getDashboardDesc() {
		return DashboardDesc;
	}

	/**
	 * @param dashboardDesc the dashboardDesc to set
	 */
	public void setDashboardDesc(String dashboardDesc) {
		DashboardDesc = dashboardDesc;
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

	public DashboardPosition getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(DashboardPosition beforeImage){
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
	public boolean equals(DashboardPosition dashboardDetails) {
		return getId() == dashboardDetails.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof DashboardPosition) {
			DashboardPosition dashboardDetails = (DashboardPosition) obj;
			return equals(dashboardDetails);
		}
		return false;
	}
 

	
}
