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
 * FileName    		:  BaseRateCode.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>BaseRateCode table</b>.<br>
 *
 */
public class BaseRateCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1288421928415683359L;
	
	private String bRType;
	private String bRTypeDesc;
	private boolean newRecord;
	private String lovValue;
	private BaseRateCode befImage;
	private LoggedInUser userDetails;
	private boolean bRTypeIsActive;

	public boolean isNew() {
		return isNewRecord();
	}

	public BaseRateCode() {
		super();
	}

	public BaseRateCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return bRType;
	}
	public void setId (String id) {
		this.bRType = id;
	}
	
	public String getBRType() {
		return bRType;
	}
	public void setBRType(String bRType) {
		this.bRType = bRType;
	}
	
	public String getBRTypeDesc() {
		return bRTypeDesc;
	}
	public void setBRTypeDesc(String bRTypeDesc) {
		this.bRTypeDesc = bRTypeDesc;
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

	public BaseRateCode getBefImage(){
		return this.befImage;
	}
	public void setBefImage(BaseRateCode beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isbRTypeIsActive() {
		return bRTypeIsActive;
	}
	public void setbRTypeIsActive(boolean bRTypeIsActive) {
		this.bRTypeIsActive = bRTypeIsActive;
	}
	
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
	
}
