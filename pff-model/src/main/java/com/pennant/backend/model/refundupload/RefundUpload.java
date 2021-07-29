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
 * FileName    		:  RefundUpload.java                                                    * 	  
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
package com.pennant.backend.model.refundupload;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Mandate table</b>.<br>
 * 
 */
@XmlType(propOrder = { "finReference", "payableAmount", "type", "feeType", "paymentDate", "paymentType", "partnerBank",
		"remarks", "IFSC", "MICR", "accountNumber", "accountHolderName", "phoneNumber", "issuingBank", "favourName",
		"payableLocation", "printingLocation", "valueDate", "status", "rejectReason" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "refund")
public class RefundUpload extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -4601315178356280082L;

	private long refundId = Long.MIN_VALUE;
	private long uploadId = 0;
	@XmlElement
	private String finReference = null;
	@XmlElement
	private BigDecimal payableAmount = BigDecimal.ZERO;
	@XmlElement
	private String type;
	@XmlElement
	private String feeType = null;
	@XmlElement
	private Date paymentDate;
	@XmlElement
	private String paymentType = null;
	@XmlElement
	private String partnerBank = null;
	@XmlElement
	private String remarks = null;
	@XmlElement
	private String IFSC = null;
	@XmlElement
	private String MICR;
	@XmlElement
	private String accountNumber = null;
	@XmlElement
	private String accountHolderName = null;
	@XmlElement
	private String phoneNumber = null;
	@XmlElement
	private String issuingBank = null;
	@XmlElement
	private String favourName = null;
	@XmlElement
	private String payableLocation = null;
	@XmlElement
	private String printingLocation = null;
	@XmlElement
	private Date valueDate;
	@XmlElement
	private String status;
	@XmlElement
	private String rejectReason;

	private String finSource;

	@XmlElement
	private WSReturnStatus returnStatus;

	private RefundUpload befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public RefundUpload() {
		super();
	}

	public Set<String> getExcludeFields() {

		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("returnStatus");
		excludeFields.add("finSource");

		return excludeFields;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public long getUploadId() {
		return uploadId;
	}

	public void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPartnerBank() {
		return partnerBank;
	}

	public void setPartnerBank(String partnerBank) {
		this.partnerBank = partnerBank;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getIFSC() {
		return IFSC;
	}

	public void setIFSC(String IFSC) {
		this.IFSC = IFSC;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getIssuingBank() {
		return issuingBank;
	}

	public void setIssuingBank(String issuingBank) {
		this.issuingBank = issuingBank;
	}

	public String getFavourName() {
		return favourName;
	}

	public void setFavourName(String favourName) {
		this.favourName = favourName;
	}

	public String getPayableLocation() {
		return payableLocation;
	}

	public void setPayableLocation(String payableLocation) {
		this.payableLocation = payableLocation;
	}

	public String getPrintingLocation() {
		return printingLocation;
	}

	public void setPrintingLocation(String printingLocation) {
		this.printingLocation = printingLocation;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public RefundUpload getBefImage() {
		return befImage;
	}

	public void setBefImage(RefundUpload befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getMICR() {
		return MICR;
	}

	public void setMICR(String mICR) {
		MICR = mICR;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getFinSource() {
		return finSource;
	}

	public void setFinSource(String finSource) {
		this.finSource = finSource;
	}

	public long getRefundId() {
		return refundId;
	}

	public void setRefundId(long refundId) {
		this.refundId = refundId;
	}

}