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
 * * FileName : CollateralSetup.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-12-2016 * * Modified Date
 * : 13-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.pff.verification.model.Verification;

/**
 * Model class for the <b>CollateralSetup table</b>.<br>
 * 
 */
@XmlType(propOrder = { "depositorCif", "collateralRef", "collateralType", "collateralCcy", "maxCollateralValue",
		"specialLTV", "collateralLoc", "valuator", "expiryDate", "reviewFrequency", "nextReviewDate",
		"multiLoanAssignment", "thirdPartyAssignment", "remarks", "collateralThirdPartyList", "extendedDetails",
		"coOwnerDetailList", "documents", "assignmentReference", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "CollateralSetup")
public class CollateralSetup extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private String collateralRef;
	@XmlElement
	private String finReference;
	@XmlElement(name = "cif")
	private String depositorCif;

	private long depositorId;
	private String depositorName;

	@XmlElement
	private String collateralType;
	private String collateralTypeName;
	@XmlElement
	private String collateralCcy;
	private BigDecimal collateralValue = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal maxCollateralValue = BigDecimal.ZERO;

	private BigDecimal bankLTV = BigDecimal.ZERO;
	@XmlElement(name = "splLtv")
	private BigDecimal specialLTV = BigDecimal.ZERO;
	private BigDecimal bankValuation = BigDecimal.ZERO;

	@XmlElement
	private String collateralLoc;
	@XmlElement
	private String valuator;
	@XmlElement
	private Date expiryDate;

	@XmlElement(name = "rvwFrq")
	private String reviewFrequency;

	@XmlElement
	private Date nextReviewDate;

	@XmlElement(name = "alwMultiLoanAssign")
	private boolean multiLoanAssignment;

	@XmlElement(name = "alwThirdPartyAssign")
	private boolean thirdPartyAssignment;

	private boolean fromLoan;

	@XmlElement
	private String remarks;
	private String lovValue;

	private CollateralSetup befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private long createdBy;
	private Timestamp createdOn;
	private String status;

	private CollateralStructure collateralStructure = null;
	private CustomerDetails customerDetails;

	@XmlElementWrapper(name = "coOwnerDetails")
	@XmlElement(name = "coOwnerDetail")
	private List<CoOwnerDetail> coOwnerDetailList = null;

	@XmlElementWrapper(name = "thirdParties")
	@XmlElement(name = "thirdParty")
	private List<CollateralThirdParty> collateralThirdPartyList = null;
	private List<AssignmentDetails> assignmentDetails = null;
	private List<FinanceReferenceDetail> aggrements = null;
	private List<FinanceReferenceDetail> checkLists = null;
	private List<FinanceCheckListReference> collateralCheckLists = null;
	private Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>(1);

	@XmlElementWrapper(name = "documents")
	@XmlElement(name = "document")
	private List<DocumentDetails> documents = null;
	@XmlElement
	private List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private List<FinFlagsDetail> finFlagsDetailsList;

	// API specific
	@XmlElementWrapper(name = "extendedDetails")
	@XmlElement(name = "extendedDetail")
	private List<ExtendedField> extendedDetails = null;

	@XmlElement
	private WSReturnStatus returnStatus;
	private String sourceId;

	@XmlElement
	private String assignmentReference;
	// CERSAI Fields
	private Date registrationDate;
	private Date modificationDate;
	private Date satisfactionDate;
	private String regStatus;
	private boolean modified = false;
	private Long assetId = null;
	private Long siId = null;
	@XmlElement
	private String areaLocalityName = "";
	@XmlElement
	private String stateName = "";
	@XmlElement
	private String cityName = "";

	@XmlElement
	private List<Verification> VerificationList;

	public CollateralSetup() {
		super();
	}

	public CollateralSetup(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("collateralTypeName");
		excludeFields.add("collateralCcyName");
		excludeFields.add("customerDetails");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("aggrements");
		excludeFields.add("checkLists");
		excludeFields.add("collateralCheckLists");
		excludeFields.add("selAnsCountMap");
		excludeFields.add("documents");
		excludeFields.add("depositorCif");
		excludeFields.add("depositorName");
		excludeFields.add("collateralStructure");
		excludeFields.add("returnStatus");
		excludeFields.add("sourceId");
		excludeFields.add("extendedDetails");
		excludeFields.add("finFlagsDetails");
		excludeFields.add("assignmentDetails");
		excludeFields.add("fromLoan");
		excludeFields.add("assignmentReference");
		excludeFields.add("registrationDate");
		excludeFields.add("modificationDate");
		excludeFields.add("satisfactionDate");
		excludeFields.add("regStatus");
		excludeFields.add("modified");
		excludeFields.add("assetId");
		excludeFields.add("siId");
		excludeFields.add("VerificationList");
		excludeFields.add("areaLocalityName");
		excludeFields.add("stateName");
		excludeFields.add("cityName");
		return excludeFields;
	}

	public String getId() {
		return collateralRef;
	}

	public void setId(String id) {
		this.collateralRef = id;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getDepositorCif() {
		return depositorCif;
	}

	public void setDepositorCif(String depositorCif) {
		this.depositorCif = depositorCif;
	}

	public long getDepositorId() {
		return depositorId;
	}

	public void setDepositorId(long depositorId) {
		this.depositorId = depositorId;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public String getCollateralTypeName() {
		return this.collateralTypeName;
	}

	public void setCollateralTypeName(String collateralTypeName) {
		this.collateralTypeName = collateralTypeName;
	}

	public String getCollateralCcy() {
		return collateralCcy;
	}

	public void setCollateralCcy(String collateralCcy) {
		this.collateralCcy = collateralCcy;
	}

	public BigDecimal getMaxCollateralValue() {
		return maxCollateralValue;
	}

	public void setMaxCollateralValue(BigDecimal maxCollateralValue) {
		this.maxCollateralValue = maxCollateralValue;
	}

	public BigDecimal getSpecialLTV() {
		return specialLTV;
	}

	public void setSpecialLTV(BigDecimal specialLTV) {
		this.specialLTV = specialLTV;
	}

	public String getCollateralLoc() {
		return collateralLoc;
	}

	public void setCollateralLoc(String collateralLoc) {
		this.collateralLoc = collateralLoc;
	}

	public String getValuator() {
		return valuator;
	}

	public void setValuator(String valuator) {
		this.valuator = valuator;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getReviewFrequency() {
		return reviewFrequency;
	}

	public void setReviewFrequency(String reviewFrequency) {
		this.reviewFrequency = reviewFrequency;
	}

	public Date getNextReviewDate() {
		return nextReviewDate;
	}

	public void setNextReviewDate(Date nextReviewDate) {
		this.nextReviewDate = nextReviewDate;
	}

	public boolean isMultiLoanAssignment() {
		return multiLoanAssignment;
	}

	public void setMultiLoanAssignment(boolean multiLoanAssignment) {
		this.multiLoanAssignment = multiLoanAssignment;
	}

	public boolean isThirdPartyAssignment() {
		return thirdPartyAssignment;
	}

	public void setThirdPartyAssignment(boolean thirdPartyAssignment) {
		this.thirdPartyAssignment = thirdPartyAssignment;
	}

	public boolean isFromLoan() {
		return fromLoan;
	}

	public void setFromLoan(boolean fromLoan) {
		this.fromLoan = fromLoan;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CollateralSetup getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CollateralSetup beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public List<CoOwnerDetail> getCoOwnerDetailList() {
		return coOwnerDetailList;
	}

	public void setCoOwnerDetailList(List<CoOwnerDetail> coOwnerDetailList) {
		this.coOwnerDetailList = coOwnerDetailList;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public List<FinanceReferenceDetail> getAggrements() {
		return aggrements;
	}

	public void setAggrements(List<FinanceReferenceDetail> aggrements) {
		this.aggrements = aggrements;
	}

	public List<FinanceReferenceDetail> getCheckLists() {
		return checkLists;
	}

	public void setCheckLists(List<FinanceReferenceDetail> checkLists) {
		this.checkLists = checkLists;
	}

	public List<DocumentDetails> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDetails> documents) {
		this.documents = documents;
	}

	public List<FinanceCheckListReference> getCollateralCheckLists() {
		return collateralCheckLists;
	}

	public void setCollateralCheckLists(List<FinanceCheckListReference> collateralCheckLists) {
		this.collateralCheckLists = collateralCheckLists;
	}

	public List<CollateralThirdParty> getCollateralThirdPartyList() {
		return collateralThirdPartyList;
	}

	public void setCollateralThirdPartyList(List<CollateralThirdParty> collateralThirdPartyList) {
		this.collateralThirdPartyList = collateralThirdPartyList;
	}

	public String getDepositorName() {
		return depositorName;
	}

	public void setDepositorName(String depositorName) {
		this.depositorName = depositorName;
	}

	public CollateralStructure getCollateralStructure() {
		return collateralStructure;
	}

	public void setCollateralStructure(CollateralStructure collateralStructure) {
		this.collateralStructure = collateralStructure;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
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

	public List<ExtendedFieldRender> getExtendedFieldRenderList() {
		return extendedFieldRenderList;
	}

	public void setExtendedFieldRenderList(List<ExtendedFieldRender> extendedFieldRenderList) {
		this.extendedFieldRenderList = extendedFieldRenderList;
	}

	public Map<Long, Long> getSelAnsCountMap() {
		return selAnsCountMap;
	}

	public void setSelAnsCountMap(Map<Long, Long> selAnsCountMap) {
		this.selAnsCountMap = selAnsCountMap;
	}

	public List<ExtendedField> getExtendedDetails() {
		return extendedDetails;
	}

	public void setExtendedDetails(List<ExtendedField> extendedDetails) {
		this.extendedDetails = extendedDetails;
	}

	public List<FinFlagsDetail> getFinFlagsDetailsList() {
		return finFlagsDetailsList;
	}

	public void setFinFlagsDetailsList(List<FinFlagsDetail> finFlagsDetailsList) {
		this.finFlagsDetailsList = finFlagsDetailsList;
	}

	public List<AssignmentDetails> getAssignmentDetails() {
		return assignmentDetails;
	}

	public void setAssignmentDetails(List<AssignmentDetails> assignmentDetails) {
		this.assignmentDetails = assignmentDetails;
	}

	public BigDecimal getBankLTV() {
		return bankLTV;
	}

	public void setBankLTV(BigDecimal bankLTV) {
		this.bankLTV = bankLTV;
	}

	public BigDecimal getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(BigDecimal collateralValue) {
		this.collateralValue = collateralValue;
	}

	public BigDecimal getBankValuation() {
		return bankValuation;
	}

	public void setBankValuation(BigDecimal bankValuation) {
		this.bankValuation = bankValuation;
	}

	public void getDeclaredFieldValues(Map<String, Object> collateralSetupMap) {
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "ft_" Should be in small case only, if we want to change the case we need to update the configuration
				// fields as well.
				collateralSetupMap.put("cs_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssignmentReference() {
		return assignmentReference;
	}

	public void setAssignmentReference(String assignmentReference) {
		this.assignmentReference = assignmentReference;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Date getSatisfactionDate() {
		return satisfactionDate;
	}

	public void setSatisfactionDate(Date satisfactionDate) {
		this.satisfactionDate = satisfactionDate;
	}

	public String getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(String regStatus) {
		this.regStatus = regStatus;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	public Long getSiId() {
		return siId;
	}

	public void setSiId(Long siId) {
		this.siId = siId;
	}

	public List<Verification> getVerificationList() {
		return VerificationList;
	}

	public void setVerificationList(List<Verification> verificationList) {
		VerificationList = verificationList;
	}

	public String getAreaLocalityName() {
		return areaLocalityName;
	}

	public void setAreaLocalityName(String areaLocalityName) {
		this.areaLocalityName = areaLocalityName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
}
