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
 * FileName    		:  LegalDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.legal;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>LegalDetail table</b>.<br>
 *
 */
public class LegalDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long legalId = Long.MIN_VALUE;
	private String legalReference;
	private String loanReference;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private String finCcy;
	private String collateralReference;
	private String branch;
	private String branchDesc;
	private Date legalDate;
	private String schedulelevelArea;
	private String legalDecision;
	private String legalRemarks;
	private String propertyDetailModt;
	private Date propertyDetailECDate;
	private String ecPropertyOwnerName;
	private boolean active = false;
	private boolean newRecord = false;
	private String lovValue;
	private LegalDetail befImage;
	private LoggedInUser userDetails;

	private Customer customer;
	private List<DocumentDetails> collateralDocumentList = new ArrayList<>();
	private List<LegalApplicantDetail> applicantDetailList = new ArrayList<>();
	private List<LegalPropertyDetail> propertyDetailList = new ArrayList<>();
	private List<LegalDocument> documentList = new ArrayList<>();
	private List<LegalPropertyTitle> propertyTitleList = new ArrayList<>();
	private List<LegalECDetail> ecdDetailsList = new ArrayList<>();
	private List<LegalNote> legalNotesList = new ArrayList<>();

	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("applicantDetailList");
		excludeFields.add("propertyDetailList");
		excludeFields.add("documentList");
		excludeFields.add("finAmount");
		excludeFields.add("finCcy");
		excludeFields.add("customer");
		excludeFields.add("propertyTitleList");
		excludeFields.add("ecdDetailsList");
		excludeFields.add("legalNotesList");
		excludeFields.add("collateralDocumentList");
		excludeFields.add("branchDesc");
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public LegalDetail() {
		super();
	}

	public LegalDetail(long id) {
		super();
		this.setId(id);
	}

	@Override
	public long getId() {
		return legalId;
	}

	@Override
	public void setId(long id) {
		this.legalId = id;
	}

	public long getLegalId() {
		return legalId;
	}

	public void setLegalId(long legalId) {
		this.legalId = legalId;
	}

	public String getLegalReference() {
		return legalReference;
	}

	public void setLegalReference(String legalReference) {
		this.legalReference = legalReference;
	}

	public String getLoanReference() {
		return loanReference;
	}

	public void setLoanReference(String loanReference) {
		this.loanReference = loanReference;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public String getCollateralReference() {
		return collateralReference;
	}

	public void setCollateralReference(String collateralReference) {
		this.collateralReference = collateralReference;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Date getLegalDate() {
		return legalDate;
	}

	public void setLegalDate(Date legalDate) {
		this.legalDate = legalDate;
	}

	public String getSchedulelevelArea() {
		return schedulelevelArea;
	}

	public void setSchedulelevelArea(String schedulelevelArea) {
		this.schedulelevelArea = schedulelevelArea;
	}

	public String getLegalDecision() {
		return legalDecision;
	}

	public void setLegalDecision(String legalDecision) {
		this.legalDecision = legalDecision;
	}

	public String getLegalRemarks() {
		return legalRemarks;
	}

	public void setLegalRemarks(String legalRemarks) {
		this.legalRemarks = legalRemarks;
	}

	public String getPropertyDetailModt() {
		return propertyDetailModt;
	}

	public void setPropertyDetailModt(String propertyDetailModt) {
		this.propertyDetailModt = propertyDetailModt;
	}

	public Date getPropertyDetailECDate() {
		return propertyDetailECDate;
	}

	public void setPropertyDetailECDate(Date propertyDetailECDate) {
		this.propertyDetailECDate = propertyDetailECDate;
	}

	public String getEcPropertyOwnerName() {
		return ecPropertyOwnerName;
	}

	public void setEcPropertyOwnerName(String ecPropertyOwnerName) {
		this.ecPropertyOwnerName = ecPropertyOwnerName;
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

	public LegalDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LegalDetail beforeImage) {
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

	public List<LegalApplicantDetail> getApplicantDetailList() {
		return applicantDetailList;
	}

	public void setApplicantDetailList(List<LegalApplicantDetail> applicantDetailList) {
		this.applicantDetailList = applicantDetailList;
	}

	public List<LegalPropertyDetail> getPropertyDetailList() {
		return propertyDetailList;
	}

	public void setPropertyDetailList(List<LegalPropertyDetail> propertyDetailList) {
		this.propertyDetailList = propertyDetailList;
	}

	public List<LegalDocument> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<LegalDocument> documentList) {
		this.documentList = documentList;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<LegalPropertyTitle> getPropertyTitleList() {
		return propertyTitleList;
	}

	public void setPropertyTitleList(List<LegalPropertyTitle> propertyTitleList) {
		this.propertyTitleList = propertyTitleList;
	}

	public List<LegalECDetail> getEcdDetailsList() {
		return ecdDetailsList;
	}

	public void setEcdDetailsList(List<LegalECDetail> ecdDetailsList) {
		this.ecdDetailsList = ecdDetailsList;
	}

	public List<LegalNote> getLegalNotesList() {
		return legalNotesList;
	}

	public void setLegalNotesList(List<LegalNote> legalNotesList) {
		this.legalNotesList = legalNotesList;
	}

	public List<DocumentDetails> getCollateralDocumentList() {
		return collateralDocumentList;
	}

	public void setCollateralDocumentList(List<DocumentDetails> collateralDocumentList) {
		this.collateralDocumentList = collateralDocumentList;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	 

}
