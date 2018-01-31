package com.pennant.backend.model.documentdetails;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = { "docCategory", "custDocTitle", "custDocIssuedCountry", "custDocSysName", "custDocIssuedOn", "custDocExpDate",
		"docPurpose", "docName", "doctype", "docImage", "docUri" })
@XmlAccessorType(XmlAccessType.NONE)
public class DocumentDetails extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = -5569765259024813213L;

	private long docId = Long.MIN_VALUE;
	private String docModule;
	private String referenceId = "";
	private String finEvent = "";

	@XmlElement
	private String docCategory;
	@XmlElement(name="docFormat")
	private String doctype;
	@XmlElement
	private String docName;
	@XmlElement(name="docContent")
	private byte[] docImage;
	private boolean docIsCustDoc;
	@XmlElement
	private String custDocTitle;
	@XmlElement(name="custDocIssuedAuth")
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
	@XmlElement(name="docRefId")
	private String docUri;
	private String lovDescCustDocIssuedCountry;
	private boolean custDocIsVerified;
	private long custDocVerifiedBy;
	private boolean custDocIsAcrive;
	private String lovDescCustCIF;

	private String lovDescDocCategoryName;
	private DocumentDetails befImage;
	private LoggedInUser userDetails;
	private boolean newRecord = false;
	private Date docReceivedDate;
	private boolean docReceived;
	private String password;
	private boolean docIsPasswordProtected= false;
	private long pdfMappingRef= Long.MIN_VALUE;
	private String pdfPassWord;
	private boolean docIsPdfExtRequired= false;
	// New proeprty added for holding the DocumentManager table's ID
	private long docRefId = Long.MIN_VALUE;

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
		this.newRecord = true;
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
		excludeFields.add("docIsCustDoc");
		excludeFields.add("password");
		// In the excludeFields method, docImage is added to avoid attachment stored in Audit Tables
		excludeFields.add("docIsPasswordProtected");
		excludeFields.add("pdfMappingRef");
		excludeFields.add("pdfPassWord");
		excludeFields.add("docIsPdfExtRequired");
		excludeFields.add("docImage");
		return excludeFields;
	}

	@Override
	public boolean isNew() {
		return newRecord;
	}

	@Override
	public long getId() {
		return docId;
	}

	@Override
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

	public void setRefId(long referenceId) {
		this.referenceId = String.valueOf(referenceId);
	}

	public String getReferenceId() {
		return referenceId;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public void setLovDescDocCategoryName(String lovDescDocCategoryName) {
		this.lovDescDocCategoryName = lovDescDocCategoryName;
	}

	public String getLovDescDocCategoryName() {
		return lovDescDocCategoryName;
	}

	public boolean isDocIsCustDoc() {
		return docIsCustDoc;
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

	public void setDocIsCustDoc(boolean docIsCustDoc) {
		this.docIsCustDoc = docIsCustDoc;
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
	public long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(long docRefId) {
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

	public long getPdfMappingRef() {
		return pdfMappingRef;
	}

	public void setPdfMappingRef(long pdfMappingRef) {
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

}
