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
 * * FileName : UploadHeader.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * * Modified Date :
 * 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.receiptupload;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ReceiptUploadHeader extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -4601315178356280082L;

	private long uploadHeaderId = Long.MIN_VALUE;
	private String fileLocation = null;
	private String fileName = null;
	private Date transactionDate = null;
	private int totalRecords = 0;
	private int procRecords = 0;
	private int successCount = 0;
	private int failedCount = 0;
	private String entityCode;
	private String entityCodeDesc;
	private int uploadProgress;
	private long receiptId;
	private String lovValue;

	private ReceiptUploadHeader befImage;

	private LoggedInUser userDetails;
	private long attemptNo;
	private int attemptStatus;

	private List<ReceiptUploadDetail> receiptUploadList = new ArrayList<>();

	public ReceiptUploadHeader() {
		super();
	}

	public Set<String> getExcludeFields() {

		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("receiptUploadList");
		excludeFields.add("fileLocation");
		excludeFields.add("entityCode");
		excludeFields.add("entityCodeDesc");
		excludeFields.add("uploadProgress");
		excludeFields.add("receiptId");
		excludeFields.add("attemptStatus");
		excludeFields.add("attemptNo");
		excludeFields.add("procRecords");
		return excludeFields;
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

	public int getProcRecords() {
		return procRecords;
	}

	public void setProcRecords(int procRecords) {
		this.procRecords = procRecords;
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
		return this.uploadHeaderId;
	}

	public void setId(long uploadHeaderId) {
		this.uploadHeaderId = uploadHeaderId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public ReceiptUploadHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(ReceiptUploadHeader befImage) {
		this.befImage = befImage;
	}

	public List<ReceiptUploadDetail> getReceiptUploadList() {
		return receiptUploadList;
	}

	public void setReceiptUploadList(List<ReceiptUploadDetail> receiptUploadList) {
		this.receiptUploadList = receiptUploadList;
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

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityCodeDesc() {
		return entityCodeDesc;
	}

	public void setEntityCodeDesc(String entityCodeDesc) {
		this.entityCodeDesc = entityCodeDesc;
	}

	public int getUploadProgress() {
		return uploadProgress;
	}

	public void setUploadProgress(int uploadProgress) {
		this.uploadProgress = uploadProgress;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public long getUploadHeaderId() {
		return uploadHeaderId;
	}

	public void setUploadHeaderId(long uploadHeaderId) {
		this.uploadHeaderId = uploadHeaderId;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public int getAttemptStatus() {
		return attemptStatus;
	}

	public void setAttemptStatus(int attemptStatus) {
		this.attemptStatus = attemptStatus;
	}

	public long getAttemptNo() {
		return attemptNo;
	}

	public void setAttemptNo(long attemptNo) {
		this.attemptNo = attemptNo;
	}

}