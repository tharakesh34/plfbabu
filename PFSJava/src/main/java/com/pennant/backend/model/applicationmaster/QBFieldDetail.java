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

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Country table</b>.<br>
 *
 */
public class QBFieldDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1073404971137750236L;

	private String entityCode;
	private String qbModule;
	private String qbFldName;
	private String qbFldDesc;
	private String qbFldType;
	private int	   qbFldLen;
	private String qbFldTableName;
	private String qbStFlds;
	private String moduleCode;

	private boolean newRecord=false;
	private String lovValue;
	@SuppressWarnings("unused")
	private QBFieldDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public QBFieldDetail(String entityCode) {
		super();
		this.entityCode= entityCode;
	}

	public QBFieldDetail() {
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
		return qbFldName;
	}

	public void setId (String id) {
		this.qbFldName = id;
	}
 
	public String getQbModule() {
		return qbModule;
	}


	public void setQbModule(String qbModule) {
		this.qbModule = qbModule;
	}


	public String getQbFldName() {
		return qbFldName;
	}


	public void setQbFldName(String qbFldName) {
		this.qbFldName = qbFldName;
	}


	public String getQbFldDesc() {
		return qbFldDesc;
	}


	public void setQbFldDesc(String qbFldDesc) {
		this.qbFldDesc = qbFldDesc;
	}


	public String getQbFldType() {
		return qbFldType;
	}


	public void setQbFldType(String qbFldType) {
		this.qbFldType = qbFldType;
	}


	public int getQbFldLen() {
		return qbFldLen;
	}


	public void setQbFldLen(int qbFldLen) {
		this.qbFldLen = qbFldLen;
	}


	public String getQbFldTableName() {
		return qbFldTableName;
	}


	public void setQbFldTableName(String qbFldTableName) {
		this.qbFldTableName = qbFldTableName;
	}


	public String getQbStFlds() {
		return qbStFlds;
	}


	public void setQbStFlds(String qbStFlds) {
		this.qbStFlds = qbStFlds;
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
}
