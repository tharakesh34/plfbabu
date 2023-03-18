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
package com.pennanttech.pennapps.pff.verification.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Verification table</b>
 *
 */

@XmlAccessorType(XmlAccessType.NONE)
public class Verification extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	@XmlElement
	private long id;
	@XmlElement
	private int verificationType;
	private int module;
	@XmlElement
	private String keyReference;
	@XmlElement
	private String referenceType;
	@XmlElement
	private String reference;
	@XmlElement
	private String referenceFor;
	private Long custId;
	@XmlElement
	private int requestType;
	private Long reinitid;
	@XmlElement
	private Long agency;
	@XmlElement
	private Long reason;
	@XmlElement
	private String remarks;
	private long createdBy;
	private Date createdOn;
	private int status;
	private Long agencyReason;
	private String agencyRemarks;
	private Timestamp verificationDate;
	private int decision;
	private String decisionRemarks;
	private Verification befImage;
	private LoggedInUser userDetails;
	private boolean approveTab;
	private boolean waiveTab;
	@XmlElement(name = "documentType")
	private int docType;
	private int oldRequestType;
	private String docName;
	private CollateralSetup collateralSetup;
	private boolean ignoreFlag;
	private boolean initiated;
	private boolean save;
	@XmlElement
	private String collateralType;

	@XmlElement
	private String cif;
	@XmlElement(name = "name")
	private String customerName;
	private String agencyName;
	private String agencyCity;
	private String reasonName;
	@XmlElementWrapper
	@XmlElement
	private List<LVDocument> lvDocuments = new ArrayList<>();
	private RCUDocument rcuDocument;
	@XmlElementWrapper
	@XmlElement
	private List<RCUDocument> rcuDocuments = new ArrayList<>();
	@XmlElementWrapper
	@XmlElement
	private List<Verification> verifications = new ArrayList<>();
	private FieldInvestigation fieldInvestigation;
	private List<CustomerDetails> customerDetailsList = new ArrayList<>();
	private Long reInitAgency;
	private List<CollateralSetup> collateralSetupList = new ArrayList<>();
	private TechnicalVerification technicalVerification;
	private LegalVerification legalVerification;
	private RiskContainmentUnit rcuVerification;
	private LegalVetting legalVetting;

	private int lastStatus;
	private Date lastVerificationDate;
	private int lastVersion;
	private boolean verificationReq;
	private String lastAgency;
	private long documentId;
	private long docRefId;
	private String verificationStatus;
	private PersonalDiscussion personalDiscussion;

	@XmlElement(name = "accountNumber")
	private String accNumber;
	@XmlElement
	private String bankName;
	// New Field for BHFL.
	@XmlElement
	private int verificationCategory;
	private BigDecimal finalValAsPerPE = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal finalValAmt = BigDecimal.ZERO;
	private String finalValDecision;
	private String finalValRemarks;
	@XmlElement
	private BigDecimal valuationAmount = BigDecimal.ZERO;

	// Value As Per Cost Of Property
	private BigDecimal valueForCOP = BigDecimal.ZERO;
	private String tvRecordStatus;
	private List<LVDocument> vettingDocuments = new ArrayList<>();
	private String city;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private String collRef;

	public Verification() {
		super();
	}

	public Verification(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("cif");
		excludeFields.add("city");
		excludeFields.add("customerName");
		excludeFields.add("agencyName");
		excludeFields.add("reasonName");
		excludeFields.add("verifications");
		excludeFields.add("fieldInvestigation");
		excludeFields.add("applicant");
		excludeFields.add("coApplicant");
		excludeFields.add("reInitAgency");
		excludeFields.add("technicalVerification");
		excludeFields.add("lvDocuments");
		excludeFields.add("legalVerification");
		excludeFields.add("legalVetting");
		excludeFields.add("agencyCity");
		excludeFields.add("approveTab");
		excludeFields.add("docType");
		excludeFields.add("docName");
		excludeFields.add("rcuVerification");
		excludeFields.add("rcuDocuments");
		excludeFields.add("rcuDocument");
		excludeFields.add("waiveTab");
		excludeFields.add("oldRequestType");
		excludeFields.add("collateralSetup");
		excludeFields.add("ignoreFlag");
		excludeFields.add("verificationReq");
		excludeFields.add("lastStatus");
		excludeFields.add("lastVersion");
		excludeFields.add("lastVerificationDate");
		excludeFields.add("lastAgency");
		excludeFields.add("documentId");
		excludeFields.add("docRefId");
		excludeFields.add("initiated");
		excludeFields.add("verificationStatus");
		excludeFields.add("save");
		excludeFields.add("personalDiscussion");
		excludeFields.add("finalValAsPerPE");
		excludeFields.add("finalValAmt");
		excludeFields.add("finalValDecision");
		excludeFields.add("finalValRemarks");
		excludeFields.add("valuationAmount");
		excludeFields.add("valueForCOP");
		excludeFields.add("tvRecordStatus");
		excludeFields.add("accNumber");
		excludeFields.add("bankName");
		excludeFields.add("collateralType");
		excludeFields.add("vettingDocuments");
		excludeFields.add("returnStatus");
		excludeFields.add("collRef");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVerificationType() {
		return verificationType;
	}

	public void setVerificationType(int verificationType) {
		this.verificationType = verificationType;
	}

	public int getModule() {
		return module;
	}

	public void setModule(int module) {
		this.module = module;
	}

	public String getKeyReference() {
		return keyReference;
	}

	public void setKeyReference(String keyReference) {
		this.keyReference = keyReference;
	}

	public String getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getReferenceFor() {
		return referenceFor;
	}

	public void setReferenceFor(String referenceFor) {
		this.referenceFor = referenceFor;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public Long getReinitid() {
		return reinitid;
	}

	public void setReinitid(Long reinitid) {
		this.reinitid = reinitid;
	}

	public Long getAgency() {
		return agency;
	}

	public void setAgency(Long agency) {
		this.agency = agency;
	}

	public String getAgencyName() {
		return this.agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public Long getReason() {
		return reason;
	}

	public void setReason(Long reason) {
		this.reason = reason;
	}

	public String getReasonName() {
		return this.reasonName;
	}

	public void setReasonName(String reasonName) {
		this.reasonName = reasonName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAgencyRemarks() {
		return agencyRemarks;
	}

	public void setAgencyRemarks(String agencyRemarks) {
		this.agencyRemarks = agencyRemarks;
	}

	public Long getAgencyReason() {
		return agencyReason;
	}

	public void setAgencyReason(Long agencyReason) {
		this.agencyReason = agencyReason;
	}

	public int getDecision() {
		return decision;
	}

	public void setDecision(int decision) {
		this.decision = decision;
	}

	public Timestamp getVerificationDate() {
		return verificationDate;
	}

	public void setVerificationDate(Timestamp verificationDate) {
		this.verificationDate = verificationDate;
	}

	public String getDecisionRemarks() {
		return decisionRemarks;
	}

	public void setDecisionRemarks(String decisionRemarks) {
		this.decisionRemarks = decisionRemarks;
	}

	public Verification getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Verification beforeImage) {
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

	public FieldInvestigation getFieldInvestigation() {
		return fieldInvestigation;
	}

	public void setFieldInvestigation(FieldInvestigation fieldInvestigation) {
		this.fieldInvestigation = fieldInvestigation;
	}

	public List<CustomerDetails> getCustomerDetailsList() {
		return customerDetailsList;
	}

	public void setCustomerDetailsList(List<CustomerDetails> customerDetailsList) {
		this.customerDetailsList = customerDetailsList;
	}

	public List<Verification> getVerifications() {
		return verifications;
	}

	public void setVerifications(List<Verification> verifications) {
		this.verifications = verifications;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public Long getReInitAgency() {
		return reInitAgency;
	}

	public void setReInitAgency(Long reInitAgency) {
		this.reInitAgency = reInitAgency;
	}

	public List<CollateralSetup> getCollateralSetupList() {
		return collateralSetupList;
	}

	public void setCollateralSetupList(List<CollateralSetup> collateralSetupList) {
		this.collateralSetupList = collateralSetupList;
	}

	public TechnicalVerification getTechnicalVerification() {
		return technicalVerification;
	}

	public void setTechnicalVerification(TechnicalVerification technicalVerification) {
		this.technicalVerification = technicalVerification;
	}

	public List<LVDocument> getLvDocuments() {
		return lvDocuments;
	}

	public void setLvDocuments(List<LVDocument> lvDocuments) {
		this.lvDocuments = lvDocuments;
	}

	public LegalVerification getLegalVerification() {
		return legalVerification;
	}

	public void setLegalVerification(LegalVerification legalVerification) {
		this.legalVerification = legalVerification;
	}

	public String getAgencyCity() {
		return agencyCity;
	}

	public void setAgencyCity(String agencyCity) {
		this.agencyCity = agencyCity;
	}

	public boolean isApproveTab() {
		return approveTab;
	}

	public void setApproveTab(boolean approveTab) {
		this.approveTab = approveTab;
	}

	public int getDocType() {
		return docType;
	}

	public void setDocType(int docType) {
		this.docType = docType;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public RiskContainmentUnit getRcuVerification() {
		return rcuVerification;
	}

	public void setRcuVerification(RiskContainmentUnit rcuVerification) {
		this.rcuVerification = rcuVerification;
	}

	public List<RCUDocument> getRcuDocuments() {
		return rcuDocuments;
	}

	public void setRcuDocuments(List<RCUDocument> rcuDocuments) {
		this.rcuDocuments = rcuDocuments;
	}

	public RCUDocument getRcuDocument() {
		return rcuDocument;
	}

	public void setRcuDocument(RCUDocument rcuDocument) {
		this.rcuDocument = rcuDocument;
	}

	public boolean isWaiveTab() {
		return waiveTab;
	}

	public void setWaiveTab(boolean waiveTab) {
		this.waiveTab = waiveTab;
	}

	public int getOldRequestType() {
		return oldRequestType;
	}

	public void setOldRequestType(int oldRequestType) {
		this.oldRequestType = oldRequestType;
	}

	public int getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(int lastStatus) {
		this.lastStatus = lastStatus;
	}

	public Date getLastVerificationDate() {
		return lastVerificationDate;
	}

	public void setLastVerificationDate(Date lastVerificationDate) {
		this.lastVerificationDate = lastVerificationDate;
	}

	public CollateralSetup getCollateralSetup() {
		return collateralSetup;
	}

	public void setCollateralSetup(CollateralSetup collateralSetup) {
		this.collateralSetup = collateralSetup;
	}

	public boolean isIgnoreFlag() {
		return ignoreFlag;
	}

	public void setIgnoreFlag(boolean ignoreFlag) {
		this.ignoreFlag = ignoreFlag;
	}

	public int getLastVersion() {
		return lastVersion;
	}

	public void setLastVersion(int lastVersion) {
		this.lastVersion = lastVersion;
	}

	public boolean isVerificationReq() {
		return verificationReq;
	}

	public void setVerificationReq(boolean verificationReq) {
		this.verificationReq = verificationReq;
	}

	public String getLastAgency() {
		return lastAgency;
	}

	public void setLastAgency(String lastAgency) {
		this.lastAgency = lastAgency;
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(long docRefId) {
		this.docRefId = docRefId;
	}

	public boolean isInitiated() {
		return initiated;
	}

	public void setInitiated(boolean initiated) {
		this.initiated = initiated;
	}

	public String getVerificationStatus() {
		return verificationStatus;
	}

	public void setVerificationStatus(String verificationStatus) {
		this.verificationStatus = verificationStatus;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
	}

	public PersonalDiscussion getPersonalDiscussion() {
		return personalDiscussion;
	}

	public void setPersonalDiscussion(PersonalDiscussion personalDiscussion) {
		this.personalDiscussion = personalDiscussion;
	}

	public BigDecimal getFinalValAsPerPE() {
		return finalValAsPerPE;
	}

	public void setFinalValAsPerPE(BigDecimal finalValAsPerPE) {
		this.finalValAsPerPE = finalValAsPerPE;
	}

	public BigDecimal getFinalValAmt() {
		return finalValAmt;
	}

	public void setFinalValAmt(BigDecimal finalValAmt) {
		this.finalValAmt = finalValAmt;
	}

	public String getFinalValDecision() {
		return finalValDecision;
	}

	public void setFinalValDecision(String finalValDecision) {
		this.finalValDecision = finalValDecision;
	}

	public String getFinalValRemarks() {
		return finalValRemarks;
	}

	public void setFinalValRemarks(String finalValRemarks) {
		this.finalValRemarks = finalValRemarks;
	}

	public int getVerificationCategory() {
		return verificationCategory;
	}

	public void setVerificationCategory(int verificationCategory) {
		this.verificationCategory = verificationCategory;
	}

	public BigDecimal getValuationAmount() {
		return valuationAmount;
	}

	public void setValuationAmount(BigDecimal valuationAmount) {
		this.valuationAmount = valuationAmount;
	}

	public BigDecimal getValueForCOP() {
		return valueForCOP;
	}

	public void setValueForCOP(BigDecimal valueForCOP) {
		this.valueForCOP = valueForCOP;
	}

	public String getTvRecordStatus() {
		return tvRecordStatus;
	}

	public void setTvRecordStatus(String tvRecordStatus) {
		this.tvRecordStatus = tvRecordStatus;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public LegalVetting getLegalVetting() {
		return legalVetting;
	}

	public void setLegalVetting(LegalVetting legalVetting) {
		this.legalVetting = legalVetting;
	}

	public List<LVDocument> getVettingDocuments() {
		return vettingDocuments;
	}

	public void setVettingDocuments(List<LVDocument> vettingDocuments) {
		this.vettingDocuments = vettingDocuments;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getCollRef() {
		return collRef;
	}

	public void setCollRef(String collRef) {
		this.collRef = collRef;
	}

}
