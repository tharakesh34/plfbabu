package com.pennant.coreinterface.model.dda;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DDAAmendmentReply")
public class DDAAmendment implements Serializable {

	private static final long serialVersionUID = 7126572659982133758L;

	private String referenceNum;
	private String custCIF;
	private String DDAReferenceNo;
	private String mobileNum;
	private String emailID;
	private String finRef;
	private Date commenceOn;
	private Date expiresOn;
	private BigDecimal maxAmount = BigDecimal.ZERO;
	private String paymentFreq;
	private String returnCode;
	private String returnText;
	private long timeStamp;
	
	public DDAAmendment() {
		
	}
	
	
	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "CIF")
	public String getCustCIF() {
		return custCIF;
	}

	public void setCIF(String cIF) {
		this.custCIF = cIF;
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

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
