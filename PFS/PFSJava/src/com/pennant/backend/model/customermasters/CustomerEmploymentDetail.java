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

import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CustomerEmploymentDetail table</b>.<br>
 *
 */
public class CustomerEmploymentDetail implements java.io.Serializable {

	private static final long serialVersionUID = -5317225672461108680L;
	
	private long custID;
	private String custEmpName;
	private Date custEmpFrom;
	private String custEmpDesg;
	private String lovDescCustEmpDesgName;
	private String custEmpDept;
	private String lovDescCustEmpDeptName;
	private String custEmpID;
	private String custEmpType;
	private String lovDescCustEmpTypeName;
	private String custEmpHNbr;
	private String custEMpFlatNbr;
	private String custEmpAddrStreet;
	private String custEMpAddrLine1;
	private String custEMpAddrLine2;
	private String custEmpPOBox;
	private String custEmpAddrCity;
	private String lovDescCustEmpAddrCityName;
	private String custEmpAddrProvince;
	private String lovDescCustEmpAddrProvinceName;
	private String custEmpAddrCountry;
	private String lovDescCustEmpAddrCountryName;
	private String custEmpAddrZIP;
	private String custEmpAddrPhone;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private String lovDescCustShrtName;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	
	private CustomerEmploymentDetail befImage;
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

	public CustomerEmploymentDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CustomerEmploymentDetail");
	}

	public CustomerEmploymentDetail(long id) {
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
	
	public String getCustEmpName() {
		return custEmpName;
	}
	public void setCustEmpName(String custEmpName) {
		this.custEmpName = custEmpName;
	}

	public Date getCustEmpFrom() {
		return custEmpFrom;
	}
	public void setCustEmpFrom(Date custEmpFrom) {
		this.custEmpFrom = custEmpFrom;
	}

	public String getCustEmpDesg() {
		return custEmpDesg;
	}
	public void setCustEmpDesg(String custEmpDesg) {
		this.custEmpDesg = custEmpDesg;
	}

	public String getLovDescCustEmpDesgName() {
		return this.lovDescCustEmpDesgName;
	}
	public void setLovDescCustEmpDesgName(String lovDescCustEmpDesgName) {
		this.lovDescCustEmpDesgName = lovDescCustEmpDesgName;
	}
	
	public String getCustEmpDept() {
		return custEmpDept;
	}
	public void setCustEmpDept(String custEmpDept) {
		this.custEmpDept = custEmpDept;
	}

	public String getLovDescCustEmpDeptName() {
		return this.lovDescCustEmpDeptName;
	}
	public void setLovDescCustEmpDeptName(String lovDescCustEmpDeptName) {
		this.lovDescCustEmpDeptName = lovDescCustEmpDeptName;
	}
	
	public String getCustEmpID() {
		return custEmpID;
	}
	public void setCustEmpID(String custEmpID) {
		this.custEmpID = custEmpID;
	}
	
	public String getCustEmpType() {
		return custEmpType;
	}
	public void setCustEmpType(String custEmpType) {
		this.custEmpType = custEmpType;
	}

	public String getLovDescCustEmpTypeName() {
		return this.lovDescCustEmpTypeName;
	}
	public void setLovDescCustEmpTypeName(String lovDescCustEmpTypeName) {
		this.lovDescCustEmpTypeName = lovDescCustEmpTypeName;
	}

	public String getCustEmpHNbr() {
		return custEmpHNbr;
	}
	public void setCustEmpHNbr(String custEmpHNbr) {
		this.custEmpHNbr = custEmpHNbr;
	}
	
	public String getCustEMpFlatNbr() {
		return custEMpFlatNbr;
	}
	public void setCustEMpFlatNbr(String custEMpFlatNbr) {
		this.custEMpFlatNbr = custEMpFlatNbr;
	}
	
	public String getCustEmpAddrStreet() {
		return custEmpAddrStreet;
	}
	public void setCustEmpAddrStreet(String custEmpAddrStreet) {
		this.custEmpAddrStreet = custEmpAddrStreet;
	}
	
	public String getCustEMpAddrLine1() {
		return custEMpAddrLine1;
	}
	public void setCustEMpAddrLine1(String custEMpAddrLine1) {
		this.custEMpAddrLine1 = custEMpAddrLine1;
	}
	
	public String getCustEMpAddrLine2() {
		return custEMpAddrLine2;
	}
	public void setCustEMpAddrLine2(String custEMpAddrLine2) {
		this.custEMpAddrLine2 = custEMpAddrLine2;
	}
	
	public String getCustEmpPOBox() {
		return custEmpPOBox;
	}
	public void setCustEmpPOBox(String custEmpPOBox) {
		this.custEmpPOBox = custEmpPOBox;
	}
	
	public String getCustEmpAddrCity() {
		return custEmpAddrCity;
	}
	public void setCustEmpAddrCity(String custEmpAddrCity) {
		this.custEmpAddrCity = custEmpAddrCity;
	}

	public String getLovDescCustEmpAddrCityName() {
		return this.lovDescCustEmpAddrCityName;
	}
	public void setLovDescCustEmpAddrCityName(String lovDescCustEmpAddrCityName) {
		this.lovDescCustEmpAddrCityName = lovDescCustEmpAddrCityName;
	}

	public String getCustEmpAddrProvince() {
		return custEmpAddrProvince;
	}
	public void setCustEmpAddrProvince(String custEmpAddrProvince) {
		this.custEmpAddrProvince = custEmpAddrProvince;
	}

	public String getLovDescCustEmpAddrProvinceName() {
		return this.lovDescCustEmpAddrProvinceName;
	}
	public void setLovDescCustEmpAddrProvinceName(String lovDescCustEmpAddrProvinceName) {
		this.lovDescCustEmpAddrProvinceName = lovDescCustEmpAddrProvinceName;
	}
	
	public String getCustEmpAddrCountry() {
		return custEmpAddrCountry;
	}
	public void setCustEmpAddrCountry(String custEmpAddrCountry) {
		this.custEmpAddrCountry = custEmpAddrCountry;
	}
	
	public String getLovDescCustEmpAddrCountryName() {
		return this.lovDescCustEmpAddrCountryName;
	}
	public void setLovDescCustEmpAddrCountryName(String lovDescCustEmpAddrCountryName) {
		this.lovDescCustEmpAddrCountryName = lovDescCustEmpAddrCountryName;
	}
	
	public String getCustEmpAddrZIP() {
		return custEmpAddrZIP;
	}
	public void setCustEmpAddrZIP(String custEmpAddrZIP) {
		this.custEmpAddrZIP = custEmpAddrZIP;
	}
	
	public String getCustEmpAddrPhone() {
		return custEmpAddrPhone;
	}
	public void setCustEmpAddrPhone(String custEmpAddrPhone) {
		this.custEmpAddrPhone = custEmpAddrPhone;
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

	public CustomerEmploymentDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerEmploymentDetail beforeImage){
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
	public boolean equals(CustomerEmploymentDetail customerEmploymentDetail) {
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

		if (obj instanceof CustomerEmploymentDetail) {
			CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) obj;
			return equals(customerEmploymentDetail);
		}
		return false;
	}
}
