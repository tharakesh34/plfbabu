package com.pennant.backend.model.receiptupload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ReceiptUploadDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -4601315178356280082L;

	private long uploadheaderId = Long.MIN_VALUE;
	private long uploadDetailId = Long.MIN_VALUE;
	private String rootId = "";
	private String reference = "";
	private String receiptPurpose = "";
	private String excessAdjustTo = "";
	private String allocationType = "";
	private BigDecimal receiptAmount = BigDecimal.ZERO;
	private String effectSchdMethod = "";
	private String remarks = "";
	private Date valueDate;
	private Date receivedDate;
	private String receiptMode = "";
	private String fundingAc = "";
	private String paymentRef = "";
	private String favourNumber = "";
	private String bankCode = "";
	private String chequeNo = "";
	private String transactionRef = "";
	private String status = "";
	private Date depositDate;
	private Date realizationDate;
	private Date instrumentDate;
	private String uploadStatus = "";
	private String reason = "";
	private Long receiptId;
	private long id = 0;
	private String subReceiptMode = "";
	private String receiptChannel = "";
	private String panNumber = "";
	private String extReference = "";
	private Long collectionAgentId;
	private String receivedFrom = "";
	private BigDecimal tdsAmount = BigDecimal.ZERO;
	private LoggedInUser loggedInUser;

	private List<UploadAlloctionDetail> listAllocationDetails = new ArrayList<>();
	private List<ErrorDetail> errorDetails = new ArrayList<>(1);

	public ReceiptUploadDetail() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;

	}

	public boolean isNew() {
		return false;
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

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
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

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public Date getInstrumentDate() {
		return instrumentDate;
	}

	public void setInstrumentDate(Date instrumentDate) {
		this.instrumentDate = instrumentDate;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
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
}