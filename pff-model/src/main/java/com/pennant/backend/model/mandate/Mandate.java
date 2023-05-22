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
 * * FileName : Mandate.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * * Modified Date :
 * 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.mandate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Mandate table</b>.<br>
 * 
 */
@XmlType(propOrder = { "custCIF", "orgReference", "useExisting", "mandateID", "mandateRef", "mandateType", "bankCode",
		"branchCode", "iFSC", "mICR", "accType", "accNumber", "accHolderName", "jointAccHolderName", "openMandate",
		"startDate", "expiryDate", "maxLimit", "periodicity", "phoneCountryCode", "phoneAreaCode", "phoneNumber",
		"status", "active", "totEMIAmount", "barCodeNumber", "amountInWords", "entityCode", "swapIsActive",
		"partnerBankId", "partnerBankName", "returnStatus", "mandateStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "mandate")
public class Mandate extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private long mandateID = Long.MIN_VALUE;
	private long requestID;
	@XmlElement
	private String mandateRef;
	@XmlElement
	private boolean openMandate;
	@XmlElement
	private String mandateType;
	@XmlElement
	private String eMandateSource;
	@XmlElement
	private String eMandateReferenceNo;
	private long custID;
	@XmlElement(name = "cif")
	private String custCIF;
	private String custCoreBank;
	private String custShrtName;
	private Long bankBranchID;
	@XmlElement
	private String branchCode;
	private String branchDesc;
	@XmlElement
	private String bankCode;
	private String bankName;
	private String city;
	@XmlElement(name = "micr")
	private String mICR;
	@XmlElement(name = "ifsc")
	private String iFSC;
	@XmlElement
	private String accNumber;
	@XmlElement
	private String accHolderName;
	@XmlElement
	private String jointAccHolderName;
	@XmlElement
	private String accType;
	private String accTypeName;
	@XmlElement
	private Date startDate;
	@XmlElement
	private Date expiryDate;
	@XmlElement
	private BigDecimal maxLimit;
	@XmlElement
	private String periodicity;
	@XmlElement
	private String phoneCountryCode;
	@XmlElement
	private String phoneAreaCode;
	@XmlElement
	private String phoneNumber;
	@XmlElement
	private String status;
	private String statusName;
	@XmlElement
	private String reason;
	private Date inputDate;
	@XmlElement
	private boolean active;
	@XmlElement
	private boolean defaultMandate;
	private String approvalID;
	private String lovValue;
	private Mandate befImage;
	private LoggedInUser userDetails;
	private String module;
	@XmlElement
	private boolean useExisting;
	private String mandateCcy;
	@XmlElement(name = "finReference")
	private String orgReference;
	private String sourceId;
	@XmlElement
	private String documentName;
	private Long documentRef = Long.MIN_VALUE;
	@XmlElement(name = "docContent")
	private byte[] docImage;
	@XmlElement(name = "docRefId")
	private String externalRef;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private BigDecimal totEMIAmount;
	private String pccityName;
	@XmlElement
	private String barCodeNumber;
	@XmlElement
	private String amountInWords;
	@XmlElement(name = "swap")
	private boolean swapIsActive;
	private long primaryMandateId = 0;
	private boolean secondaryMandate;
	private long finID;
	private String finReference;
	private long applId;
	private Date diDate;
	private String sponsorBankCode;
	private String utilityCode;
	private String companyId;
	private String appFormNo;
	private String emailId;
	private String mandateBarCode;
	private Date modificationDate;
	private String processFlag = "N";
	private Date processDate;
	private String finType;
	private String finBranch;
	private Date firstDueDate;
	private String loanBranch;
	private boolean activeFlag = true;
	@XmlElement
	private String entityCode;
	private String entityDesc;
	private boolean approveMandate;
	@XmlElement
	private Long partnerBankId;
	@XmlElement
	private String partnerBankCode;
	private String partnerBankName;
	@XmlElement
	private Boolean pennyDropStatus;
	@XmlElement
	private Date swapEffectiveDate;
	private String holdReason;
	@XmlElement
	private boolean securityMandate;
	@XmlElement
	private Long employerID;
	private String employerName;
	@XmlElement
	private String employeeNo;
	private String finRepayMethod;
	private String alwdRpyMethods;
	private Long oldMandate;
	private Long oldSecMandate;
	private boolean allowDAS;
	// API validation purpose only
	private Mandate validateMandate = this;

	private Date loanMaturityDate;
	private boolean externalMandate;
	private String strExternalMandate;
	private String strOpenMandate;
	private String strSwapIsActive;
	private String strSecurityMandate;
	private Date schdDate;
	private int instalmentNo;

	private ErrorDetail error;
	@XmlElement
	private Boolean mandateStatus;

	public Mandate() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("branchCode");
		excludeFields.add("branchDesc");
		excludeFields.add("bankCode");
		excludeFields.add("bankName");
		excludeFields.add("city");
		excludeFields.add("mICR");
		excludeFields.add("iFSC");
		excludeFields.add("accTypeName");
		excludeFields.add("statusName");
		excludeFields.add("module");
		excludeFields.add("reason");
		excludeFields.add("useExisting");
		excludeFields.add("sourceId");
		excludeFields.add("returnStatus");
		excludeFields.add("totEMIAmount");
		excludeFields.add("pccityName");
		excludeFields.add("requestID");
		excludeFields.add("docImage");
		excludeFields.add("amountInWords");
		excludeFields.add("secondaryMandate");
		excludeFields.add("applId");
		excludeFields.add("diDate");
		excludeFields.add("sponsorBankCode");
		excludeFields.add("utilityCode");
		excludeFields.add("companyId");
		excludeFields.add("appFormNo");
		excludeFields.add("emailId");
		excludeFields.add("mandateBarCode");
		excludeFields.add("modificationDate");
		excludeFields.add("processFlag");
		excludeFields.add("processDate");
		excludeFields.add("finType");
		excludeFields.add("finBranch");
		excludeFields.add("firstDueDate");
		excludeFields.add("activeFlag");
		excludeFields.add("loanBranch");
		excludeFields.add("amountInWords");
		excludeFields.add("finID");
		excludeFields.add("finReference");
		excludeFields.add("entityDesc");
		excludeFields.add("approveMandate");
		excludeFields.add("partnerBankCode");
		excludeFields.add("partnerBankName");
		excludeFields.add("pennyDropStatus");
		excludeFields.add("finRepayMethod");
		excludeFields.add("alwdRpyMethods");
		excludeFields.add("oldMandate");
		excludeFields.add("oldSecMandate");
		excludeFields.add("validateMandate");
		excludeFields.add("allowDAS");
		excludeFields.add("employerName");
		excludeFields.add("loanMaturityDate");
		excludeFields.add("error");
		excludeFields.add("custCoreBank");
		excludeFields.add("externalMandate");
		excludeFields.add("strExternalMandate");
		excludeFields.add("strOpenMandate");
		excludeFields.add("strSwapIsActive");
		excludeFields.add("mandateStatus");
		excludeFields.add("strSecurityMandate");
		excludeFields.add("schdDate");
		excludeFields.add("instalmentNo");
		excludeFields.add("strSecurityMandate");

		return excludeFields;
	}

	public long getMandateID() {
		return mandateID;
	}

	public void setMandateID(long mandateID) {
		this.mandateID = mandateID;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getMandateRef() {
		return mandateRef;
	}

	public void setMandateRef(String mandateRef) {
		this.mandateRef = mandateRef;
	}

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}

	public Long getBankBranchID() {
		return bankBranchID;
	}

	public void setBankBranchID(Long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getAccHolderName() {
		return accHolderName;
	}

	public void setAccHolderName(String accHolderName) {
		this.accHolderName = accHolderName;
	}

	public String getJointAccHolderName() {
		return jointAccHolderName;
	}

	public void setJointAccHolderName(String jointAccHolderName) {
		this.jointAccHolderName = jointAccHolderName;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getAccTypeName() {
		return this.accTypeName;
	}

	public void setAccTypeName(String accTypeName) {
		this.accTypeName = accTypeName;
	}

	public boolean isOpenMandate() {
		return openMandate;
	}

	public void setOpenMandate(boolean openMandate) {
		this.openMandate = openMandate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public BigDecimal getMaxLimit() {
		return maxLimit;
	}

	public void setMaxLimit(BigDecimal maxLimit) {
		this.maxLimit = maxLimit;
	}

	public String getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getPhoneAreaCode() {
		return phoneAreaCode;
	}

	public void setPhoneAreaCode(String phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getApprovalID() {
		return approvalID;
	}

	public void setApprovalID(String approvalID) {
		this.approvalID = approvalID;
	}

	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public Mandate getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Mandate beforeImage) {
		this.befImage = beforeImage;
	}

	@XmlTransient
	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchDesc) {
		this.branchCode = branchDesc;
	}

	public String getMICR() {
		return mICR;
	}

	public void setMICR(String mICR) {
		this.mICR = mICR;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public Date getInputDate() {
		return inputDate;
	}

	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public boolean isUseExisting() {
		return useExisting;
	}

	public void setUseExisting(boolean useExisting) {
		this.useExisting = useExisting;
	}

	public String getIFSC() {
		return iFSC;
	}

	public void setIFSC(String iFSC) {
		this.iFSC = iFSC;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getMandateCcy() {
		return mandateCcy;
	}

	public void setMandateCcy(String mandateCcy) {
		this.mandateCcy = mandateCcy;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getOrgReference() {
		return orgReference;
	}

	public void setOrgReference(String orgReference) {
		this.orgReference = orgReference;
	}

	public BigDecimal getTotEMIAmount() {
		return totEMIAmount;
	}

	public void setTotEMIAmount(BigDecimal totEMIAmount) {
		this.totEMIAmount = totEMIAmount;
	}

	public String getPccityName() {
		return pccityName;
	}

	public void setPccityName(String pccityName) {
		this.pccityName = pccityName;
	}

	public long getRequestID() {
		return requestID;
	}

	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}

	public String getBarCodeNumber() {
		return barCodeNumber;
	}

	public void setBarCodeNumber(String barCodeNumber) {
		this.barCodeNumber = barCodeNumber;
	}

	public String getAmountInWords() {
		return amountInWords;
	}

	public void setAmountInWords(String amountInWords) {
		this.amountInWords = amountInWords;
	}

	public boolean isSwapIsActive() {
		return swapIsActive;
	}

	public void setSwapIsActive(boolean swapIsActive) {
		this.swapIsActive = swapIsActive;
	}

	public long getPrimaryMandateId() {
		return primaryMandateId;
	}

	public void setPrimaryMandateId(long primaryMandateId) {
		this.primaryMandateId = primaryMandateId;
	}

	public boolean isSecondaryMandate() {
		return secondaryMandate;
	}

	public void setSecondaryMandate(boolean secondaryMandate) {
		this.secondaryMandate = secondaryMandate;
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

	public long getApplId() {
		return applId;
	}

	public void setApplId(long applId) {
		this.applId = applId;
	}

	public Date getDiDate() {
		return diDate;
	}

	public void setDiDate(Date diDate) {
		this.diDate = diDate;
	}

	public String getSponsorBankCode() {
		return sponsorBankCode;
	}

	public void setSponsorBankCode(String sponsorBankCode) {
		this.sponsorBankCode = sponsorBankCode;
	}

	public String getUtilityCode() {
		return utilityCode;
	}

	public void setUtilityCode(String utilityCode) {
		this.utilityCode = utilityCode;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getAppFormNo() {
		return appFormNo;
	}

	public void setAppFormNo(String appFormNo) {
		this.appFormNo = appFormNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMandateBarCode() {
		return mandateBarCode;
	}

	public void setMandateBarCode(String mandateBarCode) {
		this.mandateBarCode = mandateBarCode;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(String processFlag) {
		this.processFlag = processFlag;
	}

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public Date getFirstDueDate() {
		return firstDueDate;
	}

	public void setFirstDueDate(Date firstDueDate) {
		this.firstDueDate = firstDueDate;
	}

	public String getLoanBranch() {
		return loanBranch;
	}

	public void setLoanBranch(String loanBranch) {
		this.loanBranch = loanBranch;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
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

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
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

	public String getExternalRef() {
		return externalRef;
	}

	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public boolean isApproveMandate() {
		return approveMandate;
	}

	public void setApproveMandate(boolean approveMandate) {
		this.approveMandate = approveMandate;
	}

	public Long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(Long partnerBankId) {
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

	public Boolean getPennyDropStatus() {
		return pennyDropStatus;
	}

	public void setPennyDropStatus(Boolean pennyDropStatus) {
		this.pennyDropStatus = pennyDropStatus;
	}

	public boolean isDefaultMandate() {
		return defaultMandate;
	}

	public void setDefaultMandate(boolean defaultMandate) {
		this.defaultMandate = defaultMandate;
	}

	public String geteMandateSource() {
		return eMandateSource;
	}

	public void seteMandateSource(String eMandateSource) {
		this.eMandateSource = eMandateSource;
	}

	public String geteMandateReferenceNo() {
		return eMandateReferenceNo;
	}

	public void seteMandateReferenceNo(String eMandateReferenceNo) {
		this.eMandateReferenceNo = eMandateReferenceNo;
	}

	public Date getSwapEffectiveDate() {
		return swapEffectiveDate;
	}

	public void setSwapEffectiveDate(Date swapEffectiveDate) {
		this.swapEffectiveDate = swapEffectiveDate;
	}

	public String getHoldReason() {
		return holdReason;
	}

	public void setHoldReason(String holdReason) {
		this.holdReason = holdReason;
	}

	public boolean isSecurityMandate() {
		return securityMandate;
	}

	public void setSecurityMandate(boolean securityMandate) {
		this.securityMandate = securityMandate;
	}

	public Long getEmployerID() {
		return employerID;
	}

	public void setEmployerID(Long employerID) {
		this.employerID = employerID;
	}

	public String getEmployerName() {
		return employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}

	public String getAlwdRpyMethods() {
		return alwdRpyMethods;
	}

	public void setAlwdRpyMethods(String alwdRpyMethods) {
		this.alwdRpyMethods = alwdRpyMethods;
	}

	public Long getOldMandate() {
		return oldMandate;
	}

	public void setOldMandate(Long oldMandate) {
		this.oldMandate = oldMandate;
	}

	public boolean isAllowDAS() {
		return allowDAS;
	}

	public void setAllowDAS(boolean allowDAS) {
		this.allowDAS = allowDAS;
	}

	public Mandate getValidateMandate() {
		return validateMandate;
	}

	public void setValidateMandate(Mandate validateMandate) {
		this.validateMandate = validateMandate;
	}

	public Date getLoanMaturityDate() {
		return loanMaturityDate;
	}

	public void setLoanMaturityDate(Date loanMaturityDate) {
		this.loanMaturityDate = loanMaturityDate;
	}

	public Long getOldSecMandate() {
		return oldSecMandate;
	}

	public void setOldSecMandate(Long oldSecMandate) {
		this.oldSecMandate = oldSecMandate;
	}

	public ErrorDetail getError() {
		return error;
	}

	public void setError(ErrorDetail error) {
		this.error = error;
	}

	public boolean isExternalMandate() {
		return externalMandate;
	}

	public void setExternalMandate(boolean externalMandate) {
		this.externalMandate = externalMandate;
	}

	public String getStrExternalMandate() {
		return strExternalMandate;
	}

	public void setStrExternalMandate(String strExternalMandate) {
		this.strExternalMandate = strExternalMandate;
	}

	public String getStrOpenMandate() {
		return strOpenMandate;
	}

	public void setStrOpenMandate(String strOpenMandate) {
		this.strOpenMandate = strOpenMandate;
	}

	public String getStrSwapIsActive() {
		return strSwapIsActive;
	}

	public void setStrSwapIsActive(String strSwapIsActive) {
		this.strSwapIsActive = strSwapIsActive;
	}

	public Boolean getMandateStatus() {
		return mandateStatus;
	}

	public void setMandateStatus(Boolean mandateStatus) {
		this.mandateStatus = mandateStatus;
	}

	public String getStrSecurityMandate() {
		return strSecurityMandate;
	}

	public void setStrSecurityMandate(String strSecurityMandate) {
		this.strSecurityMandate = strSecurityMandate;
	}

	public Date getSchdDate() {
		return schdDate;
	}

	public void setSchdDate(Date schdDate) {
		this.schdDate = schdDate;
	}

	public int getInstalmentNo() {
		return instalmentNo;
	}

	public void setInstalmentNo(int instalmentNo) {
		this.instalmentNo = instalmentNo;
	}
}