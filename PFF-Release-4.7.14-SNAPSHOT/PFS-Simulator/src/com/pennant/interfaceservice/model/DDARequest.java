package com.pennant.interfaceservice.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "DDARegistrationRequest")
public class DDARequest {

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
	private long timeStamp;

	public DDARequest() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}
	
	@XmlElement(name = "Purpose")
	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	@XmlElement(name = "CustomerType")
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	@XmlElement(name = "CustomerName")
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@XmlElement(name = "BankName")
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@XmlElement(name = "AccountType")
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	@XmlElement(name = "Mobile")
	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	@XmlElement(name = "Email")
	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	@XmlElement(name = "CommenceOn")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getCommenceOn() {
		return commenceOn;
	}

	public void setCommenceOn(Date commenceOn) {
		this.commenceOn = commenceOn;
	}

	@XmlElement(name = "AllowedInstances")
	public int getAllowedInstances() {
		return allowedInstances;
	}

	public void setAllowedInstances(int allowedInstances) {
		this.allowedInstances = allowedInstances;
	}

	@XmlElement(name = "MaxAmount")
	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	@XmlElement(name = "CurrencyCode")
	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@XmlElement(name = "PaymentFrequency")
	public String getPaymentFreq() {
		return paymentFreq;
	}

	public void setPaymentFreq(String paymentFreq) {
		this.paymentFreq = paymentFreq;
	}

	@XmlElement(name = "CIF")
	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	@XmlElement(name = "IDType")
	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	@XmlElement(name = "IBAN")
	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@XmlElement(name = "IDNumber")
	public String getIdNum() {
		return idNum;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	@XmlElement(name = "DDAFormName")
	public String getDdaRegFormName() {
		return ddaRegFormName;
	}

	public void setDdaRegFormName(String ddaRegFormName) {
		this.ddaRegFormName = ddaRegFormName;
	}

	@XmlElement(name = "DDAFormData")
	public byte[] getDdaRegFormData() {
		return ddaRegFormData;
	}

	public void setDdaRegFormData(byte[] ddaRegFormData) {
		this.ddaRegFormData = ddaRegFormData;
	}
	
	@XmlElement(name = "ReturnCode")
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@XmlElement(name = "ReturnText")
	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}
	
	@XmlElement(name = "FinanceRef")
	public String getFinRefence() {
		return finRefence;
	}

	public void setFinRefence(String finRefence) {
		this.finRefence = finRefence;
	}

	@XmlElement(name = "Validation")
	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	@XmlElement(name = "Error")
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@XmlElement(name = "ErrorCode")
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@XmlElement(name = "ErrorDescription")
	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		@Override
		public Date unmarshal(final String v) throws Exception {
			return dateFormat.parse(v);
		}

		@Override
		public String marshal(final Date v) throws Exception {
			return dateFormat.format(v);
		}
	}
}
