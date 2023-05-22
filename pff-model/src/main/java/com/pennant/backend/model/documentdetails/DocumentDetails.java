package com.pennant.backend.model.documentdetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "referenceId", "docCategory", "custDocTitle", "custDocIssuedCountry", "custDocSysName",
		"custDocIssuedOn", "custDocExpDate", "docPurpose", "docName", "doctype", "docImage", "docUri",
		"docReceivedDate", "docOriginal", "docReceived", "remarks" })
@XmlRootElement(name = "DocumentDetail")
@XmlAccessorType(XmlAccessType.NONE)
public class DocumentDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -5569765259024813213L;

	private long docId = Long.MIN_VALUE;
	private String docModule;
	@XmlElement(name = "finReference")
	private String referenceId = "";
	private String finEvent = "";

	@XmlElement
	private String docCategory;
	@XmlElement(name = "docFormat")
	private String doctype;
	@XmlElement
	private String docName;
	@XmlElement(name = "docContent")
	private byte[] docImage;
	private String categoryCode;
	@XmlElement
	private String custDocTitle;
	@XmlElement(name = "custDocIssuedAuth")
	private String custDocSysName;
	private Timestamp custDocRcvdOn;
	@XmlElement
	private Date custDocExpDate;
	@XmlElement
	private Date custDocIssuedOn;
	@XmlElement
	private String custDocIssuedCountry;
	@XmlElement
	private String docPurpose;
	@XmlElement(name = "docRefId")
	private String docUri;
	private String lovDescCustDocIssuedCountry;
	private boolean custDocIsVerified;
	private long custDocVerifiedBy;
	private boolean custDocIsAcrive;
	@XmlElement
	private String lovDescCustCIF;

	private String lovDescDocCategoryName;
	private DocumentDetails befImage;
	private LoggedInUser userDetails;
	@XmlElement
	private Date docReceivedDate;
	@XmlElement
	private boolean docReceived;
	@XmlElement
	private boolean docOriginal;
	private String docBarcode;
	private String password;
	private boolean docIsPasswordProtected = false;
	private Long pdfMappingRef;
	private String pdfPassWord;
	private boolean docIsPdfExtRequired = false;
	@XmlElement
	private String remarks;
	@XmlElement
	private List<DocumentDetails> documents = new ArrayList<>();

	@XmlElement
	private WSReturnStatus returnStatus;
	// New proeprty added for holding the DocumentManager table's ID
	private Long docRefId;

	// Verification Fields
	private int doumentType;
	private long instructionUID = Long.MIN_VALUE;
	private String customerCif;
	private String finReference;
	private String state;
	private String status;
	private String docDesc;
	private String docExt;
	private Timestamp createdOn;
	private int retryCount;
	private String errorDesc;
	private Long custId;
	// Fields used in Godrej DMS
	private String applicationNo;
	private String leadId;
	private boolean lovDescNewImage = false;
	// Specific To Verification API
	@XmlElement
	private String lovDescCustShrtName;
	@XmlElement
	private String refId;
	// Specific To LV verification API
	@XmlElement
	private int docTypeId;

	public DocumentDetails() {
		super();
	}

	public DocumentDetails(String docModule, String docCategory, String doctype, String docName, byte[] docImage) {
		super();
		this.docModule = docModule;
		this.docCategory = docCategory;
		this.doctype = doctype;
		this.docName = docName;
		this.docImage = docImage;
		setNewRecord(true);
	}

	public DocumentDetails copyEntity() {
		DocumentDetails entity = new DocumentDetails();
		entity.setDocId(this.docId);
		entity.setDocModule(this.docModule);
		entity.setReferenceId(this.referenceId);
		entity.setFinEvent(this.finEvent);
		entity.setDocCategory(this.docCategory);
		entity.setDoctype(this.doctype);
		entity.setDocName(this.docName);
		entity.setDocImage(this.docImage);
		entity.setCategoryCode(this.categoryCode);
		entity.setCustDocTitle(this.custDocTitle);
		entity.setCustDocSysName(this.custDocSysName);
		entity.setCustDocRcvdOn(this.custDocRcvdOn);
		entity.setCustDocExpDate(this.custDocExpDate);
		entity.setCustDocIssuedOn(this.custDocIssuedOn);
		entity.setCustDocIssuedCountry(this.custDocIssuedCountry);
		entity.setDocPurpose(this.docPurpose);
		entity.setDocUri(this.docUri);
		entity.setLovDescCustDocIssuedCountry(this.lovDescCustDocIssuedCountry);
		entity.setCustDocIsVerified(this.custDocIsVerified);
		entity.setCustDocVerifiedBy(this.custDocVerifiedBy);
		entity.setCustDocIsAcrive(this.custDocIsAcrive);
		entity.setLovDescCustCIF(this.lovDescCustCIF);
		entity.setLovDescDocCategoryName(this.lovDescDocCategoryName);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setNewRecord(super.isNewRecord());
		entity.setDocReceivedDate(this.docReceivedDate);
		entity.setDocReceived(this.docReceived);
		entity.setDocOriginal(this.docOriginal);
		entity.setDocBarcode(this.docBarcode);
		entity.setPassword(this.password);
		entity.setDocIsPasswordProtected(this.docIsPasswordProtected);
		entity.setPdfMappingRef(this.pdfMappingRef);
		entity.setPdfPassWord(this.pdfPassWord);
		entity.setDocIsPdfExtRequired(this.docIsPdfExtRequired);
		entity.setRemarks(this.remarks);
		entity.setReturnStatus(this.returnStatus == null ? null : this.returnStatus.copyEntity());
		entity.setDocRefId(this.docRefId);
		entity.setDoumentType(this.doumentType);
		entity.setInstructionUID(this.instructionUID);
		entity.setCustomerCif(this.customerCif);
		entity.setFinReference(this.finReference);
		entity.setState(this.state);
		entity.setStatus(this.status);
		entity.setDocDesc(this.docDesc);
		entity.setDocExt(this.docExt);
		entity.setCreatedOn(this.createdOn);
		entity.setRetryCount(this.retryCount);
		entity.setErrorDesc(this.errorDesc);
		entity.setCustId(this.custId);
		entity.setApplicationNo(this.applicationNo);
		entity.setLeadId(this.leadId);
		entity.setLovDescNewImage(this.lovDescNewImage);
		entity.setLovDescCustShrtName(this.lovDescCustShrtName);
		entity.setRefId(this.refId);
		entity.setDocTypeId(this.docTypeId);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custDocTitle");
		excludeFields.add("custDocSysName");
		excludeFields.add("custDocRcvdOn");
		excludeFields.add("custDocExpDate");
		excludeFields.add("custDocIssuedOn");
		excludeFields.add("custDocIssuedCountry");
		excludeFields.add("custDocIsVerified");
		excludeFields.add("custDocVerifiedBy");
		excludeFields.add("custDocIsAcrive");
		excludeFields.add("categoryCode");
		excludeFields.add("password");
		// In the excludeFields method, docImage is added to avoid attachment
		// stored in Audit Tables
		excludeFields.add("docIsPasswordProtected");
		excludeFields.add("pdfMappingRef");
		excludeFields.add("pdfPassWord");
		excludeFields.add("docIsPdfExtRequired");
		excludeFields.add("docImage");
		excludeFields.add("doumentType");
		excludeFields.add("returnStatus");
		excludeFields.add("customerCif");
		excludeFields.add("finReference");
		excludeFields.add("state");
		excludeFields.add("status");
		excludeFields.add("docDesc");
		excludeFields.add("docExt");
		excludeFields.add("createdOn");
		excludeFields.add("retryCount");
		excludeFields.add("errorDesc");
		excludeFields.add("custId");

		excludeFields.add("applicationNo");
		excludeFields.add("leadId");
		excludeFields.add("lovDescCustShrtName");
		excludeFields.add("refId");
		excludeFields.add("docTypeId");
		excludeFields.add("documents");

		return excludeFields;
	}

	public long getId() {
		return docId;
	}

	public void setId(long id) {
		this.docId = id;

	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getDocModule() {
		return docModule;
	}

	public void setDocModule(String docModule) {
		this.docModule = docModule;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setBefImage(DocumentDetails befImage) {
		this.befImage = befImage;
	}

	public DocumentDetails getBefImage() {
		return befImage;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setLovDescDocCategoryName(String lovDescDocCategoryName) {
		this.lovDescDocCategoryName = lovDescDocCategoryName;
	}

	public String getLovDescDocCategoryName() {
		return lovDescDocCategoryName;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCustDocTitle() {
		return custDocTitle;
	}

	public void setCustDocTitle(String custDocTitle) {
		this.custDocTitle = custDocTitle;
	}

	public String getCustDocSysName() {
		return custDocSysName;
	}

	public void setCustDocSysName(String custDocSysName) {
		this.custDocSysName = custDocSysName;
	}

	public Timestamp getCustDocRcvdOn() {
		return custDocRcvdOn;
	}

	public void setCustDocRcvdOn(Timestamp custDocRcvdOn) {
		this.custDocRcvdOn = custDocRcvdOn;
	}

	public Date getCustDocExpDate() {
		return custDocExpDate;
	}

	public void setCustDocExpDate(Date custDocExpDate) {
		this.custDocExpDate = custDocExpDate;
	}

	public Date getCustDocIssuedOn() {
		return custDocIssuedOn;
	}

	public void setCustDocIssuedOn(Date custDocIssuedOn) {
		this.custDocIssuedOn = custDocIssuedOn;
	}

	public String getCustDocIssuedCountry() {
		return custDocIssuedCountry;
	}

	public void setCustDocIssuedCountry(String custDocIssuedCountry) {
		this.custDocIssuedCountry = custDocIssuedCountry;
	}

	public String getDocPurpose() {
		return docPurpose;
	}

	public void setDocPurpose(String docPurpose) {
		this.docPurpose = docPurpose;
	}

	public String getDocUri() {
		return docUri;
	}

	public void setDocUri(String docUri) {
		this.docUri = docUri;
	}

	public String getLovDescCustDocIssuedCountry() {
		return lovDescCustDocIssuedCountry;
	}

	public void setLovDescCustDocIssuedCountry(String lovDescCustDocIssuedCountry) {
		this.lovDescCustDocIssuedCountry = lovDescCustDocIssuedCountry;
	}

	public boolean isCustDocIsVerified() {
		return custDocIsVerified;
	}

	public void setCustDocIsVerified(boolean custDocIsVerified) {
		this.custDocIsVerified = custDocIsVerified;
	}

	public long getCustDocVerifiedBy() {
		return custDocVerifiedBy;
	}

	public void setCustDocVerifiedBy(long custDocVerifiedBy) {
		this.custDocVerifiedBy = custDocVerifiedBy;
	}

	public boolean isCustDocIsAcrive() {
		return custDocIsAcrive;
	}

	public void setCustDocIsAcrive(boolean custDocIsAcrive) {
		this.custDocIsAcrive = custDocIsAcrive;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	// Getter settters added for docRefId property.
	public Long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(Long docRefId) {
		this.docRefId = docRefId;
	}

	public boolean isDocReceived() {
		return docReceived;
	}

	public void setDocReceived(boolean docReceived) {
		this.docReceived = docReceived;
	}

	public Date getDocReceivedDate() {
		return docReceivedDate;
	}

	public void setDocReceivedDate(Date docReceivedDate) {
		this.docReceivedDate = docReceivedDate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDocIsPasswordProtected() {
		return docIsPasswordProtected;
	}

	public void setDocIsPasswordProtected(boolean docIsPasswordProtected) {
		this.docIsPasswordProtected = docIsPasswordProtected;
	}

	public Long getPdfMappingRef() {
		return pdfMappingRef;
	}

	public void setPdfMappingRef(Long pdfMappingRef) {
		this.pdfMappingRef = pdfMappingRef;
	}

	public String getPdfPassWord() {
		return pdfPassWord;
	}

	public void setPdfPassWord(String pdfPassWord) {
		this.pdfPassWord = pdfPassWord;
	}

	public boolean isDocIsPdfExtRequired() {
		return docIsPdfExtRequired;
	}

	public void setDocIsPdfExtRequired(boolean docIsPdfExtRequired) {
		this.docIsPdfExtRequired = docIsPdfExtRequired;
	}

	public int getDoumentType() {
		return doumentType;
	}

	public void setDoumentType(int doumentType) {
		this.doumentType = doumentType;
	}

	public boolean isDocOriginal() {
		return docOriginal;
	}

	public void setDocOriginal(boolean docOriginal) {
		this.docOriginal = docOriginal;
	}

	public long getInstructionUID() {
		return instructionUID;
	}

	public void setInstructionUID(long instructionUID) {
		this.instructionUID = instructionUID;
	}

	public String getDocBarcode() {
		return docBarcode;
	}

	public void setDocBarcode(String docBarcode) {
		this.docBarcode = docBarcode;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getCustomerCif() {
		return customerCif;
	}

	public void setCustomerCif(String customerCif) {
		this.customerCif = customerCif;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDocDesc() {
		return docDesc;
	}

	public void setDocDesc(String docDesc) {
		this.docDesc = docDesc;
	}

	public String getDocExt() {
		return docExt;
	}

	public void setDocExt(String docExt) {
		this.docExt = docExt;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public String getLeadId() {
		return leadId;
	}

	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}

	public boolean isLovDescNewImage() {
		return lovDescNewImage;
	}

	public void setLovDescNewImage(boolean lovDescNewImage) {
		this.lovDescNewImage = lovDescNewImage;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public int getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(int docTypeId) {
		this.docTypeId = docTypeId;
	}

	public List<DocumentDetails> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDetails> documents) {
		this.documents = documents;
	}

}
