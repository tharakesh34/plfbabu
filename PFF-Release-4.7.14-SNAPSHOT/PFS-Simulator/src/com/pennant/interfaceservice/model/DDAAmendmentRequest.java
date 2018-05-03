package com.pennant.interfaceservice.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DDAAmendmentRequest")
public class DDAAmendmentRequest {

	private String referenceNum;
	private String CIF;
	private String DDAReferenceNo;
	private String mobileNum;
	private String emailID;
	private String finRef;
	private Date commenceOn;
	private Date expiresOn;
	private BigDecimal maxAmount = BigDecimal.ZERO;
	private String paymentFreq;
	private long timeStamp;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "CIF")
	public String getCIF() {
		return CIF;
	}

	public void setCIF(String cIF) {
		CIF = cIF;
	}

	@XmlElement(name = "DDAReferenceNo")
	public String getDDAReferenceNo() {
		return DDAReferenceNo;
	}

	public void setDDAReferenceNo(String dDAReferenceNo) {
		DDAReferenceNo = dDAReferenceNo;
	}

	@XmlElement(name = "MobileNum")
	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	@XmlElement(name = "EmailID")
	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	@XmlElement(name = "FinRef")
	public String getFinRef() {
		return finRef;
	}

	public void setFinRef(String finRef) {
		this.finRef = finRef;
	}

	@XmlElement(name = "CommenceOn")
	public Date getCommenceOn() {
		return commenceOn;
	}

	public void setCommenceOn(Date commenceOn) {
		this.commenceOn = commenceOn;
	}

	@XmlElement(name = "ExpiresOn")
	public Date getExpiresOn() {
		return expiresOn;
	}

	public void setExpiresOn(Date expiresOn) {
		this.expiresOn = expiresOn;
	}

	@XmlElement(name = "MaxAmount")
	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	@XmlElement(name = "PaymentFreq")
	public String getPaymentFreq() {
		return paymentFreq;
	}

	public void setPaymentFreq(String paymentFreq) {
		this.paymentFreq = paymentFreq;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
