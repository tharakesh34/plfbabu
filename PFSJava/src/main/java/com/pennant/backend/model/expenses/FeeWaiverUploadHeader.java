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
 * * FileName : FeeWaiverUploadHeader.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-10-2021 * *
 * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.expenses;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FeeWaiverUpload;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FeeWaiverUploadHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -4601315178356280083L;

	private long uploadId = Long.MIN_VALUE;
	private String fileLocation = null;
	private String fileName = null;
	private Date transactionDate = null;
	private int totalRecords = 0;
	private int successCount = 0;
	private int failedCount = 0;
	private String module = null;
	private boolean fileDownload = false;
	private String userName;
	private FeeWaiverUploadHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private List<FeeWaiverUpload> uploadFeeWaivers = new ArrayList<>();
	@XmlTransient
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
	private Long makerId;
	private Long approverId;
	private Date approvedDate = null;

	public FeeWaiverUploadHeader() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("auditDetailMap");
		excludeFields.add("fileDownload");
		excludeFields.add("auditDetailMap");
		excludeFields.add("uploadFeeWaivers");
		excludeFields.add("userName");

		return excludeFields;
	}

	public long getUploadId() {
		return uploadId;
	}

	public void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public int getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getId() {
		return this.uploadId;
	}

	public void setId(long uploadId) {
		this.uploadId = uploadId;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public FeeWaiverUploadHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(FeeWaiverUploadHeader befImage) {
		this.befImage = befImage;
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

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public boolean isFileDownload() {
		return fileDownload;
	}

	public void setFileDownload(boolean fileDownload) {
		this.fileDownload = fileDownload;
	}

	public Long getMakerId() {
		return makerId;
	}

	public void setMakerId(Long makerId) {
		this.makerId = makerId;
	}

	public Long getApproverId() {
		return approverId;
	}

	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public List<FeeWaiverUpload> getUploadFeeWaivers() {
		return uploadFeeWaivers;
	}

	public void setUploadFeeWaivers(List<FeeWaiverUpload> uploadFeeWaivers) {
		this.uploadFeeWaivers = uploadFeeWaivers;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
