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
 * * FileName : DisbursementDetails.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-10-2011 * * Modified
 * Date : 13-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class DisbursementDetails {
	private long aggrementID;
	private long refID;
	private long reqRefNo;
	private String reqType;
	private long downloadRefId;
	private long downloadSeqNo;
	private String paymentMode;
	private String UtrNo;
	private Date paymentDate;
	private Date pickUpDate;
	private String rejectReason;
	private String fullLengthUtr;
	private long req_Ref_No;
	private long download_Referid;
	private String errorReason;

	public DisbursementDetails() {
		super();
	}

	public long getAggrementID() {
		return aggrementID;
	}

	public void setAggrementID(long aggrementID) {
		this.aggrementID = aggrementID;
	}

	public long getReqRefNo() {
		return reqRefNo;
	}

	public void setReqRefNo(long regRefNo) {
		this.reqRefNo = regRefNo;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public long getDownloadRefId() {
		return downloadRefId;
	}

	public void setDownloadRefId(long downloadRefId) {
		this.downloadRefId = downloadRefId;
	}

	public long getDownloadSeqNo() {
		return downloadSeqNo;
	}

	public void setDownloadSeqNo(long downloadSeqNo) {
		this.downloadSeqNo = downloadSeqNo;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getUtrNo() {
		return UtrNo;
	}

	public void setUtrNo(String utrNo) {
		UtrNo = utrNo;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Date getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(Date pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getFullLengthUtr() {
		return fullLengthUtr;
	}

	public void setFullLengthUtr(String fullLengthUtr) {
		this.fullLengthUtr = fullLengthUtr;
	}

	public long getRefID() {
		return refID;
	}

	public void setRefID(long refID) {
		this.refID = refID;
	}

	public long getReq_Ref_No() {
		return req_Ref_No;
	}

	public void setReq_Ref_No(long req_Ref_No) {
		this.req_Ref_No = req_Ref_No;
	}

	public long getDownload_Referid() {
		return download_Referid;
	}

	public void setDownload_Referid(long download_Referid) {
		this.download_Referid = download_Referid;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

}
