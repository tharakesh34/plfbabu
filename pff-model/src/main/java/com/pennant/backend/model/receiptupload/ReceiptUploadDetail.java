package com.pennant.backend.model.receiptupload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class ReceiptUploadDetail extends AbstractWorkflowEntity implements Entity {

	private static final long	serialVersionUID	= -4601315178356280082L;

	private long				UploadheaderId		= Long.MIN_VALUE;
	private long				UploadDetailId		= Long.MIN_VALUE;
	private String              rootId;
	private String				reference;
	private String				receiptPurpose;
	private String				excessAdjustTo;
	private String				allocationType;
	private BigDecimal			receiptAmount;
	private String				effectSchdMethod;
	private String				remarks;
	private Date				valueDate;
	private Date				receivedDate;
	private String				receiptMode;
	private String				fundingAc;
	private String				paymentRef;
	private String				favourNumber;
	private String				bankCode;
	private String				chequeNo;
	private String				transactionRef;
	private String				status;
	private Date				depositDate;
	private Date				realizationDate;
	private Date				instrumentDate;

	private String				uploadStatus;
	private String				reason;

	private String				jsonObject;
	private long                receiptId;
	private long                id;
	
	private List<UploadAlloctionDetail>	listAllocationDetails	= new ArrayList<>();

	//Getter and Setter

	public String getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
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

	public long getUploadheaderId() {
		return UploadheaderId;
	}

	public void setUploadheaderId(long uploadheaderId) {
		UploadheaderId = uploadheaderId;
	}

	public long getUploadDetailId() {
		return UploadDetailId;
	}

	public void setUploadDetailId(long uploadDetailId) {
		UploadDetailId = uploadDetailId;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}


	public String getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(String jsonObject) {
		this.jsonObject = jsonObject;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public List<UploadAlloctionDetail> getListAllocationDetails() {
		return listAllocationDetails;
	}

	public void setListAllocationDetails(List<UploadAlloctionDetail> listAllocationDetails) {
		this.listAllocationDetails = listAllocationDetails;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public String getExcessAdjustTo() {
		return excessAdjustTo;
	}

	public void setExcessAdjustTo(String excessAdjustTo) {
		this.excessAdjustTo = excessAdjustTo;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
		
	}
	
}