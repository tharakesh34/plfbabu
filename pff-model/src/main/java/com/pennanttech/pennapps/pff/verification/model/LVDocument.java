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
package com.pennanttech.pennapps.pff.verification.model;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>verification_lv_details table</b>
 *
 */
public class LVDocument extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long verificationId = Long.MIN_VALUE;
	private int seqNo;
	private Long documentId;
	private String documentSubId;
	private String remarks;
	private String docCategory;
	private String docModule;
	private String remarks1;
	private String remarks2;
	private String remarks3;

	private Long docRefID;
	private Long documentRefId;
	private String docUri;
	private String code;
	private String description;
	private boolean lvReq;
	private String docType;
	private String docName;
	private boolean newRecord = false;
	private LoggedInUser userDetails;
	private LVDocument befImage;

	public LVDocument() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("code");
		excludeFields.add("lvReq");
		excludeFields.add("docType");
		excludeFields.add("docRefID");
		excludeFields.add("docUri");
		excludeFields.add("docName");
		excludeFields.add("id");
		excludeFields.add("docCategory");
		excludeFields.add("docName");
		excludeFields.add("docUri");
		excludeFields.add("docModule");
		excludeFields.add("remarks");
		excludeFields.add("documentRefId");
		excludeFields.add("description");
		excludeFields.add("docUri");
		return excludeFields;
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

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public String getCode() {
		return code;
	}

	public String getDocModule() {
		return docModule;
	}

	public void setDocModule(String docModule) {
		this.docModule = docModule;
	}

	public String getRemarks1() {
		return remarks1;
	}

	public void setRemarks1(String remarks1) {
		this.remarks1 = remarks1;
	}

	public String getRemarks2() {
		return remarks2;
	}

	public void setRemarks2(String remarks2) {
		this.remarks2 = remarks2;
	}

	public String getRemarks3() {
		return remarks3;
	}

	public void setRemarks3(String remarks3) {
		this.remarks3 = remarks3;
	}

	public Long getDocRefID() {
		return docRefID;
	}

	public void setDocRefID(Long docRefID) {
		this.docRefID = docRefID;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isLvReq() {
		return lvReq;
	}

	public void setLvReq(boolean lvReq) {
		this.lvReq = lvReq;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public long getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(long verificationId) {
		this.verificationId = verificationId;
	}

	public Long getDocumentRefId() {
		return documentRefId;
	}

	public void setDocumentRefId(Long documentRefId) {
		this.documentRefId = documentRefId;
	}

	public String getDocUri() {
		return docUri;
	}

	public void setDocUri(String docUri) {
		this.docUri = docUri;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public LVDocument getBefImage() {
		return befImage;
	}

	public void setBefImage(LVDocument befImage) {
		this.befImage = befImage;
	}

	public String getDocumentSubId() {
		return documentSubId;
	}

	public void setDocumentSubId(String documentSubId) {
		this.documentSubId = documentSubId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
