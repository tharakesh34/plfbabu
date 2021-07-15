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
 * FileName    		:  FinTypePartnerBank.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.rmtmasters;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Model class for the <b>FinTypePartnerBank table</b>.<br>
 * 
 */
@XmlType(propOrder = { "purpose", "paymentMode", "partnerBankID" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinTypePartnerBank extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long iD = Long.MIN_VALUE;
	private String finType;
	@XmlElement
	private String purpose;
	@XmlElement
	private String paymentMode;
	@XmlElement(name = "partnerBankId")
	private long partnerBankID;
	private String partnerBankCode;
	private String partnerBankName;
	private boolean newRecord = false;
	private String lovValue;
	private String accountNo;
	private String accountType;
	private FinTypePartnerBank befImage;
	private LoggedInUser userDetails;
	private boolean vanApplicable;

	private String finTypeDesc;
	private String sponsorBankCode;
	private String clientCode;
	private String utilityCode;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinTypePartnerBank() {
		super();
	}

	public FinTypePartnerBank(long id) {
		super();
		this.setId(id);
	}

	public FinTypePartnerBank copyEntity() {
		FinTypePartnerBank entity = new FinTypePartnerBank();
		entity.setID(this.iD);
		entity.setFinType(this.finType);
		entity.setPurpose(this.purpose);
		entity.setPaymentMode(this.paymentMode);
		entity.setPartnerBankID(this.partnerBankID);
		entity.setPartnerBankCode(this.partnerBankCode);
		entity.setPartnerBankName(this.partnerBankName);
		entity.setNewRecord(this.newRecord);
		entity.setLovValue(this.lovValue);
		entity.setAccountNo(this.accountNo);
		entity.setAccountType(this.accountType);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setVanApplicable(this.vanApplicable);
		entity.setFinTypeDesc(this.finTypeDesc);
		entity.setSponsorBankCode(this.sponsorBankCode);
		entity.setClientCode(this.clientCode);
		entity.setUtilityCode(this.utilityCode);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finTypeDesc");
		excludeFields.add("partnerBankCode");
		excludeFields.add("partnerBankName");
		excludeFields.add("accountNo");
		excludeFields.add("accountType");
		excludeFields.add("sponsorBankCode");
		excludeFields.add("clientCode");
		excludeFields.add("utilityCode");
		return excludeFields;
	}

	public long getId() {
		return iD;
	}

	public void setId(long id) {
		this.iD = id;
	}

	public long getID() {
		return iD;
	}

	public void setID(long iD) {
		this.iD = iD;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public long getPartnerBankID() {
		return partnerBankID;
	}

	public void setPartnerBankID(long partnerBankID) {
		this.partnerBankID = partnerBankID;
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

	public FinTypePartnerBank getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinTypePartnerBank beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public boolean isVanApplicable() {
		return vanApplicable;
	}

	public void setVanApplicable(boolean vanApplicable) {
		this.vanApplicable = vanApplicable;
	}

	public String getSponsorBankCode() {
		return sponsorBankCode;
	}

	public void setSponsorBankCode(String sponsorBankCode) {
		this.sponsorBankCode = sponsorBankCode;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getUtilityCode() {
		return utilityCode;
	}

	public void setUtilityCode(String utilityCode) {
		this.utilityCode = utilityCode;
	}

}
