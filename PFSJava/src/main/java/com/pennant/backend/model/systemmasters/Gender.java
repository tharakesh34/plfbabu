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
 * FileName    		:  Gender.java                                                   * 	  
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

package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Gender table</b>.<br>
 * 
 */
public class Gender extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 71023783624451043L;

	private String genderCode;
	private String genderDesc;
	private boolean genderIsActive;
	private boolean newRecord;
	private String lovValue;
	private Gender befImage;
	private LoggedInUser userDetails;
	private boolean systemDefault;

	public boolean isNew() {
		return isNewRecord();
	}

	public Gender() {
		super();
	}

	public Gender(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return genderCode;
	}
	public void setId(String id) {
		this.genderCode = id;
	}

	public String getGenderCode() {
		return genderCode;
	}
	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public String getGenderDesc() {
		return genderDesc;
	}
	public void setGenderDesc(String genderDesc) {
		this.genderDesc = genderDesc;
	}

	public boolean isGenderIsActive() {
		return genderIsActive;
	}
	public void setGenderIsActive(boolean genderIsActive) {
		this.genderIsActive = genderIsActive;
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

	public Gender getBefImage() {
		return this.befImage;
	}
	public void setBefImage(Gender beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isSystemDefault() {
	    return systemDefault;
    }

	public void setSystemDefault(boolean systemDefault) {
	    this.systemDefault = systemDefault;
    }

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
