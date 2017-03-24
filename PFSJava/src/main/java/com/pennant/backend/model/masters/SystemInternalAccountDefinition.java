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
 * FileName    		:  SystemInternalAccountDefinition.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.masters;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>SystemInternalAccountDefinition table</b>.<br>
 *
 */
public class SystemInternalAccountDefinition extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String sIACode;
	private String sIAName;
	private String sIAShortName;
	private String sIAAcType;
	private String lovDescSIAAcTypeName;
	private String sIANumber;
	private boolean newRecord;
	private String lovValue;
	private SystemInternalAccountDefinition befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public SystemInternalAccountDefinition() {
		super();
	}

	public SystemInternalAccountDefinition(String id) {
		super();
		this.setId(id);
	}

	//Getter and Setter methods
	
	public String getId() {
		return sIACode;
	}
	
	public void setId (String id) {
		this.sIACode = id;
	}
	
	public String getSIACode() {
		return sIACode;
	}
	public void setSIACode(String sIACode) {
		this.sIACode = sIACode;
	}
	
	
		
	
	public String getSIAName() {
		return sIAName;
	}
	public void setSIAName(String sIAName) {
		this.sIAName = sIAName;
	}
	
	
		
	
	public String getSIAShortName() {
		return sIAShortName;
	}
	public void setSIAShortName(String sIAShortName) {
		this.sIAShortName = sIAShortName;
	}
	
	
		
	
	public String getSIAAcType() {
		return sIAAcType;
	}
	public void setSIAAcType(String sIAAcType) {
		this.sIAAcType = sIAAcType;
	}
	

	public String getLovDescSIAAcTypeName() {
		return this.lovDescSIAAcTypeName;
	}

	public void setLovDescSIAAcTypeName (String lovDescSIAAcTypeName) {
		this.lovDescSIAAcTypeName = lovDescSIAAcTypeName;
	}
	
		
	
	public String getSIANumber() {
		return sIANumber;
	}
	public void setSIANumber(String sIANumber) {
		this.sIANumber = sIANumber;
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

	public SystemInternalAccountDefinition getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(SystemInternalAccountDefinition beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
