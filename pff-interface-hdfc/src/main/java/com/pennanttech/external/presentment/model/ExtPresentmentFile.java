package com.pennanttech.external.presentment.model;

import java.math.BigDecimal;
import java.util.Date;

public class ExtPresentmentFile {

	private long agreementId;

	private String finBranchId = "";

	private String finBranchName = "";
	private String bankId = "";
	private String bankName = "";
	private String bankBranchId = "";
	private String bankBranchName = "";
	private String micr = "";
	private String chequeSerialNumber = "";
	private Date chequeDate;

	private String product;

	private String cityId;
	private String cityName;
	private String clusterId = "";

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	private String accountNo;
	private String presentmentRef;
	private BigDecimal schAmtDue;
	private String chequeSerialNo;
	private String finReference;
	private int emiNo;
	private String numberOfTerms;
	private Date schDate;
	private String customerName;
	private String acType;
	private String batchReference;
	private long id = Long.MIN_VALUE;
	private String utrNumber;
	private String bankCode;

	private long txnReference;
	private long bounceCode = 0;
	private String bounceReason = "";
	private String bounceRetrunCode = "";

	private String errorCode = "";
	private String errorMessage = "";

	private String status = "";

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getPresentmentRef() {
		return presentmentRef;
	}

	public void setPresentmentRef(String presentmentRef) {
		this.presentmentRef = presentmentRef;
	}

	public BigDecimal getSchAmtDue() {
		return schAmtDue;
	}

	public void setSchAmtDue(BigDecimal schAmtDue) {
		this.schAmtDue = schAmtDue;
	}

	public String getChequeSerialNo() {
		return chequeSerialNo;
	}

	public void setChequeSerialNo(String chequeSerialNo) {
		this.chequeSerialNo = chequeSerialNo;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public int getEmiNo() {
		return emiNo;
	}

	public void setEmiNo(int emiNo) {
		this.emiNo = emiNo;
	}

	public String getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(String numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getBatchReference() {
		return batchReference;
	}

	public void setBatchReference(String batchReference) {
		this.batchReference = batchReference;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public long getTxnReference() {
		return txnReference;
	}

	public void setTxnReference(long txnReference) {
		this.txnReference = txnReference;
	}

	public long getBounceCode() {
		return bounceCode;
	}

	public void setBounceCode(long bounceCode) {
		this.bounceCode = bounceCode;
	}

	public String getBounceReason() {
		return bounceReason;
	}

	public void setBounceReason(String bounceReason) {
		this.bounceReason = bounceReason;
	}

	public String getBounceRetrunCode() {
		return bounceRetrunCode;
	}

	public void setBounceRetrunCode(String bounceRetrunCode) {
		this.bounceRetrunCode = bounceRetrunCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getFinBranchId() {
		return finBranchId;
	}

	public void setFinBranchId(String finBranchId) {
		this.finBranchId = finBranchId;
	}

	public String getFinBranchName() {
		return finBranchName;
	}

	public void setFinBranchName(String finBranchName) {
		this.finBranchName = finBranchName;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranchId() {
		return bankBranchId;
	}

	public void setBankBranchId(String bankBranchId) {
		this.bankBranchId = bankBranchId;
	}

	public String getBankBranchName() {
		return bankBranchName;
	}

	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}

	public String getMicr() {
		return micr;
	}

	public void setMicr(String micr) {
		this.micr = micr;
	}

	public String getChequeSerialNumber() {
		return chequeSerialNumber;
	}

	public void setChequeSerialNumber(String chequeSerialNumber) {
		this.chequeSerialNumber = chequeSerialNumber;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	@Override
	public String toString() {
		return "ExternalPresentment [accountNo=" + accountNo + ", presentmentRef=" + presentmentRef + ", schAmtDue="
				+ schAmtDue + ", chequeSerialNo=" + chequeSerialNo + ", finReference=" + finReference + ", emiNo="
				+ emiNo + ", numberOfTerms=" + numberOfTerms + ", schDate=" + schDate + ", customerName=" + customerName
				+ ", acType=" + acType + ", batchReference=" + batchReference + ", id=" + id + ", utrNumber="
				+ utrNumber + ", bankCode=" + bankCode + "]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
