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
 * FileName    		:  VASProviderAccDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-09-2018    														*
 *                                                                  						*
 * Modified Date    :  24-09-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-09-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>VASProviderAccDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "id", "providerId", "entityCode", "paymentMode", "bankCode", "bankBranchID", "accountNumber",
		"receivableAdjustment", "reconciliationAmount", "active" })
@XmlAccessorType(XmlAccessType.FIELD)
public class VASProviderAccDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long providerId = 0;
	private String entityCode;
	private String entityDesc;
	private String providerDesc;
	private String paymentMode;
	private String bankCode;
	private String bankName;
	private long bankBranchID = 0;
	private String branchDesc;
	private String accountNumber;
	private String ifscCode;
	private String micrCode;
	private boolean receivableAdjustment;
	private BigDecimal reconciliationAmount = BigDecimal.ZERO;
	private boolean active = true;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private VASProviderAccDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private Long partnerBankId;
	private String partnerBankCode;
	private String partnerBankName;
	private String branchCity;
	private String branchCode;

	public boolean isNew() {
		return isNewRecord();
	}

	public VASProviderAccDetail() {
		super();
	}

	public VASProviderAccDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("providerDesc");
		excludeFields.add("branchDesc");
		excludeFields.add("entityDesc");
		excludeFields.add("bankName");
		excludeFields.add("ifscCode");
		excludeFields.add("micrCode");
		excludeFields.add("bankCode");
		excludeFields.add("partnerBankCode");
		excludeFields.add("partnerBankName");
		excludeFields.add("branchCity");
		excludeFields.add("branchCode");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProviderId() {
		return providerId;
	}

	public void setProviderId(long providerId) {
		this.providerId = providerId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getProviderDesc() {
		return providerDesc;
	}

	public void setProviderDesc(String providerDesc) {
		this.providerDesc = providerDesc;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getMicrCode() {
		return micrCode;
	}

	public void setMicrCode(String micrCode) {
		this.micrCode = micrCode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public long getBankBranchID() {
		return bankBranchID;
	}

	public void setBankBranchID(long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public boolean isReceivableAdjustment() {
		return receivableAdjustment;
	}

	public void setReceivableAdjustment(boolean receivableAdjustment) {
		this.receivableAdjustment = receivableAdjustment;
	}

	public BigDecimal getReconciliationAmount() {
		return reconciliationAmount;
	}

	public void setReconciliationAmount(BigDecimal reconciliationAmount) {
		this.reconciliationAmount = reconciliationAmount;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public VASProviderAccDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(VASProviderAccDetail beforeImage) {
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

	public Long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(Long partnerBankId) {
		this.partnerBankId = partnerBankId;
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

	public String getBranchCity() {
		return branchCity;
	}

	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

}
