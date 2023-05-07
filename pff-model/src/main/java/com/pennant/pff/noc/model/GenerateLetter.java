package com.pennant.pff.noc.model;

import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class GenerateLetter extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private long finID;
	private String finReference;
	private String letterType;
	private long feeId;
	private Date createdDate;
	private Date createdOn;
	private Date generatedDate;
	private Date generatedOn;
	private long generatedBy;
	private char requestType;
	private GenerateLetter befImage;
	private LoggedInUser userDetails;
	private ReceiptDTO receiptDTO;

	public GenerateLetter() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getLetterType() {
		return letterType;
	}

	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}

	public long getFeeId() {
		return feeId;
	}

	public void setFeeId(long feeId) {
		this.feeId = feeId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	public Date getGeneratedOn() {
		return generatedOn;
	}

	public void setGeneratedOn(Date generatedOn) {
		this.generatedOn = generatedOn;
	}

	public long getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(long generatedBy) {
		this.generatedBy = generatedBy;
	}

	public char getRequestType() {
		return requestType;
	}

	public void setRequestType(char requestType) {
		this.requestType = requestType;
	}

	public GenerateLetter getBefImage() {
		return befImage;
	}

	public void setBefImage(GenerateLetter befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public ReceiptDTO getReceiptDTO() {
		return receiptDTO;
	}

	public void setReceiptDTO(ReceiptDTO receiptDTO) {
		this.receiptDTO = receiptDTO;
	}
}