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
 * FileName    		:  Branch.java                                                   * 	  
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

package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Branch table</b>.<br>
 *
 */
public class Branch implements java.io.Serializable {

	private static final long serialVersionUID = 5702329578156631687L;

	private String branchCode = null;
	private String branchDesc;
	private String branchAddrLine1;
	private String branchAddrLine2;
	private String branchPOBox;
	private String branchCity;
	private String lovDescBranchCityName;
	private String branchProvince;
	private String lovDescBranchProvinceName;
	private String branchCountry;
	private String lovDescBranchCountryName;
	private String branchFax;
	private String branchTel;
	private String branchSwiftBankCde;
	private String branchSwiftCountry;
	private String lovDescBranchSwiftCountryName;
	private String branchSwiftLocCode;
	private String branchSwiftBrnCde;
	private String branchSortCode;
	private boolean branchIsActive;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private Branch befImage;
	private LoginUserDetails userDetails;

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

	public Branch() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Branch");
	}

	public Branch(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getId() {
		return branchCode;
	}
	public void setId (String id) {
		this.branchCode = id;
	}

	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchDesc() {
		return branchDesc;
	}
	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getBranchAddrLine1() {
		return branchAddrLine1;
	}
	public void setBranchAddrLine1(String branchAddrLine1) {
		this.branchAddrLine1 = branchAddrLine1;
	}

	public String getBranchAddrLine2() {
		return branchAddrLine2;
	}
	public void setBranchAddrLine2(String branchAddrLine2) {
		this.branchAddrLine2 = branchAddrLine2;
	}

	public String getBranchPOBox() {
		return branchPOBox;
	}
	public void setBranchPOBox(String branchPOBox) {
		this.branchPOBox = branchPOBox;
	}

	public String getBranchCity() {
		return branchCity;
	}
	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}

	public String getLovDescBranchCityName() {
		return this.lovDescBranchCityName;
	}

	public void setLovDescBranchCityName(String lovDescBranchCityName) {
		this.lovDescBranchCityName = lovDescBranchCityName;
	}

	public String getBranchProvince() {
		return branchProvince;
	}
	public void setBranchProvince(String branchProvince) {
		this.branchProvince = branchProvince;
	}	

	public String getLovDescBranchProvinceName() {
		return this.lovDescBranchProvinceName;
	}
	public void setLovDescBranchProvinceName(String lovDescBranchProvinceName) {
		this.lovDescBranchProvinceName = lovDescBranchProvinceName;
	}

	public String getBranchCountry() {
		return branchCountry;
	}
	public void setBranchCountry(String branchCountry) {
		this.branchCountry = branchCountry;
	}	

	public String getLovDescBranchCountryName() {
		return this.lovDescBranchCountryName;
	}
	public void setLovDescBranchCountryName(String lovDescBranchCountryName) {
		this.lovDescBranchCountryName = lovDescBranchCountryName;
	}

	public String getBranchFax() {
		return branchFax;
	}
	public void setBranchFax(String branchFax) {
		this.branchFax = branchFax;
	}

	public String getBranchTel() {
		return branchTel;
	}
	public void setBranchTel(String branchTel) {
		this.branchTel = branchTel;
	}

	public String getBranchSwiftBankCde() {
		return branchSwiftBankCde;
	}
	public void setBranchSwiftBankCde(String branchSwiftBankCde) {
		this.branchSwiftBankCde = branchSwiftBankCde;
	}

	public String getBranchSwiftCountry() {
		return branchSwiftCountry;
	}
	public void setBranchSwiftCountry(String branchSwiftCountry) {
		this.branchSwiftCountry = branchSwiftCountry;
	}

	public String getLovDescBranchSwiftCountryName() {
		return lovDescBranchSwiftCountryName;
	}
	public void setLovDescBranchSwiftCountryName(
			String lovDescBranchSwiftCountryName) {
		this.lovDescBranchSwiftCountryName = lovDescBranchSwiftCountryName;
	}

	public String getBranchSwiftLocCode() {
		return branchSwiftLocCode;
	}
	public void setBranchSwiftLocCode(String branchSwiftLocCode) {
		this.branchSwiftLocCode = branchSwiftLocCode;
	}

	public String getBranchSwiftBrnCde() {
		return branchSwiftBrnCde;
	}
	public void setBranchSwiftBrnCde(String branchSwiftBrnCde) {
		this.branchSwiftBrnCde = branchSwiftBrnCde;
	}

	public String getBranchSortCode() {
		return branchSortCode;
	}
	public void setBranchSortCode(String branchSortCode) {
		this.branchSortCode = branchSortCode;
	}

	public boolean isBranchIsActive() {
		return branchIsActive;
	}
	public void setBranchIsActive(boolean branchIsActive) {
		this.branchIsActive = branchIsActive;
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

	public Branch getBefImage(){
		return this.befImage;
	}
	public void setBefImage(Branch beforeImage){
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

	// Overridden Equals method to handle the comparison
	public boolean equals(Branch branch) {
		return getId() == branch.getId();
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Branch) {
			Branch branch = (Branch) obj;
			return equals(branch);
		}
		return false;
	}

}
