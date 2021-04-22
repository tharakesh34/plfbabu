
package com.pennanttech.pennapps.pff.verification.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlAccessorType(XmlAccessType.NONE)
public class RCUDocument extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	@XmlElement
	private long verificationId;
	@XmlElement
	private int seqNo;
	private Long documentId;
	@XmlElement
	private int documentType;
	@XmlElement(name = "documentName")
	private String documentSubId;
	private Long documentRefId;
	private String documentUri;
	private Long reinitid;
	@XmlElement
	private int verificationType;
	@XmlElement(name = "rcuStatus")
	private int status;
	@XmlElement
	private int pagesEyeballed;
	@XmlElement
	private int pagesSampled;
	private String initRemarks;
	@XmlElement(name = "remarks")
	private String agentRemarks;
	private String decisionRemarks;
	private boolean reInitiated;
	private int decision;

	private String docCategory;
	private String docModule;
	private String code;
	private String description;
	private boolean rcuReq;
	@XmlElement
	private String docType;
	private int docTypeId;
	private String docName;
	private String collateralRef;
	private String referenceId;
	private boolean newRecord = false;
	private LoggedInUser userDetails;
	private RCUDocument befImage;
	private ExtendedFieldRender extendedFieldRender;
	private ExtendedFieldHeader extendedFieldHeader;

	// Bank account number with numeric field type in RCU initiation
	private String accNumber;
	private String bankName;

	private String sourceId;

	public RCUDocument() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("code");
		excludeFields.add("rcuReq");
		excludeFields.add("docType");
		excludeFields.add("docRefId");
		excludeFields.add("docUri");
		excludeFields.add("docName");
		excludeFields.add("id");
		excludeFields.add("docCategory");
		excludeFields.add("docName");
		excludeFields.add("docUri");
		excludeFields.add("docModule");
		excludeFields.add("docRefID");
		excludeFields.add("description");
		excludeFields.add("collateralRef");
		excludeFields.add("docTypeId");
		excludeFields.add("reinitid");
		excludeFields.add("reInitiated");
		excludeFields.add("referenceId");
		excludeFields.add("accNumber");
		excludeFields.add("bankName");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("sourceId");
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public long getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(long verificationId) {
		this.verificationId = verificationId;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
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

	public Long getDocumentRefId() {
		return documentRefId;
	}

	public void setDocumentRefId(Long documentRefId) {
		this.documentRefId = documentRefId;
	}

	public String getDocumentUri() {
		return documentUri;
	}

	public void setDocumentUri(String documentUri) {
		this.documentUri = documentUri;
	}

	public int getVerificationType() {
		return verificationType;
	}

	public void setVerificationType(int verificationType) {
		this.verificationType = verificationType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPagesEyeballed() {
		return pagesEyeballed;
	}

	public void setPagesEyeballed(int pagesEyeballed) {
		this.pagesEyeballed = pagesEyeballed;
	}

	public int getPagesSampled() {
		return pagesSampled;
	}

	public void setPagesSampled(int pagesSampled) {
		this.pagesSampled = pagesSampled;
	}

	public String getInitRemarks() {
		return initRemarks;
	}

	public void setInitRemarks(String initRemarks) {
		this.initRemarks = initRemarks;
	}

	public String getAgentRemarks() {
		return agentRemarks;
	}

	public void setAgentRemarks(String agentRemarks) {
		this.agentRemarks = agentRemarks;
	}

	public String getDecisionRemarks() {
		return decisionRemarks;
	}

	public void setDecisionRemarks(String decisionRemarks) {
		this.decisionRemarks = decisionRemarks;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public String getDocModule() {
		return docModule;
	}

	public void setDocModule(String docModule) {
		this.docModule = docModule;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRcuReq() {
		return rcuReq;
	}

	public void setRcuReq(boolean rcuReq) {
		this.rcuReq = rcuReq;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public int getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(int docTypeId) {
		this.docTypeId = docTypeId;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public RCUDocument getBefImage() {
		return befImage;
	}

	public void setBefImage(RCUDocument befImage) {
		this.befImage = befImage;
	}

	public int getDocumentType() {
		return documentType;
	}

	public void setDocumentType(int documentType) {
		this.documentType = documentType;
	}

	public Long getReinitid() {
		return reinitid;
	}

	public void setReinitid(Long reinitid) {
		this.reinitid = reinitid;
	}

	public boolean isReInitiated() {
		return reInitiated;
	}

	public void setReInitiated(boolean reInitiated) {
		this.reInitiated = reInitiated;
	}

	public int getDecision() {
		return decision;
	}

	public void setDecision(int decision) {
		this.decision = decision;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
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

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
}
