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
 * FileName    		:  EducationalLoan.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.lmtmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>EducationalLoan table</b>.<br>
 *
 */
public class EducationalLoan implements java.io.Serializable {

	private static final long serialVersionUID = -1321513082479040600L;
	
	private String loanRefNumber;
	private boolean loanRefType;
	private String eduCourse;
	private String lovDescEduCourseName;
	private String eduSpecialization;
	private String eduCourseType;
	private String lovDescEduCourseTypeName;
	private String eduCourseFrom;
	private String eduCourseFromBranch;
	private String eduAffiliatedTo;
	private Date eduCommenceDate;
	private Date eduCompletionDate;
	private BigDecimal eduExpectedIncome;
	private String eduLoanFromBranch;
	private String lovDescEduLoanFromBranchName;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private EducationalLoan befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private List<EducationalExpense> eduExpenseList=new ArrayList<EducationalExpense>();
	private HashMap<String, List<AuditDetail>> lovDescAuditDetailMap = new HashMap<String, List<AuditDetail>>();
	public boolean isNew() {
		return isNewRecord();
	}

	public EducationalLoan() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("EducationalLoan");
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		return excludeFields;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public EducationalLoan(String loanRefNumber) {
		super();
		this.loanRefNumber = loanRefNumber;
	}

	public boolean isLoanRefType() {
		return loanRefType;
	}
	public String getLoanRefNumber() {
		return loanRefNumber;
	}

	public void setLoanRefNumber(String loanRefNumber) {
		this.loanRefNumber = loanRefNumber;
	}
	public void setLoanRefType(boolean loanRefType) {
		this.loanRefType = loanRefType;
	}
	
	public String getEduCourse() {
		return eduCourse;
	}
	public void setEduCourse(String eduCourse) {
		this.eduCourse = eduCourse;
	}

	public String getLovDescEduCourseName() {
		return this.lovDescEduCourseName;
	}
	public void setLovDescEduCourseName (String lovDescEduCourseName) {
		this.lovDescEduCourseName = lovDescEduCourseName;
	}

	public String getEduSpecialization() {
		return eduSpecialization;
	}
	public void setEduSpecialization(String eduSpecialization) {
		this.eduSpecialization = eduSpecialization;
	}

	public String getEduCourseType() {
		return eduCourseType;
	}
	public void setEduCourseType(String eduCourseType) {
		this.eduCourseType = eduCourseType;
	}

	public String getLovDescEduCourseTypeName() {
		return this.lovDescEduCourseTypeName;
	}
	public void setLovDescEduCourseTypeName (String lovDescEduCourseTypeName) {
		this.lovDescEduCourseTypeName = lovDescEduCourseTypeName;
	}

	public String getEduCourseFrom() {
		return eduCourseFrom;
	}
	public void setEduCourseFrom(String eduCourseFrom) {
		this.eduCourseFrom = eduCourseFrom;
	}

	public String getEduCourseFromBranch() {
		return eduCourseFromBranch;
	}
	public void setEduCourseFromBranch(String eduCourseFromBranch) {
		this.eduCourseFromBranch = eduCourseFromBranch;
	}

	public String getEduAffiliatedTo() {
		return eduAffiliatedTo;
	}
	public void setEduAffiliatedTo(String eduAffiliatedTo) {
		this.eduAffiliatedTo = eduAffiliatedTo;
	}

	public Date getEduCommenceDate() {
		return eduCommenceDate;
	}
	public void setEduCommenceDate(Date eduCommenceDate) {
		this.eduCommenceDate = eduCommenceDate;
	}

	public Date getEduCompletionDate() {
		return eduCompletionDate;
	}
	public void setEduCompletionDate(Date eduCompletionDate) {
		this.eduCompletionDate = eduCompletionDate;
	}

	public BigDecimal getEduExpectedIncome() {
		return eduExpectedIncome;
	}
	public void setEduExpectedIncome(BigDecimal eduExpectedIncome) {
		this.eduExpectedIncome = eduExpectedIncome;
	}

	public String getEduLoanFromBranch() {
		return eduLoanFromBranch;
	}
	public void setEduLoanFromBranch(String eduLoanFromBranch) {
		this.eduLoanFromBranch = eduLoanFromBranch;
	}

	public String getLovDescEduLoanFromBranchName() {
		return this.lovDescEduLoanFromBranchName;
	}
	public void setLovDescEduLoanFromBranchName (String lovDescEduLoanFromBranchName) {
		this.lovDescEduLoanFromBranchName = lovDescEduLoanFromBranchName;
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

	public EducationalLoan getBefImage(){
		return this.befImage;
	}
	public void setBefImage(EducationalLoan beforeImage){
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
	
	public void setEduExpenseList(List<EducationalExpense> eduExpenseList) {
		this.eduExpenseList = eduExpenseList;
	}
	public List<EducationalExpense> getEduExpenseList() {
		return eduExpenseList;
	}

	public void setLovDescAuditDetailMap(HashMap<String, List<AuditDetail>> lovDescAuditDetailMap) {
		this.lovDescAuditDetailMap = lovDescAuditDetailMap;
	}
	public HashMap<String, List<AuditDetail>> getLovDescAuditDetailMap() {
		return lovDescAuditDetailMap;
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(EducationalLoan educationalLoan) {
		return getLoanRefNumber() == educationalLoan.getLoanRefNumber();
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

		if (obj instanceof EducationalLoan) {
			EducationalLoan educationalLoan = (EducationalLoan) obj;
			return equals(educationalLoan);
		}
		return false;
	}

}
