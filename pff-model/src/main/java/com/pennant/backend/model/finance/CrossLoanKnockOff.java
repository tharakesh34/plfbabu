package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.crossloanknockoff.CrossLoanKnockoffUpload;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CrossLoanKnockOff extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 4414905784442720643L;

	private long id;
	private long transferID;
	private long knockOffId;
	private Date postDate;
	private CrossLoanKnockOff befImage;
	private LoggedInUser userDetails;
	private Date valueDate;
	private CrossLoanTransfer crossLoanTransfer;
	private FinReceiptData finReceiptData;
	private long receiptID = 0;
	private Date receiptDate;
	private String receiptPurpose;
	private String custCIF;
	private String finType;
	private BigDecimal receiptAmount;
	private String fromFinReference;
	private String toFinReference;
	private String transactionRef;
	private Date depositDate;
	private Date realizationDate;
	private String receiptMode;
	private String subReceiptMode;
	private String paymentType;
	private String knockoffType;
	private String receiptModeStatus;
	private long createdBy;
	private Timestamp createdOn;
	private long approvedBy;
	private Timestamp approvedOn;
	private FinServiceInstruction finServiceInstruction;
	private String requestSource;
	private CrossLoanKnockoffUpload crossLoanKnockoffUpload;
	private boolean cancelProcess;

	public CrossLoanKnockOff() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("finReceiptData");
		excludeFields.add("crossLoanTransfer");
		excludeFields.add("receiptModeStatus");
		excludeFields.add("receiptID");
		excludeFields.add("receiptDate");
		excludeFields.add("receiptPurpose");
		excludeFields.add("custCIF");
		excludeFields.add("finType");
		excludeFields.add("receiptAmount");
		excludeFields.add("fromFinReference");
		excludeFields.add("toFinReference");
		excludeFields.add("transactionRef");
		excludeFields.add("depositDate");
		excludeFields.add("realizationDate");
		excludeFields.add("receiptMode");
		excludeFields.add("subReceiptMode");
		excludeFields.add("paymentType");
		excludeFields.add("knockoffType");
		excludeFields.add("receiptModeStatus");
		excludeFields.add("requestSource");
		excludeFields.add("finServiceInstruction");
		excludeFields.add("crossLoanKnockoffUpload");
		excludeFields.add("cancelProcess");
		excludeFields.add("transferID");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTransferID() {
		return transferID;
	}

	public void setTransferID(long transferID) {
		this.transferID = transferID;
	}

	public long getKnockOffId() {
		return knockOffId;
	}

	public void setKnockOffId(long knockOffId) {
		this.knockOffId = knockOffId;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public CrossLoanKnockOff getBefImage() {
		return befImage;
	}

	public void setBefImage(CrossLoanKnockOff befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public CrossLoanTransfer getCrossLoanTransfer() {
		return crossLoanTransfer;
	}

	public void setCrossLoanTransfer(CrossLoanTransfer crossLoanTransfer) {
		this.crossLoanTransfer = crossLoanTransfer;
	}

	public FinReceiptData getFinReceiptData() {
		return finReceiptData;
	}

	public void setFinReceiptData(FinReceiptData finReceiptData) {
		this.finReceiptData = finReceiptData;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptPurpose() {
		return receiptPurpose;
	}

	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = receiptPurpose;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getFromFinReference() {
		return fromFinReference;
	}

	public void setFromFinReference(String fromFinReference) {
		this.fromFinReference = fromFinReference;
	}

	public String getToFinReference() {
		return toFinReference;
	}

	public void setToFinReference(String toFinReference) {
		this.toFinReference = toFinReference;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
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

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public String getSubReceiptMode() {
		return subReceiptMode;
	}

	public void setSubReceiptMode(String subReceiptMode) {
		this.subReceiptMode = subReceiptMode;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getKnockoffType() {
		return knockoffType;
	}

	public void setKnockoffType(String knockoffType) {
		this.knockoffType = knockoffType;
	}

	public String getReceiptModeStatus() {
		return receiptModeStatus;
	}

	public void setReceiptModeStatus(String receiptModeStatus) {
		this.receiptModeStatus = receiptModeStatus;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public FinServiceInstruction getFinServiceInstruction() {
		return finServiceInstruction;
	}

	public void setFinServiceInstruction(FinServiceInstruction finServiceInstruction) {
		this.finServiceInstruction = finServiceInstruction;
	}

	public String getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}

	public CrossLoanKnockoffUpload getCrossLoanKnockoffUpload() {
		return crossLoanKnockoffUpload;
	}

	public void setCrossLoanKnockoffUpload(CrossLoanKnockoffUpload crossLoanKnockoffUpload) {
		this.crossLoanKnockoffUpload = crossLoanKnockoffUpload;
	}

	public boolean isCancelProcess() {
		return cancelProcess;
	}

	public void setCancelProcess(boolean cancelProcess) {
		this.cancelProcess = cancelProcess;
	}
}
