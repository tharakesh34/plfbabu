package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class DDAProcessData implements Serializable, Entity {

	private static final long serialVersionUID = 2416587325149632633L;

	public DDAProcessData() {

	}

	private long seqNo = Long.MIN_VALUE;
	private String referenceNum;
	private String purpose;
	private String custCIF;
	private String customerType;
	private String idType;
	private String idNum;
	private String customerName;
	private String bankName;
	private String accountType;
	private String iban;
	private String mobileNum;
	private String emailID;
	private String finRefence;
	private Date commenceOn;
	private int allowedInstances;
	private BigDecimal maxAmount = BigDecimal.ZERO;
	private String currencyCode;
	private String paymentFreq;
	private String ddaRegFormName;
	private byte[] ddaRegFormData;
	private String validation;
	private String error;
	private String errorCode;
	private String errorDesc;
	private String returnCode;
	private String returnText;
	private Date valueDate;
	private boolean active;
	private String ddaReference;
	private String ddaAckStatus;
	private long timeStamp;
	
	private String oic;// Originator Identification code
	private String ddaIssuedFor;// FinCategory
	private String ddaCanResCode;
	private String captureMode;
	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@Override
	public long getId() {
		return seqNo;
	}

	@Override
	public void setId(long id) {
		this.seqNo = id;
	}
	
	public long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(long seqNo) {
		this.seqNo = seqNo;
	}
/*	public long getDdaRefId() {
		return seqNo;
	}

	public void setDdaRefId(long ddaRefId) {
		this.seqNo = ddaRefId;
	}*/

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNum() {
		return idNum;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getFinRefence() {
		return finRefence;
	}

	public void setFinRefence(String finRefence) {
		this.finRefence = finRefence;
	}

	public Date getCommenceOn() {
		return commenceOn;
	}

	public void setCommenceOn(Date commenceOn) {
		this.commenceOn = commenceOn;
	}

	public int getAllowedInstances() {
		return allowedInstances;
	}

	public void setAllowedInstances(int allowedInstances) {
		this.allowedInstances = allowedInstances;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getPaymentFreq() {
		return paymentFreq;
	}

	public void setPaymentFreq(String paymentFreq) {
		this.paymentFreq = paymentFreq;
	}

	public String getDdaRegFormName() {
		return ddaRegFormName;
	}

	public void setDdaRegFormName(String ddaRegFormName) {
		this.ddaRegFormName = ddaRegFormName;
	}

	public byte[] getDdaRegFormData() {
		return ddaRegFormData;
	}

	public void setDdaRegFormData(byte[] ddaRegFormData) {
		this.ddaRegFormData = ddaRegFormData;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public boolean isNew() {
		return false;
	}
	
	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getDdaReference() {
		return ddaReference;
	}

	public void setDdaReference(String ddaReference) {
		this.ddaReference = ddaReference;
	}
	
	public String getDdaAckStatus() {
		return ddaAckStatus;
	}

	public void setDdaAckStatus(String ddaAckStatus) {
		this.ddaAckStatus = ddaAckStatus;
	}
	
	public String getOic() {
		return oic;
	}

	public void setOic(String oic) {
		this.oic = oic;
	}

	public String getDdaIssuedFor() {
		return ddaIssuedFor;
	}

	public void setDdaIssuedFor(String ddaIssuedFor) {
		this.ddaIssuedFor = ddaIssuedFor;
	}
	
	public String getDdaCanResCode() {
		return ddaCanResCode;
	}

	public void setDdaCanResCode(String ddaCanResCode) {
		this.ddaCanResCode = ddaCanResCode;
	}

	public String getCaptureMode() {
		return captureMode;
	}

	public void setCaptureMode(String captureMode) {
		this.captureMode = captureMode;
	}

}
