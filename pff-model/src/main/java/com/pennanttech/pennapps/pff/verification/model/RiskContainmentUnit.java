package com.pennanttech.pennapps.pff.verification.model;

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
import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlAccessorType(XmlAccessType.NONE)
public class RiskContainmentUnit extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String cif;
	@XmlElement
	private String keyReference;
	@XmlElement(name = "name")
	private String custName;
	private String rcuReference;
	@XmlElement
	private long verificationId;
	@XmlElement
	private Date verificationDate;
	@XmlElement
	private String agentCode;
	@XmlElement
	private String agentName;
	@XmlElement(name = "recommendations")
	private int status;
	@XmlElement
	private Long reason;
	private String reasonCode;
	@XmlElement
	private String reasonDesc;
	@XmlElement
	private String remarks;

	private Date createdOn;
	private Long custId;
	private String agencyName;
	private Long documentId;
	private String documentSubId;

	private List<DocumentDetails> documents = null;
	@XmlElementWrapper(name = "rcuDocuments")
	@XmlElement(name = "rcuDocument")
	private List<RCUDocument> rcuDocuments = new ArrayList<>();
	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private RiskContainmentUnit befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	@XmlElement
	private WSReturnStatus returnStatus;
	private String sourceId;

	public RiskContainmentUnit() {
		super();
	}

	public RiskContainmentUnit(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("keyReference");
		excludeFields.add("cif");
		excludeFields.add("createdOn");
		excludeFields.add("reasonCode");
		excludeFields.add("reasonDesc");
		excludeFields.add("custId");
		excludeFields.add("agencyName");
		excludeFields.add("documents");
		excludeFields.add("rcuDocuments");
		excludeFields.add("documentId");
		excludeFields.add("documentSubId");
		excludeFields.add("custName");
		excludeFields.add("rcuReference");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
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

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getRcuReference() {
		return rcuReference;
	}

	public void setRcuReference(String rcuReference) {
		this.rcuReference = rcuReference;
	}

	public long getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(long verificationId) {
		this.verificationId = verificationId;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
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

	public List<DocumentDetails> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDetails> documents) {
		this.documents = documents;
	}

	public List<RCUDocument> getRcuDocuments() {
		return rcuDocuments;
	}

	public void setRcuDocuments(List<RCUDocument> rcuDocuments) {
		this.rcuDocuments = rcuDocuments;
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

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public RiskContainmentUnit getBefImage() {
		return befImage;
	}

	public void setBefImage(RiskContainmentUnit befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
