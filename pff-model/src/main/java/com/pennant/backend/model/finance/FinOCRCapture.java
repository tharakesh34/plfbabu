package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "disbSeq", "demandAmount", "paidAmount", "remarks", "fileName", "docImage" })
@XmlRootElement(name = "finOCRCapture")
@XmlAccessorType(XmlAccessType.NONE)
public class FinOCRCapture extends AbstractWorkflowEntity implements Comparable<FinOCRCapture> {
	private static final long serialVersionUID = 1L;
	private long id = Long.MIN_VALUE;
	@JsonProperty("receiptSeq")
	private int disbSeq;
	@XmlElement
	private String finReference;
	@XmlElement
	private BigDecimal demandAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal paidAmount = BigDecimal.ZERO;
	@XmlElement
	private String remarks;
	@XmlElement
	private Date receiptDate;
	private boolean newRecord = false;
	private FinOCRCapture befImage;
	private LoggedInUser userDetails;
	private Long documentRef = Long.MIN_VALUE;
	@XmlElement
	private String fileName;
	@XmlElement
	private byte[] docImage;

	public FinOCRCapture() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("docImage");
		excludeFields.add("documentRef");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getDisbSeq() {
		return disbSeq;
	}

	public void setDisbSeq(int disbSeq) {
		this.disbSeq = disbSeq;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getDemandAmount() {
		return demandAmount;
	}

	public void setDemandAmount(BigDecimal demandAmount) {
		this.demandAmount = demandAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
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

	public FinOCRCapture getBefImage() {
		return befImage;
	}

	public void setBefImage(FinOCRCapture befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getRemarks() {
		return remarks;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getDocumentRef() {
		return documentRef;
	}

	public void setDocumentRef(Long documentRef) {
		this.documentRef = documentRef;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	@Override
	public int compareTo(FinOCRCapture ocrDetail) {
		if (this.disbSeq == ocrDetail.disbSeq) {
			return 0;
		} else if (this.disbSeq > ocrDetail.disbSeq) {
			return 1;
		} else {
			return -1;
		}
	}

}
