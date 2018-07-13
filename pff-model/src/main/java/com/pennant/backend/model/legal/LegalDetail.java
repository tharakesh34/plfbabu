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
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.loanquery.QueryDetail;
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
	private String finType;
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
	private String finNextRoleCode;
	private byte[] docImage;
	private LegalDetail befImage;
	private LoggedInUser userDetails;

	private QueryDetail queryDetail = new QueryDetail();
	private List<Customer> customerList = new ArrayList<>();
	private List<DocumentDetails> collateralDocumentList = new ArrayList<>();
	private List<LegalApplicantDetail> applicantDetailList = new ArrayList<>();
	private List<LegalPropertyDetail> propertyDetailList = new ArrayList<>();
	private List<LegalDocument> documentList = new ArrayList<>();
	private List<LegalPropertyTitle> propertyTitleList = new ArrayList<>();
	private List<LegalECDetail> ecdDetailsList = new ArrayList<>();
	private List<LegalNote> legalNotesList = new ArrayList<>();
	private List<FinCovenantType> covenantTypeList = new ArrayList<>();
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	
	
	// ------------ Document generation required fields---------//
	private FinanceDetail financeDetail;
	private String custName;
	private List<JointAccountDetail> coApplicantList;
	private List<QueryDetail> queryDetailsList;
	private String userName;
	private String desgnation;
	private String empCode;
	private String strFinAmoun;
	private String finAmountWords;
	private String strPropertyDetailECDate;
	private String strAppDate;
	private String listApplicantNames;
	private String listCoApplicantNames;
	private String listPropOwnerNames;
	private String registrationOffice;
	private boolean modtDoc = false;;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("applicantDetailList");
		excludeFields.add("propertyDetailList");
		excludeFields.add("documentList");
		excludeFields.add("finAmount");
		excludeFields.add("finCcy");
		excludeFields.add("customerList");
		excludeFields.add("propertyTitleList");
		excludeFields.add("ecdDetailsList");
		excludeFields.add("legalNotesList");
		excludeFields.add("collateralDocumentList"); 
		excludeFields.add("covenantTypeList"); 
		excludeFields.add("branchDesc");
		excludeFields.add("finType");
		excludeFields.add("finNextRoleCode");
		excludeFields.add("queryDetail");
		excludeFields.add("docImage");
		excludeFields.add("financeDetail");
		excludeFields.add("custName");
		excludeFields.add("coApplicantList");
		excludeFields.add("queryDetailsList");
		excludeFields.add("userName");
		excludeFields.add("desgnation");
		excludeFields.add("empCode");
		excludeFields.add("strFinAmoun");
		excludeFields.add("strPropertyDetailECDate");
		excludeFields.add("strAppDate");
		excludeFields.add("listApplicantNames");
		excludeFields.add("listCoApplicantNames");
		excludeFields.add("listPropOwnerNames");
		excludeFields.add("finAmountWords");
		excludeFields.add("registrationOffice");
		excludeFields.add("modtDoc");
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

	public List<FinCovenantType> getCovenantTypeList() {
		return covenantTypeList;
	}

	public void setCovenantTypeList(List<FinCovenantType> covenantTypeList) {
		this.covenantTypeList = covenantTypeList;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinNextRoleCode() {
		return finNextRoleCode;
	}

	public void setFinNextRoleCode(String finNextRoleCode) {
		this.finNextRoleCode = finNextRoleCode;
	}

	public QueryDetail getQueryDetail() {
		return queryDetail;
	}

	public void setQueryDetail(QueryDetail queryDetail) {
		this.queryDetail = queryDetail;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public List<JointAccountDetail> getCoApplicantList() {
		return coApplicantList;
	}

	public void setCoApplicantList(List<JointAccountDetail> coApplicantList) {
		this.coApplicantList = coApplicantList;
	}

	public List<QueryDetail> getQueryDetailsList() {
		return queryDetailsList;
	}

	public void setQueryDetailsList(List<QueryDetail> queryDetailsList) {
		this.queryDetailsList = queryDetailsList;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDesgnation() {
		return desgnation;
	}

	public void setDesgnation(String desgnation) {
		this.desgnation = desgnation;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getStrFinAmoun() {
		return strFinAmoun;
	}

	public void setStrFinAmoun(String strFinAmoun) {
		this.strFinAmoun = strFinAmoun;
	}

	public String getStrPropertyDetailECDate() {
		return strPropertyDetailECDate;
	}

	public void setStrPropertyDetailECDate(String strPropertyDetailECDate) {
		this.strPropertyDetailECDate = strPropertyDetailECDate;
	}

	public String getStrAppDate() {
		return strAppDate;
	}

	public void setStrAppDate(String strAppDate) {
		this.strAppDate = strAppDate;
	}

	public String getListApplicantNames() {
		return listApplicantNames;
	}

	public void setListApplicantNames(String listApplicantNames) {
		this.listApplicantNames = listApplicantNames;
	}

	public String getListCoApplicantNames() {
		return listCoApplicantNames;
	}

	public void setListCoApplicantNames(String listCoApplicantNames) {
		this.listCoApplicantNames = listCoApplicantNames;
	}

	public String getListPropOwnerNames() {
		return listPropOwnerNames;
	}

	public void setListPropOwnerNames(String listPropOwnerNames) {
		this.listPropOwnerNames = listPropOwnerNames;
	}

	public String getFinAmountWords() {
		return finAmountWords;
	}

	public void setFinAmountWords(String finAmountWords) {
		this.finAmountWords = finAmountWords;
	}

	public boolean isModtDoc() {
		return modtDoc;
	}

	public void setModtDoc(boolean modtDoc) {
		this.modtDoc = modtDoc;
	}

	public List<Customer> getCustomerList() {
		return customerList;
	}

	public void setCustomerList(List<Customer> customerList) {
		this.customerList = customerList;
	}

	public String getRegistrationOffice() {
		return registrationOffice;
	}

	public void setRegistrationOffice(String registrationOffice) {
		this.registrationOffice = registrationOffice;
	}

}
