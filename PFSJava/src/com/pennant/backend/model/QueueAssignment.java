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
 *
 * FileName    		:  QueueAssignment.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model;

import java.sql.Timestamp;

public class QueueAssignment {
	
	private String module;
	private long userId;
	private long lovDescQAUserId;
	private String roleCode;
	private int assignedRcdCount=0;
	private Timestamp lastAssignedOn = new Timestamp(System.currentTimeMillis());
	private int processedRcdCount = 0;
	private Timestamp lastProcessedOn = null;
	private boolean userActive = true;
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getAssignedRcdCount() {
		return assignedRcdCount;
	}
	public void setAssignedRcdCount(int assignedRcdCount) {
		this.assignedRcdCount = assignedRcdCount;
	}
	public Timestamp getLastAssignedOn() {
		return lastAssignedOn;
	}
	public void setLastAssignedOn(Timestamp lastAssignedOn) {
		this.lastAssignedOn = lastAssignedOn;
	}
	public int getProcessedRcdCount() {
		return processedRcdCount;
	}
	public void setProcessedRcdCount(int processedRcdCount) {
		this.processedRcdCount = processedRcdCount;
	}
	public Timestamp getLastProcessedOn() {
		return lastProcessedOn;
	}
	public void setLastProcessedOn(Timestamp lastProcessedOn) {
		this.lastProcessedOn = lastProcessedOn;
	}
	public boolean isUserActive() {
		return userActive;
	}
	public void setUserActive(boolean userActive) {
		this.userActive = userActive;
	}
	public long getLovDescQAUserId() {
	    return lovDescQAUserId;
    }
	public void setLovDescQAUserId(long lovDescQAUserId) {
	    this.lovDescQAUserId = lovDescQAUserId;
    }
	public String getRoleCode() {
	    return roleCode;
    }
	public void setRoleCode(String roleCode) {
	    this.roleCode = roleCode;
    }
}
