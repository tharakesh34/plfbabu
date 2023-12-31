package com.pennanttech.pennapps.pff.verification.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class LegalVetting extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long verificationId;
	private String cif;
	private String keyReference;
	private String collateralType;
	private String referenceFor;

	private Date verificationDate;
	private String agentCode;
	private String agentName;
	private int status;
	private Long reason;
	private String remarks;
	private String verificationFromName;
	private int verificationCategory;

	private String reasonCode;
	private String reasonDesc;
	private Long custId;
	private String agency;
	private String agencyName;
	private Date createdOn;
	private Long documentId;
	private String documentSubId;

	private List<DocumentDetails> documents = null;
	private List<LVDocument> vettingDocuments = new ArrayList<>();
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private LegalVetting befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public LegalVetting() {
		super();
	}

	public LegalVetting(long id) {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("reasonCode");
		excludeFields.add("reasonDesc");
		excludeFields.add("cif");
		excludeFields.add("collateralType");
		excludeFields.add("referenceFor");
		excludeFields.add("verificationId");
		excludeFields.add("custId");
		excludeFields.add("agency");
		excludeFields.add("agencyName");
		excludeFields.add("createdOn");
		excludeFields.add("reasonDesc");
		excludeFields.add("documents");
		excludeFields.add("vettingDocuments");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("keyReference");
		excludeFields.add("verificationFromName");
		excludeFields.add("documentId");
		excludeFields.add("documentSubId");
		excludeFields.add("verificationCategory");

		return excludeFields;
	}

	public Date getVerificationDate() {
		return verificationDate;
	}

	public void setVerificationDate(Date verificationDate) {
		this.verificationDate = verificationDate;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getVerificationFromName() {
		return verificationFromName;
	}

	public void setVerificationFromName(String verificationFromName) {
		this.verificationFromName = verificationFromName;
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

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public String getReferenceFor() {
		return referenceFor;
	}

	public void setReferenceFor(String referenceFor) {
		this.referenceFor = referenceFor;
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

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getDocumentSubId() {
		return documentSubId;
	}

	public void setDocumentSubId(String documentSubId) {
		this.documentSubId = documentSubId;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public LegalVetting getBefImage() {
		return befImage;
	}

	public void setBefImage(LegalVetting befImage) {
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

	public List<DocumentDetails> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDetails> documents) {
		this.documents = documents;
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

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public int getVerificationCategory() {
		return verificationCategory;
	}

	public void setVerificationCategory(int verificationCategory) {
		this.verificationCategory = verificationCategory;
	}

	public long getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(long verificationId) {
		this.verificationId = verificationId;
	}

	public List<LVDocument> getVettingDocuments() {
		return vettingDocuments;
	}

	public void setVettingDocuments(List<LVDocument> vettingDocuments) {
		this.vettingDocuments = vettingDocuments;
	}
}