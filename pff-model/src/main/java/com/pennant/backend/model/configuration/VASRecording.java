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
 * FileName    		:  VASRecording.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2016    														*
 *                                                                  						*
 * Modified Date    :  02-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.model.configuration;

import java.math.BigDecimal;
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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>VASRecording table</b>.<br>
 * 
 */
@XmlType(propOrder = { "cif", "finReference", "collateralRef", "productCode", "postingAgainst", "primaryLinkRef",
		"vasReference", "fee", "feePaymentMode", "valueDate", "accrualTillDate", "recurringDate", "dsaId", "dmaId",
		"fulfilOfficerId", "referralId", "renewalFee", "vasStatus", "extendedDetails", "documents", "waivedAmt",
		"returnStatus" })
@XmlRootElement(name = "vasDetail")
@XmlAccessorType(XmlAccessType.NONE)
public class VASRecording extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "product")
	private String productCode;
	private String productDesc;
	@XmlElement
	private String postingAgainst;
	@XmlElement
	private String primaryLinkRef;
	@XmlElement
	private String vasReference;
	@XmlElement
	private BigDecimal fee = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal renewalFee = BigDecimal.ZERO;
	@XmlElement
	private String feePaymentMode;
	@XmlElement
	private Date valueDate;
	@XmlElement
	private Date accrualTillDate;
	@XmlElement
	private Date recurringDate;
	@XmlElement
	private String dsaId;
	private String dsaIdDesc;
	@XmlElement
	private String dmaId;
	private String dmaIdDesc;
	@XmlElement
	private String fulfilOfficerId;
	private String fulfilOfficerIdDesc;
	@XmlElement
	private String referralId;
	private String referralIdDesc;
	private String sourceId;
	private String productType;
	private String productTypeDesc;
	private String productCtg;
	private String productCtgDesc;
	private String manufacturerDesc;
	@XmlElement
	private String vasStatus;
	private boolean financeProcess;
	private long feeAccounting = 0;

	// Insurance Fields
	private String status;
	private String entityCode;
	private String entityDesc;
	private String oldVasReference;
	private String remarks;
	private String reason;
	private BigDecimal cancelAmt;
	private boolean cancelAfterFLP;
	private String finType;
	private int flpDays;
	private String serviceReqNumber;
	private boolean insuranceCancel;

	private boolean termInsuranceLien;
	private String providerName;
	private String policyNumber;
	private boolean medicalApplicable;
	private String medicalStatus;

	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private VASRecording befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String insStatus;

	private ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
	private VASConfiguration vasConfiguration = null;
	private List<FinanceReferenceDetail> aggrements = null;
	private List<FinanceCheckListReference> vasCheckLists = null;
	private Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>(1);
	@XmlElementWrapper(name = "documents")
	@XmlElement(name = "document")
	private List<DocumentDetails> documents = null;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private List<FinanceReferenceDetail> checkLists = null;
	private VasCustomer vasCustomer = null;
	private List<ReturnDataSet> returnDataSetList = new ArrayList<ReturnDataSet>(1);
	@XmlElement(name = "fees")
	private List<FinFeeDetail> finFeeDetailsList = new ArrayList<FinFeeDetail>();
	@XmlElementWrapper(name = "extendedDetails")
	@XmlElement(name = "extendedDetail")
	private List<ExtendedField> extendedDetails = null;
	// API Specific
	@XmlElement
	private String cif;
	@XmlElement
	private String finReference;
	@XmlElement
	private String collateralRef;
	@XmlElement
	private WSReturnStatus returnStatus;
	private BigDecimal paidAmt = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal waivedAmt = BigDecimal.ZERO;
	private BigDecimal partnerPremiumAmt = BigDecimal.ZERO;

	private long manualAdviseId = Long.MIN_VALUE;
	private long paymentInsId = Long.MIN_VALUE;
	private long receivableAdviseId = Long.MIN_VALUE;

	public boolean isNew() {
		return isNewRecord();
	}

	public VASRecording() {
		super();
	}

	public VASRecording(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("productDesc");
		excludeFields.add("dsaIdDesc");
		excludeFields.add("dmaIdDesc");
		excludeFields.add("fulfilOfficerIdDesc");
		excludeFields.add("referralIdDesc");
		excludeFields.add("vasConfiguration");
		excludeFields.add("aggrements");
		excludeFields.add("checkLists");
		excludeFields.add("vasCheckLists");
		excludeFields.add("documents");
		excludeFields.add("extendedFieldRenderList");
		excludeFields.add("selAnsCountMap");
		excludeFields.add("sourceId");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("vasCustomer");
		excludeFields.add("productType");
		excludeFields.add("productTypeDesc");
		excludeFields.add("productCtg");
		excludeFields.add("productCtgDesc");
		excludeFields.add("manufacturerDesc");
		excludeFields.add("transactionEntries");
		excludeFields.add("returnDataSetList");
		excludeFields.add("DISBURSE");
		excludeFields.add("finBranch");
		excludeFields.add("extendedDetails");
		excludeFields.add("returnStatus");
		excludeFields.add("feeAccounting");
		excludeFields.add("cif");
		excludeFields.add("finReference");
		excludeFields.add("collateralRef");
		excludeFields.add("entityDesc");
		excludeFields.add("finType");
		excludeFields.add("flpDays");
		excludeFields.add("insuranceCancel");
		excludeFields.add("partnerPremiumAmt");
		excludeFields.add("receivableAdviseId");

		excludeFields.add("termInsuranceLien");
		excludeFields.add("providerName");
		excludeFields.add("policyNumber");
		excludeFields.add("medicalApplicable");
		excludeFields.add("medicalStatus");
		excludeFields.add("insStatus");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return productCode;
	}

	public void setId(String id) {
		this.productCode = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getPostingAgainst() {
		return postingAgainst;
	}

	public void setPostingAgainst(String postingAgainst) {
		this.postingAgainst = postingAgainst;
	}

	public String getPrimaryLinkRef() {
		return primaryLinkRef;
	}

	public void setPrimaryLinkRef(String primaryLinkRef) {
		this.primaryLinkRef = primaryLinkRef;
	}

	public String getVasReference() {
		return vasReference;
	}

	public void setVasReference(String vasReference) {
		this.vasReference = vasReference;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getFeePaymentMode() {
		return feePaymentMode;
	}

	public void setFeePaymentMode(String feePaymentMode) {
		this.feePaymentMode = feePaymentMode;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getAccrualTillDate() {
		return accrualTillDate;
	}

	public void setAccrualTillDate(Date accrualTillDate) {
		this.accrualTillDate = accrualTillDate;
	}

	public Date getRecurringDate() {
		return recurringDate;
	}

	public void setRecurringDate(Date recurringDate) {
		this.recurringDate = recurringDate;
	}

	public String getDsaId() {
		return dsaId;
	}

	public void setDsaId(String dsaId) {
		this.dsaId = dsaId;
	}

	public String getDmaId() {
		return dmaId;
	}

	public void setDmaId(String dmaId) {
		this.dmaId = dmaId;
	}

	public String getFulfilOfficerId() {
		return fulfilOfficerId;
	}

	public void setFulfilOfficerId(String fulfilOfficerId) {
		this.fulfilOfficerId = fulfilOfficerId;
	}

	public String getReferralId() {
		return referralId;
	}

	public void setReferralId(String referralId) {
		this.referralId = referralId;
	}

	@XmlTransient
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public VASRecording getBefImage() {
		return this.befImage;
	}

	public void setBefImage(VASRecording beforeImage) {
		this.befImage = beforeImage;
	}

	@XmlTransient
	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getDsaIdDesc() {
		return dsaIdDesc;
	}

	public void setDsaIdDesc(String dsaIdDesc) {
		this.dsaIdDesc = dsaIdDesc;
	}

	public String getDmaIdDesc() {
		return dmaIdDesc;
	}

	public void setDmaIdDesc(String dmaIdDesc) {
		this.dmaIdDesc = dmaIdDesc;
	}

	public String getFulfilOfficerIdDesc() {
		return fulfilOfficerIdDesc;
	}

	public void setFulfilOfficerIdDesc(String fulfilOfficerIdDesc) {
		this.fulfilOfficerIdDesc = fulfilOfficerIdDesc;
	}

	public String getReferralIdDesc() {
		return referralIdDesc;
	}

	public void setReferralIdDesc(String referralIdDesc) {
		this.referralIdDesc = referralIdDesc;
	}

	public VASConfiguration getVasConfiguration() {
		return vasConfiguration;
	}

	public void setVasConfiguration(VASConfiguration vasConfiguration) {
		this.vasConfiguration = vasConfiguration;
	}

	public List<FinanceReferenceDetail> getAggrements() {
		return aggrements;
	}

	public void setAggrements(List<FinanceReferenceDetail> aggrements) {
		this.aggrements = aggrements;
	}

	public List<FinanceCheckListReference> getVasCheckLists() {
		return vasCheckLists;
	}

	public void setVasCheckLists(List<FinanceCheckListReference> vasCheckLists) {
		this.vasCheckLists = vasCheckLists;
	}

	public Map<Long, Long> getSelAnsCountMap() {
		return selAnsCountMap;
	}

	public void setSelAnsCountMap(Map<Long, Long> selAnsCountMap) {
		this.selAnsCountMap = selAnsCountMap;
	}

	public List<DocumentDetails> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDetails> documents) {
		this.documents = documents;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public BigDecimal getRenewalFee() {
		return renewalFee;
	}

	public void setRenewalFee(BigDecimal renewalFee) {
		this.renewalFee = renewalFee;
	}

	public List<FinanceReferenceDetail> getCheckLists() {
		return checkLists;
	}

	public void setCheckLists(List<FinanceReferenceDetail> checkLists) {
		this.checkLists = checkLists;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public VasCustomer getVasCustomer() {
		return vasCustomer;
	}

	public void setVasCustomer(VasCustomer vasCustomer) {
		this.vasCustomer = vasCustomer;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductTypeDesc() {
		return productTypeDesc;
	}

	public void setProductTypeDesc(String productTypeDesc) {
		this.productTypeDesc = productTypeDesc;
	}

	public String getProductCtg() {
		return productCtg;
	}

	public void setProductCtg(String productCtg) {
		this.productCtg = productCtg;
	}

	public String getProductCtgDesc() {
		return productCtgDesc;
	}

	public void setProductCtgDesc(String productCtgDesc) {
		this.productCtgDesc = productCtgDesc;
	}

	public String getManufacturerDesc() {
		return manufacturerDesc;
	}

	public void setManufacturerDesc(String manufacturerDesc) {
		this.manufacturerDesc = manufacturerDesc;
	}

	public List<ReturnDataSet> getReturnDataSetList() {
		return returnDataSetList;
	}

	public void setReturnDataSetList(List<ReturnDataSet> returnDataSetList) {
		this.returnDataSetList = returnDataSetList;
	}

	public String getVasStatus() {
		return vasStatus;
	}

	public void setVasStatus(String vasStatus) {
		this.vasStatus = vasStatus;
	}

	public boolean isFinanceProcess() {
		return financeProcess;
	}

	public void setFinanceProcess(boolean financeProcess) {
		this.financeProcess = financeProcess;
	}

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> vasRecordingMap = new HashMap<>();

		return getDeclaredFieldValues(vasRecordingMap);
	}

	public Map<String, Object> getDeclaredFieldValues(Map<String, Object> map) {
		map.put("vr_productCode", this.productCode);
		map.put("vr_productDesc", this.productDesc);
		map.put("vr_postingAgainst", this.postingAgainst);
		map.put("vr_primaryLinkRef", this.primaryLinkRef);
		map.put("vr_vasReference", this.vasReference);
		map.put("vr_fee", this.fee);
		map.put("vr_renewalFee", this.renewalFee);
		map.put("vr_feePaymentMode", this.feePaymentMode);
		map.put("vr_valueDate", this.valueDate);
		map.put("vr_accrualTillDate", this.accrualTillDate);
		map.put("vr_recurringDate", this.recurringDate);
		map.put("vr_dsaId", this.dsaId);
		map.put("vr_dsaIdDesc", this.dsaIdDesc);
		map.put("vr_dmaId", this.dmaId);
		map.put("vr_dmaIdDesc", this.dmaIdDesc);
		map.put("vr_fulfilOfficerId", this.fulfilOfficerId);
		map.put("vr_fulfilOfficerIdDesc", this.fulfilOfficerIdDesc);
		map.put("vr_referralId", this.referralId);
		map.put("vr_referralIdDesc", this.referralIdDesc);
		map.put("vr_sourceId", this.sourceId);
		map.put("vr_productType", this.productType);
		map.put("vr_productTypeDesc", this.productTypeDesc);
		map.put("vr_productCtg", this.productCtg);
		map.put("vr_productCtgDesc", this.productCtgDesc);
		map.put("vr_manufacturerDesc", this.manufacturerDesc);
		map.put("vr_vasStatus", this.vasStatus);
		map.put("vr_financeProcess", this.financeProcess);
		map.put("vr_feeAccounting", this.feeAccounting);
		map.put("vr_status", this.status);
		map.put("vr_entityCode", this.entityCode);
		map.put("vr_entityDesc", this.entityDesc);
		map.put("vr_oldVasReference", this.oldVasReference);
		map.put("vr_remarks", this.remarks);
		map.put("vr_reason", this.reason);
		map.put("vr_cancelAmt", this.cancelAmt);
		map.put("vr_cancelAfterFLP", this.cancelAfterFLP);
		map.put("vr_finType", this.finType);
		map.put("vr_flpDays", this.flpDays);
		map.put("vr_serviceReqNumber", this.serviceReqNumber);
		map.put("vr_insuranceCancel", this.insuranceCancel);
		map.put("vr_termInsuranceLien", this.termInsuranceLien);
		map.put("vr_providerName", this.providerName);
		map.put("vr_policyNumber", this.policyNumber);
		map.put("vr_medicalApplicable", this.medicalApplicable);
		map.put("vr_medicalStatus", this.medicalStatus);
		map.put("vr_newRecord", this.newRecord);
		map.put("vr_lovValue", this.lovValue);
		map.put("vr_befImage", this.befImage);
		map.put("vr_userDetails", this.userDetails);
		map.put("vr_insStatus", this.insStatus);
		map.put("vr_extendedFieldRender", this.extendedFieldRender);
		map.put("vr_vasConfiguration", this.vasConfiguration);
		map.put("vr_aggrements", this.aggrements);
		map.put("vr_vasCheckLists", this.vasCheckLists);
		map.put("vr_selAnsCountMap", this.selAnsCountMap);
		map.put("vr_documents", this.documents);
		map.put("vr_auditDetailMap", this.auditDetailMap);
		map.put("vr_checkLists", this.checkLists);
		map.put("vr_vasCustomer", this.vasCustomer);
		map.put("vr_returnDataSetList", this.returnDataSetList);
		map.put("vr_finFeeDetailsList", this.finFeeDetailsList);
		map.put("vr_extendedDetails", this.extendedDetails);
		map.put("vr_cif", this.cif);
		map.put("vr_finReference", this.finReference);
		map.put("vr_collateralRef", this.collateralRef);
		map.put("vr_returnStatus", this.returnStatus);
		map.put("vr_paidAmt", this.paidAmt);
		map.put("vr_waivedAmt", this.waivedAmt);
		map.put("vr_partnerPremiumAmt", this.partnerPremiumAmt);
		map.put("vr_manualAdviseId", this.manualAdviseId);
		map.put("vr_paymentInsId", this.paymentInsId);
		map.put("vr_receivableAdviseId", this.receivableAdviseId);

		return map;
	}

	public List<FinFeeDetail> getFinFeeDetailsList() {
		return finFeeDetailsList;
	}

	public void setFinFeeDetailsList(List<FinFeeDetail> finFeeDetailsList) {
		this.finFeeDetailsList = finFeeDetailsList;
	}

	public List<ExtendedField> getExtendedDetails() {
		return extendedDetails;
	}

	public void setExtendedDetails(List<ExtendedField> extendedDetails) {
		this.extendedDetails = extendedDetails;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public long getFeeAccounting() {
		return feeAccounting;
	}

	public void setFeeAccounting(long feeAccounting) {
		this.feeAccounting = feeAccounting;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public BigDecimal getPaidAmt() {
		return paidAmt;
	}

	public void setPaidAmt(BigDecimal paidAmt) {
		this.paidAmt = paidAmt;
	}

	public BigDecimal getWaivedAmt() {
		return waivedAmt;
	}

	public void setWaivedAmt(BigDecimal waivedAmt) {
		this.waivedAmt = waivedAmt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getFlpDays() {
		return flpDays;
	}

	public void setFlpDays(int flpDays) {
		this.flpDays = flpDays;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public boolean isCancelAfterFLP() {
		return cancelAfterFLP;
	}

	public void setCancelAfterFLP(boolean cancelAfterFLP) {
		this.cancelAfterFLP = cancelAfterFLP;
	}

	public String getOldVasReference() {
		return oldVasReference;
	}

	public void setOldVasReference(String oldVasReference) {
		this.oldVasReference = oldVasReference;
	}

	public String getServiceReqNumber() {
		return serviceReqNumber;
	}

	public void setServiceReqNumber(String serviceReqNumber) {
		this.serviceReqNumber = serviceReqNumber;
	}

	public boolean isInsuranceCancel() {
		return insuranceCancel;
	}

	public void setInsuranceCancel(boolean insuranceCancel) {
		this.insuranceCancel = insuranceCancel;
	}

	public BigDecimal getCancelAmt() {
		return cancelAmt;
	}

	public void setCancelAmt(BigDecimal cancelAmt) {
		this.cancelAmt = cancelAmt;
	}

	public long getManualAdviseId() {
		return manualAdviseId;
	}

	public void setManualAdviseId(long manualAdviseId) {
		this.manualAdviseId = manualAdviseId;
	}

	public long getPaymentInsId() {
		return paymentInsId;
	}

	public void setPaymentInsId(long paymentInsId) {
		this.paymentInsId = paymentInsId;
	}

	public long getReceivableAdviseId() {
		return receivableAdviseId;
	}

	public void setReceivableAdviseId(long receivableAdviseId) {
		this.receivableAdviseId = receivableAdviseId;
	}

	public BigDecimal getPartnerPremiumAmt() {
		return partnerPremiumAmt;
	}

	public void setPartnerPremiumAmt(BigDecimal partnerPremiumAmt) {
		this.partnerPremiumAmt = partnerPremiumAmt;
	}

	public boolean isTermInsuranceLien() {
		return termInsuranceLien;
	}

	public void setTermInsuranceLien(boolean termInsuranceLien) {
		this.termInsuranceLien = termInsuranceLien;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public boolean isMedicalApplicable() {
		return medicalApplicable;
	}

	public void setMedicalApplicable(boolean medicalApplicable) {
		this.medicalApplicable = medicalApplicable;
	}

	public String getMedicalStatus() {
		return medicalStatus;
	}

	public void setMedicalStatus(String medicalStatus) {
		this.medicalStatus = medicalStatus;
	}

	public String getInsStatus() {
		return insStatus;
	}

	public void setInsStatus(String insStatus) {
		this.insStatus = insStatus;
	}

}