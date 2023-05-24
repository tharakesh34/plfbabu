package com.pennanttech.external.gst.model;

import java.math.BigDecimal;
import java.util.Date;

public class GSTRequestDetail {

	private String requestType;
	private String customerId;
	private String accountId;
	private String gstin;
	private String serviceCode = "";
	private String hsn;
	private String transactionCode;
	private BigDecimal transactionVolume = null;
	private BigDecimal transactionValue = null;
	private BigDecimal transactionPricedCharge = null;
	private String chargeInclusiveOfTax = "Y";
	private BigDecimal taxAmount = null;
	private Date transactionDate;
	private String transactionUid;
	private String sourceBranch = null;
	private String sourceState;
	private String destinationBranch = null;
	private String destinationState;
	private String currencyCode = null;
	private String channel = null;
	private String sourceSystem = "PLF";
	private String customerExempt;
	private String accountExempt = "N";
	private String branchExempt = "N";
	private String serviceChargeExempt = "N";
	private String transactionExempt = "N";
	private String relatedEntity = "N";
	private BigDecimal standardFee = null;
	private String reversalTransactionFlag;
	private String originalTransactionId;
	private String taxSplitInsourceSystem = "N";
	private BigDecimal cgstValue = null;
	private BigDecimal sgstValue = null;
	private BigDecimal igstValue = null;
	private BigDecimal utGstValue = null;
	private String cgstSgstUtgstState = null;
	private String glAccountId;
	private String gstInvoiceNumber;
	private String usercField1 = null;
	private String usercField2 = "LEA";
	private String usercField3 = null;
	private String usercField4 = null;
	private String usercField5 = null;
	private BigDecimal usernField1 = null;
	private BigDecimal usernField2 = null;
	private Date userdField1 = null;
	private Date userdField2 = null;
	private String actualUserId = null;
	private String userDepartment = null;
	private Date requestDate;
	private String revenueAccountNumber = null;
	private String igstAccountNumber = null;
	private String cgstAccountNumber = null;
	private String sgstAccountNumber = null;
	private String utgstAccountNumber = null;
	private BigDecimal cgstCess1 = null;
	private BigDecimal cgstCess2 = null;
	private BigDecimal cgstCess3 = null;
	private BigDecimal sgstCess1 = null;
	private BigDecimal sgstCess2 = null;
	private BigDecimal sgstCess3 = null;
	private BigDecimal igstCess1 = null;
	private BigDecimal igstCess2 = null;
	private BigDecimal igstCess3 = null;
	private BigDecimal utgstCess1 = null;
	private BigDecimal utgstCess2 = null;
	private BigDecimal utgstCess3 = null;
	private BigDecimal gstRate = null;
	private BigDecimal cgstRate = null;
	private BigDecimal sgstRate = null;
	private BigDecimal igstRate = null;
	private BigDecimal utgstRate = null;
	private String customerName;
	private String cessApplicable = "N";
	private String businessUnit = null;
	private String gstrField1 = null;
	private String gstrField2 = null;
	private String gstrField3 = null;
	private String gstrField4 = null;
	private String gstrField5 = null;
	private String gstrField6 = null;
	private String gstrField7 = null;
	private String gstrField8 = null;
	private String gstrField9 = null;
	private String gstrField10 = null;
	private String gstrField11 = null;
	private String gstrField12 = null;
	private Date gstrField13 = null;
	private String gstrField14 = null;
	private String gstrField15 = null;
	private String usercField6 = null;
	private String usercField7 = null;
	private String usercField8 = null;
	private String usercField9 = null;
	private String usercField10 = null;
	private String usercField11 = null;
	private String usercField12 = null;
	private String usercField13 = null;
	private String usercField14 = null;
	private String usercField15 = null;
	private BigDecimal usernField3 = null;
	private BigDecimal usernField4 = null;
	private Date userdField3 = null;
	private Date userdField4 = null;
	private long gstVoucherId;
	private String amountType;
	private String referenceField;
	private long reference;
	private BigDecimal amountPaid;
	private String misc;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

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

	public String getHsn() {
		return hsn;
	}

	public void setHsn(String hsn) {
		this.hsn = hsn;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public BigDecimal getTransactionVolume() {
		return transactionVolume;
	}

	public void setTransactionVolume(BigDecimal transactionVolume) {
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

	public String getTransactionUid() {
		return transactionUid;
	}

	public void setTransactionUid(String transactionUid) {
		this.transactionUid = transactionUid;
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

	public String getTaxSplitInsourceSystem() {
		return taxSplitInsourceSystem;
	}

	public void setTaxSplitInsourceSystem(String taxSplitInsourceSystem) {
		this.taxSplitInsourceSystem = taxSplitInsourceSystem;
	}

	public BigDecimal getCgstValue() {
		return cgstValue;
	}

	public void setCgstValue(BigDecimal cgstValue) {
		this.cgstValue = cgstValue;
	}

	public BigDecimal getSgstValue() {
		return sgstValue;
	}

	public void setSgstValue(BigDecimal sgstValue) {
		this.sgstValue = sgstValue;
	}

	public BigDecimal getIgstValue() {
		return igstValue;
	}

	public void setIgstValue(BigDecimal igstValue) {
		this.igstValue = igstValue;
	}

	public BigDecimal getUtGstValue() {
		return utGstValue;
	}

	public void setUtGstValue(BigDecimal utGstValue) {
		this.utGstValue = utGstValue;
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

	public String getGstInvoiceNumber() {
		return gstInvoiceNumber;
	}

	public void setGstInvoiceNumber(String gstInvoiceNumber) {
		this.gstInvoiceNumber = gstInvoiceNumber;
	}

	public String getUsercField1() {
		return usercField1;
	}

	public void setUsercField1(String usercField1) {
		this.usercField1 = usercField1;
	}

	public String getUsercField2() {
		return usercField2;
	}

	public void setUsercField2(String usercField2) {
		this.usercField2 = usercField2;
	}

	public String getUsercField3() {
		return usercField3;
	}

	public void setUsercField3(String usercField3) {
		this.usercField3 = usercField3;
	}

	public String getUsercField4() {
		return usercField4;
	}

	public void setUsercField4(String usercField4) {
		this.usercField4 = usercField4;
	}

	public String getUsercField5() {
		return usercField5;
	}

	public void setUsercField5(String usercField5) {
		this.usercField5 = usercField5;
	}

	public BigDecimal getUsernField1() {
		return usernField1;
	}

	public void setUsernField1(BigDecimal usernField1) {
		this.usernField1 = usernField1;
	}

	public BigDecimal getUsernField2() {
		return usernField2;
	}

	public void setUsernField2(BigDecimal usernField2) {
		this.usernField2 = usernField2;
	}

	public Date getUserdField1() {
		return userdField1;
	}

	public void setUserdField1(Date userdField1) {
		this.userdField1 = userdField1;
	}

	public Date getUserdField2() {
		return userdField2;
	}

	public void setUserdField2(Date userdField2) {
		this.userdField2 = userdField2;
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

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getRevenueAccountNumber() {
		return revenueAccountNumber;
	}

	public void setRevenueAccountNumber(String revenueAccountNumber) {
		this.revenueAccountNumber = revenueAccountNumber;
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public String getGstrField1() {
		return gstrField1;
	}

	public void setGstrField1(String gstrField1) {
		this.gstrField1 = gstrField1;
	}

	public String getGstrField2() {
		return gstrField2;
	}

	public void setGstrField2(String gstrField2) {
		this.gstrField2 = gstrField2;
	}

	public String getGstrField3() {
		return gstrField3;
	}

	public void setGstrField3(String gstrField3) {
		this.gstrField3 = gstrField3;
	}

	public String getGstrField4() {
		return gstrField4;
	}

	public void setGstrField4(String gstrField4) {
		this.gstrField4 = gstrField4;
	}

	public String getGstrField5() {
		return gstrField5;
	}

	public void setGstrField5(String gstrField5) {
		this.gstrField5 = gstrField5;
	}

	public String getGstrField6() {
		return gstrField6;
	}

	public void setGstrField6(String gstrField6) {
		this.gstrField6 = gstrField6;
	}

	public String getGstrField7() {
		return gstrField7;
	}

	public void setGstrField7(String gstrField7) {
		this.gstrField7 = gstrField7;
	}

	public String getGstrField8() {
		return gstrField8;
	}

	public void setGstrField8(String gstrField8) {
		this.gstrField8 = gstrField8;
	}

	public String getGstrField9() {
		return gstrField9;
	}

	public void setGstrField9(String gstrField9) {
		this.gstrField9 = gstrField9;
	}

	public String getGstrField10() {
		return gstrField10;
	}

	public void setGstrField10(String gstrField10) {
		this.gstrField10 = gstrField10;
	}

	public String getGstrField11() {
		return gstrField11;
	}

	public void setGstrField11(String gstrField11) {
		this.gstrField11 = gstrField11;
	}

	public String getGstrField12() {
		return gstrField12;
	}

	public void setGstrField12(String gstrField12) {
		this.gstrField12 = gstrField12;
	}

	public Date getGstrField13() {
		return gstrField13;
	}

	public void setGstrField13(Date gstrField13) {
		this.gstrField13 = gstrField13;
	}

	public String getGstrField14() {
		return gstrField14;
	}

	public void setGstrField14(String gstrField14) {
		this.gstrField14 = gstrField14;
	}

	public String getGstrField15() {
		return gstrField15;
	}

	public void setGstrField15(String gstrField15) {
		this.gstrField15 = gstrField15;
	}

	public String getUsercField6() {
		return usercField6;
	}

	public void setUsercField6(String usercField6) {
		this.usercField6 = usercField6;
	}

	public String getUsercField7() {
		return usercField7;
	}

	public void setUsercField7(String usercField7) {
		this.usercField7 = usercField7;
	}

	public String getUsercField8() {
		return usercField8;
	}

	public void setUsercField8(String usercField8) {
		this.usercField8 = usercField8;
	}

	public String getUsercField9() {
		return usercField9;
	}

	public void setUsercField9(String usercField9) {
		this.usercField9 = usercField9;
	}

	public String getUsercField10() {
		return usercField10;
	}

	public void setUsercField10(String usercField10) {
		this.usercField10 = usercField10;
	}

	public String getUsercField11() {
		return usercField11;
	}

	public void setUsercField11(String usercField11) {
		this.usercField11 = usercField11;
	}

	public String getUsercField12() {
		return usercField12;
	}

	public void setUsercField12(String usercField12) {
		this.usercField12 = usercField12;
	}

	public String getUsercField13() {
		return usercField13;
	}

	public void setUsercField13(String usercField13) {
		this.usercField13 = usercField13;
	}

	public String getUsercField14() {
		return usercField14;
	}

	public void setUsercField14(String usercField14) {
		this.usercField14 = usercField14;
	}

	public String getUsercField15() {
		return usercField15;
	}

	public void setUsercField15(String usercField15) {
		this.usercField15 = usercField15;
	}

	public BigDecimal getUsernField3() {
		return usernField3;
	}

	public void setUsernField3(BigDecimal usernField3) {
		this.usernField3 = usernField3;
	}

	public BigDecimal getUsernField4() {
		return usernField4;
	}

	public void setUsernField4(BigDecimal usernField4) {
		this.usernField4 = usernField4;
	}

	public Date getUserdField3() {
		return userdField3;
	}

	public void setUserdField3(Date userdField3) {
		this.userdField3 = userdField3;
	}

	public Date getUserdField4() {
		return userdField4;
	}

	public void setUserdField4(Date userdField4) {
		this.userdField4 = userdField4;
	}

	public long getGstVoucherId() {
		return gstVoucherId;
	}

	public void setGstVoucherId(long gstVoucherId) {
		this.gstVoucherId = gstVoucherId;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public String getReferenceField() {
		return referenceField;
	}

	public void setReferenceField(String referenceField) {
		this.referenceField = referenceField;
	}

	public long getReference() {
		return reference;
	}

	public void setReference(long reference) {
		this.reference = reference;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getMisc() {
		return misc;
	}

	public void setMisc(String misc) {
		this.misc = misc;
	}
}
