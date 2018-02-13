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
 * FileName    		:  LovFieldCode.java                                                   * 	  
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

package com.pennant.backend.model.staticparms;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>LovFieldCode table</b>.<br>
 *
 */
public class LovFieldCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String fieldCode;
	private String fieldCodeDesc;
	private String fieldCodeType;
	private boolean  fieldEdit;
	private boolean isActive;
	private boolean newRecord;
	private String lovValue;
	private LovFieldCode befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public LovFieldCode() {
		super();
	}

	public LovFieldCode(String id) {
		super();
		this.setId(id);
	}

	//Getter and Setter methods
	
	public String getId() {
		return fieldCode;
	}
	
	public void setId (String id) {
		this.fieldCode = id;
	}
	
	public String getFieldCode() {
		return fieldCode;
	}
	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}
	
	
		
	
	public String getFieldCodeDesc() {
		return fieldCodeDesc;
	}
	public void setFieldCodeDesc(String fieldCodeDesc) {
		this.fieldCodeDesc = fieldCodeDesc;
	}
	
	
		
	
	public String getFieldCodeType() {
		return fieldCodeType;
	}
	public void setFieldCodeType(String fieldCodeType) {
		this.fieldCodeType = fieldCodeType;
	}
	
	
	public boolean isFieldEdit() {
		return fieldEdit;
	}

	public void setFieldEdit(boolean fieldEdit) {
		this.fieldEdit = fieldEdit;
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean isIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public void setActive(boolean isActive) {
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

	public LovFieldCode getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(LovFieldCode beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
