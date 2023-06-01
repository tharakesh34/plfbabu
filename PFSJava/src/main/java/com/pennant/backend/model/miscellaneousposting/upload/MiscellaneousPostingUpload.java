package com.pennant.backend.model.miscellaneousposting.upload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class MiscellaneousPostingUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private long batchReference;
	private String batchName;
	private String batchPurpose;
	private String debitGL;
	private String creditGL;
	private BigDecimal txnAmount = BigDecimal.ZERO;
	private Date valueDate;
	private String narrLine1;
	private String narrLine2;
	private String narrLine3;
	private String narrLine4;
	private String branch;
	private String postingDivision;
	private String currency;
	private LoggedInUser userDetails;
	private BigDecimal totDebitsByBatchCcy;
	private BigDecimal totCreditsByBatchCcy;
	private int debitCount;
	private int creditsCount;
	private Date appDate;
	private String currencyParm;

	private List<MiscellaneousPostingUpload> mpList = new ArrayList<>();

	public MiscellaneousPostingUpload() {
		super();
	}

	public long getBatchReference() {
		return batchReference;
	}

	public void setBatchReference(long batchReference) {
		this.batchReference = batchReference;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getBatchPurpose() {
		return batchPurpose;
	}

	public void setBatchPurpose(String batchPurpose) {
		this.batchPurpose = batchPurpose;
	}

	public String getDebitGL() {
		return debitGL;
	}

	public void setDebitGL(String debitGL) {
		this.debitGL = debitGL;
	}

	public String getCreditGL() {
		return creditGL;
	}

	public void setCreditGL(String creditGL) {
		this.creditGL = creditGL;
	}

	public BigDecimal getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(BigDecimal txnAmount) {
		this.txnAmount = txnAmount;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
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

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getPostingDivision() {
		return postingDivision;
	}

	public void setPostingDivision(String postingDivision) {
		this.postingDivision = postingDivision;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<MiscellaneousPostingUpload> getMpList() {
		return mpList;
	}

	public void setMpList(List<MiscellaneousPostingUpload> mpList) {
		this.mpList = mpList;
	}

	public BigDecimal getTotDebitsByBatchCcy() {
		return totDebitsByBatchCcy;
	}

	public void setTotDebitsByBatchCcy(BigDecimal totDebitsByBatchCcy) {
		this.totDebitsByBatchCcy = totDebitsByBatchCcy;
	}

	public BigDecimal getTotCreditsByBatchCcy() {
		return totCreditsByBatchCcy;
	}

	public void setTotCreditsByBatchCcy(BigDecimal totCreditsByBatchCcy) {
		this.totCreditsByBatchCcy = totCreditsByBatchCcy;
	}

	public int getDebitCount() {
		return debitCount;
	}

	public void setDebitCount(int debitCount) {
		this.debitCount = debitCount;
	}

	public int getCreditsCount() {
		return creditsCount;
	}

	public void setCreditsCount(int creditsCount) {
		this.creditsCount = creditsCount;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getCurrencyParm() {
		return currencyParm;
	}

	public void setCurrencyParm(String currencyParm) {
		this.currencyParm = currencyParm;
	}

}