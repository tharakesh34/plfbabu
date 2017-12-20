package com.pennanttech.niyogin.communication.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "mobileNumber", "messageBody", "returnCode", "returnText" })
@XmlRootElement(name = "sms")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sms implements Serializable {

	private static final long serialVersionUID = -7898538635970927544L;
	@XmlElement(name = "mobile")
	private String mobileNumber;
	@XmlElement(name = "text")
	private String messageBody;
	@XmlElement(name = "statusCode")
	private String returnCode;
	@XmlElement(name = "message")
	private String returnText;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
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

	@Override
	public String toString() {
		return "Sms [mobileNumber=" + mobileNumber + ", messageBody=" + messageBody + ", returnCode=" + returnCode
				+ ", returnText=" + returnText + "]";
	}
	
}
