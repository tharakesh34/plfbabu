package com.pennant.backend.model.financemanagement;

import java.util.Date;

import com.pennant.backend.model.Entity;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class PresentmentDetailHeader extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 1L;
	
	private long extractId = Long.MIN_VALUE;
	private String extractReference;
	private long batchId;
	private Date fromDate;
	private Date toDate;
	private String mandateType;
	private String loanType;
	private boolean newRecord = false;

	public long getExtractId() {
		return extractId;
	}

	public void setExtractId(long extractId) {
		this.extractId = extractId;
	}

	public String getExtractReference() {
		return extractReference;
	}

	public void setExtractReference(String extractReference) {
		this.extractReference = extractReference;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public PresentmentDetailHeader() {
		super();
	}

	public PresentmentDetailHeader(long id) {
		super();
		this.setId(id);
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void setId(long id) {

	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

}
