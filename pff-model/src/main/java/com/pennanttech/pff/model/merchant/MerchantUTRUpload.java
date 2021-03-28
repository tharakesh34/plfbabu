package com.pennanttech.pff.model.merchant;

import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class MerchantUTRUpload extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String lanReference;
	private String utrNumber;
	private Date transferDate;
	private String status;
	private String remarks;

	public MerchantUTRUpload() {
		super();
	}

	public String getLanReference() {
		return lanReference;
	}

	public void setLanReference(String lanReference) {
		this.lanReference = lanReference;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
