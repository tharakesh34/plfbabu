/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinTypePartnerBank.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * * Modified
 * Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.rmtmasters;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinTypePartnerBank table</b>.<br>
 * 
 */
@XmlType(propOrder = { "purpose", "paymentMode", "partnerBankID" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinTypePartnerBank extends AbstractWorkflowEntity {
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
	private String issuingBankCode;
	private String issuingBankName;
	private String printingLoc;
	private String printingLocDesc;
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
	private String branchCode;
	private String branchDesc;
	private String clusterCode;
	private String name;
	private Long clusterId;
	private String clusterType;
	private String entityCode;
	private String suspenseAc;
	private long finID;
	private String favourName;
	private String payableLoc;
	private String divisionCode;

	public FinTypePartnerBank() {
		super();
	}

	public FinTypePartnerBank(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finTypeDesc");
		excludeFields.add("partnerBankCode");
		excludeFields.add("partnerBankName");
		excludeFields.add("issuingBankCode");
		excludeFields.add("issuingBankName");
		excludeFields.add("printingLoc");
		excludeFields.add("printingLocDesc");
		excludeFields.add("accountNo");
		excludeFields.add("accountType");
		excludeFields.add("sponsorBankCode");
		excludeFields.add("clientCode");
		excludeFields.add("utilityCode");
		excludeFields.add("branchDesc");
		excludeFields.add("name");
		excludeFields.add("clusterCode");
		excludeFields.add("clusterType");
		excludeFields.add("entityCode");
		excludeFields.add("suspenseAc");
		excludeFields.add("finID");
		excludeFields.add("favourName");
		excludeFields.add("payableLoc");

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

	public Long getPartnerBankID() {
		return partnerBankID;
	}

	public void setPartnerBankID(Long partnerBankID) {
		this.partnerBankID = partnerBankID;
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

	public String getIssuingBankCode() {
		return issuingBankCode;
	}

	public void setIssuingBankCode(String issuingBankCode) {
		this.issuingBankCode = issuingBankCode;
	}

	public String getIssuingBankName() {
		return issuingBankName;
	}

	public void setIssuingBankName(String issuingBankName) {
		this.issuingBankName = issuingBankName;
	}

	public String getPrintingLoc() {
		return printingLoc;
	}

	public void setPrintingLoc(String printingLoc) {
		this.printingLoc = printingLoc;
	}

	public String getPrintingLocDesc() {
		return printingLocDesc;
	}

	public void setPrintingLocDesc(String printingLocDesc) {
		this.printingLocDesc = printingLocDesc;
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

	public String getClusterCode() {
		return clusterCode;
	}

	public void setClusterCode(String clusterCode) {
		this.clusterCode = clusterCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getClusterId() {
		return clusterId;
	}

	public void setClusterId(Long clusterId) {
		this.clusterId = clusterId;
	}

	public String getClusterType() {
		return clusterType;
	}

	public void setClusterType(String clusterType) {
		this.clusterType = clusterType;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getSuspenseAc() {
		return suspenseAc;
	}

	public void setSuspenseAc(String suspenseAc) {
		this.suspenseAc = suspenseAc;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFavourName() {
		return favourName;
	}

	public void setFavourName(String favourName) {
		this.favourName = favourName;
	}

	public String getPayableLoc() {
		return payableLoc;
	}

	public void setPayableLoc(String payableLoc) {
		this.payableLoc = payableLoc;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}
	
	public String getMapping() {
		return String.format(
				"[Loan Type - %s] and [Purpose - %s] and [Payment Mode - %s] and [[Branch Code - %s ] or [Cluster Id - %d] ] ",
				this.finType, this.purpose, this.paymentMode, this.branchCode, this.clusterId);
	}
}
