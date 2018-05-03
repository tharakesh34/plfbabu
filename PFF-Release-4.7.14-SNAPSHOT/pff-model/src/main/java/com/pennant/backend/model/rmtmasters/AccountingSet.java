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
 * FileName    		:  AccountingSet.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.rmtmasters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>AccountingSet table</b>.<br>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountingSet extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 3832850641524383002L;
	
	private long accountSetid = Long.MIN_VALUE;
	private String eventCode;
	private String lovDescEventCodeName;
	private String accountSetCode;
	private String accountSetCodeName;
	private boolean entryByInvestment;
	
	private boolean newRecord;
	private String lovValue;
	private AccountingSet befImage;
	
	@XmlTransient
	private LoggedInUser userDetails;
	private boolean SystemDefault;

	private List<TransactionEntry> lovDescTransactionEntries=new ArrayList<TransactionEntry>();
	
	@XmlTransient
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

	public AccountingSet() {
		super();
	}

	public AccountingSet(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return accountSetid;
	}
	public void setId (long id) {
		this.accountSetid = id;
	}
	
	public long getAccountSetid() {
		return accountSetid;
	}
	public void setAccountSetid(long accountSetid) {
		this.accountSetid = accountSetid;
	}
	
	public String getEventCode() {
		return eventCode;
	}
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getLovDescEventCodeName() {
		return this.lovDescEventCodeName;
	}
	public void setLovDescEventCodeName (String lovDescEventCodeName) {
		this.lovDescEventCodeName = lovDescEventCodeName;
	}
	
	public String getAccountSetCode() {
		return accountSetCode;
	}
	public void setAccountSetCode(String accountSetCode) {
		this.accountSetCode = accountSetCode;
	}
	
	public String getAccountSetCodeName() {
		return accountSetCodeName;
	}
	public void setAccountSetCodeName(String accountSetCodeName) {
		this.accountSetCodeName = accountSetCodeName;
	}

	public void setEntryByInvestment(boolean entryByInvestment) {
	    this.entryByInvestment = entryByInvestment;
    }

	public boolean isEntryByInvestment() {
	    return entryByInvestment;
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

	public AccountingSet getBefImage(){
		return this.befImage;
	}
	public void setBefImage(AccountingSet beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setTransactionEntries(List<TransactionEntry> transactionEntries) {
		//this.lovDescTransactionEntries.clear();
		this.lovDescTransactionEntries = transactionEntries;
	}
	public List<TransactionEntry> getTransactionEntries() {
		return lovDescTransactionEntries;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public boolean isSystemDefault() {
		return SystemDefault;
	}
	public void setSystemDefault(boolean systemDefault) {
		SystemDefault = systemDefault;
	}
	
	public String getStringaERuleId(){
		return String.valueOf(this.accountSetid);
	}
	
}
