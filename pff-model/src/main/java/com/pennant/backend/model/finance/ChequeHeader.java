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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ChequeDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "chequeDetailsID", "headerID", "bankBranchID", "accountNo", "chequeSerialNo", "chequeDate",
		"eMIRefNo", "amount", "chequeCcy", "status", "active" })
@XmlAccessorType(XmlAccessType.FIELD)
public class ChequeHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long headerID = 0;
	private String finReference;
	private int noOfCheques;
	private BigDecimal totalAmount;
	private boolean active = false;
	private List<ChequeDetail> chequeDetailList = new ArrayList<ChequeDetail>();
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private ChequeHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
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

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

}
