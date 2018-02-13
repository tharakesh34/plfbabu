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
 * FileName    		:  OverdueCharge.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-05-2012    														*
 *                                                                  						*
 * Modified Date    :  10-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>OverdueCharge table</b>.<br>
 *
 */
public class OverdueCharge extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 7210084222491555044L;

	private String oDCRuleCode = null;
	private String oDCPLAccount;
	private String lovDescODCPLAccountName;
	private String oDCCharityAccount;
	private String lovDescODCCharityAccountName;
	private String oDCPLSubHead;
	private String lovDescODCPLSubHeadName;
	private String oDCCharitySubHead;
	private String lovDescODCCharitySubHeadName;
	
	private BigDecimal oDCPLShare;
	private boolean oDCSweepCharges;
	private String oDCRuleDescription;
	private boolean newRecord=false;
	private String lovValue;
	private OverdueCharge befImage;
	private LoggedInUser userDetails;

	private List<OverdueChargeDetail> lovDescOverdueChargeDetail=new ArrayList<OverdueChargeDetail>();
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public void setChargeDetailEntries(List<OverdueChargeDetail> chargeDetailEntries) {
		this.lovDescOverdueChargeDetail = chargeDetailEntries;
	}
	public List<OverdueChargeDetail> getChargeDetailEntries() {
		return lovDescOverdueChargeDetail;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public boolean isNew() {
		return isNewRecord();
	}

	public OverdueCharge() {
		super();
	}
	public OverdueCharge(String id) {
		super();
		this.setId(id);
	}


	public String getId() {
		return oDCRuleCode;
	}
	public void setId (String id) {
		this.oDCRuleCode = id;
	}

	public String getODCRuleCode() {
		return oDCRuleCode;
	}
	public void setODCRuleCode(String oDCRuleCode) {
		this.oDCRuleCode = oDCRuleCode;
	}

	public String getODCPLAccount() {
		return oDCPLAccount;
	}
	public void setODCPLAccount(String oDCPLAccount) {
		this.oDCPLAccount = oDCPLAccount;
	}

	public String getLovDescODCPLAccountName() {
		return this.lovDescODCPLAccountName;
	}
	public void setLovDescODCPLAccountName (String lovDescODCPLAccountName) {
		this.lovDescODCPLAccountName = lovDescODCPLAccountName;
	}

	public String getODCCharityAccount() {
		return oDCCharityAccount;
	}
	public void setODCCharityAccount(String oDCCharityAccount) {
		this.oDCCharityAccount = oDCCharityAccount;
	}

	public String getLovDescODCCharityAccountName() {
		return this.lovDescODCCharityAccountName;
	}
	public void setLovDescODCCharityAccountName (String lovDescODCCharityAccountName) {
		this.lovDescODCCharityAccountName = lovDescODCCharityAccountName;
	}

	public BigDecimal getODCPLShare() {
		return oDCPLShare;
	}
	public void setODCPLShare(BigDecimal oDCPLShare) {
		this.oDCPLShare = oDCPLShare;
	}

	public boolean isODCSweepCharges() {
		return oDCSweepCharges;
	}
	public void setODCSweepCharges(boolean oDCSweepCharges) {
		this.oDCSweepCharges = oDCSweepCharges;
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

	public OverdueCharge getBefImage(){
		return this.befImage;
	}
	public void setBefImage(OverdueCharge beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public String getoDCPLSubHead() {
    	return oDCPLSubHead;
    }
	public void setoDCPLSubHead(String oDCPLSubHead) {
    	this.oDCPLSubHead = oDCPLSubHead;
    }
	
	public String getLovDescODCPLSubHeadName() {
    	return lovDescODCPLSubHeadName;
    }
	public void setLovDescODCPLSubHeadName(String lovDescODCPLSubHeadName) {
    	this.lovDescODCPLSubHeadName = lovDescODCPLSubHeadName;
    }
	
	public String getoDCCharitySubHead() {
    	return oDCCharitySubHead;
    }
	public void setoDCCharitySubHead(String oDCCharitySubHead) {
    	this.oDCCharitySubHead = oDCCharitySubHead;
    }
	
	public String getLovDescODCCharitySubHeadName() {
    	return lovDescODCCharitySubHeadName;
    }
	public void setLovDescODCCharitySubHeadName(String lovDescODCCharitySubHeadName) {
    	this.lovDescODCCharitySubHeadName = lovDescODCCharitySubHeadName;
    }

	public String getoDCRuleDescription() {
		return oDCRuleDescription;
	}
	public void setoDCRuleDescription(String oDCRuleDescription) {
		this.oDCRuleDescription = oDCRuleDescription;
	}
}
