package com.pennant.interfaces.model;

import java.util.Date;

public class Header {
	private String MsgFormat = null;
	private String MsgVersion = null;
	private String RequestorId = null;
	private String RequestorChannelId = null;
	private String RequestorUserId = null;
	private String RequestorLanguage = null;
	private String RequestorSecurityInfo = null;
	private String EaiReference = null;
	private String TimeStamp = null;
	private Date   requestedOn = null;
	private String ReturnCode = null;
	private String ReturnDesc = null;
	private byte[] MessageId = null;

	public Header() {
		super();
	}

	public String getMsgFormat() {
		return MsgFormat;
	}

	public void setMsgFormat(String msgFormat) {
		this.MsgFormat = msgFormat;
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return TimeStamp;
	}

	/**
	 * @param timeStamp
	 *            the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		TimeStamp = timeStamp;
	}

	public Date getRequestedOn() {
		return requestedOn;
	}

	public void setRequestedOn(Date requestedOn) {
		this.requestedOn = requestedOn;
	}

	/**
	 * @return the msgVersion
	 */
	public String getMsgVersion() {
		return MsgVersion;
	}

	/**
	 * @param msgVersion
	 *            the msgVersion to set
	 */
	public void setMsgVersion(String msgVersion) {
		MsgVersion = msgVersion;
	}

	/**
	 * @return the requestorChannelId
	 */
	public String getRequestorChannelId() {
		return RequestorChannelId;
	}

	/**
	 * @param requestorChannelId
	 *            the requestorChannelId to set
	 */
	public void setRequestorChannelId(String requestorChannelId) {
		RequestorChannelId = requestorChannelId;
	}

	/**
	 * @return the requestorUserId
	 */
	public String getRequestorUserId() {
		return RequestorUserId;
	}

	/**
	 * @param requestorUserId
	 *            the requestorUserId to set
	 */
	public void setRequestorUserId(String requestorUserId) {
		RequestorUserId = requestorUserId;
	}

	/**
	 * @return the requestorLanguage
	 */
	public String getRequestorLanguage() {
		return RequestorLanguage;
	}

	/**
	 * @param requestorLanguage
	 *            the requestorLanguage to set
	 */
	public void setRequestorLanguage(String requestorLanguage) {
		RequestorLanguage = requestorLanguage;
	}

	/**
	 * @return the requestorSecurityInfo
	 */
	public String getRequestorSecurityInfo() {
		return RequestorSecurityInfo;
	}

	/**
	 * @param requestorSecurityInfo
	 *            the requestorSecurityInfo to set
	 */
	public void setRequestorSecurityInfo(String requestorSecurityInfo) {
		RequestorSecurityInfo = requestorSecurityInfo;
	}

	/**
	 * @return the returnCode
	 */
	public String getReturnCode() {
		return ReturnCode;
	}

	/**
	 * @param returnCode
	 *            the returnCode to set
	 */
	public void setReturnCode(String returnCode) {
		ReturnCode = returnCode;
	}

	/**
	 * @return the returnDesc
	 */
	public String getReturnDesc() {
		return ReturnDesc;
	}

	/**
	 * @param returnDesc
	 *            the returnDesc to set
	 */
	public void setReturnDesc(String returnDesc) {
		ReturnDesc = returnDesc;
	}

	/**
	 * @return the messageId
	 */
	public byte[] getMessageId() {
		return MessageId;
	}

	/**
	 * @param messageId
	 *            the messageId to set
	 */
	public void setMessageId(byte[] messageId) {
		MessageId = messageId;
	}
	
	public String getEaiReference() {
		return EaiReference;
	}

	public void setEaiReference(String eaiReference) {
		EaiReference = eaiReference;
	}

	public String getRequestorId() {
		return RequestorId;
	}

	public void setRequestorId(String requestorId) {
		RequestorId = requestorId;
	}
}
