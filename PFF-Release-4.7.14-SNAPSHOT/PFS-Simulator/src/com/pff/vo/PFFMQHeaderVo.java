package com.pff.vo;

import org.apache.commons.lang.StringUtils;

public class PFFMQHeaderVo {

	private String 	messageHeader;
	private String 	messageFormat;
	private String 	messageVersion;
	private String 	messageRequestId;
	private String 	messageRequestChId;
	private String 	messageType;
	private String 	messageRequestUserId;
	private String 	messageRequestLanguage;
	private String 	messageSecurityInfo;
	private String 	messageReturnCode;
	private String 	messageReturnDesc;
	private byte[] 	mqMessageId;
	private String 	messageSendTime;
	private String 	messageExtra1;
	private String 	messageExtra2;
	private String  eaiReference;
	private String  timeStamp;
	private String  refNumber;

	


	public byte[] getMqMessageId() {
		return mqMessageId;
	}
	public void setMqMessageId(byte[] mqMessageId) {
		this.mqMessageId = mqMessageId;
	}

	public String getMessageReturnCode() {
		return messageReturnCode;
	}
	public void setMessageReturnCode(String messageReturnCode) {
		this.messageReturnCode = messageReturnCode;
	}
	public String getMessageReturnDesc() {
		return messageReturnDesc;
	}
	public void setMessageReturnDesc(String messageReturnDesc) {
		this.messageReturnDesc = messageReturnDesc;
	}

	public boolean isError(){
		if(StringUtils.equals(getReturnCode(), "00000")){
			return true;
		}
		return false;
	}

	public String getReturnCode(){
		
		return messageReturnCode;
		/*return StringUtils.leftPad(StringUtils.trimToEmpty(messageReturnCode), 5,'0');*/
	}
	public String getMessageExtra1() {
		return messageExtra1;
	}
	public void setMessageExtra1(String messageExtra1) {
		this.messageExtra1 = messageExtra1;
	}
	public String getMessageExtra2() {
		return messageExtra2;
	}
	public void setMessageExtra2(String messageExtra2) {
		this.messageExtra2 = messageExtra2;
	}
	public String getMessageSecurityInfo() {
		return messageSecurityInfo;
	}
	public void setMessageSecurityInfo(String messageSecurityInfo) {
		this.messageSecurityInfo = messageSecurityInfo;
	}
	public String getMessageRequestLanguage() {
		return messageRequestLanguage;
	}
	public void setMessageRequestLanguage(String messageRequestLanguage) {
		this.messageRequestLanguage = messageRequestLanguage;
	}
	public String getMessageSendTime() {
		return messageSendTime;
	}
	public String getMessageHeader() {
		return messageHeader;
	}
	public void setMessageHeader(String messageHeader) {
		this.messageHeader = messageHeader;
	}
	public String getMessageFormat() {
		return messageFormat;
	}
	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}
	public String getMessageRequestChId() {
		return messageRequestChId;
	}
	public void setMessageRequestChId(String messageRequestChId) {
		this.messageRequestChId = messageRequestChId;
	}
	public String getMessageVersion() {
		return messageVersion;
	}
	public void setMessageVersion(String messageVersion) {
		this.messageVersion = messageVersion;
	}
	public String getMessageRequestUserId() {
		return messageRequestUserId;
	}
	public void setMessageRequestUserId(String messageRequestUserId) {
		this.messageRequestUserId = messageRequestUserId;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getEaiReference() {
		return eaiReference;
	}
	public void setEaiReference(String eaiReference) {
		this.eaiReference = eaiReference;
	}
	public String getMessageRequestId() {
		return messageRequestId;
	}
	public void setMessageRequestId(String messageRequestId) {
		this.messageRequestId = messageRequestId;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}
	public void setMessageSendTime(String messageSendTime) {
		this.messageSendTime = messageSendTime;
	}
}