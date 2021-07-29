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
 * * FileName : LegalDocument.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-06-2018 * * Modified Date :
 * 18-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.legal;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>LegalDocument table</b>.<br>
 *
 */
@XmlType(propOrder = { "documentDate", "documentNo", "documentDetail", "surveyNo", "documentType", "documentCategory",
		"scheduleType", "documentHolderProperty", "documentPropertyAddress", "documentBriefTracking",
		"documentMortgage" })
@XmlAccessorType(XmlAccessType.NONE)
public class LegalDocument extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long legalDocumentId = Long.MIN_VALUE;
	private long legalId = Long.MIN_VALUE;
	private int seqNum = 0;
	private byte[] docImage;
	private Long documentReference = Long.MIN_VALUE;
	@XmlElement(name = "docDate")
	private Date documentDate;
	@XmlElement(name = "docDetail")
	private String documentDetail;
	private String documentName;
	@XmlElement(name = "docNo")
	private String documentNo;
	private String uploadDocumentType;
	@XmlElement
	private String surveyNo;
	@XmlElement(name = "docType")
	private String documentType;
	@XmlElement(name = "docCategory")
	private String documentCategory;
	@XmlElement
	private String scheduleType;
	private String documentTypeVerify;
	private String documentRemarks;
	private String documentTypeApprove;
	private String documentAccepted;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private LegalDocument befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	// ----Merge fields ----//
	private String documentDateStr;
	private String documentAcceptedName;
	private String documentTypeApproveName;

	// --- Document Tracking fields -----//
	@XmlElement(name = "docHolder")
	private String documentHolderProperty;
	@XmlElement(name = "docPropertyAddrs")
	private String documentPropertyAddress;
	@XmlElement(name = "docBriefTracking")
	private String documentBriefTracking;
	@XmlElement(name = "docMortage")
	private boolean documentMortgage = false;
	private String finReference;
	private Long custId;
	private boolean docMandatory;

	public LegalDocument() {
		super();
	}

	public LegalDocument(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("legalReference");
		excludeFields.add("docImage");
		excludeFields.add("seqNum");
		excludeFields.add("documentDateStr");
		excludeFields.add("documentAcceptedName");
		excludeFields.add("documentTypeApproveName");
		excludeFields.add("finReference");
		excludeFields.add("custId");
		excludeFields.add("docMandatory");
		return excludeFields;
	}

	public long getId() {
		return legalDocumentId;
	}

	public void setId(long id) {
		this.legalDocumentId = id;
	}

	public long getLegalDocumentId() {
		return legalDocumentId;
	}

	public void setLegalDocumentId(long legalDocumentId) {
		this.legalDocumentId = legalDocumentId;
	}

	public Date getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(Date documentDate) {
		this.documentDate = documentDate;
	}

	public String getDocumentDetail() {
		return documentDetail;
	}

	public void setDocumentDetail(String documentDetail) {
		this.documentDetail = documentDetail;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public String getSurveyNo() {
		return surveyNo;
	}

	public void setSurveyNo(String surveyNo) {
		this.surveyNo = surveyNo;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentCategory() {
		return documentCategory;
	}

	public void setDocumentCategory(String documentCategory) {
		this.documentCategory = documentCategory;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getDocumentTypeVerify() {
		return documentTypeVerify;
	}

	public void setDocumentTypeVerify(String documentTypeVerify) {
		this.documentTypeVerify = documentTypeVerify;
	}

	public String getDocumentRemarks() {
		return documentRemarks;
	}

	public void setDocumentRemarks(String documentRemarks) {
		this.documentRemarks = documentRemarks;
	}

	public String getDocumentTypeApprove() {
		return documentTypeApprove;
	}

	public void setDocumentTypeApprove(String documentTypeApprove) {
		this.documentTypeApprove = documentTypeApprove;
	}

	public String getDocumentAccepted() {
		return documentAccepted;
	}

	public void setDocumentAccepted(String documentAccepted) {
		this.documentAccepted = documentAccepted;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public LegalDocument getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LegalDocument beforeImage) {
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

	public long getLegalId() {
		return legalId;
	}

	public void setLegalId(long legalId) {
		this.legalId = legalId;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public Long getDocumentReference() {
		return documentReference;
	}

	public void setDocumentReference(Long documentReference) {
		this.documentReference = documentReference;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getUploadDocumentType() {
		return uploadDocumentType;
	}

	public void setUploadDocumentType(String uploadDocumentType) {
		this.uploadDocumentType = uploadDocumentType;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public String getDocumentDateStr() {
		return documentDateStr;
	}

	public void setDocumentDateStr(String documentDateStr) {
		this.documentDateStr = documentDateStr;
	}

	public String getDocumentAcceptedName() {
		return documentAcceptedName;
	}

	public void setDocumentAcceptedName(String documentAcceptedName) {
		this.documentAcceptedName = documentAcceptedName;
	}

	public String getDocumentTypeApproveName() {
		return documentTypeApproveName;
	}

	public void setDocumentTypeApproveName(String documentTypeApproveName) {
		this.documentTypeApproveName = documentTypeApproveName;
	}

	public String getDocumentHolderProperty() {
		return documentHolderProperty;
	}

	public void setDocumentHolderProperty(String documentHolderProperty) {
		this.documentHolderProperty = documentHolderProperty;
	}

	public String getDocumentPropertyAddress() {
		return documentPropertyAddress;
	}

	public void setDocumentPropertyAddress(String documentPropertyAddress) {
		this.documentPropertyAddress = documentPropertyAddress;
	}

	public String getDocumentBriefTracking() {
		return documentBriefTracking;
	}

	public void setDocumentBriefTracking(String documentBriefTracking) {
		this.documentBriefTracking = documentBriefTracking;
	}

	public boolean isDocumentMortgage() {
		return documentMortgage;
	}

	public void setDocumentMortgage(boolean documentMortgage) {
		this.documentMortgage = documentMortgage;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public boolean isDocMandatory() {
		return docMandatory;
	}

	public void setDocMandatory(boolean docMandatory) {
		this.docMandatory = docMandatory;
	}

}
