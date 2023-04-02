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
 * * FileName : ChequeDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-11-2017 * * Modified Date :
 * 27-11-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-11-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ChequeDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "chequeDetailsID", "headerID", "bankBranchID", "accountNo", "chequeSerialNo", "chequeDate",
		"eMIRefNo", "amount", "chequeCcy", "status", "active" })
@XmlAccessorType(XmlAccessType.NONE)
public class ChequeDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private long chequeDetailsID;
	private long headerID = 0;
	private long bankBranchID = 0;
	private String accountNo;
	@XmlElement
	private int chequeSerialNo;
	@XmlElement
	private String chequeType;
	@XmlElement
	private Date chequeDate;
	private int eMIRefNo = -1;
	@XmlElement
	private BigDecimal amount;
	private String chequeCcy;
	private String chequeStatus;
	@XmlElement
	private String accountType;
	private String accHolderName;
	private String status;
	private boolean active = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private ChequeDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String documentName;
	private Long documentRef = Long.MIN_VALUE;
	@XmlElement(name = "docContent")
	private byte[] docImage;
	private boolean isUpload;

	private String bankCode;
	private String branchCode;
	private String branchDesc;
	@XmlElement
	private String micr;
	private String ifsc;
	private String city;
	private String bankName;

	private Date chequeBounceDate;
	private String chequeNumber;
	private String chequeBounceReason;
	private String sourceId;
	@XmlElement
	private boolean isDelete = false;
	private boolean oldCheque;
	private String chequeSerialNumber;
	private Date schdDate;

	public ChequeDetail() {
		super();
	}

	public ChequeDetail(int id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("bankCode");
		excludeFields.add("branchCode");
		excludeFields.add("branchDesc");
		excludeFields.add("micr");
		excludeFields.add("ifsc");
		excludeFields.add("city");
		excludeFields.add("bankName");
		excludeFields.add("bankBranchIDName");
		excludeFields.add("docImage");
		excludeFields.add("isUpload");
		excludeFields.add("chequeBounceDate");
		excludeFields.add("chequeNumber");
		excludeFields.add("chequeBounceReason");
		excludeFields.add("excbankBranchID");
		excludeFields.add("sourceId");
		excludeFields.add("isDelete");
		excludeFields.add("oldCheque");
		excludeFields.add("chequeSerialNumber");
		excludeFields.add("schdDate");
		return excludeFields;
	}

	public long getId() {
		return chequeDetailsID;
	}

	public void setId(long id) {
		this.chequeDetailsID = id;
	}

	public long getChequeDetailsID() {
		return chequeDetailsID;
	}

	public void setChequeDetailsID(long chequeDetailsID) {
		this.chequeDetailsID = chequeDetailsID;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
	}

	public long getBankBranchID() {
		return bankBranchID;
	}

	public void setBankBranchID(long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public int getChequeSerialNo() {
		return Integer.valueOf(getChequeSerialNumber());
	}

	public void setChequeSerialNo(int chequeSerialNo) {
		this.chequeSerialNo = chequeSerialNo;
		this.chequeSerialNumber = StringUtils.leftPad("" + chequeSerialNo, 6, "0");
	}

	public String getChequeSerialNumber() {
		if (chequeSerialNumber == null) {
			this.chequeSerialNumber = StringUtils.leftPad("" + chequeSerialNo, 6, "0");
		}
		return chequeSerialNumber;
	}

	public void setChequeSerialNumber(String chequeSerialNumber) {
		this.chequeSerialNumber = chequeSerialNumber;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getChequeCcy() {
		return chequeCcy;
	}

	public void setChequeCcy(String chequeCcy) {
		this.chequeCcy = chequeCcy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public ChequeDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(ChequeDetail beforeImage) {
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

	public String getChequeType() {
		return chequeType;
	}

	public void setChequeType(String chequeType) {
		this.chequeType = chequeType;
	}

	public int geteMIRefNo() {
		return eMIRefNo;
	}

	public void seteMIRefNo(int eMIRefNo) {
		this.eMIRefNo = eMIRefNo;
	}

	public Long getDocumentRef() {
		return documentRef;
	}

	public void setDocumentRef(Long documentRef) {
		this.documentRef = documentRef;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getChequeStatus() {
		return chequeStatus;
	}

	public void setChequeStatus(String chequeStatus) {
		this.chequeStatus = chequeStatus;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccHolderName() {
		return accHolderName;
	}

	public void setAccHolderName(String accHolderName) {
		this.accHolderName = accHolderName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getMicr() {
		return micr;
	}

	public void setMicr(String micr) {
		this.micr = micr;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public boolean isUpload() {
		return isUpload;
	}

	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}

	public Date getChequeBounceDate() {
		return chequeBounceDate;
	}

	public void setChequeBounceDate(Date chequeBounceDate) {
		this.chequeBounceDate = chequeBounceDate;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public String getChequeBounceReason() {
		return chequeBounceReason;
	}

	public void setChequeBounceReason(String chequeBounceReason) {
		this.chequeBounceReason = chequeBounceReason;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public boolean isOldCheque() {
		return oldCheque;
	}

	public void setOldCheque(boolean oldCheque) {
		this.oldCheque = oldCheque;
	}

	public Date getSchdDate() {
		return schdDate;
	}

	public void setSchdDate(Date schdDate) {
		this.schdDate = schdDate;
	}
}