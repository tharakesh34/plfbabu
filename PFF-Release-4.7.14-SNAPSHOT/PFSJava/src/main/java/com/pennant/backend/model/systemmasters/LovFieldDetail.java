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
 * FileName    		:  LovFieldDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>LovFieldDetail table</b>.<br>
 *
 */
public class LovFieldDetail extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 8249116628404142373L;
	
	private long fieldCodeId = Long.MIN_VALUE;
	private String fieldCode;
	private String lovDescFieldCodeName;
	private String fieldCodeValue;
	private String valueDesc;
	private boolean isActive;
	private boolean newRecord;
	private String lovValue;
	private LovFieldDetail befImage;
	private LoggedInUser userDetails;
	private boolean systemDefault;

	public boolean isNew() {
		return isNewRecord();
	}

	public LovFieldDetail() {
		super();
	}

	public LovFieldDetail(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return fieldCodeId;
	}
	public void setId (long id) {
		this.fieldCodeId = id;
	}
	
	public long getFieldCodeId() {
		return fieldCodeId;
	}
	public void setFieldCodeId(long fieldCodeId) {
		this.fieldCodeId = fieldCodeId;
	}
	
	public String getFieldCode() {
		return fieldCode;
	}
	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getLovDescFieldCodeName() {
		return this.lovDescFieldCodeName;
	}
	public void setLovDescFieldCodeName (String lovDescFieldCodeName) {
		this.lovDescFieldCodeName = lovDescFieldCodeName;
	}
	
	public String getFieldCodeValue() {
		return fieldCodeValue;
	}
	public void setFieldCodeValue(String fieldCodeValue) {
		this.fieldCodeValue = fieldCodeValue;
	}
	
	public String getValueDesc() {
		return valueDesc;
	}
	public void setValueDesc(String valueDesc) {
		this.valueDesc = valueDesc;
	}

	public boolean isIsActive() {
		return isActive;
	}
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
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

	public LovFieldDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(LovFieldDetail beforeImage){
		this.befImage=beforeImage;
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
