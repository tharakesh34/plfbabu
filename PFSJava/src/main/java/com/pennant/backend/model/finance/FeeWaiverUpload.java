package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FeeWaiverUpload extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long waiverId = Long.MIN_VALUE;
	private long uploadId = 0;
	private String finReference;
	private Long feeTypeID;
	private String feeTypeCode;
	private Date valueDate;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private String remarks;
	private String reason;
	private String status;
	private String rejectStage;
	@XmlTransient
	private FeeWaiverUpload befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public FeeWaiverUpload() {
		super();
	}

	public FeeWaiverUpload(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("feeTypeID");
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getUploadId() {
		return uploadId;
	}

	public void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}

	public long getId() {
		return uploadId;
	}

	public void setId(long id) {
		this.uploadId = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRejectStage() {
		return rejectStage;
	}

	public void setRejectStage(String rejectStage) {
		this.rejectStage = rejectStage;
	}

	public FeeWaiverUpload getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FeeWaiverUpload beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getWaiverId() {
		return waiverId;
	}

	public void setWaiverId(long waiverId) {
		this.waiverId = waiverId;
	}

	public Long getFeeTypeID() {
		return feeTypeID;
	}

	public void setFeeTypeID(Long feeTypeID) {
		this.feeTypeID = feeTypeID;
	}
}
