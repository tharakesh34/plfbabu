package com.pennant.backend.model.rmtmasters;

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
 * FileName    		:  FinTypeAccount.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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


import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Collateral table</b>.<br>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FinTypeAccount extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String finType = null;
	private String finCcy;
	private String event;
	private boolean alwManualEntry;
	private boolean alwCustomerAccount;
	private String accountReceivable;
	private String defaultAccNum;
	private String custAccountTypes;
	private boolean newRecord=false;
	private String lovValue;
	private FinTypeAccount befImage;
	
	@XmlTransient
	private LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinTypeAccount() {
		super();
	}

	public FinTypeAccount(String id) {
		super();
		this.setId(id);
	}
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}
	//Getter and Setter methods
	
	public String getId() {
		return finType;
	}
	
	public void setId (String id) {
		this.finType = id;
	}
		
	
	public String getFinType() {
    	return finType;
    }

	public void setFinType(String finType) {
    	this.finType = finType;
    }

	public String getFinCcy() {
    	return finCcy;
    }

	public void setFinCcy(String finCcy) {
    	this.finCcy = finCcy;
    }

	public String getEvent() {
    	return event;
    }

	public void setEvent(String event) {
    	this.event = event;
    }

	public boolean isAlwManualEntry() {
    	return alwManualEntry;
    }

	public void setAlwManualEntry(boolean alwManualEntry) {
    	this.alwManualEntry = alwManualEntry;
    }

	public boolean isAlwCustomerAccount() {
    	return alwCustomerAccount;
    }

	public void setAlwCustomerAccount(boolean alwCustomerAccount) {
    	this.alwCustomerAccount = alwCustomerAccount;
    }

	public String getAccountReceivable() {
    	return accountReceivable;
    }

	public void setAccountReceivable(String accountReceivable) {
    	this.accountReceivable = accountReceivable;
    }

	public String getCustAccountTypes() {
    	return custAccountTypes;
    }

	public void setCustAccountTypes(String custAccountTypes) {
    	this.custAccountTypes = custAccountTypes;
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

	public FinTypeAccount getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(FinTypeAccount beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public String getDefaultAccNum() {
		return defaultAccNum;
	}

	public void setDefaultAccNum(String defaultAccNum) {
		this.defaultAccNum = defaultAccNum;
	}

}

