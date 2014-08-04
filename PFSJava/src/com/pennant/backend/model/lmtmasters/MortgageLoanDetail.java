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
 * FileName    		:  MortgageLoanDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-10-2011    														*
 *                                                                  						*
 * Modified Date    :  14-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-10-2011       Pennant	                 0.1                                            * 
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
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>MortgageLoanDetail table</b>.<br>
 */
public class MortgageLoanDetail implements java.io.Serializable {

	private static final long serialVersionUID = 222073095204344055L;
	
	private String loanRefNumber;
	private long mortgProperty;
	private String lovDescMortgPropertyName;
	private BigDecimal mortgCurrentValue;
	private String mortgPurposeOfLoan;
	private long mortgPropertyRelation;
	private String lovDescMortgPropertyRelationName;
	private long mortgOwnership;
	private String lovDescMortgOwnershipName;
	private String mortgAddrHNbr;
	private String mortgAddrFlatNbr;
	private String mortgAddrStreet;
	private String mortgAddrLane1;
	private String mortgAddrLane2;
	private String mortgAddrPOBox;
	private String mortgAddrCountry;
	private String lovDescMortgAddrCountryName;
	private String mortgAddrProvince;
	private String lovDescMortgAddrProvinceName;
	private String mortgAddrCity;
	private String lovDescMortgAddrCityName;
	private String mortgAddrZIP;
	private String mortgAddrPhone;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private MortgageLoanDetail befImage;
	private LoginUserDetails userDetails;
	
	
	private String mortDeedNo;
	private String mortRegistrationNo;
	private BigDecimal mortAreaSF;
	private BigDecimal mortAreaSM;
	private BigDecimal mortPricePF;
	private int mortAge;
	private BigDecimal mortFinRatio;
	private String mortStatus;

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

	public MortgageLoanDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("MortgageLoanDetail");
	}

	public MortgageLoanDetail(String id) {
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
	public void setId (String id) {
		this.loanRefNumber = id;
	}
	
	public String getLoanRefNumber() {
		return loanRefNumber;
	}
	public void setLoanRefNumber(String loanRefNumber) {
		this.loanRefNumber = loanRefNumber;
	}
	public long getMortgProperty() {
		return mortgProperty;
	}
	public void setMortgProperty(long mortgProperty) {
		this.mortgProperty = mortgProperty;
	}

	public String getLovDescMortgPropertyName() {
		return this.lovDescMortgPropertyName;
	}
	public void setLovDescMortgPropertyName (String lovDescMortgPropertyName) {
		this.lovDescMortgPropertyName = lovDescMortgPropertyName;
	}
	
	public BigDecimal getMortgCurrentValue() {
		return mortgCurrentValue;
	}
	public void setMortgCurrentValue(BigDecimal mortgCurrentValue) {
		this.mortgCurrentValue = mortgCurrentValue;
	}
	
	public String getMortgPurposeOfLoan() {
		return mortgPurposeOfLoan;
	}
	public void setMortgPurposeOfLoan(String mortgPurposeOfLoan) {
		this.mortgPurposeOfLoan = mortgPurposeOfLoan;
	}
	
	public long getMortgPropertyRelation() {
		return mortgPropertyRelation;
	}
	public void setMortgPropertyRelation(long mortgPropertyRelation) {
		this.mortgPropertyRelation = mortgPropertyRelation;
	}

	public String getLovDescMortgPropertyRelationName() {
		return this.lovDescMortgPropertyRelationName;
	}
	public void setLovDescMortgPropertyRelationName (String lovDescMortgPropertyRelationName) {
		this.lovDescMortgPropertyRelationName = lovDescMortgPropertyRelationName;
	}
	
	public long getMortgOwnership() {
		return mortgOwnership;
	}
	public void setMortgOwnership(long mortgOwnership) {
		this.mortgOwnership = mortgOwnership;
	}

	public String getLovDescMortgOwnershipName() {
		return this.lovDescMortgOwnershipName;
	}
	public void setLovDescMortgOwnershipName (String lovDescMortgOwnershipName) {
		this.lovDescMortgOwnershipName = lovDescMortgOwnershipName;
	}
	
	public String getMortgAddrHNbr() {
		return mortgAddrHNbr;
	}
	public void setMortgAddrHNbr(String mortgAddrHNbr) {
		this.mortgAddrHNbr = mortgAddrHNbr;
	}
	
	public String getMortgAddrFlatNbr() {
		return mortgAddrFlatNbr;
	}
	public void setMortgAddrFlatNbr(String mortgAddrFlatNbr) {
		this.mortgAddrFlatNbr = mortgAddrFlatNbr;
	}
	
	public String getMortgAddrStreet() {
		return mortgAddrStreet;
	}
	public void setMortgAddrStreet(String mortgAddrStreet) {
		this.mortgAddrStreet = mortgAddrStreet;
	}
	
	public String getMortgAddrLane1() {
		return mortgAddrLane1;
	}
	public void setMortgAddrLane1(String mortgAddrLane1) {
		this.mortgAddrLane1 = mortgAddrLane1;
	}
	
	public String getMortgAddrLane2() {
		return mortgAddrLane2;
	}
	public void setMortgAddrLane2(String mortgAddrLane2) {
		this.mortgAddrLane2 = mortgAddrLane2;
	}
	
	public String getMortgAddrPOBox() {
		return mortgAddrPOBox;
	}
	public void setMortgAddrPOBox(String mortgAddrPOBox) {
		this.mortgAddrPOBox = mortgAddrPOBox;
	}
	
	public String getMortgAddrCountry() {
		return mortgAddrCountry;
	}
	public void setMortgAddrCountry(String mortgAddrCountry) {
		this.mortgAddrCountry = mortgAddrCountry;
	}

	public String getLovDescMortgAddrCountryName() {
		return this.lovDescMortgAddrCountryName;
	}
	public void setLovDescMortgAddrCountryName (String lovDescMortgAddrCountryName) {
		this.lovDescMortgAddrCountryName = lovDescMortgAddrCountryName;
	}
	
	public String getMortgAddrProvince() {
		return mortgAddrProvince;
	}
	public void setMortgAddrProvince(String mortgAddrProvince) {
		this.mortgAddrProvince = mortgAddrProvince;
	}

	public String getLovDescMortgAddrProvinceName() {
		return this.lovDescMortgAddrProvinceName;
	}
	public void setLovDescMortgAddrProvinceName (String lovDescMortgAddrProvinceName) {
		this.lovDescMortgAddrProvinceName = lovDescMortgAddrProvinceName;
	}
	
	public String getMortgAddrCity() {
		return mortgAddrCity;
	}
	public void setMortgAddrCity(String mortgAddrCity) {
		this.mortgAddrCity = mortgAddrCity;
	}

	public String getLovDescMortgAddrCityName() {
		return this.lovDescMortgAddrCityName;
	}
	public void setLovDescMortgAddrCityName (String lovDescMortgAddrCityName) {
		this.lovDescMortgAddrCityName = lovDescMortgAddrCityName;
	}
	
	public String getMortgAddrZIP() {
		return mortgAddrZIP;
	}
	public void setMortgAddrZIP(String mortgAddrZIP) {
		this.mortgAddrZIP = mortgAddrZIP;
	}
	
	public String getMortgAddrPhone() {
		return mortgAddrPhone;
	}
	public void setMortgAddrPhone(String mortgAddrPhone) {
		this.mortgAddrPhone = mortgAddrPhone;
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

	public MortgageLoanDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(MortgageLoanDetail beforeImage){
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
	public boolean equals(MortgageLoanDetail mortgageLoanDetail) {
		return getId() == mortgageLoanDetail.getId();
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

		if (obj instanceof MortgageLoanDetail) {
			MortgageLoanDetail mortgageLoanDetail = (MortgageLoanDetail) obj;
			return equals(mortgageLoanDetail);
		}
		return false;
	}

	public String getMortDeedNo() {
    	return mortDeedNo;
    }

	public void setMortDeedNo(String mortDeedNo) {
    	this.mortDeedNo = mortDeedNo;
    }

	public String getMortRegistrationNo() {
    	return mortRegistrationNo;
    }

	public void setMortRegistrationNo(String mortRegistrationNo) {
    	this.mortRegistrationNo = mortRegistrationNo;
    }

	public BigDecimal getMortAreaSF() {
    	return mortAreaSF;
    }

	public void setMortAreaSF(BigDecimal mortAreaSF) {
    	this.mortAreaSF = mortAreaSF;
    }

	public BigDecimal getMortAreaSM() {
    	return mortAreaSM;
    }

	public void setMortAreaSM(BigDecimal mortAreaSM) {
    	this.mortAreaSM = mortAreaSM;
    }

	public BigDecimal getMortPricePF() {
    	return mortPricePF;
    }

	public void setMortPricePF(BigDecimal mortPricePF) {
    	this.mortPricePF = mortPricePF;
    }

	public int getMortAge() {
    	return mortAge;
    }

	public void setMortAge(int mortAge) {
    	this.mortAge = mortAge;
    }

	public BigDecimal getMortFinRatio() {
    	return mortFinRatio;
    }

	public void setMortFinRatio(BigDecimal mortFinRatio) {
    	this.mortFinRatio = mortFinRatio;
    }

	public String getMortStatus() {
    	return mortStatus;
    }

	public void setMortStatus(String mortStatus) {
    	this.mortStatus = mortStatus;
    }
	
}
