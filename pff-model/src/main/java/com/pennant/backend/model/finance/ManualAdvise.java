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
 * FileName    		:  ManualAdvise.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ManualAdvise table</b>.<br>
 *
 */
@XmlType(propOrder = { "finReference", "adviseType", "feeTypeCode", "adviseAmount", "valueDate", "remarks" })
@XmlAccessorType(XmlAccessType.NONE)
public class ManualAdvise extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long adviseID = Long.MIN_VALUE;
	@XmlElement
	private int adviseType;
	private String adviseTypeName;
	@XmlElement
	private String finReference;
	private long feeTypeID;
	private String feeTypeDesc;
	@XmlElement
	private String feeTypeCode;
	private int sequence = 0;
	@XmlElement
	private BigDecimal adviseAmount = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private BigDecimal reservedAmt = BigDecimal.ZERO;
	private BigDecimal balanceAmt = BigDecimal.ZERO;
	private BigDecimal taxPercent = BigDecimal.ZERO;

	// GST Paid Fields
	private BigDecimal paidCGST = BigDecimal.ZERO;
	private BigDecimal paidSGST = BigDecimal.ZERO;
	private BigDecimal paidUGST = BigDecimal.ZERO;
	private BigDecimal paidIGST = BigDecimal.ZERO;
	private BigDecimal paidCESS = BigDecimal.ZERO;

	// GST Waived Fields
	private BigDecimal waivedCGST = BigDecimal.ZERO;
	private BigDecimal waivedSGST = BigDecimal.ZERO;
	private BigDecimal waivedUGST = BigDecimal.ZERO;
	private BigDecimal waivedIGST = BigDecimal.ZERO;
	private BigDecimal waivedCESS = BigDecimal.ZERO;

	// TDS Fields
	private BigDecimal tdsPaid = BigDecimal.ZERO;

	@XmlElement
	private String remarks;
	@XmlElement
	private Date valueDate;
	private Date postDate;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private ManualAdvise befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private long bounceID = 0;
	private String bounceCode;
	private String bounceCodeDesc;
	private long receiptID = 0;

	// GST fields
	private boolean taxApplicable;
	private String taxComponent;

	// GST fields
	private boolean tdsReq;

	// API fields
	@XmlElement
	private boolean stp;
	private String finSourceId;
	private String finSource = "PFF";
	private boolean dueCreation;
	private long linkedTranId;
	private boolean holdDue;

	private FeeType feeType;
	private List<DocumentDetails> documentDetails = new ArrayList<>(1);;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public String getFinSource() {
		return finSource;
	}

	public void setFinSource(String finSource) {
		this.finSource = finSource;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public ManualAdvise() {
		super();
	}

	public ManualAdvise(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("adviseTypeName");
		excludeFields.add("finReferenceName");
		excludeFields.add("feeTypeDesc");
		excludeFields.add("feeTypeCode");
		excludeFields.add("bounceCode");
		excludeFields.add("bounceCodeDesc");
		excludeFields.add("taxPercent");
		excludeFields.add("taxApplicable");
		excludeFields.add("taxComponent");
		excludeFields.add("stp");
		excludeFields.add("finSourceId");
		excludeFields.add("documentDetails");
		excludeFields.add("auditDetailMap");
		excludeFields.add("feeType");
		excludeFields.add("tdsPaid");
		excludeFields.add("tdsReq");
		return excludeFields;
	}

	public long getId() {
		return adviseID;
	}

	public void setId(long id) {
		this.adviseID = id;
	}

	public long getAdviseID() {
		return adviseID;
	}

	public void setAdviseID(long adviseID) {
		this.adviseID = adviseID;
	}

	public int getAdviseType() {
		return adviseType;
	}

	public void setAdviseType(int adviseType) {
		this.adviseType = adviseType;
	}

	public String getAdviseTypeName() {
		return this.adviseTypeName;
	}

	public void setAdviseTypeName(String adviseTypeName) {
		this.adviseTypeName = adviseTypeName;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getFeeTypeID() {
		return feeTypeID;
	}

	public void setFeeTypeID(long feeTypeID) {
		this.feeTypeID = feeTypeID;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public BigDecimal getAdviseAmount() {
		return adviseAmount;
	}

	public void setAdviseAmount(BigDecimal adviseAmount) {
		this.adviseAmount = adviseAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
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

	public ManualAdvise getBefImage() {
		return this.befImage;
	}

	public void setBefImage(ManualAdvise beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}

	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public long getBounceID() {
		return bounceID;
	}

	public void setBounceID(long bounceID) {
		this.bounceID = bounceID;
	}

	public String getBounceCode() {
		return bounceCode;
	}

	public void setBounceCode(String bounceCode) {
		this.bounceCode = bounceCode;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public BigDecimal getReservedAmt() {
		return reservedAmt;
	}

	public void setReservedAmt(BigDecimal reservedAmt) {
		this.reservedAmt = reservedAmt;
	}

	public BigDecimal getBalanceAmt() {
		return balanceAmt;
	}

	public void setBalanceAmt(BigDecimal balanceAmt) {
		this.balanceAmt = balanceAmt;
	}

	public String getBounceCodeDesc() {
		return bounceCodeDesc;
	}

	public void setBounceCodeDesc(String bounceCodeDesc) {
		this.bounceCodeDesc = bounceCodeDesc;
	}

	public BigDecimal getTaxPercent() {
		return taxPercent;
	}

	public void setTaxPercent(BigDecimal taxPercent) {
		this.taxPercent = taxPercent;
	}

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public String getTaxComponent() {
		return taxComponent;
	}

	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}

	public BigDecimal getPaidCGST() {
		return paidCGST;
	}

	public void setPaidCGST(BigDecimal paidCGST) {
		this.paidCGST = paidCGST;
	}

	public BigDecimal getPaidSGST() {
		return paidSGST;
	}

	public void setPaidSGST(BigDecimal paidSGST) {
		this.paidSGST = paidSGST;
	}

	public BigDecimal getPaidUGST() {
		return paidUGST;
	}

	public void setPaidUGST(BigDecimal paidUGST) {
		this.paidUGST = paidUGST;
	}

	public BigDecimal getPaidIGST() {
		return paidIGST;
	}

	public void setPaidIGST(BigDecimal paidIGST) {
		this.paidIGST = paidIGST;
	}

	public boolean isStp() {
		return stp;
	}

	public void setStp(boolean stp) {
		this.stp = stp;
	}

	public String getFinSourceId() {
		return finSourceId;
	}

	public void setFinSourceId(String finSourceId) {
		this.finSourceId = finSourceId;
	}

	public BigDecimal getWaivedCGST() {
		return waivedCGST;
	}

	public void setWaivedCGST(BigDecimal waivedCGST) {
		this.waivedCGST = waivedCGST;
	}

	public BigDecimal getWaivedSGST() {
		return waivedSGST;
	}

	public void setWaivedSGST(BigDecimal waivedSGST) {
		this.waivedSGST = waivedSGST;
	}

	public BigDecimal getWaivedUGST() {
		return waivedUGST;
	}

	public void setWaivedUGST(BigDecimal waivedUGST) {
		this.waivedUGST = waivedUGST;
	}

	public BigDecimal getWaivedIGST() {
		return waivedIGST;
	}

	public void setWaivedIGST(BigDecimal waivedIGST) {
		this.waivedIGST = waivedIGST;
	}

	public BigDecimal getPaidCESS() {
		return paidCESS;
	}

	public void setPaidCESS(BigDecimal paidCESS) {
		this.paidCESS = paidCESS;
	}

	public BigDecimal getWaivedCESS() {
		return waivedCESS;
	}

	public void setWaivedCESS(BigDecimal waivedCESS) {
		this.waivedCESS = waivedCESS;
	}

	public boolean isDueCreation() {
		return dueCreation;
	}

	public void setDueCreation(boolean dueCreation) {
		this.dueCreation = dueCreation;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public FeeType getFeeType() {
		return feeType;
	}

	public void setFeeType(FeeType feeType) {
		this.feeType = feeType;
	}

	public BigDecimal getTdsPaid() {
		return tdsPaid;
	}

	public void setTdsPaid(BigDecimal tdsPaid) {
		this.tdsPaid = tdsPaid;
	}

	public boolean isTdsReq() {
		return tdsReq;
	}

	public void setTdsReq(boolean tdsReq) {
		this.tdsReq = tdsReq;
	}

	public boolean isHoldDue() {
		return holdDue;
	}

	public void setHoldDue(boolean holdDue) {
		this.holdDue = holdDue;
	}

	public List<DocumentDetails> getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(List<DocumentDetails> documentDetails) {
		this.documentDetails = documentDetails;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

}
