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
 * FileName    		:  CustomerPRelation.java                                                   * 	  
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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CustomerPRelation table</b>.<br>
 *
 */
public class CustomerPRelation implements Serializable, Entity {

	private static final long serialVersionUID = -4817133724784086357L;

	private long pRCustID =Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private int pRCustPRSNo;
	private String pRRelationCode;
	private String lovDescPRRelationCodeName;//new
	private String pRRelationCustID;
	private boolean pRisGuardian;
	private String pRFName;
	private String pRMName;
	private String pRLName;
	private String pRSName;
	private String pRFNameLclLng;
	private String pRMNameLclLng;
	private String pRLNameLclLng;
	private Date pRDOB;
	private String pRAddrHNbr;
	private String pRAddrFNbr;
	private String pRAddrStreet;
	private String pRAddrLine1;
	private String pRAddrLine2;
	private String pRAddrPOBox;
	private String pRAddrCity;
	private String lovDescPRAddrCityName;
	private String pRAddrProvince;
	private String lovDescPRAddrProvinceName;
	private String pRAddrCountry;
	private String lovDescPRAddrCountryName;
	private String pRAddrZIP;
	private String pRPhone;
	private String pRMail;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	private CustomerPRelation befImage;
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

	public CustomerPRelation() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CustomerPRelation");
	}

	public CustomerPRelation(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getId() {
		return pRCustID;
	}
	public void setId (long id) {
		this.pRCustID = id;
	}

	public long getPRCustID() {
		return pRCustID;
	}
	public void setPRCustID(long pRCustID) {
		this.pRCustID = pRCustID;
	}

	public int getPRCustPRSNo() {
		return pRCustPRSNo;
	}
	public void setPRCustPRSNo(int pRCustPRSNo) {
		this.pRCustPRSNo = pRCustPRSNo;
	}

	public String getPRRelationCode() {
		return pRRelationCode;
	}
	public void setPRRelationCode(String pRRelationCode) {
		this.pRRelationCode = pRRelationCode;
	}

	public String getLovDescPRRelationCodeName() {
		return this.lovDescPRRelationCodeName;
	}
	public void setLovDescPRRelationCodeName(String lovDescPRRelationCodeName) {
		this.lovDescPRRelationCodeName = lovDescPRRelationCodeName;
	}

	public String getPRRelationCustID() {
		return pRRelationCustID;
	}
	public void setPRRelationCustID(String pRRelationCustID) {
		this.pRRelationCustID = pRRelationCustID;
	}

	public boolean isPRisGuardian() {
		return pRisGuardian;
	}
	public void setPRisGuardian(boolean pRisGuardian) {
		this.pRisGuardian = pRisGuardian;
	}

	public String getPRFName() {
		return pRFName;
	}
	public void setPRFName(String pRFName) {
		this.pRFName = pRFName;
	}

	public String getPRMName() {
		return pRMName;
	}
	public void setPRMName(String pRMName) {
		this.pRMName = pRMName;
	}

	public String getPRLName() {
		return pRLName;
	}
	public void setPRLName(String pRLName) {
		this.pRLName = pRLName;
	}

	public String getPRSName() {
		return pRSName;
	}
	public void setPRSName(String pRSName) {
		this.pRSName = pRSName;
	}

	public String getPRFNameLclLng() {
		return pRFNameLclLng;
	}
	public void setPRFNameLclLng(String pRFNameLclLng) {
		this.pRFNameLclLng = pRFNameLclLng;
	}

	public String getPRMNameLclLng() {
		return pRMNameLclLng;
	}
	public void setPRMNameLclLng(String pRMNameLclLng) {
		this.pRMNameLclLng = pRMNameLclLng;
	}

	public String getPRLNameLclLng() {
		return pRLNameLclLng;
	}
	public void setPRLNameLclLng(String pRLNameLclLng) {
		this.pRLNameLclLng = pRLNameLclLng;
	}

	public Date getPRDOB() {
		return pRDOB;
	}
	public void setPRDOB(Date pRDOB) {
		this.pRDOB = pRDOB;
	}

	public String getPRAddrHNbr() {
		return pRAddrHNbr;
	}
	public void setPRAddrHNbr(String pRAddrHNbr) {
		this.pRAddrHNbr = pRAddrHNbr;
	}

	public String getPRAddrFNbr() {
		return pRAddrFNbr;
	}
	public void setPRAddrFNbr(String pRAddrFNbr) {
		this.pRAddrFNbr = pRAddrFNbr;
	}

	public String getPRAddrStreet() {
		return pRAddrStreet;
	}
	public void setPRAddrStreet(String pRAddrStreet) {
		this.pRAddrStreet = pRAddrStreet;
	}

	public String getPRAddrLine1() {
		return pRAddrLine1;
	}
	public void setPRAddrLine1(String pRAddrLine1) {
		this.pRAddrLine1 = pRAddrLine1;
	}

	public String getPRAddrLine2() {
		return pRAddrLine2;
	}
	public void setPRAddrLine2(String pRAddrLine2) {
		this.pRAddrLine2 = pRAddrLine2;
	}

	public String getPRAddrPOBox() {
		return pRAddrPOBox;
	}
	public void setPRAddrPOBox(String pRAddrPOBox) {
		this.pRAddrPOBox = pRAddrPOBox;
	}

	public String getPRAddrCity() {
		return pRAddrCity;
	}
	public void setPRAddrCity(String pRAddrCity) {
		this.pRAddrCity = pRAddrCity;
	}

	public String getLovDescPRAddrCityName() {
		return this.lovDescPRAddrCityName;
	}
	public void setLovDescPRAddrCityName(String lovDescPRAddrCityName) {
		this.lovDescPRAddrCityName = lovDescPRAddrCityName;
	}

	public String getPRAddrProvince() {
		return pRAddrProvince;
	}
	public void setPRAddrProvince(String pRAddrProvince) {
		this.pRAddrProvince = pRAddrProvince;
	}

	public String getLovDescPRAddrProvinceName() {
		return this.lovDescPRAddrProvinceName;
	}
	public void setLovDescPRAddrProvinceName(String lovDescPRAddrProvinceName) {
		this.lovDescPRAddrProvinceName = lovDescPRAddrProvinceName;
	}

	public String getPRAddrCountry() {
		return pRAddrCountry;
	}
	public void setPRAddrCountry(String pRAddrCountry) {
		this.pRAddrCountry = pRAddrCountry;
	}

	public String getLovDescPRAddrCountryName() {
		return this.lovDescPRAddrCountryName;
	}
	public void setLovDescPRAddrCountryName(String lovDescPRAddrCountryName) {
		this.lovDescPRAddrCountryName = lovDescPRAddrCountryName;
	}

	public String getPRAddrZIP() {
		return pRAddrZIP;
	}
	public void setPRAddrZIP(String pRAddrZIP) {
		this.pRAddrZIP = pRAddrZIP;
	}

	public String getPRPhone() {
		return pRPhone;
	}
	public void setPRPhone(String pRPhone) {
		this.pRPhone = pRPhone;
	}

	public String getPRMail() {
		return pRMail;
	}
	public void setPRMail(String pRMail) {
		this.pRMail = pRMail;
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

	public CustomerPRelation getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerPRelation beforeImage){
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

	public void setLoginDetails(LoginUserDetails userDetails){
		this.lastMntBy=userDetails.getLoginUsrID();
		this.userDetails=userDetails;

	}

	// Overridden Equals method to handle the comparison
	public boolean equals(CustomerPRelation customerPRelation) {
		if(getPRCustID()== customerPRelation.getPRCustID() && 
				getPRCustPRSNo()==customerPRelation.getPRCustPRSNo()){
			return true;
		}
		return false;
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

		if (obj instanceof CustomerPRelation) {
			CustomerPRelation customerPRelation = (CustomerPRelation) obj;
			return equals(customerPRelation);
		}
		return false;
	}

}
