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
 * FileName    		:  HomeLoanDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>HomeLoanDetail table</b>.<br>
 *
 */
public class HomeLoanDetail implements java.io.Serializable {
	
	private static final long serialVersionUID = -6234931333270161797L;

	private String loanRefNumber;
	private boolean loanRefType;
	private long homeDetails;
	private String lovDescHomeDetailsName;
	private String homeBuilderName;
	private BigDecimal homeCostPerFlat;
	private BigDecimal homeCostOfLand;
	private BigDecimal homeCostOfConstruction;
	private String homeConstructionStage;
	private Date homeDateOfPocession;
	private BigDecimal homeAreaOfLand;
	private BigDecimal homeAreaOfFlat;
	private long homePropertyType;
	private String lovDescHomePropertyTypeName;
	private long homeOwnerShipType;
	private String lovDescHomeOwnerShipTypeName;
	private String homeAddrFlatNbr;
	private String homeAddrStreet;
	private String homeAddrLane1;
	private String homeAddrLane2;
	private String homeAddrPOBox;
	private String homeAddrCountry;
	private String lovDescHomeAddrCountryName;
	private String homeAddrProvince;
	private String lovDescHomeAddrProvinceName;
	private String homeAddrCity;
	private String lovDescHomeAddrCityName;
	private String homeAddrZIP;
	private String homeAddrPhone;
	private String homeTitleDeedNo;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private HomeLoanDetail befImage;
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

	public HomeLoanDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("HomeLoanDetail");
	}

	public HomeLoanDetail(String id) {
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getId() {
		return loanRefNumber;
	}
	public void setId (String loanRefNumber) {
		this.loanRefNumber = loanRefNumber;
	}
	
	public String getLoanRefNumber() {
		return loanRefNumber;
	}
	public void setLoanRefNumber(String loanRefNumber) {
		this.loanRefNumber = loanRefNumber;
	}
	
	public boolean isLoanRefType() {
		return loanRefType;
	}
	public void setLoanRefType(boolean loanRefType) {
		this.loanRefType = loanRefType;
	}
	
	public long getHomeDetails() {
		return homeDetails;
	}
	public void setHomeDetails(long homeDetails) {
		this.homeDetails = homeDetails;
	}

	public String getLovDescHomeDetailsName() {
		return this.lovDescHomeDetailsName;
	}
	public void setLovDescHomeDetailsName (String lovDescHomeDetailsName) {
		this.lovDescHomeDetailsName = lovDescHomeDetailsName;
	}
	
	public String getHomeBuilderName() {
		return homeBuilderName;
	}
	public void setHomeBuilderName(String homeBuilderName) {
		this.homeBuilderName = homeBuilderName;
	}
	
	public BigDecimal getHomeCostPerFlat() {
		return homeCostPerFlat;
	}
	public void setHomeCostPerFlat(BigDecimal homeCostPerFlat) {
		this.homeCostPerFlat = homeCostPerFlat;
	}
	
	public BigDecimal getHomeCostOfLand() {
		return homeCostOfLand;
	}
	public void setHomeCostOfLand(BigDecimal homeCostOfLand) {
		this.homeCostOfLand = homeCostOfLand;
	}
	
	public BigDecimal getHomeCostOfConstruction() {
		return homeCostOfConstruction;
	}
	public void setHomeCostOfConstruction(BigDecimal homeCostOfConstruction) {
		this.homeCostOfConstruction = homeCostOfConstruction;
	}
	
	public String getHomeConstructionStage() {
		return homeConstructionStage;
	}
	public void setHomeConstructionStage(String homeConstructionStage) {
		this.homeConstructionStage = homeConstructionStage;
	}

	public Date getHomeDateOfPocession() {
		return homeDateOfPocession;
	}
	public void setHomeDateOfPocession(Date homeDateOfPocession) {
		this.homeDateOfPocession = homeDateOfPocession;
	}
	
	public BigDecimal getHomeAreaOfLand() {
		return homeAreaOfLand;
	}
	public void setHomeAreaOfLand(BigDecimal homeAreaOfLand) {
		this.homeAreaOfLand = homeAreaOfLand;
	}
	
	public BigDecimal getHomeAreaOfFlat() {
		return homeAreaOfFlat;
	}
	public void setHomeAreaOfFlat(BigDecimal homeAreaOfFlat) {
		this.homeAreaOfFlat = homeAreaOfFlat;
	}
	
	public long getHomePropertyType() {
		return homePropertyType;
	}
	public void setHomePropertyType(long homePropertyType) {
		this.homePropertyType = homePropertyType;
	}

	public String getLovDescHomePropertyTypeName() {
		return this.lovDescHomePropertyTypeName;
	}
	public void setLovDescHomePropertyTypeName (String lovDescHomePropertyTypeName) {
		this.lovDescHomePropertyTypeName = lovDescHomePropertyTypeName;
	}
	
	public long getHomeOwnerShipType() {
		return homeOwnerShipType;
	}
	public void setHomeOwnerShipType(long homeOwnerShipType) {
		this.homeOwnerShipType = homeOwnerShipType;
	}

	public String getLovDescHomeOwnerShipTypeName() {
		return this.lovDescHomeOwnerShipTypeName;
	}
	public void setLovDescHomeOwnerShipTypeName (String lovDescHomeOwnerShipTypeName) {
		this.lovDescHomeOwnerShipTypeName = lovDescHomeOwnerShipTypeName;
	}
	
	public String getHomeAddrFlatNbr() {
		return homeAddrFlatNbr;
	}
	public void setHomeAddrFlatNbr(String homeAddrFlatNbr) {
		this.homeAddrFlatNbr = homeAddrFlatNbr;
	}
	
	public String getHomeAddrStreet() {
		return homeAddrStreet;
	}
	public void setHomeAddrStreet(String homeAddrStreet) {
		this.homeAddrStreet = homeAddrStreet;
	}
	
	public String getHomeAddrLane1() {
		return homeAddrLane1;
	}
	public void setHomeAddrLane1(String homeAddrLane1) {
		this.homeAddrLane1 = homeAddrLane1;
	}
	
	public String getHomeAddrLane2() {
		return homeAddrLane2;
	}
	public void setHomeAddrLane2(String homeAddrLane2) {
		this.homeAddrLane2 = homeAddrLane2;
	}
	
	public String getHomeAddrPOBox() {
		return homeAddrPOBox;
	}
	public void setHomeAddrPOBox(String homeAddrPOBox) {
		this.homeAddrPOBox = homeAddrPOBox;
	}
	
	public String getHomeAddrCountry() {
		return homeAddrCountry;
	}
	public void setHomeAddrCountry(String homeAddrCountry) {
		this.homeAddrCountry = homeAddrCountry;
	}

	public String getLovDescHomeAddrCountryName() {
		return this.lovDescHomeAddrCountryName;
	}
	public void setLovDescHomeAddrCountryName (String lovDescHomeAddrCountryName) {
		this.lovDescHomeAddrCountryName = lovDescHomeAddrCountryName;
	}
	
	public String getHomeAddrProvince() {
		return homeAddrProvince;
	}
	public void setHomeAddrProvince(String homeAddrProvince) {
		this.homeAddrProvince = homeAddrProvince;
	}

	public String getLovDescHomeAddrProvinceName() {
		return this.lovDescHomeAddrProvinceName;
	}
	public void setLovDescHomeAddrProvinceName (String lovDescHomeAddrProvinceName) {
		this.lovDescHomeAddrProvinceName = lovDescHomeAddrProvinceName;
	}
	
	public String getHomeAddrCity() {
		return homeAddrCity;
	}
	public void setHomeAddrCity(String homeAddrCity) {
		this.homeAddrCity = homeAddrCity;
	}

	public String getLovDescHomeAddrCityName() {
		return this.lovDescHomeAddrCityName;
	}
	public void setLovDescHomeAddrCityName (String lovDescHomeAddrCityName) {
		this.lovDescHomeAddrCityName = lovDescHomeAddrCityName;
	}
	
	public String getHomeAddrZIP() {
		return homeAddrZIP;
	}
	public void setHomeAddrZIP(String homeAddrZIP) {
		this.homeAddrZIP = homeAddrZIP;
	}
	
	public String getHomeAddrPhone() {
		return homeAddrPhone;
	}
	public void setHomeAddrPhone(String homeAddrPhone) {
		this.homeAddrPhone = homeAddrPhone;
	}
	
	public String getHomeTitleDeedNo() {
    	return homeTitleDeedNo;
    }

	public void setHomeTitleDeedNo(String homeTitleDeedNo) {
    	this.homeTitleDeedNo = homeTitleDeedNo;
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

	public HomeLoanDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(HomeLoanDetail beforeImage){
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
	public boolean equals(HomeLoanDetail homeLoanDetail) {
		return getId() == homeLoanDetail.getId();
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

		if (obj instanceof HomeLoanDetail) {
			HomeLoanDetail homeLoanDetail = (HomeLoanDetail) obj;
			return equals(homeLoanDetail);
		}
		return false;
	}
	
}
