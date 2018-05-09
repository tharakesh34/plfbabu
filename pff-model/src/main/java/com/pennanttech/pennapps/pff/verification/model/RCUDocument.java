
package com.pennanttech.pennapps.pff.verification.model;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class RCUDocument extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long rcuId = Long.MIN_VALUE;
	private int seqNo;
	private Long documentId;
	private String documentSubId;
	private String docCategory;
	private String docModule;
	private int pagesEyeballed;
	private int pagesSampled;
	private String remarks;
	private String verificationType;
	private String rcuStatus;

	private Long docRefId;
	private String code;
	private String description;
	private boolean rcuReq;
	private String docType;
	private int docTypeId;
	private String docName;
	private String collateralRef;
	private boolean newRecord = false;

	private LoggedInUser userDetails;
	private Verification befImage;

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
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getRcuId() {
		return rcuId;
	}

	public void setRcuId(long rcuId) {
		this.rcuId = rcuId;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getVerificationType() {
		return verificationType;
	}

	public void setVerificationType(String verificationType) {
		this.verificationType = verificationType;
	}

	public String getRcuStatus() {
		return rcuStatus;
	}

	public void setRcuStatus(String rcuStatus) {
		this.rcuStatus = rcuStatus;
	}

	public Long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(Long docRefId) {
		this.docRefId = docRefId;
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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Verification getBefImage() {
		return befImage;
	}

	public void setBefImage(Verification befImage) {
		this.befImage = befImage;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

}
