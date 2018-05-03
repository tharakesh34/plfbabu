package com.pennant.coreinterface.model.dda;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DDACancellationReply")
public class DDACancellation implements Serializable {

	private static final long serialVersionUID = 2055641859286499196L;

	private String referenceNum;
	private String isNumber;
	private String ddaReferenceNo;
	private String ddaCanResCode;
	private String captureMode;
	private String ddaRegFormName;
	private byte[] ddaRegFormData;
	private String returnCode;
	private String returnText;
	private long timeStamp;

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

	@XmlElement(name = "ISNumber")
	public String getIsNumber() {
		return isNumber;
	}

	public void setIsNumber(String isNumber) {
		this.isNumber = isNumber;
	}

	@XmlElement(name = "DDAReferenceNo")
	public String getDdaReferenceNo() {
		return ddaReferenceNo;
	}

	public void setDdaReferenceNo(String ddaReferenceNo) {
		this.ddaReferenceNo = ddaReferenceNo;
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

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
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
