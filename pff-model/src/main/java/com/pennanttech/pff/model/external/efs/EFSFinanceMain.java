package com.pennanttech.pff.model.external.efs;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class EFSFinanceMain implements Serializable {
	private static final long serialVersionUID = 4627888611885054024L;

	private long id;
	private String finEvent;
	@XmlElement
	private String finReference;
	private long referenceId;
	private Date createdOn;
	private Date sentOn;
	private String remarks;
	private boolean processedFlag = false;
	private int noOfAttempts = 0;
	private Date retryOn;
	@XmlElement
	private Date closedDate;
	@XmlElement
	private String status;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("closedDate");
		excludeFields.add("status");
		return excludeFields;
	}

	public EFSFinanceMain() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getSentOn() {
		return sentOn;
	}

	public void setSentOn(Date sentOn) {
		this.sentOn = sentOn;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isProcessedFlag() {
		return processedFlag;
	}

	public void setProcessedFlag(boolean processedFlag) {
		this.processedFlag = processedFlag;
	}

	public int getNoOfAttempts() {
		return noOfAttempts;
	}

	public void setNoOfAttempts(int noOfAttempts) {
		this.noOfAttempts = noOfAttempts;
	}

	public Date getRetryOn() {
		return retryOn;
	}

	public void setRetryOn(Date retryOn) {
		this.retryOn = retryOn;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
