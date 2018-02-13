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
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>VASRecording table</b>.<br>
 * 
 */
@XmlType(propOrder = { "cif","finReference","collateralRef","productCode", "postingAgainst", "primaryLinkRef", "vasReference", "fee",
		"feePaymentMode", "valueDate", "accrualTillDate", "recurringDate", "dsaId", "dmaId", "fulfilOfficerId",
		"referralId","renewalFee","vasStatus","extendedDetails", "documents","waivedAmt","returnStatus" })
@XmlRootElement(name = "vasDetail")
@XmlAccessorType(XmlAccessType.NONE)
public class VASRecording extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement(name="product")
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
	
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private VASRecording befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	
	private ExtendedFieldRender					extendedFieldRender	= new ExtendedFieldRender();
	private VASConfiguration					vasConfiguration	= null;
	private List<FinanceReferenceDetail>		aggrements			= null;
	private List<FinanceCheckListReference>		vasCheckLists		= null;
	private Map<Long, Long>						selAnsCountMap		= new HashMap<Long, Long>(1);
	@XmlElementWrapper(name="documents")
	@XmlElement(name="document")
	private List<DocumentDetails>				documents			= null;
	private HashMap<String, List<AuditDetail>>	auditDetailMap		= new HashMap<String, List<AuditDetail>>();
	private List<FinanceReferenceDetail>		checkLists			= null;
	private VasCustomer							vasCustomer			= null;
	private List<ReturnDataSet> 				returnDataSetList 	= new ArrayList<ReturnDataSet>(1);
	private List<FinFeeDetail>					finFeeDetailsList	= new ArrayList<FinFeeDetail>();
	@XmlElementWrapper(name="extendedDetails")
	@XmlElement(name="extendedDetail")
	private List<ExtendedField> extendedDetails = null;
	//API Specific
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

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
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

	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> vasRecordingMap = new HashMap<String, Object>();	
		
		return getDeclaredFieldValues(vasRecordingMap);
	}

	public HashMap<String, Object> getDeclaredFieldValues(HashMap<String, Object> vasRecordingMap) {
		//feeMap.put(String.valueOf(fee), getFee());
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				vasRecordingMap.put("vr_" + this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return vasRecordingMap;
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

}