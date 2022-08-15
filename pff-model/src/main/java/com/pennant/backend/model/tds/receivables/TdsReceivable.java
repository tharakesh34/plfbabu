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
 * * FileName : TdsReceivable.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * * Modified Date :
 * 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.tds.receivables;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.model.adapter.DateFormatterAdapter;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>TdsReceivable table</b>.<br>
 *
 */
@XmlType(propOrder = { "iD", "tANID", "certificateNumber", "certificateUploadOn", "certificateAmount", "assessmentYear",
		"dateOfReceipt", "certificateQuarter", "docID" })
@XmlAccessorType(XmlAccessType.FIELD)
public class TdsReceivable extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long tanID;
	private long txnID;

	private String tanNumber;
	private String tanHolderName;
	private String certificateNumber;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date certificateDate;
	private BigDecimal certificateAmount = BigDecimal.ZERO;
	private String assessmentYear;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date dateOfReceipt;
	private String certificateQuarter;
	private String uploadCertificate = null;
	private String status;
	private String txnStatus;
	private BigDecimal utilizedAmount = BigDecimal.ZERO;
	private BigDecimal balanceAmount = BigDecimal.ZERO;
	private Date tranDate;
	private DocumentDetails documentDetails;

	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private TdsReceivable befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	@XmlElement
	private String docName;
	private long docRefId = Long.MIN_VALUE;
	@XmlElement(name = "docContent")
	private byte[] docImage;
	private long docID;
	@XmlElement(name = "docFormat")
	private String docType;
	private boolean docIsMandatory = false;
	@XmlElement(name = "docCategory")
	private String docCategory;

	private List<TdsReceivablesTxn> tdsReceivablesTxnList = new ArrayList<>();

	@XmlTransient
	private TdsReceivablesTxn tdsReceivablesTxn;

	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public boolean isNew() {
		return isNewRecord();
	}

	public TdsReceivable() {
		super();
	}

	public TdsReceivable(long id) {
		super();
		this.setId(id);
	}

	public TdsReceivable(String docCategory, String doctype, String docName, byte[] docImage) {
		super();
		this.docCategory = docCategory;
		this.docType = doctype;
		this.docName = docName;
		this.docImage = docImage;
		this.newRecord = true;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("tanHolderName");
		excludeFields.add("tanNumber");
		excludeFields.add("uploadCertificate");
		excludeFields.add("docName");
		excludeFields.add("docRefId");
		excludeFields.add("docImage");
		excludeFields.add("docType");
		excludeFields.add("docIsMandatory");
		excludeFields.add("docCategory");
		excludeFields.add("txnID");
		excludeFields.add("tdsReceivablesTxn");
		excludeFields.add("documentDetails");
		excludeFields.add("tranDate");
		excludeFields.add("txnStatus");
		excludeFields.add("lovValue");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCertificateNumber() {
		return certificateNumber;
	}

	public void setCertificateNumber(String certificateNumber) {
		this.certificateNumber = certificateNumber;
	}

	public Date getCertificateDate() {
		return certificateDate;
	}

	public void setCertificateDate(Date certificateDate) {
		this.certificateDate = certificateDate;
	}

	public BigDecimal getCertificateAmount() {
		return certificateAmount;
	}

	public void setCertificateAmount(BigDecimal certificateAmount) {
		this.certificateAmount = certificateAmount;
	}

	public String getAssessmentYear() {
		return assessmentYear;
	}

	public void setAssessmentYear(String assessmentYear) {
		this.assessmentYear = assessmentYear;
	}

	public Date getDateOfReceipt() {
		return dateOfReceipt;
	}

	public void setDateOfReceipt(Date dateOfReceipt) {
		this.dateOfReceipt = dateOfReceipt;
	}

	public String getCertificateQuarter() {
		return certificateQuarter;
	}

	public void setCertificateQuarter(String certificateQuarter) {
		this.certificateQuarter = certificateQuarter;
	}

	public String getUploadCertificate() {
		return uploadCertificate;
	}

	public void setUploadCertificate(String uploadCertificate) {
		this.uploadCertificate = uploadCertificate;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public TdsReceivable getBefImage() {
		return this.befImage;
	}

	public void setBefImage(TdsReceivable beforeImage) {
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

	public String getTanNumber() {
		return tanNumber;
	}

	public void setTanNumber(String tanNumber) {
		this.tanNumber = tanNumber;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(long docRefId) {
		this.docRefId = docRefId;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public String getTanHolderName() {
		return tanHolderName;
	}

	public void setTanHolderName(String tanHolderName) {
		this.tanHolderName = tanHolderName;
	}

	public long getDocID() {
		return docID;
	}

	public void setDocID(long docID) {
		this.docID = docID;
	}

	public BigDecimal getUtilizedAmount() {
		return utilizedAmount;
	}

	public void setUtilizedAmount(BigDecimal utilizedAmount) {
		this.utilizedAmount = utilizedAmount;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public boolean isDocIsMandatory() {
		return docIsMandatory;
	}

	public void setDocIsMandatory(boolean docIsMandatory) {
		this.docIsMandatory = docIsMandatory;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public long getTanID() {
		return tanID;
	}

	public void setTanID(long tanID) {
		this.tanID = tanID;
	}

	public long getTxnID() {
		return txnID;
	}

	public void setTxnID(long txnID) {
		this.txnID = txnID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public TdsReceivablesTxn getTdsReceivablesTxn() {
		return tdsReceivablesTxn;
	}

	public void setTdsReceivablesTxn(TdsReceivablesTxn tdsReceivablesTxn) {
		this.tdsReceivablesTxn = tdsReceivablesTxn;
	}

	public DocumentDetails getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(DocumentDetails documentDetails) {
		this.documentDetails = documentDetails;
	}

	public List<TdsReceivablesTxn> getTdsReceivablesTxnList() {
		return tdsReceivablesTxnList;
	}

	public void setTdsReceivablesTxnList(List<TdsReceivablesTxn> tdsReceivablesTxnList) {
		this.tdsReceivablesTxnList = tdsReceivablesTxnList;
	}

	public Date getTranDate() {
		return tranDate;
	}

	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}

	public String getTxnStatus() {
		return txnStatus;
	}

	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}

}