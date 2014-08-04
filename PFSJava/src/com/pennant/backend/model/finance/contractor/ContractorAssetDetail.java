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
 * FileName    		:  ContractorAssetDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-09-2013    														*
 *                                                                  						*
 * Modified Date    :  27-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.finance.contractor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>ContractorAssetDetail table</b>.<br>
 *
 */
public class ContractorAssetDetail implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String finReference;
	private long contractorId;
	private long custID;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private String assetDesc;
	private BigDecimal assetValue= BigDecimal.ZERO;
	private BigDecimal totClaimAmt= BigDecimal.ZERO;
	private BigDecimal totAdvanceAmt= BigDecimal.ZERO;
 	private BigDecimal lovDescClaimPercent;
 	
 	
	private int version;
	@XmlTransient
	private long lastMntBy;
	private String lastMaintainedUser;
	@XmlTransient
	private Timestamp lastMntOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar lastMaintainedOn;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private ContractorAssetDetail befImage;
	@XmlTransient
	private LoginUserDetails userDetails;
	@XmlTransient
	private String recordStatus;
	@XmlTransient
	private String roleCode="";
	@XmlTransient
	private String nextRoleCode= "";
	@XmlTransient
	private String taskId="";
	@XmlTransient
	private String nextTaskId= "";
	@XmlTransient
	private String recordType;
	@XmlTransient
	private String userAction = "Save";
	@XmlTransient
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public ContractorAssetDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("ContractorAssetDetail");
	}

	public ContractorAssetDetail(String id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
	return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		// ++++++++++++++++++ getter / setter +++++++++++++++++++//
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return finReference;
	}
	
	public void setId (String id) {
		this.finReference = id;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public long getContractorId() {
		return contractorId;
	}
	public void setContractorId(long contractorId) {
		this.contractorId = contractorId;
	}
	
	public long getCustID() {
    	return custID;
    }

	public void setCustID(long custID) {
    	this.custID = custID;
    }

	public String getLovDescCustCIF() {
    	return lovDescCustCIF;
    }

	public void setLovDescCustCIF(String lovDescCustCIF) {
    	this.lovDescCustCIF = lovDescCustCIF;
    }

	public String getLovDescCustShrtName() {
    	return lovDescCustShrtName;
    }

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
    	this.lovDescCustShrtName = lovDescCustShrtName;
    }

	public String getAssetDesc() {
		return assetDesc;
	}
	public void setAssetDesc(String assetDesc) {
		this.assetDesc = assetDesc;
	}
	
	
		
	
	public BigDecimal getAssetValue() {
		return assetValue;
	}
	public void setAssetValue(BigDecimal assetValue) {
		this.assetValue = assetValue;
	}
	
	
		
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	@XmlTransient
	public long getLastMntBy() {
		return lastMntBy;
	}
	
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getLastMaintainedUser() {
		return lastMaintainedUser;
	}

	public void setLastMaintainedUser(String lastMaintainedUser) {
		this.lastMaintainedUser = lastMaintainedUser;
	}
	
	@XmlTransient
	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMaintainedOn(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar != null) {
			lastMntOn = DateUtility.ConvertFromXMLTime(xmlCalendar);
			lastMaintainedOn = xmlCalendar;
		}
	}

	public XMLGregorianCalendar getLastMaintainedOn()
			throws DatatypeConfigurationException {

		if (lastMntOn == null) {
			return null;
		}
		return DateUtility.getXMLDate(lastMntOn);
	}

	
	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
	}

	@XmlTransient
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public ContractorAssetDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(ContractorAssetDetail beforeImage){
		this.befImage=beforeImage;
	}

	@XmlTransient
	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	@XmlTransient
	public String getRecordStatus() {
		return recordStatus;
	}
	
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	@XmlTransient
	public String getRoleCode() {
		return roleCode;
	}
	
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	@XmlTransient
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	
	@XmlTransient
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@XmlTransient
	public String getNextTaskId() {
		return nextTaskId;
	}
	
	
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	@XmlTransient
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	@XmlTransient
	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(ContractorAssetDetail contractorAssetDetail) {
		return getId() == contractorAssetDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof ContractorAssetDetail) {
			ContractorAssetDetail contractorAssetDetail = (ContractorAssetDetail) obj;
			return equals(contractorAssetDetail);
		}
		return false;
	}

	public BigDecimal getLovDescClaimPercent() {
    	return lovDescClaimPercent;
    }

	public void setLovDescClaimPercent(BigDecimal lovDescClaimPercent) {
    	this.lovDescClaimPercent = lovDescClaimPercent;
    }

	public BigDecimal getTotClaimAmt() {
    	return totClaimAmt;
    }

	public void setTotClaimAmt(BigDecimal totClaimAmt) {
    	this.totClaimAmt = totClaimAmt;
    }

	public BigDecimal getTotAdvanceAmt() {
    	return totAdvanceAmt;
    }

	public void setTotAdvanceAmt(BigDecimal totAdvanceAmt) {
    	this.totAdvanceAmt = totAdvanceAmt;
    }
}
