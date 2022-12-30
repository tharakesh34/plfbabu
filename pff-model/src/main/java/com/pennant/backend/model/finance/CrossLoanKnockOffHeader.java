package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CrossLoanKnockOffHeader extends AbstractWorkflowEntity implements Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4414905784442720643L;

	private long crossLoanHeaderId = 0;
	private long crossLoanId = 0;
	private long knockOffReceiptId = 0;
	private Date postDate;
	private CrossLoanKnockOffHeader befImage;
	private LoggedInUser userDetails;
	private Date valueDate;
	private CrossLoanTransfer crossLoanTransfer;
	private FinReceiptData finReceiptData;
	private boolean newRecord;

	private long receiptId;
	private Date receiptDate;
	private String receiptPurpose;
	private String custCif;
	private String finType;
	private BigDecimal receiptAmount;
	private String fromFinreference;
	private String toFinreference;
	private String transactionRef;
	private Date depositDate;
	private Date realizationDate;
	private String receiptMode;
	private String paymentType;
	private String knockoffType;
	private String receiptModeStatus;

	public CrossLoanKnockOffHeader() {
		super();
	}

	public CrossLoanKnockOffHeader(long crossLoanId) {
		super();
		this.crossLoanId = crossLoanId;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finReceiptData");
		excludeFields.add("crossLoanTransfer");
		excludeFields.add("receiptModeStatus");
		excludeFields.add("receiptId");
		excludeFields.add("receiptDate");
		excludeFields.add("receiptPurpose");
		excludeFields.add("custCif");
		excludeFields.add("finType");
		excludeFields.add("receiptAmount");
		excludeFields.add("fromFinreference");
		excludeFields.add("toFinreference");
		excludeFields.add("transactionRef");
		excludeFields.add("depositDate");
		excludeFields.add("realizationDate");
		excludeFields.add("receiptMode");
		excludeFields.add("paymentType");
		excludeFields.add("knockoffType");
		excludeFields.add("receiptModeStatus");
		return excludeFields;
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	@Override
	public long getId() {
		return this.crossLoanHeaderId;
	}

	@Override
	public void setId(long id) {
		this.crossLoanHeaderId = id;
	}

	public long getCrossLoanHeaderId() {
		return crossLoanHeaderId;
	}

	public void setCrossLoanHeaderId(long crossLoanHeaderId) {
		this.crossLoanHeaderId = crossLoanHeaderId;
	}

	public long getCrossLoanId() {
		return crossLoanId;
	}

	public void setCrossLoanId(long crossLoanId) {
		this.crossLoanId = crossLoanId;
	}

	public long getKnockOffReceiptId() {
		return knockOffReceiptId;
	}

	public void setKnockOffReceiptId(long knockOffReceiptId) {
		this.knockOffReceiptId = knockOffReceiptId;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public CrossLoanKnockOffHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(CrossLoanKnockOffHeader befImage) {
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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinReceiptData getFinReceiptData() {
		return finReceiptData;
	}

	public void setFinReceiptData(FinReceiptData finReceiptData) {
		this.finReceiptData = finReceiptData;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
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

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
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

	public String getFromFinreference() {
		return fromFinreference;
	}

	public void setFromFinreference(String fromFinreference) {
		this.fromFinreference = fromFinreference;
	}

	public String getToFinreference() {
		return toFinreference;
	}

	public void setToFinreference(String toFinreference) {
		this.toFinreference = toFinreference;
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

}
