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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Verification table</b>.<br>
 *
 */
public class Verification extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private int verificationType;
	private int module;
	private String keyReference;
	private String referenceType;
	private String reference;
	private String referenceFor;
	private Long custId;
	private int requestType;
	private Long reinitid;
	private Long agency;
	private Long reason;
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

	private String cif;
	private String customerName;
	private String agencyName;
	private String reasonName;

	private List<Verification> verifications = new ArrayList<>();
	private FieldInvestigation fieldInvestigation;
	private List<CustomerDetails> customerDetailsList = new ArrayList<>();
	private boolean newRecord = false;
	private Long reInitAgency;
	private String reInitRemarks;
	private List<CollateralSetup> collateralSetupList = new ArrayList<>();
	private TechnicalVerification technicalVerification;

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
		excludeFields.add("customerName");
		excludeFields.add("agencyName");
		excludeFields.add("reasonName");
		excludeFields.add("verifications");
		excludeFields.add("fieldInvestigation");
		excludeFields.add("applicant");
		excludeFields.add("coApplicant");
		excludeFields.add("reInitAgency");
		excludeFields.add("reInitRemarks");
		excludeFields.add("technicalVerification");
		excludeFields.add("collaterals");
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public Long getReInitAgency() {
		return reInitAgency;
	}

	public void setReInitAgency(Long reInitAgency) {
		this.reInitAgency = reInitAgency;
	}

	public String getReInitRemarks() {
		return reInitRemarks;
	}

	public void setReInitRemarks(String reInitRemarks) {
		this.reInitRemarks = reInitRemarks;
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

}
