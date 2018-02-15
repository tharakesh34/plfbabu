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
 * FileName    		:  WeekendMaster.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.smtmasters;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>WeekendMaster table</b>.<br>
 *
 */
public class WeekendMaster  extends AbstractWorkflowEntity {
	
	private static final long serialVersionUID = -750235755863660231L;
	
	private String weekendCode;
	private String weekendDesc;
	private String weekend;
	private boolean newRecord;
	private WeekendMaster befImage;
	private LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public WeekendMaster() {
		super();
	}

	public WeekendMaster(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return weekendCode;
	}
	public void setId (String id) {
		this.weekendCode = id;
	}
	
	public String getWeekendCode() {
		return weekendCode;
	}
	public void setWeekendCode(String weekendCode) {
		this.weekendCode = weekendCode;
	}
	

	
	public String getWeekendDesc() {
		return weekendDesc;
	}
	public void setWeekendDesc(String weekendDesc) {
		this.weekendDesc = weekendDesc;
	}
	
	public String getWeekend() {
		return weekend;
	}
	public void setWeekend(String weekend) {
		this.weekend = weekend;
	}
	

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public WeekendMaster getBefImage(){
		return this.befImage;
	}
	public void setBefImage(WeekendMaster beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}


}
