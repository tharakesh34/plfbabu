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
 * FileName    		:  Country.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  08-03-2011    
 *                                                                  
 * Modified Date    :  08-03-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-03-2011       PENNANT TECHONOLOGIES	                 0.1                                         * 
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

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Country table</b>.<br>
 *
 */
public class RBFieldDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1073404971137750236L;

	private String entityCode;
	private String rbModule;
	private String rbEvent;
	private String rbFldName;
	private String rbFldDesc;
	private String rbFldType;
	private int	   rbFldLen;
	private String rbFldTableName;
	private String rbStFlds;
	private String moduleCode;

	private boolean newRecord=false;
	private String lovValue;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}


	public RBFieldDetail(String entityCode) {
		super();
		this.entityCode= entityCode;
	}

	public RBFieldDetail() {
		super();
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("countryCodeName");
		excludeFields.add("provinceCodeName");
		
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return rbFldName;
	}

	public void setId (String id) {
		this.rbFldName = id;
	}
 
	public String getRbModule() {
		return rbModule;
	}


	public void setRbModule(String rbModule) {
		this.rbModule = rbModule;
	}


	public String getRbFldName() {
		return rbFldName;
	}


	public void setRbFldName(String rbFldName) {
		this.rbFldName = rbFldName;
	}


	public String getRbFldDesc() {
		return rbFldDesc;
	}


	public void setRbFldDesc(String rbFldDesc) {
		this.rbFldDesc = rbFldDesc;
	}


	public String getRbFldType() {
		return rbFldType;
	}


	public void setRbFldType(String rbFldType) {
		this.rbFldType = rbFldType;
	}


	public int getRbFldLen() {
		return rbFldLen;
	}


	public void setRbFldLen(int rbFldLen) {
		this.rbFldLen = rbFldLen;
	}


	public String getRbFldTableName() {
		return rbFldTableName;
	}


	public void setRbFldTableName(String rbFldTableName) {
		this.rbFldTableName = rbFldTableName;
	}


	public String getRbStFlds() {
		return rbStFlds;
	}


	public void setRbStFlds(String rbStFlds) {
		this.rbStFlds = rbStFlds;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getModuleCode() {
		return moduleCode;
	}


	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}


	public String getRbEvent() {
		return rbEvent;
	}


	public void setRbEvent(String rbEvent) {
		this.rbEvent = rbEvent;
	}
}
