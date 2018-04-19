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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FieldInvestigation table</b>.<br>
 *
 */
public class FieldInvestigation extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String cif;
	private String keyReference;
	private String name;
	private String addressType;
	private String houseNumber;
	private String flatNumber;
	private String street;
	private String addressLine1;
	private String addressLine2;
	private long verificationId;
	private String addressLine3;
	private String addressLine4;
	private String addressLine5;
	private String poBox;
	private String country;
	private String province;
	private String city;
	private String zipCode;
	private String contactNumber1;
	private String contactNumber2;

	private Date date;
	private int type;
	private int yearsAtPresentAddress;
	private String personMet;
	private int ownershipStatus;
	private String relationship;
	private int neighbourhoodFeedBack;
	private String contactNumber;
	private String observationRemarks;
	private int livingStandard;
	private boolean negativeCheck;
	private int noofAttempts;

	private String agentCode;
	private String agentName;
	private int status;
	private Long reason;
	private String summaryRemarks;

	private String statusName;
	private String reasonName;
	private Date createdOn;
	private String reasonCode;
	private String reasonDesc;
	private String lovrelationdesc;
	private Long custId;
	private Long agencyId;
	private String agencyName;
	private List<DocumentDetails> documents					= null;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private FieldInvestigation befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	

	public FieldInvestigation() {
		super();
	}

	public FieldInvestigation(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("statusName");
		excludeFields.add("reasonName");
		excludeFields.add("keyReference");
		excludeFields.add("cif");
		excludeFields.add("createdOn");
		excludeFields.add("reasonCode");
		excludeFields.add("reasonDesc");
		excludeFields.add("lovrelationdesc");
		excludeFields.add("custId");
		excludeFields.add("agencyId");
		excludeFields.add("agencyName");
		excludeFields.add("documents");
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return verificationId;
	}

	public void setId(long id) {
		this.verificationId = id;
	}

	public long getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(long verificationId) {
		this.verificationId = verificationId;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getReason() {
		return reason;
	}

	public void setReason(Long reason) {
		this.reason = reason;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getObservationRemarks() {
		return observationRemarks;
	}

	public void setObservationRemarks(String observationRemarks) {
		this.observationRemarks = observationRemarks;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getFlatNumber() {
		return flatNumber;
	}

	public void setFlatNumber(String flatNumber) {
		this.flatNumber = flatNumber;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine4() {
		return addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getAddressLine5() {
		return addressLine5;
	}

	public void setAddressLine5(String addressLine5) {
		this.addressLine5 = addressLine5;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getContactNumber1() {
		return contactNumber1;
	}

	public void setContactNumber1(String contactNumber1) {
		this.contactNumber1 = contactNumber1;
	}

	public String getContactNumber2() {
		return contactNumber2;
	}

	public void setContactNumber2(String contactNumber2) {
		this.contactNumber2 = contactNumber2;
	}

	public String getPoBox() {
		return poBox;
	}

	public void setPoBox(String poBox) {
		this.poBox = poBox;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public int getOwnershipStatus() {
		return ownershipStatus;
	}

	public void setOwnershipStatus(int ownershipStatus) {
		this.ownershipStatus = ownershipStatus;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getYearsAtPresentAddress() {
		return yearsAtPresentAddress;
	}

	public void setYearsAtPresentAddress(int yearsAtPresentAddress) {
		this.yearsAtPresentAddress = yearsAtPresentAddress;
	}

	public String getPersonMet() {
		return personMet;
	}

	public void setPersonMet(String personMet) {
		this.personMet = personMet;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public int getNeighbourhoodFeedBack() {
		return neighbourhoodFeedBack;
	}

	public void setNeighbourhoodFeedBack(int neighbourhoodFeedBack) {
		this.neighbourhoodFeedBack = neighbourhoodFeedBack;
	}

	public int getLivingStandard() {
		return livingStandard;
	}

	public void setLivingStandard(int livingStandard) {
		this.livingStandard = livingStandard;
	}

	public boolean isNegativeCheck() {
		return negativeCheck;
	}

	public void setNegativeCheck(boolean negativeCheck) {
		this.negativeCheck = negativeCheck;
	}

	public int getNoofAttempts() {
		return noofAttempts;
	}

	public void setNoofAttempts(int noofAttempts) {
		this.noofAttempts = noofAttempts;
	}

	public String getSummaryRemarks() {
		return summaryRemarks;
	}

	public void setSummaryRemarks(String summaryRemarks) {
		this.summaryRemarks = summaryRemarks;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getReasonName() {
		return reasonName;
	}

	public void setReasonName(String reasonName) {
		this.reasonName = reasonName;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getKeyReference() {
		return keyReference;
	}

	public void setKeyReference(String keyReference) {
		this.keyReference = keyReference;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getReasonDesc() {
		return reasonDesc;
	}

	public void setReasonDesc(String reasonDesc) {
		this.reasonDesc = reasonDesc;
	}

	public String getLovrelationdesc() {
		return lovrelationdesc;
	}

	public void setLovrelationdesc(String lovrelationdesc) {
		this.lovrelationdesc = lovrelationdesc;
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

	public FieldInvestigation getBefImage() {
		return befImage;
	}

	public void setBefImage(FieldInvestigation befImage) {
		this.befImage = befImage;
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

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public Long getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(Long agencyId) {
		this.agencyId = agencyId;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public List<DocumentDetails> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDetails> documents) {
		this.documents = documents;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
	
}
