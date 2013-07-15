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
 * FileName    		:  CorporateCustomerDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CorporateCustomerDetail table</b>.<br>
 *
 */
public class CorporateCustomerDetail implements java.io.Serializable, Entity {
	
	private static final long serialVersionUID = -8247646523739076607L;
	
	private long custId = Long.MIN_VALUE;
	private String name;
	private String phoneNumber;
	private String phoneNumber1;
	private String emailId;
	private Date bussCommenceDate;
	private Date servCommenceDate;
	private Date bankRelationshipDate;
	private BigDecimal paidUpCapital;
	private BigDecimal authorizedCapital;
	private BigDecimal reservesAndSurPlus;
	private BigDecimal intangibleAssets;
	private BigDecimal tangibleNetWorth;
	private BigDecimal longTermLiabilities;
	private BigDecimal capitalEmployed;
	private BigDecimal investments;
	private BigDecimal nonCurrentAssets;
	private BigDecimal netWorkingCapital;
	private BigDecimal netSales;
	private BigDecimal otherIncome;
	private BigDecimal netProfitAfterTax;
	private BigDecimal depreciation;
	private BigDecimal cashAccurals;
	private BigDecimal annualTurnover;
	private BigDecimal returnOnCapitalEmp;
	private BigDecimal currentAssets;
	private BigDecimal currentLiabilities;
	private BigDecimal currentBookValue;
	private BigDecimal currentMarketValue;
	private BigDecimal promotersShare;
	private BigDecimal associatesShare;
	private BigDecimal publicShare;
	private BigDecimal finInstShare;
	private BigDecimal others;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;	
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private CorporateCustomerDetail befImage;
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

	public CorporateCustomerDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CorporateCustomerDetail");
	}

	public CorporateCustomerDetail(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return custId;
	}
	public void setId (long id) {
		this.custId = id;
	}
	
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber1() {
		return phoneNumber1;
	}
	public void setPhoneNumber1(String phoneNumber1) {
		this.phoneNumber1 = phoneNumber1;
	}

	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	public Date getBussCommenceDate() {
		return bussCommenceDate;
	}
	public void setBussCommenceDate(Date bussCommenceDate) {
		this.bussCommenceDate = bussCommenceDate;
	}
	
	public Date getServCommenceDate() {
		return servCommenceDate;
	}
	public void setServCommenceDate(Date servCommenceDate) {
		this.servCommenceDate = servCommenceDate;
	}
	
	public Date getBankRelationshipDate() {
		return bankRelationshipDate;
	}
	public void setBankRelationshipDate(Date bankRelationshipDate) {
		this.bankRelationshipDate = bankRelationshipDate;
	}
	
	public BigDecimal getPaidUpCapital() {
		return paidUpCapital;
	}
	public void setPaidUpCapital(BigDecimal paidUpCapital) {
		this.paidUpCapital = paidUpCapital;
	}
	
	public BigDecimal getAuthorizedCapital() {
		return authorizedCapital;
	}
	public void setAuthorizedCapital(BigDecimal authorizedCapital) {
		this.authorizedCapital = authorizedCapital;
	}
	
	public BigDecimal getReservesAndSurPlus() {
		return reservesAndSurPlus;
	}
	public void setReservesAndSurPlus(BigDecimal reservesAndSurPlus) {
		this.reservesAndSurPlus = reservesAndSurPlus;
	}
	
	public BigDecimal getIntangibleAssets() {
		return intangibleAssets;
	}
	public void setIntangibleAssets(BigDecimal intangibleAssets) {
		this.intangibleAssets = intangibleAssets;
	}
	
	public BigDecimal getTangibleNetWorth() {
		return tangibleNetWorth;
	}
	public void setTangibleNetWorth(BigDecimal tangibleNetWorth) {
		this.tangibleNetWorth = tangibleNetWorth;
	}
	
	public BigDecimal getLongTermLiabilities() {
		return longTermLiabilities;
	}
	public void setLongTermLiabilities(BigDecimal longTermLiabilities) {
		this.longTermLiabilities = longTermLiabilities;
	}
	
	public BigDecimal getCapitalEmployed() {
		return capitalEmployed;
	}
	public void setCapitalEmployed(BigDecimal capitalEmployed) {
		this.capitalEmployed = capitalEmployed;
	}
	
	public BigDecimal getInvestments() {
		return investments;
	}
	public void setInvestments(BigDecimal investments) {
		this.investments = investments;
	}
	
	public BigDecimal getNonCurrentAssets() {
		return nonCurrentAssets;
	}
	public void setNonCurrentAssets(BigDecimal nonCurrentAssets) {
		this.nonCurrentAssets = nonCurrentAssets;
	}
	
	public BigDecimal getNetWorkingCapital() {
		return netWorkingCapital;
	}
	public void setNetWorkingCapital(BigDecimal netWorkingCapital) {
		this.netWorkingCapital = netWorkingCapital;
	}
	
	public BigDecimal getNetSales() {
		return netSales;
	}
	public void setNetSales(BigDecimal netSales) {
		this.netSales = netSales;
	}
	
	public BigDecimal getOtherIncome() {
		return otherIncome;
	}
	public void setOtherIncome(BigDecimal otherIncome) {
		this.otherIncome = otherIncome;
	}
	
	public BigDecimal getNetProfitAfterTax() {
		return netProfitAfterTax;
	}
	public void setNetProfitAfterTax(BigDecimal netProfitAfterTax) {
		this.netProfitAfterTax = netProfitAfterTax;
	}
	
	public BigDecimal getDepreciation() {
		return depreciation;
	}
	public void setDepreciation(BigDecimal depreciation) {
		this.depreciation = depreciation;
	}
	
	public BigDecimal getCashAccurals() {
		return cashAccurals;
	}
	public void setCashAccurals(BigDecimal cashAccurals) {
		this.cashAccurals = cashAccurals;
	}
	
	public BigDecimal getAnnualTurnover() {
		return annualTurnover;
	}
	public void setAnnualTurnover(BigDecimal annualTurnover) {
		this.annualTurnover = annualTurnover;
	}
	
	public BigDecimal getReturnOnCapitalEmp() {
		return returnOnCapitalEmp;
	}
	public void setReturnOnCapitalEmp(BigDecimal returnOnCapitalEmp) {
		this.returnOnCapitalEmp = returnOnCapitalEmp;
	}
	
	public BigDecimal getCurrentAssets() {
		return currentAssets;
	}
	public void setCurrentAssets(BigDecimal currentAssets) {
		this.currentAssets = currentAssets;
	}
	
	public BigDecimal getCurrentLiabilities() {
		return currentLiabilities;
	}
	public void setCurrentLiabilities(BigDecimal currentLiabilities) {
		this.currentLiabilities = currentLiabilities;
	}
	
	public BigDecimal getCurrentBookValue() {
		return currentBookValue;
	}
	public void setCurrentBookValue(BigDecimal currentBookValue) {
		this.currentBookValue = currentBookValue;
	}
	
	public BigDecimal getCurrentMarketValue() {
		return currentMarketValue;
	}
	public void setCurrentMarketValue(BigDecimal currentMarketValue) {
		this.currentMarketValue = currentMarketValue;
	}
	
	public BigDecimal getPromotersShare() {
		return promotersShare;
	}
	public void setPromotersShare(BigDecimal promotersShare) {
		this.promotersShare = promotersShare;
	}
	
	public BigDecimal getAssociatesShare() {
		return associatesShare;
	}
	public void setAssociatesShare(BigDecimal associatesShare) {
		this.associatesShare = associatesShare;
	}
	
	public BigDecimal getPublicShare() {
		return publicShare;
	}
	public void setPublicShare(BigDecimal publicShare) {
		this.publicShare = publicShare;
	}
	
	public BigDecimal getFinInstShare() {
		return finInstShare;
	}
	public void setFinInstShare(BigDecimal finInstShare) {
		this.finInstShare = finInstShare;
	}
	
	public BigDecimal getOthers() {
		return others;
	}
	public void setOthers(BigDecimal others) {
		this.others = others;
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

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
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

	public CorporateCustomerDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CorporateCustomerDetail beforeImage){
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
	public boolean equals(CorporateCustomerDetail corporateCustomerDetail) {
		return getId() == corporateCustomerDetail.getId();
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

		if (obj instanceof CorporateCustomerDetail) {
			CorporateCustomerDetail corporateCustomerDetail = (CorporateCustomerDetail) obj;
			return equals(corporateCustomerDetail);
		}
		return false;
	}
}
