package com.pennant.backend.model.receiptstatus.upload;

import java.util.Date;

import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ReceiptStatusUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private long receiptId;
	private String statusRM;
	private Date realizationDate;
	private Date bounceDate;
	private String borcReason;
	private String borcRemarks;
	private LoggedInUser userDetails;

	public ReceiptStatusUpload() {
		super();
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public String getStatusRM() {
		return statusRM;
	}

	public void setStatusRM(String statusRM) {
		this.statusRM = statusRM;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public Date getBounceDate() {
		return bounceDate;
	}

	public void setBounceDate(Date bounceDate) {
		this.bounceDate = bounceDate;
	}

	public String getBorcReason() {
		return borcReason;
	}

	public void setBorcReason(String borcReason) {
		this.borcReason = borcReason;
	}

	public String getBorcRemarks() {
		return borcRemarks;
	}

	public void setBorcRemarks(String borcRemarks) {
		this.borcRemarks = borcRemarks;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
