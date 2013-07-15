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
 * FileName    		:  CustomerIncome.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CustomerIncome table</b>.<br>
 *
 */
public class CustomerIncome implements java.io.Serializable {

	private static final long serialVersionUID = -1276183069308329161L;

	private long custID =Long.MIN_VALUE;		
	private String custIncomeType;
	private String lovDescCustIncomeTypeName;
	private BigDecimal custIncome;
	private int lovDescCcyEditField;
	private String custIncomeCountry;
	private String lovDescCustIncomeCountryName;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private CustomerIncome befImage;
	private LoginUserDetails userDetails;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerIncome() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CustomerIncome");
	}

	public CustomerIncome(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getId() {
		return custID;
	}
	public void setId (long id) {
		this.custID = id;
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

	public String getCustIncomeType() {
		return custIncomeType;
	}
	public void setCustIncomeType(String custIncomeType) {
		this.custIncomeType = custIncomeType;
	}

	public void setLovDescCcyEditField(int lovDescCcyEditField) {
	    this.lovDescCcyEditField = lovDescCcyEditField;
    }
	public int getLovDescCcyEditField() {
	    return lovDescCcyEditField;
    }
	
	public String getLovDescCustIncomeTypeName() {
		return this.lovDescCustIncomeTypeName;
	}
	public void setLovDescCustIncomeTypeName(String lovDescCustIncomeTypeName) {
		this.lovDescCustIncomeTypeName = lovDescCustIncomeTypeName;
	}

	public BigDecimal getCustIncome() {
		return custIncome;
	}
	public void setCustIncome(BigDecimal custIncome) {
		this.custIncome = custIncome;
	}

	public String getCustIncomeCountry() {
		return custIncomeCountry;
	}
	public void setCustIncomeCountry(String custIncomeCountry) {
		this.custIncomeCountry = custIncomeCountry;
	}

	public String getLovDescCustIncomeCountryName() {
		return this.lovDescCustIncomeCountryName;
	}
	public void setLovDescCustIncomeCountryName(String lovDescCustIncomeCountryName) {
		this.lovDescCustIncomeCountryName = lovDescCustIncomeCountryName;
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

	public CustomerIncome getBefImage(){
		return this.befImage;
	}	
	public void setBefImage(CustomerIncome beforeImage){
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

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}
	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public void setLoginDetails(LoginUserDetails userDetails){
		this.lastMntBy=userDetails.getLoginUsrID();
		this.userDetails=userDetails;
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(CustomerIncome customerIncome) {
		if(getCustID()==customerIncome.getCustID() 
				&& getCustIncomeType()==customerIncome.getCustIncomeType() 
				&& getCustIncomeCountry()==customerIncome.getCustIncomeCountry()){
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof CustomerIncome) {
			CustomerIncome customerIncome = (CustomerIncome) obj;
			return equals(customerIncome);
		}
		return false;
	}

}
