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
 * FileName    		:  Beneficiary.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2016    														*
 *                                                                  						*
 * Modified Date    :  01-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.model.beneficiary;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Beneficiary table</b>.<br>
 * 
 */

@XmlType(propOrder = { "custCIF", "beneficiaryId", "bankCode", "branchCode", "iFSC", "accNumber", "accHolderName",
		"phoneCountryCode", "phoneAreaCode", "phoneNumber", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class Beneficiary extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "beneficiaryID")
	private long beneficiaryId = Long.MIN_VALUE;
	private long custID;
	@XmlElement(name = "cif")
	private String custCIF;
	private String custShrtName;
	private long bankBranchID;
	@XmlElement
	private String branchCode;
	private String branchDesc;
	@XmlElement
	private String bankCode;
	private String bankName;
	private String city;
	@XmlElement(name = "ifsc")
	private String iFSC;
	@XmlElement(name = "accountNo")
	private String accNumber;
	@XmlElement(name = "acHolderName")
	private String accHolderName;
	@XmlElement
	private String phoneCountryCode;
	@XmlElement
	private String phoneAreaCode;
	@XmlElement
	private String phoneNumber;
	private String email;
	private boolean newRecord;
	private String lovValue;
	private Beneficiary befImage;
	private LoggedInUser userDetails;
	private Beneficiary validateBeneficiary = this;
	@XmlElement
	private WSReturnStatus returnStatus;
	private String sourceId;
	
	private boolean beneficiaryActive;
	private boolean defaultBeneficiary;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public Beneficiary() {
		super();
	}

	public Beneficiary(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("branchCode");
		excludeFields.add("branchDesc");
		excludeFields.add("bankCode");
		excludeFields.add("bankName");
		excludeFields.add("city");
		excludeFields.add("iFSC");
		excludeFields.add("returnStatus");
		excludeFields.add("sourceId");
		excludeFields.add("validateBeneficiary");
		return excludeFields;
	}

	public long getId() {
		return beneficiaryId;
	}

	public void setId(long id) {
		this.beneficiaryId = id;
	}

	public long getBeneficiaryId() {
		return beneficiaryId;
	}

	public void setBeneficiaryId(long beneficiaryId) {
		this.beneficiaryId = beneficiaryId;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public long getBankBranchID() {
		return bankBranchID;
	}

	public void setBankBranchID(long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getAccHolderName() {
		return accHolderName;
	}

	public void setAccHolderName(String accHolderName) {
		this.accHolderName = accHolderName;
	}

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getPhoneAreaCode() {
		return phoneAreaCode;
	}

	public void setPhoneAreaCode(String phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public Beneficiary getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Beneficiary beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public Beneficiary getValidateBeneficiary() {
		return validateBeneficiary;
	}

	public void setValidateBeneficiary(Beneficiary validateBeneficiary) {
		this.validateBeneficiary = validateBeneficiary;
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

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getiFSC() {
		return iFSC;
	}

	public void setiFSC(String iFSC) {
		this.iFSC = iFSC;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public boolean isBeneficiaryActive() {
		return beneficiaryActive;
	}

	public void setBeneficiaryActive(boolean beneficiaryActive) {
		this.beneficiaryActive = beneficiaryActive;
	}

	public boolean isDefaultBeneficiary() {
		return defaultBeneficiary;
	}

	public void setDefaultBeneficiary(boolean defaultBeneficiary) {
		this.defaultBeneficiary = defaultBeneficiary;
	}
	
	

}
