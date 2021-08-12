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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ChequeDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "chequeDetailsID", "headerID", "bankBranchID", "accountNo", "chequeSerialNo", "chequeDate",
		"eMIRefNo", "amount", "chequeCcy", "status", "active" })
@XmlAccessorType(XmlAccessType.NONE)
public class ChequeHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private long headerID = 0;
	private long finID;
	private String finReference;
	private BigDecimal totalAmount;
	private boolean active = false;
	@XmlElementWrapper(name = "chequeDetails")
	@XmlElement
	private List<ChequeDetail> chequeDetailList = new ArrayList<ChequeDetail>();
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private ChequeHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	@XmlElement
	private int noOfCheques;
	@XmlElement
	private long bankBranchID;
	@XmlElement
	private String accHolderName;
	@XmlElement
	private String accountNo;
	@XmlElement
	private int chequeSerialNo;
	private String sourceId;

	@XmlElement
	public WSReturnStatus returnStatus = null;

	public ChequeHeader() {
		super();
	}

	public ChequeHeader(int id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("bankBranchIDName");
		excludeFields.add("accHolderName");
		excludeFields.add("accountNo");
		excludeFields.add("bankBranchID");
		excludeFields.add("chequeSerialNo");
		excludeFields.add("sourceId");
		excludeFields.add("returnStatus");
		return excludeFields;
	}

	public long getId() {
		return headerID;
	}

	public void setId(long id) {
		this.headerID = id;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
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

	public int getNoOfCheques() {
		return noOfCheques;
	}

	public void setNoOfCheques(int noOfCheques) {
		this.noOfCheques = noOfCheques;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public ChequeHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(ChequeHeader befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<ChequeDetail> getChequeDetailList() {
		return chequeDetailList;
	}

	public void setChequeDetailList(List<ChequeDetail> chequeDetailList) {
		this.chequeDetailList = chequeDetailList;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getAccHolderName() {
		return accHolderName;
	}

	public void setAccHolderName(String accHolderName) {
		this.accHolderName = accHolderName;
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

	public long getBankBranchID() {
		return bankBranchID;
	}

	public void setBankBranchID(long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
