package com.pennanttech.pff.odsettlementprocess.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class ODSettlementProcess extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 4524370250934339266L;

	private Long id = Long.MIN_VALUE;
	private Long requestBatchId;
	private String terminalId;
	private String merchantName;
	private long custId;
	private String txnId;
	private Timestamp txnDate;
	private String txnType;
	private String reference;
	private String currency;
	private BigDecimal amount;
	private String oDSettlementRef;
	private String name;
	private String fileName;
	private Date endTime;
	private String status;
	private String fileLocation;
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

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public Timestamp getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(Timestamp txnDate) {
		this.txnDate = txnDate;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getoDSettlementRef() {
		return oDSettlementRef;
	}

	public void setoDSettlementRef(String oDSettlementRef) {
		this.oDSettlementRef = oDSettlementRef;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
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
