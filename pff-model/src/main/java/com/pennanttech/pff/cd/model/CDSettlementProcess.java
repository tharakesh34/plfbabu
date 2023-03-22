package com.pennanttech.pff.cd.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class CDSettlementProcess extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private Long id = Long.MIN_VALUE;
	private Long requestBatchId;
	private String settlementRef;
	private String customerRef;
	private String EMIOffer;
	private BigDecimal subPayByManfacturer;
	private BigDecimal subvensionAmount;
	private String custName;
	private String custAddress;
	private String custMobile;
	private String custEmail;
	private String storeName;
	private String storeAddress;
	private String storeCountry;
	private String storeState;
	private String storeCity;
	private String issuer;
	private String category;
	private String description;
	private String serial;
	private String manufacturer;
	private BigDecimal transactionAmount;
	private String acquirer;
	private Long manufactureId = Long.MIN_VALUE;
	private String terminalId;
	private String settlementBatch;
	private String bankInvoice;
	private String authCode;
	private String hostReference;
	private Timestamp transactionDateTime;
	private Timestamp settlementDateTime;
	private String billingInvoice;
	private String transactionStatus;
	private String reason;
	private String productCategory;
	private String productSubCategory1;
	private String productSubCategory2;
	private String modelName;
	private BigDecimal maxValueOfProduct;
	private String merchantName;
	private String name;
	private String fileLocation;
	private String fileName;
	private Date endTime;
	private String status;
	private String alwFileDownload;
	private String postEvent;
	private long configId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRequestBatchId() {
		return requestBatchId;
	}

	public void setRequestBatchId(Long requestBatchId) {
		this.requestBatchId = requestBatchId;
	}

	public String getSettlementRef() {
		return settlementRef;
	}

	public void setSettlementRef(String settlementRef) {
		this.settlementRef = settlementRef;
	}

	public String getCustomerRef() {
		return customerRef;
	}

	public void setCustomerRef(String customerRef) {
		this.customerRef = customerRef;
	}

	public String getEMIOffer() {
		return EMIOffer;
	}

	public void setEMIOffer(String eMIOffer) {
		EMIOffer = eMIOffer;
	}

	public BigDecimal getSubPayByManfacturer() {
		return subPayByManfacturer;
	}

	public void setSubPayByManfacturer(BigDecimal subPayByManfacturer) {
		this.subPayByManfacturer = subPayByManfacturer;
	}

	public BigDecimal getSubvensionAmount() {
		return subvensionAmount;
	}

	public void setSubvensionAmount(BigDecimal subvensionAmount) {
		this.subvensionAmount = subvensionAmount;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustAddress() {
		return custAddress;
	}

	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}

	public String getCustMobile() {
		return custMobile;
	}

	public void setCustMobile(String custMobile) {
		this.custMobile = custMobile;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreAddress() {
		return storeAddress;
	}

	public void setStoreAddress(String storeAddress) {
		this.storeAddress = storeAddress;
	}

	public String getStoreCountry() {
		return storeCountry;
	}

	public void setStoreCountry(String storeCountry) {
		this.storeCountry = storeCountry;
	}

	public String getStoreState() {
		return storeState;
	}

	public void setStoreState(String storeState) {
		this.storeState = storeState;
	}

	public String getStoreCity() {
		return storeCity;
	}

	public void setStoreCity(String storeCity) {
		this.storeCity = storeCity;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public Long getManufactureId() {
		return manufactureId;
	}

	public void setManufactureId(Long manufactureId) {
		this.manufactureId = manufactureId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getSettlementBatch() {
		return settlementBatch;
	}

	public void setSettlementBatch(String settlementBatch) {
		this.settlementBatch = settlementBatch;
	}

	public String getBankInvoice() {
		return bankInvoice;
	}

	public void setBankInvoice(String bankInvoice) {
		this.bankInvoice = bankInvoice;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getHostReference() {
		return hostReference;
	}

	public void setHostReference(String hostReference) {
		this.hostReference = hostReference;
	}

	public Timestamp getTransactionDateTime() {
		return transactionDateTime;
	}

	public void setTransactionDateTime(Timestamp transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}

	public Timestamp getSettlementDateTime() {
		return settlementDateTime;
	}

	public void setSettlementDateTime(Timestamp settlementDateTime) {
		this.settlementDateTime = settlementDateTime;
	}

	public String getBillingInvoice() {
		return billingInvoice;
	}

	public void setBillingInvoice(String billingInvoice) {
		this.billingInvoice = billingInvoice;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getProductSubCategory1() {
		return productSubCategory1;
	}

	public void setProductSubCategory1(String productSubCategory1) {
		this.productSubCategory1 = productSubCategory1;
	}

	public String getProductSubCategory2() {
		return productSubCategory2;
	}

	public void setProductSubCategory2(String productSubCategory2) {
		this.productSubCategory2 = productSubCategory2;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public BigDecimal getMaxValueOfProduct() {
		return maxValueOfProduct;
	}

	public void setMaxValueOfProduct(BigDecimal maxValueOfProduct) {
		this.maxValueOfProduct = maxValueOfProduct;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getName() {
		return name;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public String getFileName() {
		return fileName;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAlwFileDownload() {
		return alwFileDownload;
	}

	public void setAlwFileDownload(String alwFileDownload) {
		this.alwFileDownload = alwFileDownload;
	}

	public String getPostEvent() {
		return postEvent;
	}

	public void setPostEvent(String postEvent) {
		this.postEvent = postEvent;
	}

	public long getConfigId() {
		return configId;
	}

	public void setConfigId(long configId) {
		this.configId = configId;
	}

}
