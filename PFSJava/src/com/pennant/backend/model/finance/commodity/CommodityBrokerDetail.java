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
 * FileName    		:  CommodityBrokerDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.finance.commodity;

import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CommodityBrokerDetail table</b>.<br>
 *
 */
public class CommodityBrokerDetail implements java.io.Serializable {

	private static final long serialVersionUID = 5232119312085110695L;
	private String     brokerCode = null;
	private long       brokerCustID;

	private Date       brokerFrom;
	private String     brokerAddrHNbr;
	private String     brokerAddrFlatNbr;
	private String     brokerAddrStreet;
	private String     brokerAddrLane1;
	private String     brokerAddrLane2;
	private String     brokerAddrPOBox;
	private String     brokerAddrCountry;
	private String     brokerAddrProvince;
	private String     brokerAddrCity;
	private String     brokerAddrZIP;
	private String     brokerAddrPhone;
	private String     brokerAddrFax;
	private String     brokerEmail;
	private String     agreementRef;
	
	private String     lovDescBrokerCIF;
	private String     lovDescBrokerShortName;
	private String     lovDescBrokerAddrCountryName;
	private String     lovDescBrokerAddrProvinceName;
	private String     lovDescBrokerAddrCityName;
	
	private int        version;
	private long       lastMntBy;
	private Timestamp  lastMntOn;
	private boolean    newRecord=false;
	private String     lovValue;
	private String     recordStatus;
	private String     roleCode="";
	private String     nextRoleCode= "";
	private String     taskId="";
	private String     nextTaskId= "";
	private String     recordType;
	
	private String     userAction = "Save";
	private long       workflowId = 0;
	
	private CommodityBrokerDetail befImage;
	private LoginUserDetails userDetails;
	public boolean isNew() {
		return isNewRecord();
	}

	public CommodityBrokerDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CommodityBrokerDetail");
	}

	public CommodityBrokerDetail(String id) {
		this.setId(id);
	}

	//Getter and Setter methods

	public String getId() {
		return brokerCode;
	}

	public void setId (String id) {
		this.brokerCode = id;
	}

	public String getBrokerCode() {
		return brokerCode;
	}
	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}


	public long getBrokerCustID() {
		return brokerCustID;
	}
	public void setBrokerCustID(long brokerCustID) {
		this.brokerCustID = brokerCustID;
	}

	
	public String getBrokerAddrHNbr() {
		return brokerAddrHNbr;
	}
	public void setBrokerAddrHNbr(String brokerAddrHNbr) {
		this.brokerAddrHNbr = brokerAddrHNbr;
	}

	public String getBrokerAddrFlatNbr() {
		return brokerAddrFlatNbr;
	}
	public void setBrokerAddrFlatNbr(String brokerAddrFlatNbr) {
		this.brokerAddrFlatNbr = brokerAddrFlatNbr;
	}

	public String getBrokerAddrStreet() {
		return brokerAddrStreet;
	}
	public void setBrokerAddrStreet(String brokerAddrStreet) {
		this.brokerAddrStreet = brokerAddrStreet;
	}
	public String getBrokerAddrLane1() {
		return brokerAddrLane1;
	}
	public void setBrokerAddrLane1(String brokerAddrLane1) {
		this.brokerAddrLane1 = brokerAddrLane1;
	}

	public String getBrokerAddrLane2() {
		return brokerAddrLane2;
	}
	public void setBrokerAddrLane2(String brokerAddrLane2) {
		this.brokerAddrLane2 = brokerAddrLane2;
	}
	public String getBrokerAddrPOBox() {
		return brokerAddrPOBox;
	}
	public void setBrokerAddrPOBox(String brokerAddrPOBox) {
		this.brokerAddrPOBox = brokerAddrPOBox;
	}
	public String getBrokerAddrCountry() {
		return brokerAddrCountry;
	}
	public void setBrokerAddrCountry(String brokerAddrCountry) {
		this.brokerAddrCountry = brokerAddrCountry;
	}

	public String getBrokerAddrProvince() {
		return brokerAddrProvince;
	}
	public void setBrokerAddrProvince(String brokerAddrProvince) {
		this.brokerAddrProvince = brokerAddrProvince;
	}
	public String getBrokerAddrCity() {
		return brokerAddrCity;
	}
	public void setBrokerAddrCity(String brokerAddrCity) {
		this.brokerAddrCity = brokerAddrCity;
	}
	public String getBrokerAddrZIP() {
		return brokerAddrZIP;
	}
	public void setBrokerAddrZIP(String brokerAddrZIP) {
		this.brokerAddrZIP = brokerAddrZIP;
	}
	public String getBrokerAddrPhone() {
		return brokerAddrPhone;
	}
	public void setBrokerAddrPhone(String brokerAddrPhone) {
		this.brokerAddrPhone = brokerAddrPhone;
	}
	public String getBrokerAddrFax() {
		return brokerAddrFax;
	}
	public void setBrokerAddrFax(String brokerAddrFax) {
		this.brokerAddrFax = brokerAddrFax;
	}
	public String getBrokerEmail() {
		return brokerEmail;
	}
	public void setBrokerEmail(String brokerEmail) {
		this.brokerEmail = brokerEmail;
	}
	public String getAgreementRef() {
		return agreementRef;
	}
	public void setAgreementRef(String agreementRef) {
		this.agreementRef = agreementRef;
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

	public CommodityBrokerDetail getBefImage(){
		return this.befImage;
	}

	public void setBefImage(CommodityBrokerDetail beforeImage){
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

	public void setLovDescBrokerAddrCountryName(
			String lovDescBrokerAddrCountryName) {
		this.lovDescBrokerAddrCountryName = lovDescBrokerAddrCountryName;
	}

	public String getLovDescBrokerAddrCountryName() {
		return lovDescBrokerAddrCountryName;
	}

	public void setLovDescBrokerAddrProvinceName(
			String lovDescBrokerAddrProvinceName) {
		this.lovDescBrokerAddrProvinceName = lovDescBrokerAddrProvinceName;
	}

	public String getLovDescBrokerAddrProvinceName() {
		return lovDescBrokerAddrProvinceName;
	}

	public void setLovDescBrokerAddrCityName(String lovDescBrokerAddrCityName) {
		this.lovDescBrokerAddrCityName = lovDescBrokerAddrCityName;
	}

	public String getLovDescBrokerAddrCityName() {
		return lovDescBrokerAddrCityName;
	}

	public void setLovDescBrokerShortName(String lovDescBrokerShortName) {
		this.lovDescBrokerShortName = lovDescBrokerShortName;
	}

	public String getLovDescBrokerShortName() {
		return lovDescBrokerShortName;
	}

	public void setLovDescBrokerCIF(String lovDescBrokerCIF) {
		this.lovDescBrokerCIF = lovDescBrokerCIF;
	}

	public String getLovDescBrokerCIF() {
		return lovDescBrokerCIF;
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(CommodityBrokerDetail commodityBrokerDetail) {
		return getId() == commodityBrokerDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof CommodityBrokerDetail) {
			CommodityBrokerDetail commodityBrokerDetail = (CommodityBrokerDetail) obj;
			return equals(commodityBrokerDetail);
		}
		return false;
	}

	public void setBrokerFrom(Date brokerFrom) {
		this.brokerFrom = brokerFrom;
	}

	public Date getBrokerFrom() {
		return brokerFrom;
	}

}
