package com.pennant.backend.model.lenderdataupload;

import java.io.Serializable;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class LenderDataUpload extends AbstractWorkflowEntity implements Entity, Serializable {

	private static final long serialVersionUID = 5866599146152795309L;

	private long uploadId = Long.MIN_VALUE;
	private long uploadHeaderId = Long.MIN_VALUE;
	private String lenderId;
	private String finReference;
	private boolean status;
	private String reason;

	public long getUploadId() {
		return uploadId;
	}

	public void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}

	public long getUploadHeaderId() {
		return uploadHeaderId;
	}

	public void setUploadHeaderId(long uploadHeaderId) {
		this.uploadHeaderId = uploadHeaderId;
	}

	public String getLenderId() {
		return lenderId;
	}

	public void setLenderId(String lenderId) {
		this.lenderId = lenderId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return uploadId;
	}

	@Override
	public void setId(long id) {
		uploadId = id;
	}
}
