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
 * FileName    		:  Provision.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Provision table</b>.<br>
 *
 */
public class Provision implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private String finBranch;
	private String finType;
	private long custID;
	private Date provisionCalDate;
	private BigDecimal provisionedAmt = new BigDecimal(0);
	private BigDecimal provisionAmtCal = new BigDecimal(0);
	private BigDecimal provisionDue = new BigDecimal(0);
	private BigDecimal nonFormulaProv = new BigDecimal(0);
	private boolean useNFProv;
	private boolean autoReleaseNFP;
	private BigDecimal principalDue = new BigDecimal(0);
	private BigDecimal profitDue  = new BigDecimal(0);
	private Date dueFromDate;
	private Date lastFullyPaidDate;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private Provision befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	private int lovDescFinFormatter;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	
	private List<ProvisionMovement> provisionMovementList = new ArrayList<ProvisionMovement>();

	public boolean isNew() {
		return isNewRecord();
	}

	public Provision() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Provision");
	}

	public Provision(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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
	
	public String getFinBranch() {
		return finBranch;
	}
	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}
	
	public Date getProvisionCalDate() {
		return provisionCalDate;
	}
	public void setProvisionCalDate(Date provisionCalDate) {
		this.provisionCalDate = provisionCalDate;
	}

	public BigDecimal getProvisionedAmt() {
		return provisionedAmt;
	}
	public void setProvisionedAmt(BigDecimal provisionedAmt) {
		this.provisionedAmt = provisionedAmt;
	}
	
	public BigDecimal getProvisionAmtCal() {
		return provisionAmtCal;
	}
	public void setProvisionAmtCal(BigDecimal provisionAmtCal) {
		this.provisionAmtCal = provisionAmtCal;
	}
	
	public BigDecimal getProvisionDue() {
		return provisionDue;
	}
	public void setProvisionDue(BigDecimal provisionDue) {
		this.provisionDue = provisionDue;
	}
	
	public BigDecimal getNonFormulaProv() {
		return nonFormulaProv;
	}
	public void setNonFormulaProv(BigDecimal nonFormulaProv) {
		this.nonFormulaProv = nonFormulaProv;
	}
	
	
		
	
	public boolean isUseNFProv() {
		return useNFProv;
	}
	public void setUseNFProv(boolean useNFProv) {
		this.useNFProv = useNFProv;
	}
	
	
		
	
	public boolean isAutoReleaseNFP() {
		return autoReleaseNFP;
	}
	public void setAutoReleaseNFP(boolean autoReleaseNFP) {
		this.autoReleaseNFP = autoReleaseNFP;
	}
	
	
		
	
	public BigDecimal getPrincipalDue() {
		return principalDue;
	}
	public void setPrincipalDue(BigDecimal principalDue) {
		this.principalDue = principalDue;
	}
	
	
		
	
	public BigDecimal getProfitDue() {
		return profitDue;
	}
	public void setProfitDue(BigDecimal profitDue) {
		this.profitDue = profitDue;
	}
	
	
		
	
	public Date getDueFromDate() {
		return dueFromDate;
	}
	public void setDueFromDate(Date dueFromDate) {
		this.dueFromDate = dueFromDate;
	}
	
	
		
	
	public Date getLastFullyPaidDate() {
		return lastFullyPaidDate;
	}
	public void setLastFullyPaidDate(Date lastFullyPaidDate) {
		this.lastFullyPaidDate = lastFullyPaidDate;
	}
	
	
		
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public long getLastMntBy() {
		return lastMntBy;
	}
	
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public Provision getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(Provision beforeImage){
		this.befImage=beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}
	
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	public String getRoleCode() {
		return roleCode;
	}
	
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}
	
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

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
	
	public int getLovDescFinFormatter() {
		return lovDescFinFormatter;
	}
	public void setLovDescFinFormatter(int lovDescFinFormatter) {
		this.lovDescFinFormatter = lovDescFinFormatter;
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

	// Overidden Equals method to handle the comparision
	public boolean equals(Provision provision) {
		return getId() == provision.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Provision) {
			Provision provision = (Provision) obj;
			return equals(provision);
		}
		return false;
	}

	public void setProvisionMovementList(List<ProvisionMovement> provisionMovementList) {
		this.provisionMovementList = provisionMovementList;
	}

	public List<ProvisionMovement> getProvisionMovementList() {
		return provisionMovementList;
	}
}
