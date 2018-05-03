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
 * FileName    		:  PhoneType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
 * Model class for the <b>PhoneType table</b>.<br>
 * 
 */
public class PhoneType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -1255476184146614098L;

	private String phoneTypeCode;
	private String phoneTypeDesc;
	private String phoneTypeRegex;
	private int phoneTypePriority;
	private boolean phoneTypeIsActive;
	private boolean newRecord;
	private String lovValue;
	private PhoneType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public PhoneType() {
		super();
	}

	public PhoneType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return phoneTypeCode;
	}
	public void setId(String id) {
		this.phoneTypeCode = id;
	}
	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}

	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}
	public String getPhoneTypeDesc() {
		return phoneTypeDesc;
	}

	public void setPhoneTypeDesc(String phoneTypeDesc) {
		this.phoneTypeDesc = phoneTypeDesc;
	}

	public int getPhoneTypePriority() {
		return phoneTypePriority;
	}
	public void setPhoneTypePriority(int phoneTypePriority) {
		this.phoneTypePriority = phoneTypePriority;
	}

	public boolean isPhoneTypeIsActive() {
		return phoneTypeIsActive;
	}
	public void setPhoneTypeIsActive(boolean phoneTypeIsActive) {
		this.phoneTypeIsActive = phoneTypeIsActive;
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

	public PhoneType getBefImage() {
		return this.befImage;
	}
	public void setBefImage(PhoneType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getPhoneTypeRegex() {
		return phoneTypeRegex;
	}

	public void setPhoneTypeRegex(String phoneTypeRegex) {
		this.phoneTypeRegex = phoneTypeRegex;
	}
}
