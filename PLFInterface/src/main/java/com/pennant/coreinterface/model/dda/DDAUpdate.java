package com.pennant.coreinterface.model.dda;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DDAUpdateReply")
public class DDAUpdate implements Serializable {

	private static final long serialVersionUID = 4290300511424501355L;

	private String referenceNum;
	private String type;
	private String iSNumber;
	private String accountNumber;
	private String ddaReferenceNo;
	private String action;
	private String returnCode;
	private String returnText;
	private long timeStamp;

	public DDAUpdate() {

	}

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "Type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "ISNumber")
	public String getiSNumber() {
		return iSNumber;
	}

	public void setiSNumber(String iSNumber) {
		this.iSNumber = iSNumber;
	}

	@XmlElement(name = "AccountNumber")
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@XmlElement(name = "DdaReferenceNo")
	public String getDdaReferenceNo() {
		return ddaReferenceNo;
	}

	public void setDdaReferenceNo(String ddaReferenceNo) {
		this.ddaReferenceNo = ddaReferenceNo;
	}

	@XmlElement(name = "Action")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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
