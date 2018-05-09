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

public class LegalVerification extends AbstractWorkflowEntity {
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

	private String reasonCode;
	private String reasonDesc;
	private Long custId;
	private String agency;
	private String agencyName;
	private Date createdOn;
	private Long documentId;
	private String documentSubId;

	private List<DocumentDetails> documents = null;
	private List<LVDocument> lvDocuments = new ArrayList<>();
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private LegalVerification befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public LegalVerification() {
		super();
	}

	public LegalVerification(long id) {
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
		excludeFields.add("lvDocuments");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("keyReference");
		excludeFields.add("verificationFromName");
		excludeFields.add("documentId");
		excludeFields.add("documentSubId");

		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
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

	public long getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(long verificationId) {
		this.verificationId = verificationId;
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

	public List<LVDocument> getLvDocuments() {
		return lvDocuments;
	}

	public void setLvDocuments(List<LVDocument> lvDocuments) {
		this.lvDocuments = lvDocuments;
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

	public LegalVerification getBefImage() {
		return befImage;
	}

	public void setBefImage(LegalVerification befImage) {
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

}
