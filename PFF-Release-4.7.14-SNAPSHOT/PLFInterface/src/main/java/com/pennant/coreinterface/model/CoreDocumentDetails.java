package com.pennant.coreinterface.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class CoreDocumentDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -5569765259024813213L;

	private long docId = Long.MIN_VALUE;
	private String docModule;
	private String referenceId = "";
	private String finEvent = "";

	private String docCategory;
	private String doctype;
	private String docName;
	private byte[] docImage;

	private String custDocTitle;
	private String custDocSysName;
	private Timestamp custDocRcvdOn;
	private Date custDocExpDate;
	private Date custDocIssuedOn;
	private String custDocIssuedCountry;
	private String lovDescCustDocIssuedCountry;
	private boolean custDocIsVerified;
	private long custDocVerifiedBy;
	private boolean custDocIsAcrive;
	private String lovDescCustCIF;

	private String lovDescDocCategoryName;
	private boolean newRecord = false;

	// New proeprty added for holding the DocumentManager table's ID
	private long docRefId = Long.MIN_VALUE;

	public CoreDocumentDetails() {
		super();
	}

	public CoreDocumentDetails(String docModule, String docCategory, String doctype, String docName, byte[] docImage) {
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
		// In the excludeFields method, docImage is added to avoid attachment
		// stored in Audit Tables
		excludeFields.add("docImage");
		return excludeFields;
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
	public long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(long docRefId) {
		this.docRefId = docRefId;
	}

}
