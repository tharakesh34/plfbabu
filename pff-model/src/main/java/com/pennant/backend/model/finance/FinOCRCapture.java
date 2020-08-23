package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinOCRCapture extends AbstractWorkflowEntity implements Comparable<FinOCRCapture> {

	private static final long serialVersionUID = 1L;
	private long id = Long.MIN_VALUE;
	private int disbSeq;
	private String finReference;
	private BigDecimal demandAmount = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private String remarks;
	private boolean newRecord = false;
	private FinOCRCapture befImage;
	private LoggedInUser userDetails;

	public FinOCRCapture() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
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

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
