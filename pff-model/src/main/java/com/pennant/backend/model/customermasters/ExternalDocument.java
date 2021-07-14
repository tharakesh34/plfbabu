package com.pennant.backend.model.customermasters;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ExternalDocument extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = -5945200122646428518L;

	private long id = Long.MIN_VALUE;
	private long custId;
	private String finReference;
	@XmlElement
	private String docName;
	private long bankId;
	@XmlElement
	private Timestamp fromDate;
	@XmlElement
	private Timestamp toDate;
	@XmlElement
	private String passwordProtected;
	@XmlElement
	private String password;
	private long docRefId;
	private String docUri;
	@XmlElement
	private byte[] docImage;
	@XmlElement
	private String docType;
	private boolean newRecord;
	private DocumentDetails befImage;
	private LoggedInUser userDetails;
	//As per ExternalDocuments below fields are added
	@XmlElement
	private boolean bankReport = false;
	private boolean lovDescNewImage = false;

	public ExternalDocument() {
		super();
	}

	public ExternalDocument(String docType, String docName, byte[] docImage) {
		super();
		this.docType = docType;
		this.docName = docName;
		this.docImage = docImage;
		this.newRecord = true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public long getBankId() {
		return bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public Timestamp getToDate() {
		return toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

	public String getPasswordProtected() {
		return passwordProtected;
	}

	public void setPasswordProtected(String passwordProtected) {
		this.passwordProtected = passwordProtected;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(long docRefId) {
		this.docRefId = docRefId;
	}

	public String getDocUri() {
		return docUri;
	}

	public void setDocUri(String docUri) {
		this.docUri = docUri;
	}

	public DocumentDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(DocumentDetails befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	public boolean isBankReport() {
		return bankReport;
	}

	public void setBankReport(boolean bankReport) {
		this.bankReport = bankReport;
	}

	public boolean isLovDescNewImage() {
		return lovDescNewImage;
	}

	public void setLovDescNewImage(boolean lovDescNewImage) {
		this.lovDescNewImage = lovDescNewImage;
	}
}
