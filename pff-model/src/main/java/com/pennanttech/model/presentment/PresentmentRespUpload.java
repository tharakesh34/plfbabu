package com.pennanttech.model.presentment;

import java.util.Date;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.upload.model.UploadDetails;

public class PresentmentRespUpload extends UploadDetails {
	private static final long serialVersionUID = 3557119742009775415L;

	private String presentmentReference;
	private String hostReference;
	private String instalmentNo;
	private String amountCleared;
	private Date clearingDate;
	private String clearingStatus;
	private String bounceCode;
	private String bounceRemarks;
	private String reasonCode;
	private String bankCode;
	private String bankName;
	private String branchCode;
	private String branchName;
	private String partnerBankCode;
	private String partnerBankName;
	private String bankAddress;
	private String accountNumber;
	private String ifscCode;
	private String umrnNo;
	private String micrCode;
	private String chequeSerialNo;
	private String corporateUserNo;
	private String corporateUserName;
	private String destAccHolder;
	private String debitCreditFlag;
	private int processFlag;
	private int threadId;
	private String utrNumber;
	private String fateCorrection;
	private FinanceMain fm;

	public PresentmentRespUpload() {
		super();
	}

	public String getPresentmentReference() {
		return presentmentReference;
	}

	public void setPresentmentReference(String presentmentReference) {
		this.presentmentReference = presentmentReference;
	}

	public String getHostReference() {
		return hostReference;
	}

	public void setHostReference(String hostReference) {
		this.hostReference = hostReference;
	}

	public String getInstalmentNo() {
		return instalmentNo;
	}

	public void setInstalmentNo(String instalmentNo) {
		this.instalmentNo = instalmentNo;
	}

	public String getAmountCleared() {
		return amountCleared;
	}

	public void setAmountCleared(String amountCleared) {
		this.amountCleared = amountCleared;
	}

	public Date getClearingDate() {
		return clearingDate;
	}

	public void setClearingDate(Date clearingDate) {
		this.clearingDate = clearingDate;
	}

	public String getClearingStatus() {
		return clearingStatus;
	}

	public void setClearingStatus(String clearingStatus) {
		this.clearingStatus = clearingStatus;
	}

	public String getBounceCode() {
		return bounceCode;
	}

	public void setBounceCode(String bounceCode) {
		this.bounceCode = bounceCode;
	}

	public String getBounceRemarks() {
		return bounceRemarks;
	}

	public void setBounceRemarks(String bounceRemarks) {
		this.bounceRemarks = bounceRemarks;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getUmrnNo() {
		return umrnNo;
	}

	public void setUmrnNo(String umrnNo) {
		this.umrnNo = umrnNo;
	}

	public String getMicrCode() {
		return micrCode;
	}

	public void setMicrCode(String micrCode) {
		this.micrCode = micrCode;
	}

	public String getChequeSerialNo() {
		return chequeSerialNo;
	}

	public void setChequeSerialNo(String chequeSerialNo) {
		this.chequeSerialNo = chequeSerialNo;
	}

	public String getCorporateUserNo() {
		return corporateUserNo;
	}

	public void setCorporateUserNo(String corporateUserNo) {
		this.corporateUserNo = corporateUserNo;
	}

	public String getCorporateUserName() {
		return corporateUserName;
	}

	public void setCorporateUserName(String corporateUserName) {
		this.corporateUserName = corporateUserName;
	}

	public String getDestAccHolder() {
		return destAccHolder;
	}

	public void setDestAccHolder(String destAccHolder) {
		this.destAccHolder = destAccHolder;
	}

	public String getDebitCreditFlag() {
		return debitCreditFlag;
	}

	public void setDebitCreditFlag(String debitCreditFlag) {
		this.debitCreditFlag = debitCreditFlag;
	}

	public int getProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(int processFlag) {
		this.processFlag = processFlag;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

	public String getFateCorrection() {
		return fateCorrection;
	}

	public void setFateCorrection(String fateCorrection) {
		this.fateCorrection = fateCorrection;
	}

	public FinanceMain getFm() {
		return fm;
	}

	public void setFm(FinanceMain fm) {
		this.fm = fm;
	}

}
