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
import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FieldInvestigation table</b>.<br>
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class TechnicalVerification extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	@XmlElement
	private long verificationId;
	@XmlElement
	private String agentCode;
	@XmlElement
	private String agentName;
	private int type;
	@XmlElement
	private Date verifiedDate;
	@XmlElement(name = "recommendations")
	private int status;
	@XmlElement
	private Long reason;
	@XmlElement(name = "remarks")
	private String summaryRemarks;
	private String sourceFormName;
	private String verificationFormName;
	private String observationRemarks;
	@XmlElement
	private BigDecimal valuationAmount;

	private String reasonCode;
	private String reasonDesc;
	private String agencyName;
	private Timestamp createdOn;
	private String cif;
	private Long custId;
	@XmlElement(name = "name")
	private String custName;
	@XmlElement
	private String keyReference;
	@XmlElement
	private String collateralType;
	@XmlElement
	private String collateralRef;
	private String collateralCcy;
	private String collateralLoc;
	private String contactNumber1;
	private String contactNumber2;
	private String lovrelationdesc;
	private List<DocumentDetails> documents = null;

	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;

	private ExtendedFieldHeader onePagerExtHeader;
	private ExtendedFieldRender onePagerExtRender;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private TechnicalVerification befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private String productCategory;
	private int verificationCategory;

	private String documentName;
	private Long documentRef;
	@XmlElement(name = "docContent")
	private byte[] docImage;
	private String loanType;
	private String lovDescLoanTypeName;
	private String sourcingBranch;
	private String lovDescSourcingBranch;

	@XmlElementWrapper(name = "extendedDetails")
	@XmlElement(name = "extendedDetail")
	private List<ExtendedField> extendedDetails;

	@XmlElement
	private WSReturnStatus returnStatus;
	private String sourceId;

	public TechnicalVerification() {
		super();
	}

	public TechnicalVerification(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("onePagerExtRender");
		excludeFields.add("onePagerExtHeader");
		excludeFields.add("reasonCode");
		excludeFields.add("reasonDesc");
		excludeFields.add("agencyName");
		excludeFields.add("createdOn");
		excludeFields.add("cif");
		excludeFields.add("custId");
		excludeFields.add("custName");
		excludeFields.add("keyReference");
		excludeFields.add("collateralType");
		excludeFields.add("collateralRef");
		excludeFields.add("collateralCcy");
		excludeFields.add("collateralLoc");
		excludeFields.add("contactNumber1");
		excludeFields.add("contactNumber2");
		excludeFields.add("lovrelationdesc");
		excludeFields.add("documents");
		excludeFields.add("docImage");
		excludeFields.add("productCategory");
		excludeFields.add("verificationCategory");
		excludeFields.add("loanType");
		excludeFields.add("lovDescLoanTypeName");
		excludeFields.add("sourcingBranch");
		excludeFields.add("lovDescSourcingBranch");
		excludeFields.add("extendedDetails");
		excludeFields.add("returnStatus");
		excludeFields.add("sourceId");
		return excludeFields;
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

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getKeyReference() {
		return keyReference;
	}

	public void setKeyReference(String keyReference) {
		this.keyReference = keyReference;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public String getCollateralCcy() {
		return collateralCcy;
	}

	public void setCollateralCcy(String collateralCcy) {
		this.collateralCcy = collateralCcy;
	}

	public String getCollateralLoc() {
		return collateralLoc;
	}

	public void setCollateralLoc(String collateralLoc) {
		this.collateralLoc = collateralLoc;
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

	public String getLovrelationdesc() {
		return lovrelationdesc;
	}

	public void setLovrelationdesc(String lovrelationdesc) {
		this.lovrelationdesc = lovrelationdesc;
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

	public String getSummaryRemarks() {
		return summaryRemarks;
	}

	public void setSummaryRemarks(String summaryRemarks) {
		this.summaryRemarks = summaryRemarks;
	}

	public String getSourceFormName() {
		return sourceFormName;
	}

	public void setSourceFormName(String sourceFormName) {
		this.sourceFormName = sourceFormName;
	}

	public String getVerificationFormName() {
		return verificationFormName;
	}

	public void setVerificationFormName(String verificationFormName) {
		this.verificationFormName = verificationFormName;
	}

	public String getObservationRemarks() {
		return observationRemarks;
	}

	public void setObservationRemarks(String observationRemarks) {
		this.observationRemarks = observationRemarks;
	}

	public BigDecimal getValuationAmount() {
		return valuationAmount;
	}

	public void setValuationAmount(BigDecimal valuationAmount) {
		this.valuationAmount = valuationAmount;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getReasonDesc() {
		return reasonDesc;
	}

	public void setReasonDesc(String reasonDesc) {
		this.reasonDesc = reasonDesc;
	}

	public Date getVerifiedDate() {
		return verifiedDate;
	}

	public void setVerifiedDate(Date verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public ExtendedFieldRender getOnePagerExtRender() {
		return onePagerExtRender;
	}

	public void setOnePagerExtRender(ExtendedFieldRender onePagerExtRender) {
		this.onePagerExtRender = onePagerExtRender;
	}

	public ExtendedFieldHeader getOnePagerExtHeader() {
		return onePagerExtHeader;
	}

	public void setOnePagerExtHeader(ExtendedFieldHeader onePagerExtHeader) {
		this.onePagerExtHeader = onePagerExtHeader;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public TechnicalVerification getBefImage() {
		return befImage;
	}

	public void setBefImage(TechnicalVerification befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public int getVerificationCategory() {
		return verificationCategory;
	}

	public void setVerificationCategory(int verificationCategory) {
		this.verificationCategory = verificationCategory;
	}

	public Long getDocumentRef() {
		return documentRef;
	}

	public void setDocumentRef(Long documentRef) {
		this.documentRef = documentRef;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getLovDescLoanTypeName() {
		return lovDescLoanTypeName;
	}

	public void setLovDescLoanTypeName(String lovDescLoanTypeName) {
		this.lovDescLoanTypeName = lovDescLoanTypeName;
	}

	public String getSourcingBranch() {
		return sourcingBranch;
	}

	public void setSourcingBranch(String sourcingBranch) {
		this.sourcingBranch = sourcingBranch;
	}

	public String getLovDescSourcingBranch() {
		return lovDescSourcingBranch;
	}

	public void setLovDescSourcingBranch(String lovDescSourcingBranch) {
		this.lovDescSourcingBranch = lovDescSourcingBranch;
	}

	public List<ExtendedField> getExtendedDetails() {
		return extendedDetails;
	}

	public void setExtendedDetails(List<ExtendedField> extendedDetails) {
		this.extendedDetails = extendedDetails;
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

}
