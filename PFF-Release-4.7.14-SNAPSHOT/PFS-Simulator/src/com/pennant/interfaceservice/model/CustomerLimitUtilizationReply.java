package com.pennant.interfaceservice.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LimitUtilizationReply")
public class CustomerLimitUtilizationReply {

	private String referenceNum;
	private String dealID;
	private String customerReference;
	private String limitRef;
	private String overrides;
	private String response;
	private String errMsg;
	private String returnCode;
	private String returnText;
	private long timeStamp;

	private List<CustomerLimitUtilizationReply> customerLimitUtilizationDetails;
	
	private String resDetails;
	private String msgBreach;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	@XmlElement(name="ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name="DealID")
	public String getDealID() {
		return dealID;
	}

	public void setDealID(String dealID) {
		this.dealID = dealID;
	}

	@XmlElement(name="CustomerReference")
	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	@XmlElement(name="LimitRef")
	public String getLimitRef() {
		return limitRef;
	}

	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}

	@XmlElement(name = "Overrides")
	public String getOverrides() {
		return overrides;
	}

	public void setOverrides(String overrides) {
		this.overrides = overrides;
	}
	
	@XmlElement(name = "Response")
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@XmlElement(name="ErrMsg")
	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	@XmlElement(name="ReturnCode")
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@XmlElement(name="ReturnText")
	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@XmlElement(name="TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@XmlElement(name="ResDetails")
	public String getResDetails() {
		return resDetails;
	}

	public void setResDetails(String resDetails) {
		this.resDetails = resDetails;
	}

	@XmlElement(name="MsgBreach")
	public String getMsgBreach() {
		return msgBreach;
	}

	public void setMsgBreach(String msgBreach) {
		this.msgBreach = msgBreach;
	}

	public List<CustomerLimitUtilizationReply> getCustomerLimitUtilizationDetails() {
		return customerLimitUtilizationDetails;
	}

	public void setCustomerLimitUtilizationDetails(
			List<CustomerLimitUtilizationReply> customerLimitUtilizationDetails) {
		this.customerLimitUtilizationDetails = customerLimitUtilizationDetails;
	}
}
