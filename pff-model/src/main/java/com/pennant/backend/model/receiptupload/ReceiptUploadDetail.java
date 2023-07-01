package com.pennant.backend.model.receiptupload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ReceiptUploadDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -4601315178356280082L;

	private long uploadheaderId = Long.MIN_VALUE;
	private long uploadDetailId = Long.MIN_VALUE;
	private String rootId = "";
	private Long finID;
	private String reference = "";
	private String receiptPurpose = "";
	private String excessAdjustTo = "";
	private String allocationType = "";
	private BigDecimal receiptAmount = BigDecimal.ZERO;
	private String strReceiptAmount;
	private String effectSchdMethod = "";
	private String remarks = "";
	private Date valueDate;
	private String strValueDate;
	private Date receivedDate;
	private String strReceivedDate;
	private String receiptMode = "";
	private String fundingAc = "";
	private String paymentRef = "";
	private String favourNumber = "";
	private String bankCode = "";
	private String chequeNo = "";
	private String transactionRef = "";
	private String status = "";
	private Date depositDate;
	private String strDepositDate;
	private Date realizationDate;
	private String strRealizationDate;
	private Date instrumentDate;
	private String strInstrumentDate;
	private int processingStatus;
	private String reason = "";
	private Long receiptId;
	private long id = 0;
	private String subReceiptMode = "";
	private String receiptChannel = "";
	private String panNumber = "";
	private String extReference = "";
	private Long collectionAgentId;
	private String strCollectionAgentId;
	private String receivedFrom = "";
	private BigDecimal tdsAmount = BigDecimal.ZERO;
	private LoggedInUser loggedInUser;
	private Integer threadId;
	private Long bounceId;
	private String bounceReason;
	private String cancelReason;
	private Date bounceDate;
	private String strBounceDate;
	private boolean isNewReceipt;
	private boolean bckdtdWthOldDues;
	private String strBckdtdWthOldDues;
	private Date appDate;
	private String entityCode;
	private String fileName;
	private boolean dedupCheck;
	private boolean receiptdetailExits;

	private List<UploadAlloctionDetail> listAllocationDetails = new ArrayList<>();
	private List<ErrorDetail> errorDetails = new ArrayList<>(1);

	private Set<String> txnKeys = new HashSet<>();
	private Set<String> txnChequeKeys = new HashSet<>();
	private Set<String> receiptValidList = new HashSet<>();
	private String cancelRemarks;
	private String bounceRemarks;

	public ReceiptUploadDetail() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;

	}

	public long getUploadheaderId() {
		return uploadheaderId;
	}

	public void setUploadheaderId(long uploadheaderId) {
		this.uploadheaderId = uploadheaderId;
	}

	public long getUploadDetailId() {
		return uploadDetailId;
	}

	public void setUploadDetailId(long uploadDetailId) {
		this.uploadDetailId = uploadDetailId;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public Long getFinID() {
		return finID;
	}

	public void setFinID(Long finID) {
		this.finID = finID;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getReceiptPurpose() {
		return receiptPurpose;
	}

	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = receiptPurpose;
	}

	public String getExcessAdjustTo() {
		return excessAdjustTo;
	}

	public void setExcessAdjustTo(String excessAdjustTo) {
		this.excessAdjustTo = excessAdjustTo;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getStrReceiptAmount() {
		return strReceiptAmount;
	}

	public void setStrReceiptAmount(String strReceiptAmount) {
		this.strReceiptAmount = strReceiptAmount;
	}

	public String getEffectSchdMethod() {
		return effectSchdMethod;
	}

	public void setEffectSchdMethod(String effectSchdMethod) {
		this.effectSchdMethod = effectSchdMethod;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getStrValueDate() {
		return strValueDate;
	}

	public void setStrValueDate(String strValueDate) {
		this.strValueDate = strValueDate;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getStrReceivedDate() {
		return strReceivedDate;
	}

	public void setStrReceivedDate(String strReceivedDate) {
		this.strReceivedDate = strReceivedDate;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public String getFundingAc() {
		return fundingAc;
	}

	public void setFundingAc(String fundingAc) {
		this.fundingAc = fundingAc;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public String getFavourNumber() {
		return favourNumber;
	}

	public void setFavourNumber(String favourNumber) {
		this.favourNumber = favourNumber;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}

	public String getStrDepositDate() {
		return strDepositDate;
	}

	public void setStrDepositDate(String strDepositDate) {
		this.strDepositDate = strDepositDate;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public String getStrRealizationDate() {
		return strRealizationDate;
	}

	public void setStrRealizationDate(String strRealizationDate) {
		this.strRealizationDate = strRealizationDate;
	}

	public Date getInstrumentDate() {
		return instrumentDate;
	}

	public void setInstrumentDate(Date instrumentDate) {
		this.instrumentDate = instrumentDate;
	}

	public String getStrInstrumentDate() {
		return strInstrumentDate;
	}

	public void setStrInstrumentDate(String strInstrumentDate) {
		this.strInstrumentDate = strInstrumentDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}

	public String getSubReceiptMode() {
		return subReceiptMode;
	}

	public void setSubReceiptMode(String subReceiptMode) {
		this.subReceiptMode = subReceiptMode;
	}

	public String getReceiptChannel() {
		return receiptChannel;
	}

	public void setReceiptChannel(String receiptChannel) {
		this.receiptChannel = receiptChannel;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getExtReference() {
		return extReference;
	}

	public void setExtReference(String extReference) {
		this.extReference = extReference;
	}

	public Long getCollectionAgentId() {
		return collectionAgentId;
	}

	public void setCollectionAgentId(Long collectionAgentId) {
		this.collectionAgentId = collectionAgentId;
	}

	public String getStrCollectionAgentId() {
		return strCollectionAgentId;
	}

	public void setStrCollectionAgentId(String strCollectionAgentId) {
		this.strCollectionAgentId = strCollectionAgentId;
	}

	public String getReceivedFrom() {
		return receivedFrom;
	}

	public void setReceivedFrom(String receivedFrom) {
		this.receivedFrom = receivedFrom;
	}

	public LoggedInUser getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(LoggedInUser loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public List<UploadAlloctionDetail> getListAllocationDetails() {
		return listAllocationDetails;
	}

	public void setListAllocationDetails(List<UploadAlloctionDetail> listAllocationDetails) {
		this.listAllocationDetails = listAllocationDetails;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public BigDecimal getTdsAmount() {
		return tdsAmount;
	}

	public void setTdsAmount(BigDecimal tdsAmount) {
		this.tdsAmount = tdsAmount;
	}

	public Integer getThreadId() {
		return threadId;
	}

	public void setThreadId(Integer threadId) {
		this.threadId = threadId;
	}

	public int getProcessingStatus() {
		return processingStatus;
	}

	public void setProcessingStatus(int processingStatus) {
		this.processingStatus = processingStatus;
	}

	public Long getBounceId() {
		return bounceId;
	}

	public void setBounceId(Long bounceId) {
		this.bounceId = bounceId;
	}

	public String getBounceReason() {
		return bounceReason == null ? "" : bounceReason;
	}

	public void setBounceReason(String bounceReason) {
		this.bounceReason = bounceReason;
	}

	public String getCancelReason() {
		return cancelReason == null ? "" : cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public Date getBounceDate() {
		return bounceDate;
	}

	public void setBounceDate(Date bounceDate) {
		this.bounceDate = bounceDate;
	}

	public String getStrBounceDate() {
		return strBounceDate;
	}

	public void setStrBounceDate(String strBounceDate) {
		this.strBounceDate = strBounceDate;
	}

	public boolean isNewReceipt() {
		return isNewReceipt;
	}

	public void setNewReceipt(boolean isNewReceipt) {
		this.isNewReceipt = isNewReceipt;
	}

	public boolean isBckdtdWthOldDues() {
		return bckdtdWthOldDues;
	}

	public void setBckdtdWthOldDues(boolean bckdtdWthOldDues) {
		this.bckdtdWthOldDues = bckdtdWthOldDues;
	}

	public String getStrBckdtdWthOldDues() {
		return strBckdtdWthOldDues;
	}

	public void setStrBckdtdWthOldDues(String strBckdtdWthOldDues) {
		this.strBckdtdWthOldDues = strBckdtdWthOldDues;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isDedupCheck() {
		return dedupCheck;
	}

	public void setDedupCheck(boolean dedupCheck) {
		this.dedupCheck = dedupCheck;
	}

	public boolean isReceiptdetailExits() {
		return receiptdetailExits;
	}

	public void setReceiptdetailExits(boolean receiptdetailExits) {
		this.receiptdetailExits = receiptdetailExits;
	}

	public Set<String> getTxnKeys() {
		return txnKeys;
	}

	public void setTxnKeys(Set<String> txnKeys) {
		this.txnKeys = txnKeys;
	}

	public Set<String> getTxnChequeKeys() {
		return txnChequeKeys;
	}

	public void setTxnChequeKeys(Set<String> txnChequeKeys) {
		this.txnChequeKeys = txnChequeKeys;
	}

	public Set<String> getReceiptValidList() {
		return receiptValidList;
	}

	public void setReceiptValidList(Set<String> receiptValidList) {
		this.receiptValidList = receiptValidList;
	}

	public String getCancelRemarks() {
		return cancelRemarks;
	}

	public void setCancelRemarks(String cancelRemarks) {
		this.cancelRemarks = cancelRemarks;
	}

	public String getBounceRemarks() {
		return bounceRemarks;
	}

	public void setBounceRemarks(String bounceRemarks) {
		this.bounceRemarks = bounceRemarks;
	}

}