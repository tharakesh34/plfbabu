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
 * FileName    		:  PresentmentDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.app.util.DateFormatterAdapter;
import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>PresentmentDetail table</b>.<br>
 * 
 */
@XmlType(propOrder = { "detailID", "presentmentID", "finReference", "schDate", "schSeq", "mandateID", "schAmtDue",
		"schPriDue", "schPftDue", "schFeeDue", "schInsDue", "schPenaltyDue", "advanceAmt", "excessID", "adviseAmt",
		"excludeReason", "presentmentAmt", "status", "bounceID" })
@XmlAccessorType(XmlAccessType.FIELD)
public class PresentmentDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long detailID = Long.MIN_VALUE;
	private long extractID  = Long.MIN_VALUE;
	private long presentmentID = Long.MIN_VALUE;
	private String presentmentIDName;
	private String finReference;
	private String finTypeDesc;
	private String finReferenceName;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date schDate;
	private Date defSchdDate;
	private long schSeq;
	private long mandateID;
	private String mandateType;
	private Date mandateExpiry;
	private BigDecimal schAmtDue;
	private BigDecimal schPriDue;
	private BigDecimal schPftDue;
	private BigDecimal schFeeDue;
	private BigDecimal schInsDue;
	private BigDecimal schPenaltyDue;
	private BigDecimal advanceAmt;
	private Long excessID;
	private String excessIDName;
	private BigDecimal adviseAmt;
	private int excludeReason;
	private String excludeReasonName;
	private BigDecimal presentmentAmt;
	private int status;
	private String statusName;
	private long bounceID;
	private String bounceIDName;
	private String customerCif;
	private String loanType;
	private String loanTypeName;
	private String mandateStatus;


	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private PresentmentDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public PresentmentDetail() {
		super();
	}

	public PresentmentDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("presentmentIDName");
		excludeFields.add("finReferenceName");
		excludeFields.add("mandateIDName");
		excludeFields.add("excessIDName");
		excludeFields.add("excludeReasonName");
		excludeFields.add("statusName");
		excludeFields.add("bounceIDName");
		excludeFields.add("customerCif");
		excludeFields.add("loanType");
		excludeFields.add("loanTypeName");
		excludeFields.add("mandateType");
		excludeFields.add("finTypeDesc");
		return excludeFields;
	}

	public long getId() {
		return presentmentID;
	}

	public void setId(long id) {
		this.presentmentID = id;
	}

	public long getPresentmentID() {
		return presentmentID;
	}

	public void setPresentmentID(long presentmentID) {
		this.presentmentID = presentmentID;
	}

	public String getPresentmentIDName() {
		return this.presentmentIDName;
	}

	public void setPresentmentIDName(String presentmentIDName) {
		this.presentmentIDName = presentmentIDName;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinReferenceName() {
		return this.finReferenceName;
	}

	public void setFinReferenceName(String finReferenceName) {
		this.finReferenceName = finReferenceName;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public long getSchSeq() {
		return schSeq;
	}

	public void setSchSeq(long schSeq) {
		this.schSeq = schSeq;
	}

	public long getMandateID() {
		return mandateID;
	}

	public void setMandateID(long mandateID) {
		this.mandateID = mandateID;
	}

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}

	public BigDecimal getSchAmtDue() {
		return schAmtDue;
	}

	public void setSchAmtDue(BigDecimal schAmtDue) {
		this.schAmtDue = schAmtDue;
	}

	public BigDecimal getSchPriDue() {
		return schPriDue;
	}

	public void setSchPriDue(BigDecimal schPriDue) {
		this.schPriDue = schPriDue;
	}

	public BigDecimal getSchPftDue() {
		return schPftDue;
	}

	public void setSchPftDue(BigDecimal schPftDue) {
		this.schPftDue = schPftDue;
	}

	public BigDecimal getSchFeeDue() {
		return schFeeDue;
	}

	public void setSchFeeDue(BigDecimal schFeeDue) {
		this.schFeeDue = schFeeDue;
	}

	public BigDecimal getSchInsDue() {
		return schInsDue;
	}

	public void setSchInsDue(BigDecimal schInsDue) {
		this.schInsDue = schInsDue;
	}

	public BigDecimal getSchPenaltyDue() {
		return schPenaltyDue;
	}

	public void setSchPenaltyDue(BigDecimal schPenaltyDue) {
		this.schPenaltyDue = schPenaltyDue;
	}

	public BigDecimal getAdvanceAmt() {
		return advanceAmt;
	}

	public void setAdvanceAmt(BigDecimal advanceAmt) {
		this.advanceAmt = advanceAmt;
	}

	public Long getExcessID() {
		return excessID;
	}

	public void setExcessID(Long excessID) {
		this.excessID = excessID;
	}

	public String getExcessIDName() {
		return this.excessIDName;
	}

	public void setExcessIDName(String excessIDName) {
		this.excessIDName = excessIDName;
	}

	public BigDecimal getAdviseAmt() {
		return adviseAmt;
	}

	public void setAdviseAmt(BigDecimal adviseAmt) {
		this.adviseAmt = adviseAmt;
	}

	public int getExcludeReason() {
		return excludeReason;
	}

	public void setExcludeReason(int excludeReason) {
		this.excludeReason = excludeReason;
	}

	public String getExcludeReasonName() {
		return this.excludeReasonName;
	}

	public void setExcludeReasonName(String excludeReasonName) {
		this.excludeReasonName = excludeReasonName;
	}

	public BigDecimal getPresentmentAmt() {
		return presentmentAmt;
	}

	public void setPresentmentAmt(BigDecimal presentmentAmt) {
		this.presentmentAmt = presentmentAmt;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public long getBounceID() {
		return bounceID;
	}

	public void setBounceID(long bounceID) {
		this.bounceID = bounceID;
	}

	public String getBounceIDName() {
		return this.bounceIDName;
	}

	public void setBounceIDName(String bounceIDName) {
		this.bounceIDName = bounceIDName;
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

	public PresentmentDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PresentmentDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getCustomerCif() {
		return customerCif;
	}

	public void setCustomerCif(String customerCif) {
		this.customerCif = customerCif;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getLoanTypeName() {
		return loanTypeName;
	}

	public void setLoanTypeName(String loanTypeName) {
		this.loanTypeName = loanTypeName;
	}

	public Date getDefSchdDate() {
		return defSchdDate;
	}

	public void setDefSchdDate(Date defSchdDate) {
		this.defSchdDate = defSchdDate;
	}

	public long getDetailID() {
		return detailID;
	}

	public void setDetailID(long detailID) {
		this.detailID = detailID;
	}

	public long getExtractID() {
		return extractID;
	}

	public void setExtractID(long extractID) {
		this.extractID = extractID;
	}

	public String getMandateStatus() {
		return mandateStatus;
	}

	public void setMandateStatus(String mandateStatus) {
		this.mandateStatus = mandateStatus;
	}

	public Date getMandateExpiry() {
		return mandateExpiry;
	}

	public void setMandateExpiry(Date mandateExpiry) {
		this.mandateExpiry = mandateExpiry;
	}


	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

}
