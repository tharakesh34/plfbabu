package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class TaxDownload {

	private long headerId;
	private String extractionType;
	private Date transactionDate;
	private String hostSystemTransactionId;
	private String transactionType;
	private String businessArea;
	private String sourceSystem;
	private String companyCode;
	private String customerName;
	private long customerId;
	private String customerGstin;
	private String customerAddress;
	private String customerStateCode;
	private Date addressChangeDate;
	private String panNo;
	private String hsnSacCode;
	private String natureOfService;
	private String loanAccountNo;
	private String productCode;
	private long chargeCode;
	private long loanBranch;
	private String loanBranchState;
	private String loanServicingBranch;
	private String txnBranchAddress;
	private String txnBranchStateCode;
	private BigDecimal transactionAmount;
	private String reverseChargeApplicable;
	private String invoiceType;
	private String originalInvoiceNo;
	private String bflGstinNo;
	private String invoiceNo;
	private String sfdcDealid;
	private BigDecimal taxableAmount;
	private BigDecimal cgstRate;
	private BigDecimal cgstAmount;
	private BigDecimal cgstSurchargeRate;
	private BigDecimal cgstSurchargeAmount;
	private BigDecimal cgstCessRate;
	private BigDecimal cgstCessAmount;
	private BigDecimal sgstRate;
	private BigDecimal sgstAmount;
	private BigDecimal sgstSurchargeRate;
	private BigDecimal sgstSurchargeAmount;
	private BigDecimal sgstCessRate;
	private BigDecimal sgstCessAmount;
	private BigDecimal igstRate;
	private BigDecimal igstAmount;
	private BigDecimal igstSurchargeRate;
	private BigDecimal igstSurchargeAmount;
	private BigDecimal igstCessRate;
	private BigDecimal igstCessAmount;
	private BigDecimal utgstRate;
	private BigDecimal utgstAmount;
	private BigDecimal utgstSurchargeRate;
	private BigDecimal utgstSurchargeAmount;
	private BigDecimal utgstCessRate;
	private BigDecimal utgstCessAmount;
	private long txnAdviceid;
	private String toState;
	private String fromState;
	private long chequeId;
	private String exempted;
	private Date businessDatetime;
	private Date processDatetime;
	private String processedFlag;
	private long reverseChargecodeId;
	private long agreementId;
	private String considerForGst;
	private String exemptedState;
	private String exemptedCustomer;
	private String loanBranchAddress;

	private String entityName;
	private String entityGSTIN;
	private String ledgerCode;
	private String finBranchId;
	private String registeredCustomer;
	private String interIntraState;
	private String entityCode;
	private BigDecimal amount;
	private String finReference;
	private String branchProvince;

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public String getExtractionType() {
		return extractionType;
	}

	public void setExtractionType(String extractionType) {
		this.extractionType = extractionType;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getHostSystemTransactionId() {
		return hostSystemTransactionId;
	}

	public void setHostSystemTransactionId(String hostSystemTransactionId) {
		this.hostSystemTransactionId = hostSystemTransactionId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getBusinessArea() {
		return businessArea;
	}

	public void setBusinessArea(String businessArea) {
		this.businessArea = businessArea;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerGstin() {
		return customerGstin;
	}

	public void setCustomerGstin(String customerGstin) {
		this.customerGstin = customerGstin;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCustomerStateCode() {
		return customerStateCode;
	}

	public void setCustomerStateCode(String customerStateCode) {
		this.customerStateCode = customerStateCode;
	}

	public Date getAddressChangeDate() {
		return addressChangeDate;
	}

	public void setAddressChangeDate(Date addressChangeDate) {
		this.addressChangeDate = addressChangeDate;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getHsnSacCode() {
		return hsnSacCode;
	}

	public void setHsnSacCode(String hsnSacCode) {
		this.hsnSacCode = hsnSacCode;
	}

	public String getNatureOfService() {
		return natureOfService;
	}

	public void setNatureOfService(String natureOfService) {
		this.natureOfService = natureOfService;
	}

	public String getLoanAccountNo() {
		return loanAccountNo;
	}

	public void setLoanAccountNo(String loanAccountNo) {
		this.loanAccountNo = loanAccountNo;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public long getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(long chargeCode) {
		this.chargeCode = chargeCode;
	}

	public long getLoanBranch() {
		return loanBranch;
	}

	public void setLoanBranch(long loanBranch) {
		this.loanBranch = loanBranch;
	}

	public String getLoanBranchState() {
		return loanBranchState;
	}

	public void setLoanBranchState(String loanBranchState) {
		this.loanBranchState = loanBranchState;
	}

	public String getLoanServicingBranch() {
		return loanServicingBranch;
	}

	public void setLoanServicingBranch(String loanServicingBranch) {
		this.loanServicingBranch = loanServicingBranch;
	}

	public String getTxnBranchAddress() {
		return txnBranchAddress;
	}

	public void setTxnBranchAddress(String txnBranchAddress) {
		this.txnBranchAddress = txnBranchAddress;
	}

	public String getTxnBranchStateCode() {
		return txnBranchStateCode;
	}

	public void setTxnBranchStateCode(String txnBranchStateCode) {
		this.txnBranchStateCode = txnBranchStateCode;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getReverseChargeApplicable() {
		return reverseChargeApplicable;
	}

	public void setReverseChargeApplicable(String reverseChargeApplicable) {
		this.reverseChargeApplicable = reverseChargeApplicable;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getOriginalInvoiceNo() {
		return originalInvoiceNo;
	}

	public void setOriginalInvoiceNo(String originalInvoiceNo) {
		this.originalInvoiceNo = originalInvoiceNo;
	}

	public String getBflGstinNo() {
		return bflGstinNo;
	}

	public void setBflGstinNo(String bflGstinNo) {
		this.bflGstinNo = bflGstinNo;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getSfdcDealid() {
		return sfdcDealid;
	}

	public void setSfdcDealid(String sfdcDealid) {
		this.sfdcDealid = sfdcDealid;
	}

	public BigDecimal getTaxableAmount() {
		return taxableAmount;
	}

	public void setTaxableAmount(BigDecimal taxableAmount) {
		this.taxableAmount = taxableAmount;
	}

	public BigDecimal getCgstRate() {
		return cgstRate;
	}

	public void setCgstRate(BigDecimal cgstRate) {
		this.cgstRate = cgstRate;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getCgstSurchargeRate() {
		return cgstSurchargeRate;
	}

	public void setCgstSurchargeRate(BigDecimal cgstSurchargeRate) {
		this.cgstSurchargeRate = cgstSurchargeRate;
	}

	public BigDecimal getCgstSurchargeAmount() {
		return cgstSurchargeAmount;
	}

	public void setCgstSurchargeAmount(BigDecimal cgstSurchargeAmount) {
		this.cgstSurchargeAmount = cgstSurchargeAmount;
	}

	public BigDecimal getCgstCessRate() {
		return cgstCessRate;
	}

	public void setCgstCessRate(BigDecimal cgstCessRate) {
		this.cgstCessRate = cgstCessRate;
	}

	public BigDecimal getCgstCessAmount() {
		return cgstCessAmount;
	}

	public void setCgstCessAmount(BigDecimal cgstCessAmount) {
		this.cgstCessAmount = cgstCessAmount;
	}

	public BigDecimal getSgstRate() {
		return sgstRate;
	}

	public void setSgstRate(BigDecimal sgstRate) {
		this.sgstRate = sgstRate;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getSgstSurchargeRate() {
		return sgstSurchargeRate;
	}

	public void setSgstSurchargeRate(BigDecimal sgstSurchargeRate) {
		this.sgstSurchargeRate = sgstSurchargeRate;
	}

	public BigDecimal getSgstSurchargeAmount() {
		return sgstSurchargeAmount;
	}

	public void setSgstSurchargeAmount(BigDecimal sgstSurchargeAmount) {
		this.sgstSurchargeAmount = sgstSurchargeAmount;
	}

	public BigDecimal getSgstCessRate() {
		return sgstCessRate;
	}

	public void setSgstCessRate(BigDecimal sgstCessRate) {
		this.sgstCessRate = sgstCessRate;
	}

	public BigDecimal getSgstCessAmount() {
		return sgstCessAmount;
	}

	public void setSgstCessAmount(BigDecimal sgstCessAmount) {
		this.sgstCessAmount = sgstCessAmount;
	}

	public BigDecimal getIgstRate() {
		return igstRate;
	}

	public void setIgstRate(BigDecimal igstRate) {
		this.igstRate = igstRate;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public BigDecimal getIgstSurchargeRate() {
		return igstSurchargeRate;
	}

	public void setIgstSurchargeRate(BigDecimal igstSurchargeRate) {
		this.igstSurchargeRate = igstSurchargeRate;
	}

	public BigDecimal getIgstSurchargeAmount() {
		return igstSurchargeAmount;
	}

	public void setIgstSurchargeAmount(BigDecimal igstSurchargeAmount) {
		this.igstSurchargeAmount = igstSurchargeAmount;
	}

	public BigDecimal getIgstCessRate() {
		return igstCessRate;
	}

	public void setIgstCessRate(BigDecimal igstCessRate) {
		this.igstCessRate = igstCessRate;
	}

	public BigDecimal getIgstCessAmount() {
		return igstCessAmount;
	}

	public void setIgstCessAmount(BigDecimal igstCessAmount) {
		this.igstCessAmount = igstCessAmount;
	}

	public BigDecimal getUtgstRate() {
		return utgstRate;
	}

	public void setUtgstRate(BigDecimal utgstRate) {
		this.utgstRate = utgstRate;
	}

	public BigDecimal getUtgstAmount() {
		return utgstAmount;
	}

	public void setUtgstAmount(BigDecimal utgstAmount) {
		this.utgstAmount = utgstAmount;
	}

	public BigDecimal getUtgstSurchargeRate() {
		return utgstSurchargeRate;
	}

	public void setUtgstSurchargeRate(BigDecimal utgstSurchargeRate) {
		this.utgstSurchargeRate = utgstSurchargeRate;
	}

	public BigDecimal getUtgstSurchargeAmount() {
		return utgstSurchargeAmount;
	}

	public void setUtgstSurchargeAmount(BigDecimal utgstSurchargeAmount) {
		this.utgstSurchargeAmount = utgstSurchargeAmount;
	}

	public BigDecimal getUtgstCessRate() {
		return utgstCessRate;
	}

	public void setUtgstCessRate(BigDecimal utgstCessRate) {
		this.utgstCessRate = utgstCessRate;
	}

	public BigDecimal getUtgstCessAmount() {
		return utgstCessAmount;
	}

	public void setUtgstCessAmount(BigDecimal utgstCessAmount) {
		this.utgstCessAmount = utgstCessAmount;
	}

	public long getTxnAdviceid() {
		return txnAdviceid;
	}

	public void setTxnAdviceid(long txnAdviceid) {
		this.txnAdviceid = txnAdviceid;
	}

	public String getToState() {
		return toState;
	}

	public void setToState(String toState) {
		this.toState = toState;
	}

	public String getFromState() {
		return fromState;
	}

	public void setFromState(String fromState) {
		this.fromState = fromState;
	}

	public long getChequeId() {
		return chequeId;
	}

	public void setChequeId(long chequeId) {
		this.chequeId = chequeId;
	}

	public String getExempted() {
		return exempted;
	}

	public void setExempted(String exempted) {
		this.exempted = exempted;
	}

	public Date getBusinessDatetime() {
		return businessDatetime;
	}

	public void setBusinessDatetime(Date businessDatetime) {
		this.businessDatetime = businessDatetime;
	}

	public Date getProcessDatetime() {
		return processDatetime;
	}

	public void setProcessDatetime(Date processDatetime) {
		this.processDatetime = processDatetime;
	}

	public String getProcessedFlag() {
		return processedFlag;
	}

	public void setProcessedFlag(String processedFlag) {
		this.processedFlag = processedFlag;
	}

	public long getReverseChargecodeId() {
		return reverseChargecodeId;
	}

	public void setReverseChargecodeId(long reverseChargecodeId) {
		this.reverseChargecodeId = reverseChargecodeId;
	}

	public long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	public String getConsiderForGst() {
		return considerForGst;
	}

	public void setConsiderForGst(String considerForGst) {
		this.considerForGst = considerForGst;
	}

	public String getExemptedState() {
		return exemptedState;
	}

	public void setExemptedState(String exemptedState) {
		this.exemptedState = exemptedState;
	}

	public String getExemptedCustomer() {
		return exemptedCustomer;
	}

	public void setExemptedCustomer(String exemptedCustomer) {
		this.exemptedCustomer = exemptedCustomer;
	}

	public String getLoanBranchAddress() {
		return loanBranchAddress;
	}

	public void setLoanBranchAddress(String loanBranchAddress) {
		this.loanBranchAddress = loanBranchAddress;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityGSTIN() {
		return entityGSTIN;
	}

	public void setEntityGSTIN(String entityGSTIN) {
		this.entityGSTIN = entityGSTIN;
	}

	public String getLedgerCode() {
		return ledgerCode;
	}

	public void setLedgerCode(String ledgerCode) {
		this.ledgerCode = ledgerCode;
	}

	public String getFinBranchId() {
		return finBranchId;
	}

	public void setFinBranchId(String finBranchId) {
		this.finBranchId = finBranchId;
	}

	public String getRegisteredCustomer() {
		return registeredCustomer;
	}

	public void setRegisteredCustomer(String registeredCustomer) {
		this.registeredCustomer = registeredCustomer;
	}

	public String getInterIntraState() {
		return interIntraState;
	}

	public void setInterIntraState(String interIntraState) {
		this.interIntraState = interIntraState;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getBranchProvince() {
		return branchProvince;
	}

	public void setBranchProvince(String branchProvince) {
		this.branchProvince = branchProvince;
	}

}
