package com.pennant.backend.model.miscPostingUpload;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class MiscPostingUpload extends AbstractWorkflowEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7649459318662830965L;
	private long miscPostingId = Long.MIN_VALUE;
	private long uploadId;
	private String batch;
	private String branch;
	private String batchPurpose;
	private String postAgainst;
	private String reference;
	private String postingDivision;
	private String account;
	private String txnEntry;
	private Date valueDate;
	private BigDecimal txnAmount;
	private String narrLine1;
	private String narrLine2;
	private String narrLine3;
	private String narrLine4;

	private MiscPostingUpload befImage;
	private LoggedInUser userDetails;
	private String uploadStatus;
	private String reason;
	private String batchSeq;
	private long transactionId;

	public MiscPostingUpload() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("batchSeq");
		return excludeFields;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public long getMiscPostingId() {
		return miscPostingId;
	}

	public void setMiscPostingId(long miscPostingId) {
		this.miscPostingId = miscPostingId;
	}

	public long getUploadId() {
		return uploadId;
	}

	public void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getBatchPurpose() {
		return batchPurpose;
	}

	public void setBatchPurpose(String batchPurpose) {
		this.batchPurpose = batchPurpose;
	}

	public String getPostAgainst() {
		return postAgainst;
	}

	public void setPostAgainst(String postAgainst) {
		this.postAgainst = postAgainst;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getPostingDivision() {
		return postingDivision;
	}

	public void setPostingDivision(String postingDivision) {
		this.postingDivision = postingDivision;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTxnEntry() {
		return txnEntry;
	}

	public void setTxnEntry(String txnEntry) {
		this.txnEntry = txnEntry;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(BigDecimal txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getNarrLine1() {
		return narrLine1;
	}

	public void setNarrLine1(String narrLine1) {
		this.narrLine1 = narrLine1;
	}

	public String getNarrLine2() {
		return narrLine2;
	}

	public void setNarrLine2(String narrLine2) {
		this.narrLine2 = narrLine2;
	}

	public String getNarrLine3() {
		return narrLine3;
	}

	public void setNarrLine3(String narrLine3) {
		this.narrLine3 = narrLine3;
	}

	public String getNarrLine4() {
		return narrLine4;
	}

	public void setNarrLine4(String narrLine4) {
		this.narrLine4 = narrLine4;
	}

	public MiscPostingUpload getBefImage() {
		return befImage;
	}

	public void setBefImage(MiscPostingUpload befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public long getId() {
		return this.miscPostingId;
	}

	public void setId(long id) {
		this.miscPostingId = id;
	}

	public String getBatchSeq() {
		return batchSeq;
	}

	public void setBatchSeq(String batchSeq) {
		this.batchSeq = batchSeq;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

}
