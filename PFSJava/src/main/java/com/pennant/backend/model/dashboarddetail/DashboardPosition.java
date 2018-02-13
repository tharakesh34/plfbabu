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

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>DashboardDetails table</b>.<br>
 *
 */
public class DashboardPosition extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String dashboardRef ;
	private long UsrId;
	private int dashboardCol;
	private int dashboardRow;
	private int dashboardColIndex;
	private String DashboardDesc;
	 
	private boolean newRecord=false;
 	private DashboardPosition befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}
	
	public DashboardPosition() {
		super();
	}

	public DashboardPosition(String id) {
		super();
		this.setDashboardRef(id);
	}

	//Getter and Setter methods
	
	public void setId(String dashboardRef) {
		this.dashboardRef = dashboardRef;
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

	public int getDashboardColIndex() {
	    return dashboardColIndex;
    }

	public void setDashboardColIndex(int dashboardColIndex) {
	    this.dashboardColIndex = dashboardColIndex;
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
