/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  UploadHeader.java                                                    * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017     														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
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

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.assignmentupload.AssignmentUpload;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.UploadManualAdvise;
import com.pennant.backend.model.miscPostingUpload.MiscPostingUpload;
import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class UploadHeader extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = -4601315178356280082L;

	private long uploadId = Long.MIN_VALUE;
	private String fileLocation = null;
	private String fileName = null;
	private Date transactionDate = null;
	private int totalRecords = 0;
	private int successCount = 0;
	private int failedCount = 0;
	private String module = null;
	private boolean fileDownload = false;

	private UploadHeader befImage;
	private boolean newRecord;
	private String finSource;

	private String entityCode;
	private String entityDesc;
	private Long assignmentPartnerId;
	private String assignmentPartnerCode;
	private String assignmentPartnerDesc;
	private String userName;

	@XmlTransient
	private LoggedInUser userDetails;

	private List<RefundUpload> refundUploads = new ArrayList<RefundUpload>();
	private List<AssignmentUpload> assignmentUploads = new ArrayList<AssignmentUpload>();
	private List<MiscPostingUpload> miscPostingUploads = new ArrayList<MiscPostingUpload>();
	private List<UploadManualAdvise> uploadManualAdvises = new ArrayList<UploadManualAdvise>();

	@XmlTransient
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private Long makerId;
	private Long approverId;
	private Date approvedDate = null;
	private boolean validationReq = true;

	public UploadHeader() {
		super();
	}

	public Set<String> getExcludeFields() {

		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("refundUploads");
		excludeFields.add("auditDetailMap");
		excludeFields.add("finSource");
		excludeFields.add("entityDesc");
		excludeFields.add("assignmentPartnerCode");
		excludeFields.add("assignmentPartnerDesc");
		excludeFields.add("assignmentUploads");
		excludeFields.add("entityDesc");
		excludeFields.add("miscPostingUploads");
		excludeFields.add("auditDetailMap");
		excludeFields.add("userName");
		excludeFields.add("fileDownload");
		excludeFields.add("assignmentPartnerId");
		excludeFields.add("assignmentUploads");
		excludeFields.add("auditDetailMap");
		excludeFields.add("uploadManualAdvises");
		excludeFields.add("validationReq");
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

	@Override
	public boolean isNew() {
		return this.newRecord;
	}

	@Override
	public long getId() {
		return this.uploadId;
	}

	@Override
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

	public UploadHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(UploadHeader befImage) {
		this.befImage = befImage;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
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

	public List<RefundUpload> getRefundUploads() {
		return refundUploads;
	}

	public void setRefundUploads(List<RefundUpload> refundUploads) {
		this.refundUploads = refundUploads;
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

	public String getFinSource() {
		return finSource;
	}

	public void setFinSource(String finSource) {
		this.finSource = finSource;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public Long getAssignmentPartnerId() {
		return assignmentPartnerId;
	}

	public void setAssignmentPartnerId(Long assignmentPartnerId) {
		this.assignmentPartnerId = assignmentPartnerId;
	}

	public String getAssignmentPartnerCode() {
		return assignmentPartnerCode;
	}

	public void setAssignmentPartnerCode(String assignmentPartnerCode) {
		this.assignmentPartnerCode = assignmentPartnerCode;
	}

	public String getAssignmentPartnerDesc() {
		return assignmentPartnerDesc;
	}

	public void setAssignmentPartnerDesc(String assignmentPartnerDesc) {
		this.assignmentPartnerDesc = assignmentPartnerDesc;
	}

	public List<AssignmentUpload> getAssignmentUploads() {
		return assignmentUploads;
	}

	public void setAssignmentUploads(List<AssignmentUpload> assignmentUploads) {
		this.assignmentUploads = assignmentUploads;
	}

	public List<MiscPostingUpload> getMiscPostingUploads() {
		return miscPostingUploads;
	}

	public void setMiscPostingUploads(List<MiscPostingUpload> miscPostingUpload) {
		this.miscPostingUploads = miscPostingUpload;
	}

	public Long getMakerId() {
		return makerId;
	}

	public void setMakerId(Long makerId) {
		this.makerId = makerId;
	}

	public List<UploadManualAdvise> getUploadManualAdvises() {
		return uploadManualAdvises;
	}

	public void setUploadManualAdvises(List<UploadManualAdvise> uploadManualAdvises) {
		this.uploadManualAdvises = uploadManualAdvises;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isValidationReq() {
		return validationReq;
	}

	public void setValidationReq(boolean validationReq) {
		this.validationReq = validationReq;
	}
}