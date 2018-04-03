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
 * FileName    		:  ChequeDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  27-11-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-11-2017       PENNANT	                 0.1                                            * 
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ChequeDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "chequeDetailsID", "headerID", "bankBranchID", "accountNo", "chequeSerialNo", "chequeDate",
		"eMIRefNo", "amount", "chequeCcy", "status", "active" })
@XmlAccessorType(XmlAccessType.FIELD)
public class ChequeDetail extends AbstractWorkflowEntity {
	private static final long	serialVersionUID	= 1L;

	private long				chequeDetailsID;
	private long				headerID			= 0;
	private long				bankBranchID		= 0;
	private String				bankBranchIDName;
	private String				accountNo;
	private int					chequeSerialNo;
	private String				chequeType;
	private Date				chequeDate;
	private String				eMIRefNo;
	private BigDecimal			amount;
	private String				chequeCcy;
	private String				status;
	private boolean				active				= false;
	@XmlTransient
	private boolean				newRecord			= false;
	@XmlTransient
	private String				lovValue;
	@XmlTransient
	private ChequeDetail		befImage;
	@XmlTransient
	private LoggedInUser		userDetails;

	private String				documentName;
	private long				documentRef			= Long.MIN_VALUE;
	private byte[]				docImage;

	public boolean isNew() {
		return isNewRecord();
	}

	public ChequeDetail() {
		super();
	}

	public ChequeDetail(int id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("bankBranchIDName");
		excludeFields.add("docImage");
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

	public String getBankBranchIDName() {
		return this.bankBranchIDName;
	}

	public void setBankBranchIDName(String bankBranchIDName) {
		this.bankBranchIDName = bankBranchIDName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public int getChequeSerialNo() {
		return chequeSerialNo;
	}

	public void setChequeSerialNo(int chequeSerialNo) {
		this.chequeSerialNo = chequeSerialNo;
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

	public String geteMIRefNo() {
		return eMIRefNo;
	}

	public void seteMIRefNo(String eMIRefNo) {
		this.eMIRefNo = eMIRefNo;
	}

	public long getDocumentRef() {
		return documentRef;
	}

	public void setDocumentRef(long documentRef) {
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
}
