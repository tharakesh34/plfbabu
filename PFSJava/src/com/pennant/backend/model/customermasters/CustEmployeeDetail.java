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
 * FileName    		:  CustomerEmploymentDetail.java                                                   * 	  
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CustomerEmploymentDetail table</b>.<br>
 *
 */
public class CustEmployeeDetail implements java.io.Serializable {

	private static final long serialVersionUID = -5317225672461108680L;
	
	private long custID;
	private String empStatus;
	private String lovDescEmpStatus;
	private String empSector;
	private String lovDescEmpSector;
	private String profession;
	private String lovDescProfession;
	private long empName;
	private String lovDescEmpName;
	private String empNameForOthers;
	private String empDesg;
	private String lovDescEmpDesg;
	private String empDept;
	private String lovDescEmpDept;
	private Date empFrom;
	private BigDecimal 	monthlyIncome = BigDecimal.ZERO;
	private String otherIncome;
	private String lovDescOtherIncome;
	private BigDecimal 	additionalIncome = BigDecimal.ZERO;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private String lovDescCustShrtName;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	
	private CustEmployeeDetail befImage;
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

	public CustEmployeeDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CustomerEmploymentDetail");
	}

	public CustEmployeeDetail(long id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		return excludeFields;
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
	
	public String getEmpStatus() {
		return empStatus;
	}
	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}

	public String getLovDescEmpStatus() {
		return lovDescEmpStatus;
	}
	public void setLovDescEmpStatus(String lovDescEmpStatus) {
		this.lovDescEmpStatus = lovDescEmpStatus;
	}

	public String getEmpSector() {
		return empSector;
	}
	public void setEmpSector(String empSector) {
		this.empSector = empSector;
	}

	public String getLovDescEmpSector() {
		return lovDescEmpSector;
	}
	public void setLovDescEmpSector(String lovDescEmpSector) {
		this.lovDescEmpSector = lovDescEmpSector;
	}

	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getLovDescProfession() {
		return lovDescProfession;
	}
	public void setLovDescProfession(String lovDescProfession) {
		this.lovDescProfession = lovDescProfession;
	}

	public long getEmpName() {
		return empName;
	}
	public void setEmpName(long empName) {
		this.empName = empName;
	}

	public String getLovDescEmpName() {
		return lovDescEmpName;
	}
	public void setLovDescEmpName(String lovDescEmpName) {
		this.lovDescEmpName = lovDescEmpName;
	}

	public String getEmpNameForOthers() {
		return empNameForOthers;
	}
	public void setEmpNameForOthers(String empNameForOthers) {
		this.empNameForOthers = empNameForOthers;
	}

	public String getEmpDesg() {
		return empDesg;
	}
	public void setEmpDesg(String empDesg) {
		this.empDesg = empDesg;
	}

	public String getLovDescEmpDesg() {
		return lovDescEmpDesg;
	}
	public void setLovDescEmpDesg(String lovDescEmpDesg) {
		this.lovDescEmpDesg = lovDescEmpDesg;
	}

	public String getEmpDept() {
		return empDept;
	}
	public void setEmpDept(String empDept) {
		this.empDept = empDept;
	}

	public String getLovDescEmpDept() {
		return lovDescEmpDept;
	}
	public void setLovDescEmpDept(String lovDescEmpDept) {
		this.lovDescEmpDept = lovDescEmpDept;
	}

	public Date getEmpFrom() {
		return empFrom;
	}
	public void setEmpFrom(Date empFrom) {
		this.empFrom = empFrom;
	}

	public BigDecimal getMonthlyIncome() {
		return monthlyIncome;
	}
	public void setMonthlyIncome(BigDecimal monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}

	public String getOtherIncome() {
		return otherIncome;
	}
	public void setOtherIncome(String otherIncome) {
		this.otherIncome = otherIncome;
	}

	public String getLovDescOtherIncome() {
		return lovDescOtherIncome;
	}
	public void setLovDescOtherIncome(String lovDescOtherIncome) {
		this.lovDescOtherIncome = lovDescOtherIncome;
	}

	public BigDecimal getAdditionalIncome() {
		return additionalIncome;
	}
	public void setAdditionalIncome(BigDecimal additionalIncome) {
		this.additionalIncome = additionalIncome;
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

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public CustEmployeeDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustEmployeeDetail beforeImage){
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
	
	public void setLoginDetails(LoginUserDetails userDetails){
		this.lastMntBy=userDetails.getLoginUsrID();
		this.userDetails=userDetails;
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(CustEmployeeDetail customerEmploymentDetail) {
		return getId() == customerEmploymentDetail.getId();
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

		if (obj instanceof CustEmployeeDetail) {
			CustEmployeeDetail customerEmploymentDetail = (CustEmployeeDetail) obj;
			return equals(customerEmploymentDetail);
		}
		return false;
	}
}
