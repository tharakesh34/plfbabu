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
 * * FileName : PaymentHeader.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified Date :
 * 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.pff.payment.model;

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

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennanttech.model.adapter.DateFormatterAdapter;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PaymentHeader table</b>.<br>
 * 
 */
@XmlType(propOrder = { "paymentId", "paymentType", "paymentAmount", "createdOn", "approvedOn", "status" })
@XmlAccessorType(XmlAccessType.NONE)
public class PaymentHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	@XmlElement
	private long paymentId = Long.MIN_VALUE;
	@XmlElement
	private String paymentType;
	private long finID;
	@XmlElement
	private String finReference;
	@XmlElement
	private BigDecimal paymentAmount = BigDecimal.ZERO;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date createdOn;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date approvedOn;
	private String status;
	private String paymentInstrType;
	private long linkedTranId;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private PaymentHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	@XmlElement(name = "paymentDetails")
	private List<PaymentDetail> paymentDetailList = new ArrayList<>();
	private List<PaymentDetail> calPaymentDetailList = new ArrayList<>();
	@XmlElement
	private PaymentInstruction paymentInstruction;
	private Beneficiary defaultBeneficiary;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private String finSource;
	private BigDecimal odAgainstLoan = BigDecimal.ZERO;
	private BigDecimal odAgainstCustomer = BigDecimal.ZERO;
	private long custID;
	private String custCoreBank;
	@XmlElement
	private WSReturnStatus returnStatus;
	private String sourceId;
	private Date appDate;

	public PaymentHeader() {
		super();
	}

	public PaymentHeader(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("paymentDetail");
		excludeFields.add("paymentInstruction");
		excludeFields.add("paymentInstrType");
		excludeFields.add("defaultBeneficiary");
		excludeFields.add("finSource");
		excludeFields.add("odAgainstLoan");
		excludeFields.add("odAgainstCustomer");
		excludeFields.add("custID");
		excludeFields.add("custCoreBank");
		excludeFields.add("returnStatus");
		excludeFields.add("sourceId");
		excludeFields.add("feeType");
		excludeFields.add("appDate");

		return excludeFields;
	}

	public long getId() {
		return paymentId;
	}

	public void setId(long id) {
		this.paymentId = id;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentInstrType() {
		return paymentInstrType;
	}

	public void setPaymentInstrType(String paymentInstrType) {
		this.paymentInstrType = paymentInstrType;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public PaymentHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PaymentHeader beforeImage) {
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

	public PaymentInstruction getPaymentInstruction() {
		return paymentInstruction;
	}

	public void setPaymentInstruction(PaymentInstruction paymentInstruction) {
		this.paymentInstruction = paymentInstruction;
	}

	public List<PaymentDetail> getPaymentDetailList() {
		return paymentDetailList;
	}

	public void setPaymentDetailList(List<PaymentDetail> paymentDetailList) {
		this.paymentDetailList = paymentDetailList;
	}

	public List<PaymentDetail> getCalPaymentDetailList() {
		return calPaymentDetailList;
	}

	public void setCalPaymentDetailList(List<PaymentDetail> calPaymentDetailList) {
		this.calPaymentDetailList = calPaymentDetailList;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public Beneficiary getDefaultBeneficiary() {
		return defaultBeneficiary;
	}

	public void setDefaultBeneficiary(Beneficiary defaultBeneficiary) {
		this.defaultBeneficiary = defaultBeneficiary;
	}

	public String getFinSource() {
		return finSource;
	}

	public void setFinSource(String finSource) {
		this.finSource = finSource;
	}

	public BigDecimal getOdAgainstLoan() {
		return odAgainstLoan;
	}

	public void setOdAgainstLoan(BigDecimal odAgainstLoan) {
		this.odAgainstLoan = odAgainstLoan;
	}

	public BigDecimal getOdAgainstCustomer() {
		return odAgainstCustomer;
	}

	public void setOdAgainstCustomer(BigDecimal odAgainstCustomer) {
		this.odAgainstCustomer = odAgainstCustomer;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}
}
