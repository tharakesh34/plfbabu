package com.pennanttech.external.gst.model;

import java.math.BigDecimal;
import java.util.Date;

public class GSTRespDetail {
	private String requestType;
	private String customerId;
	private String accountId;
	private String gstin;
	private String serviceCode;
	private String sac;
	private String transactionCode;
	private long transactionVolume;
	private BigDecimal transactionValue;
	private BigDecimal transactionPricedCharge;
	private String chargeInclusiveOfTax;
	private BigDecimal taxAmount;
	private Date transactionDate;
	private Long transactionUID;
	private String sourceBranch;
	private String sourceState;
	private String destinationBranch;
	private String destinationState;
	private String currencyCode;
	private String channel;
	private String sourceSystem;
	private String customerExempt;
	private String accountExempt;
	private String branchExempt;
	private String serviceChargeExempt;
	private String transactionExempt;
	private String relatedEntity;
	private BigDecimal standardFee;
	private String reversalTransactionFlag;
	private String originalTransactionId;
	private String userCField1;
	private String userCField2;
	private String userCField3;
	private String userCField4;
	private String userCField5;
	private long userNField1;
	private long userNField2;
	private String userDField1;
	private String userDField2;
	private String actualUserId;
	private String userDepartment;
	private String requestDate;
	private String successStatus;
	private String failureReason;
	private String gstExempted;
	private String gstExemptReason;
	private BigDecimal gstTaxAmount;
	private String igstAccountNumber;
	private String cgstAccountNumber;
	private String sgstAccountNumber;
	private String utgstAccountNumber;
	private BigDecimal igstAmount;
	private BigDecimal cgstAmount;
	private BigDecimal sgstAmount;
	private BigDecimal utgstAmount;
	private String gstInvoiceNumber;
	private String gstInvoiceDate;
	private String cgstSgstUtgstState;
	private String glAccountId;
	private BigDecimal cgstCess1;
	private BigDecimal cgstCess2;
	private BigDecimal cgstCess3;
	private BigDecimal sgstCess1;
	private BigDecimal sgstCess2;
	private BigDecimal sgstCess3;
	private BigDecimal igstCess1;
	private BigDecimal igstCess2;
	private BigDecimal igstCess3;
	private BigDecimal utgstCess1;
	private BigDecimal utgstCess2;
	private BigDecimal utgstCess3;
	private BigDecimal gstRate;
	private BigDecimal cgstRate;
	private BigDecimal sgstRate;
	private BigDecimal igstRate;
	private BigDecimal utgstRate;
	private String systemCField1;
	private String systemCField2;
	private String systemCField3;
	private String systemCField4;
	private String systemCField5;
	private long systemNField1;
	private long systemNField2;
	private String systemDField1;
	private String systemDField2;
	private String transactionType;
	private String invoiceType;
	private String orginalInvoiceNumber;
	private String originalInvoiceDate;
	private String gstinOfBank;
	private String customerName;
	private String bankName;
	private String sacDescription;
	private String transactionIndicator;
	private String glBranch;
	private BigDecimal totalInvoiceValue;
	private String cessApplicable;
	private String businessUnit;
	private String gstRField1;
	private String gstRField2;
	private String gstRField3;
	private String gstRField4;
	private String gstRField5;
	private String gstRField6;
	private String gstRField7;
	private String gstRField8;
	private String gstRField9;
	private String gstRField10;
	private String gstRField11;
	private String gstRField12;
	private Date gstRField13;
	private String gstRField14;
	private String gstRField15;
	private String userCField6;
	private String userCField7;
	private String userCField8;
	private String userCField9;
	private String userCField10;
	private String userCField11;
	private String userCField12;
	private String userCField13;
	private String userCField14;
	private String userCField15;
	private long userNField3;
	private long userNField4;
	private Date userDField3;
	private Date userDField4;
	private BigDecimal taxRoundingDifference;

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getSac() {
		return sac;
	}

	public void setSac(String sac) {
		this.sac = sac;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public long getTransactionVolume() {
		return transactionVolume;
	}

	public void setTransactionVolume(long transactionVolume) {
		this.transactionVolume = transactionVolume;
	}

	public BigDecimal getTransactionValue() {
		return transactionValue;
	}

	public void setTransactionValue(BigDecimal transactionValue) {
		this.transactionValue = transactionValue;
	}

	public BigDecimal getTransactionPricedCharge() {
		return transactionPricedCharge;
	}

	public void setTransactionPricedCharge(BigDecimal transactionPricedCharge) {
		this.transactionPricedCharge = transactionPricedCharge;
	}

	public String getChargeInclusiveOfTax() {
		return chargeInclusiveOfTax;
	}

	public void setChargeInclusiveOfTax(String chargeInclusiveOfTax) {
		this.chargeInclusiveOfTax = chargeInclusiveOfTax;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Long getTransactionUID() {
		return transactionUID;
	}

	public void setTransactionUID(Long transactionUID) {
		this.transactionUID = transactionUID;
	}

	public String getSourceBranch() {
		return sourceBranch;
	}

	public void setSourceBranch(String sourceBranch) {
		this.sourceBranch = sourceBranch;
	}

	public String getSourceState() {
		return sourceState;
	}

	public void setSourceState(String sourceState) {
		this.sourceState = sourceState;
	}

	public String getDestinationBranch() {
		return destinationBranch;
	}

	public void setDestinationBranch(String destinationBranch) {
		this.destinationBranch = destinationBranch;
	}

	public String getDestinationState() {
		return destinationState;
	}

	public void setDestinationState(String destinationState) {
		this.destinationState = destinationState;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getCustomerExempt() {
		return customerExempt;
	}

	public void setCustomerExempt(String customerExempt) {
		this.customerExempt = customerExempt;
	}

	public String getAccountExempt() {
		return accountExempt;
	}

	public void setAccountExempt(String accountExempt) {
		this.accountExempt = accountExempt;
	}

	public String getBranchExempt() {
		return branchExempt;
	}

	public void setBranchExempt(String branchExempt) {
		this.branchExempt = branchExempt;
	}

	public String getServiceChargeExempt() {
		return serviceChargeExempt;
	}

	public void setServiceChargeExempt(String serviceChargeExempt) {
		this.serviceChargeExempt = serviceChargeExempt;
	}

	public String getTransactionExempt() {
		return transactionExempt;
	}

	public void setTransactionExempt(String transactionExempt) {
		this.transactionExempt = transactionExempt;
	}

	public String getRelatedEntity() {
		return relatedEntity;
	}

	public void setRelatedEntity(String relatedEntity) {
		this.relatedEntity = relatedEntity;
	}

	public BigDecimal getStandardFee() {
		return standardFee;
	}

	public void setStandardFee(BigDecimal standardFee) {
		this.standardFee = standardFee;
	}

	public String getReversalTransactionFlag() {
		return reversalTransactionFlag;
	}

	public void setReversalTransactionFlag(String reversalTransactionFlag) {
		this.reversalTransactionFlag = reversalTransactionFlag;
	}

	public String getOriginalTransactionId() {
		return originalTransactionId;
	}

	public void setOriginalTransactionId(String originalTransactionId) {
		this.originalTransactionId = originalTransactionId;
	}

	public String getUserCField1() {
		return userCField1;
	}

	public void setUserCField1(String userCField1) {
		this.userCField1 = userCField1;
	}

	public String getUserCField2() {
		return userCField2;
	}

	public void setUserCField2(String userCField2) {
		this.userCField2 = userCField2;
	}

	public String getUserCField3() {
		return userCField3;
	}

	public void setUserCField3(String userCField3) {
		this.userCField3 = userCField3;
	}

	public String getUserCField4() {
		return userCField4;
	}

	public void setUserCField4(String userCField4) {
		this.userCField4 = userCField4;
	}

	public String getUserCField5() {
		return userCField5;
	}

	public void setUserCField5(String userCField5) {
		this.userCField5 = userCField5;
	}

	public long getUserNField1() {
		return userNField1;
	}

	public void setUserNField1(long userNField1) {
		this.userNField1 = userNField1;
	}

	public long getUserNField2() {
		return userNField2;
	}

	public void setUserNField2(long userNField2) {
		this.userNField2 = userNField2;
	}

	public String getUserDField1() {
		return userDField1;
	}

	public void setUserDField1(String userDField1) {
		this.userDField1 = userDField1;
	}

	public String getUserDField2() {
		return userDField2;
	}

	public void setUserDField2(String userDField2) {
		this.userDField2 = userDField2;
	}

	public String getActualUserId() {
		return actualUserId;
	}

	public void setActualUserId(String actualUserId) {
		this.actualUserId = actualUserId;
	}

	public String getUserDepartment() {
		return userDepartment;
	}

	public void setUserDepartment(String userDepartment) {
		this.userDepartment = userDepartment;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getSuccessStatus() {
		return successStatus;
	}

	public void setSuccessStatus(String successStatus) {
		this.successStatus = successStatus;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public String getGstExempted() {
		return gstExempted;
	}

	public void setGstExempted(String gstExempted) {
		this.gstExempted = gstExempted;
	}

	public String getGstExemptReason() {
		return gstExemptReason;
	}

	public void setGstExemptReason(String gstExemptReason) {
		this.gstExemptReason = gstExemptReason;
	}

	public BigDecimal getGstTaxAmount() {
		return gstTaxAmount;
	}

	public void setGstTaxAmount(BigDecimal gstTaxAmount) {
		this.gstTaxAmount = gstTaxAmount;
	}

	public String getIgstAccountNumber() {
		return igstAccountNumber;
	}

	public void setIgstAccountNumber(String igstAccountNumber) {
		this.igstAccountNumber = igstAccountNumber;
	}

	public String getCgstAccountNumber() {
		return cgstAccountNumber;
	}

	public void setCgstAccountNumber(String cgstAccountNumber) {
		this.cgstAccountNumber = cgstAccountNumber;
	}

	public String getSgstAccountNumber() {
		return sgstAccountNumber;
	}

	public void setSgstAccountNumber(String sgstAccountNumber) {
		this.sgstAccountNumber = sgstAccountNumber;
	}

	public String getUtgstAccountNumber() {
		return utgstAccountNumber;
	}

	public void setUtgstAccountNumber(String utgstAccountNumber) {
		this.utgstAccountNumber = utgstAccountNumber;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getUtgstAmount() {
		return utgstAmount;
	}

	public void setUtgstAmount(BigDecimal utgstAmount) {
		this.utgstAmount = utgstAmount;
	}

	public String getGstInvoiceNumber() {
		return gstInvoiceNumber;
	}

	public void setGstInvoiceNumber(String gstInvoiceNumber) {
		this.gstInvoiceNumber = gstInvoiceNumber;
	}

	public String getGstInvoiceDate() {
		return gstInvoiceDate;
	}

	public void setGstInvoiceDate(String gstInvoiceDate) {
		this.gstInvoiceDate = gstInvoiceDate;
	}

	public String getCgstSgstUtgstState() {
		return cgstSgstUtgstState;
	}

	public void setCgstSgstUtgstState(String cgstSgstUtgstState) {
		this.cgstSgstUtgstState = cgstSgstUtgstState;
	}

	public String getGlAccountId() {
		return glAccountId;
	}

	public void setGlAccountId(String glAccountId) {
		this.glAccountId = glAccountId;
	}

	public BigDecimal getCgstCess1() {
		return cgstCess1;
	}

	public void setCgstCess1(BigDecimal cgstCess1) {
		this.cgstCess1 = cgstCess1;
	}

	public BigDecimal getCgstCess2() {
		return cgstCess2;
	}

	public void setCgstCess2(BigDecimal cgstCess2) {
		this.cgstCess2 = cgstCess2;
	}

	public BigDecimal getCgstCess3() {
		return cgstCess3;
	}

	public void setCgstCess3(BigDecimal cgstCess3) {
		this.cgstCess3 = cgstCess3;
	}

	public BigDecimal getSgstCess1() {
		return sgstCess1;
	}

	public void setSgstCess1(BigDecimal sgstCess1) {
		this.sgstCess1 = sgstCess1;
	}

	public BigDecimal getSgstCess2() {
		return sgstCess2;
	}

	public void setSgstCess2(BigDecimal sgstCess2) {
		this.sgstCess2 = sgstCess2;
	}

	public BigDecimal getSgstCess3() {
		return sgstCess3;
	}

	public void setSgstCess3(BigDecimal sgstCess3) {
		this.sgstCess3 = sgstCess3;
	}

	public BigDecimal getIgstCess1() {
		return igstCess1;
	}

	public void setIgstCess1(BigDecimal igstCess1) {
		this.igstCess1 = igstCess1;
	}

	public BigDecimal getIgstCess2() {
		return igstCess2;
	}

	public void setIgstCess2(BigDecimal igstCess2) {
		this.igstCess2 = igstCess2;
	}

	public BigDecimal getIgstCess3() {
		return igstCess3;
	}

	public void setIgstCess3(BigDecimal igstCess3) {
		this.igstCess3 = igstCess3;
	}

	public BigDecimal getUtgstCess1() {
		return utgstCess1;
	}

	public void setUtgstCess1(BigDecimal utgstCess1) {
		this.utgstCess1 = utgstCess1;
	}

	public BigDecimal getUtgstCess2() {
		return utgstCess2;
	}

	public void setUtgstCess2(BigDecimal utgstCess2) {
		this.utgstCess2 = utgstCess2;
	}

	public BigDecimal getUtgstCess3() {
		return utgstCess3;
	}

	public void setUtgstCess3(BigDecimal utgstCess3) {
		this.utgstCess3 = utgstCess3;
	}

	public BigDecimal getGstRate() {
		return gstRate;
	}

	public void setGstRate(BigDecimal gstRate) {
		this.gstRate = gstRate;
	}

	public BigDecimal getCgstRate() {
		return cgstRate;
	}

	public void setCgstRate(BigDecimal cgstRate) {
		this.cgstRate = cgstRate;
	}

	public BigDecimal getSgstRate() {
		return sgstRate;
	}

	public void setSgstRate(BigDecimal sgstRate) {
		this.sgstRate = sgstRate;
	}

	public BigDecimal getIgstRate() {
		return igstRate;
	}

	public void setIgstRate(BigDecimal igstRate) {
		this.igstRate = igstRate;
	}

	public BigDecimal getUtgstRate() {
		return utgstRate;
	}

	public void setUtgstRate(BigDecimal utgstRate) {
		this.utgstRate = utgstRate;
	}

	public String getSystemCField1() {
		return systemCField1;
	}

	public void setSystemCField1(String systemCField1) {
		this.systemCField1 = systemCField1;
	}

	public String getSystemCField2() {
		return systemCField2;
	}

	public void setSystemCField2(String systemCField2) {
		this.systemCField2 = systemCField2;
	}

	public String getSystemCField3() {
		return systemCField3;
	}

	public void setSystemCField3(String systemCField3) {
		this.systemCField3 = systemCField3;
	}

	public String getSystemCField4() {
		return systemCField4;
	}

	public void setSystemCField4(String systemCField4) {
		this.systemCField4 = systemCField4;
	}

	public String getSystemCField5() {
		return systemCField5;
	}

	public void setSystemCField5(String systemCField5) {
		this.systemCField5 = systemCField5;
	}

	public long getSystemNField1() {
		return systemNField1;
	}

	public void setSystemNField1(long systemNField1) {
		this.systemNField1 = systemNField1;
	}

	public long getSystemNField2() {
		return systemNField2;
	}

	public void setSystemNField2(long systemNField2) {
		this.systemNField2 = systemNField2;
	}

	public String getSystemDField1() {
		return systemDField1;
	}

	public void setSystemDField1(String systemDField1) {
		this.systemDField1 = systemDField1;
	}

	public String getSystemDField2() {
		return systemDField2;
	}

	public void setSystemDField2(String systemDField2) {
		this.systemDField2 = systemDField2;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getOrginalInvoiceNumber() {
		return orginalInvoiceNumber;
	}

	public void setOrginalInvoiceNumber(String orginalInvoiceNumber) {
		this.orginalInvoiceNumber = orginalInvoiceNumber;
	}

	public String getOriginalInvoiceDate() {
		return originalInvoiceDate;
	}

	public void setOriginalInvoiceDate(String originalInvoiceDate) {
		this.originalInvoiceDate = originalInvoiceDate;
	}

	public String getGstinOfBank() {
		return gstinOfBank;
	}

	public void setGstinOfBank(String gstinOfBank) {
		this.gstinOfBank = gstinOfBank;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getSacDescription() {
		return sacDescription;
	}

	public void setSacDescription(String sacDescription) {
		this.sacDescription = sacDescription;
	}

	public String getTransactionIndicator() {
		return transactionIndicator;
	}

	public void setTransactionIndicator(String transactionIndicator) {
		this.transactionIndicator = transactionIndicator;
	}

	public String getGlBranch() {
		return glBranch;
	}

	public void setGlBranch(String glBranch) {
		this.glBranch = glBranch;
	}

	public BigDecimal getTotalInvoiceValue() {
		return totalInvoiceValue;
	}

	public void setTotalInvoiceValue(BigDecimal totalInvoiceValue) {
		this.totalInvoiceValue = totalInvoiceValue;
	}

	public String getCessApplicable() {
		return cessApplicable;
	}

	public void setCessApplicable(String cessApplicable) {
		this.cessApplicable = cessApplicable;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public String getGstRField1() {
		return gstRField1;
	}

	public void setGstRField1(String gstRField1) {
		this.gstRField1 = gstRField1;
	}

	public String getGstRField2() {
		return gstRField2;
	}

	public void setGstRField2(String gstRField2) {
		this.gstRField2 = gstRField2;
	}

	public String getGstRField3() {
		return gstRField3;
	}

	public void setGstRField3(String gstRField3) {
		this.gstRField3 = gstRField3;
	}

	public String getGstRField4() {
		return gstRField4;
	}

	public void setGstRField4(String gstRField4) {
		this.gstRField4 = gstRField4;
	}

	public String getGstRField5() {
		return gstRField5;
	}

	public void setGstRField5(String gstRField5) {
		this.gstRField5 = gstRField5;
	}

	public String getGstRField6() {
		return gstRField6;
	}

	public void setGstRField6(String gstRField6) {
		this.gstRField6 = gstRField6;
	}

	public String getGstRField7() {
		return gstRField7;
	}

	public void setGstRField7(String gstRField7) {
		this.gstRField7 = gstRField7;
	}

	public String getGstRField8() {
		return gstRField8;
	}

	public void setGstRField8(String gstRField8) {
		this.gstRField8 = gstRField8;
	}

	public String getGstRField9() {
		return gstRField9;
	}

	public void setGstRField9(String gstRField9) {
		this.gstRField9 = gstRField9;
	}

	public String getGstRField10() {
		return gstRField10;
	}

	public void setGstRField10(String gstRField10) {
		this.gstRField10 = gstRField10;
	}

	public String getGstRField11() {
		return gstRField11;
	}

	public void setGstRField11(String gstRField11) {
		this.gstRField11 = gstRField11;
	}

	public String getGstRField12() {
		return gstRField12;
	}

	public void setGstRField12(String gstRField12) {
		this.gstRField12 = gstRField12;
	}

	public Date getGstRField13() {
		return gstRField13;
	}

	public void setGstRField13(Date gstRField13) {
		this.gstRField13 = gstRField13;
	}

	public String getGstRField14() {
		return gstRField14;
	}

	public void setGstRField14(String gstRField14) {
		this.gstRField14 = gstRField14;
	}

	public String getGstRField15() {
		return gstRField15;
	}

	public void setGstRField15(String gstRField15) {
		this.gstRField15 = gstRField15;
	}

	public String getUserCField6() {
		return userCField6;
	}

	public void setUserCField6(String userCField6) {
		this.userCField6 = userCField6;
	}

	public String getUserCField7() {
		return userCField7;
	}

	public void setUserCField7(String userCField7) {
		this.userCField7 = userCField7;
	}

	public String getUserCField8() {
		return userCField8;
	}

	public void setUserCField8(String userCField8) {
		this.userCField8 = userCField8;
	}

	public String getUserCField9() {
		return userCField9;
	}

	public void setUserCField9(String userCField9) {
		this.userCField9 = userCField9;
	}

	public String getUserCField10() {
		return userCField10;
	}

	public void setUserCField10(String userCField10) {
		this.userCField10 = userCField10;
	}

	public String getUserCField11() {
		return userCField11;
	}

	public void setUserCField11(String userCField11) {
		this.userCField11 = userCField11;
	}

	public String getUserCField12() {
		return userCField12;
	}

	public void setUserCField12(String userCField12) {
		this.userCField12 = userCField12;
	}

	public String getUserCField13() {
		return userCField13;
	}

	public void setUserCField13(String userCField13) {
		this.userCField13 = userCField13;
	}

	public String getUserCField14() {
		return userCField14;
	}

	public void setUserCField14(String userCField14) {
		this.userCField14 = userCField14;
	}

	public String getUserCField15() {
		return userCField15;
	}

	public void setUserCField15(String userCField15) {
		this.userCField15 = userCField15;
	}

	public long getUserNField3() {
		return userNField3;
	}

	public void setUserNField3(long userNField3) {
		this.userNField3 = userNField3;
	}

	public long getUserNField4() {
		return userNField4;
	}

	public void setUserNField4(long userNField4) {
		this.userNField4 = userNField4;
	}

	public Date getUserDField3() {
		return userDField3;
	}

	public void setUserDField3(Date userDField3) {
		this.userDField3 = userDField3;
	}

	public Date getUserDField4() {
		return userDField4;
	}

	public void setUserDField4(Date userDField4) {
		this.userDField4 = userDField4;
	}

	public BigDecimal getTaxRoundingDifference() {
		return taxRoundingDifference;
	}

	public void setTaxRoundingDifference(BigDecimal taxRoundingDifference) {
		this.taxRoundingDifference = taxRoundingDifference;
	}

}
