/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ManualAdvise.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 * * Modified Date :
 * 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>UploadManualAdvise table</b>.<br>
 *
 */
public class UploadManualAdvise extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long adviseId = Long.MIN_VALUE;
	private long uploadId = 0;
	private String finReference;
	private String adviseType;
	private Long feeTypeID;
	private String feeTypeCode;
	private BigDecimal adviseAmount = BigDecimal.ZERO;
	private Date valueDate;
	private String remarks;
	private String reason;
	private String status;
	private String rejectStage;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private UploadManualAdvise befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private long manualAdviseId = 0;

	public long getManualAdviseId() {
		return manualAdviseId;
	}

	public void setManualAdviseId(long manualAdviseId) {
		this.manualAdviseId = manualAdviseId;
	}

	public UploadManualAdvise() {
		super();
	}

	public UploadManualAdvise(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("feeTypeID");
		return excludeFields;
	}

	public String getAdviseType() {
		return adviseType;
	}

	public void setAdviseType(String adviseType) {
		this.adviseType = adviseType;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Long getFeeTypeID() {
		return feeTypeID;
	}

	public void setFeeTypeID(Long feeTypeID) {
		this.feeTypeID = feeTypeID;
	}

	public BigDecimal getAdviseAmount() {
		return adviseAmount;
	}

	public void setAdviseAmount(BigDecimal adviseAmount) {
		this.adviseAmount = adviseAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public UploadManualAdvise getBefImage() {
		return this.befImage;
	}

	public void setBefImage(UploadManualAdvise beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public long getUploadId() {
		return uploadId;
	}

	public void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
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

	public long getId() {
		return uploadId;
	}

	public void setId(long id) {
		this.uploadId = id;
	}

	public long getAdviseId() {
		return adviseId;
	}

	public void setAdviseId(long adviseId) {
		this.adviseId = adviseId;
	}

	public String getRejectStage() {
		return rejectStage;
	}

	public void setRejectStage(String rejectStage) {
		this.rejectStage = rejectStage;
	}
}