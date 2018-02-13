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
 * FileName    		:  PresentmentHeader.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-05-2017    														*
 *                                                                  						*
 * Modified Date    :  01-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-05-2017       PENNANT	                 0.1                                            * 
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

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.app.util.DateFormatterAdapter;
import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>PresentmentHeader table</b>.<br>
 * 
 */
@XmlType(propOrder = { "id", "reference", "presentmentDate", "partnerBankId", "fromDate", "toDate", "status",
		"mandateType", "loanType", "finBranch", "schdate" })
@XmlAccessorType(XmlAccessType.FIELD)
public class PresentmentHeader extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String reference;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date presentmentDate;
	private long partnerBankId;
	private String partnerBankCode;
	private String partnerBankName;
	private String partnerAcctNumber;
	private String partnerAcctType;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date fromDate;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date toDate;
	private int status;
	private String   bankCode;
	private String   bankName;
	private String mandateType;
	private String mandateTypeName;
	private String loanType;
	private String loanTypeName;
	private String finBranch;
	private String finBranchName;
	private long dBStatusId;
	private long importStatusId;
	private int totalRecords;
	private int processedRecords;
	private int successRecords;
	private int failedRecords;

	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date schdate;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private PresentmentHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public PresentmentHeader() {
		super();
	}

	public PresentmentHeader(long id) {
		super();
		this.setId(id);
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Date getPresentmentDate() {
		return presentmentDate;
	}

	public void setPresentmentDate(Date presentmentDate) {
		this.presentmentDate = presentmentDate;
	}

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public String getPartnerAcctNumber() {
		return partnerAcctNumber;
	}

	public void setPartnerAcctNumber(String partnerAcctNumber) {
		this.partnerAcctNumber = partnerAcctNumber;
	}

	public String getPartnerAcctType() {
		return partnerAcctType;
	}

	public void setPartnerAcctType(String partnerAcctType) {
		this.partnerAcctType = partnerAcctType;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}

	public String getMandateTypeName() {
		return this.mandateTypeName;
	}

	public void setMandateTypeName(String mandateTypeName) {
		this.mandateTypeName = mandateTypeName;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getLoanTypeName() {
		return this.loanTypeName;
	}

	public void setLoanTypeName(String loanTypeName) {
		this.loanTypeName = loanTypeName;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinBranchName() {
		return this.finBranchName;
	}

	public void setFinBranchName(String finBranchName) {
		this.finBranchName = finBranchName;
	}

	public Date getSchdate() {
		return schdate;
	}

	public void setSchdate(Date schdate) {
		this.schdate = schdate;
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

	public PresentmentHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PresentmentHeader beforeImage) {
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

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public long getdBStatusId() {
		return dBStatusId;
	}

	public void setdBStatusId(long dBStatusId) {
		this.dBStatusId = dBStatusId;
	}

	public long getImportStatusId() {
		return importStatusId;
	}

	public void setImportStatusId(long importStatusId) {
		this.importStatusId = importStatusId;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getProcessedRecords() {
		return processedRecords;
	}

	public void setProcessedRecords(int processedRecords) {
		this.processedRecords = processedRecords;
	}

	public int getSuccessRecords() {
		return successRecords;
	}

	public void setSuccessRecords(int successRecords) {
		this.successRecords = successRecords;
	}

	public int getFailedRecords() {
		return failedRecords;
	}

	public void setFailedRecords(int failedRecords) {
		this.failedRecords = failedRecords;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	
}
