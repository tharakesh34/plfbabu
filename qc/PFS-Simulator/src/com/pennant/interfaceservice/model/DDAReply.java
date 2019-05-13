package com.pennant.interfaceservice.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DDARegistrationReply")
public class DDAReply {

	private String referenceNum;
	private String returnCode;
	private String returnText;
	private long timeStamp;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	@XmlElement(name = "ReferenceNum", required=true)
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
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
