package com.pennant.backend.model.finance.covenant;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CovenantDocument extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long covenantId;
	private Long documentId;
	private Date receivableDate;
	@XmlElement
	private Date frequencyDate;
	private Date documentReceivedDate;
	@XmlElement(name = "isOriginalDocument")
	private boolean originalDocument;
	private String lovValue;
	private CovenantDocument befImage;
	private LoggedInUser userDetails;
	private DocumentDetails documentDetail;
	private String covenantType;
	private String docCategory;
	@XmlElement
	private String docName;
	@XmlElement
	private String doctype;
	private Long custId;
	@XmlElement(name = "docContent")
	private byte[] docImage;
	@XmlElement
	private String docStatus;

	public CovenantDocument() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("documentDetail");
		excludeFields.add("docName");
		excludeFields.add("doctype");
		excludeFields.add("docCategory");
		excludeFields.add("frequencyDate");
		excludeFields.add("custId");
		excludeFields.add("docImage");
		excludeFields.add("originalDocument");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCovenantId() {
		return covenantId;
	}

	public void setCovenantId(long covenantId) {
		this.covenantId = covenantId;
	}

	public Date getReceivableDate() {
		return receivableDate;
	}

	public void setReceivableDate(Date receivableDate) {
		this.receivableDate = receivableDate;
	}

	public Date getDocumentReceivedDate() {
		return documentReceivedDate;
	}

	public void setDocumentReceivedDate(Date documentReceivedDate) {
		this.documentReceivedDate = documentReceivedDate;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CovenantDocument getBefImage() {
		return befImage;
	}

	public void setBefImage(CovenantDocument befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public DocumentDetails getDocumentDetail() {
		return documentDetail;
	}

	public void setDocumentDetail(DocumentDetails documentDetail) {
		this.documentDetail = documentDetail;
	}

	public String getCovenantType() {
		return covenantType;
	}

	public void setCovenantType(String covenantType) {
		this.covenantType = covenantType;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public Date getFrequencyDate() {
		return frequencyDate;
	}

	public void setFrequencyDate(Date frequencyDate) {
		this.frequencyDate = frequencyDate;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public boolean isOriginalDocument() {
		return originalDocument;
	}

	public void setOriginalDocument(boolean originalDocument) {
		this.originalDocument = originalDocument;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

}
