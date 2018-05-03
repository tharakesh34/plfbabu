package com.pennant.mq.model;

import java.io.Serializable;

import com.pennant.mq.util.InterfaceMasterConfigUtil;

public class AHBMQHeader implements Serializable {

	private static final long serialVersionUID = 8364281527362719181L;

	private String msgFormat;
	private String msgVersion;
	private String requestorId;
	private String requestorChannelId;
	private String requestorUserId;
	private String requestorLanguage;
	private String requestorSecurityInfo;
	private String eaiReference;
	private String returnCode;
	private String returnText;
	private String returnTime;
	private String referenceNum;
	
	/**
	 * Method for setting default header values
	 * 
	 * @param msgFormat
	 * @param requestorChannelId
	 * @param securityInfo
	 */
	@Deprecated
	public AHBMQHeader(String msgFormat, String requestorChannelId, String securityInfo) {
		super();
		this.msgFormat = msgFormat;
		this.msgVersion = InterfaceMasterConfigUtil.MSGVERSION;
		this.requestorId = InterfaceMasterConfigUtil.REQUESTOR_ID;
		this.requestorChannelId = requestorChannelId;
		this.requestorUserId = InterfaceMasterConfigUtil.REQUESTOR_USERID;
		this.requestorLanguage = InterfaceMasterConfigUtil.REQUESTOR_LANG;
		this.requestorSecurityInfo = securityInfo;
		this.eaiReference = InterfaceMasterConfigUtil.EAIREFERENCE;
		this.returnCode = InterfaceMasterConfigUtil.RETURNCODE;
		
	}
	
	/**
	 * Method for setting default header values
	 * 
	 * @param msgFormat
	 */
	public AHBMQHeader(String msgFormat) {
		super();
		this.msgFormat = msgFormat;
		this.msgVersion = InterfaceMasterConfigUtil.MSGVERSION;
		this.requestorId = InterfaceMasterConfigUtil.REQUESTOR_ID;
		this.requestorChannelId = InterfaceMasterConfigUtil.REQCHANNEL_ID;
		this.requestorUserId = InterfaceMasterConfigUtil.REQUESTOR_USERID;
		this.requestorLanguage = InterfaceMasterConfigUtil.REQUESTOR_LANG;
		this.requestorSecurityInfo = InterfaceMasterConfigUtil.SECURITY_INFO;
		this.eaiReference = InterfaceMasterConfigUtil.EAIREFERENCE;
		this.returnCode = InterfaceMasterConfigUtil.RETURNCODE;
	}

	public String getMsgFormat() {
		return msgFormat;
	}

	public void setMsgFormat(String msgFormat) {
		this.msgFormat = msgFormat;
	}

	public String getMsgVersion() {
		return msgVersion;
	}

	public void setMsgVersion(String msgVersion) {
		this.msgVersion = msgVersion;
	}

	public String getRequestorId() {
		return requestorId;
	}

	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}

	public String getRequestorChannelId() {
		return requestorChannelId;
	}

	public void setRequestorChannelId(String requestorChannelId) {
		this.requestorChannelId = requestorChannelId;
	}

	public String getRequestorUserId() {
		return requestorUserId;
	}

	public void setRequestorUserId(String requestorUserId) {
		this.requestorUserId = requestorUserId;
	}

	public String getRequestorLanguage() {
		return requestorLanguage;
	}

	public void setRequestorLanguage(String requestorLanguage) {
		this.requestorLanguage = requestorLanguage;
	}

	public String getRequestorSecurityInfo() {
		return requestorSecurityInfo;
	}

	public void setRequestorSecurityInfo(String requestorSecurityInfo) {
		this.requestorSecurityInfo = requestorSecurityInfo;
	}

	public String getEaiReference() {
		return eaiReference;
	}

	public void setEaiReference(String eaiReference) {
		this.eaiReference = eaiReference;
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

	public String getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getErrorMessage() {
		return "Unable to process the request. Below is the response received from Host:\n" + this.returnCode + " - " + (this.returnText == null ? "NO ERROR MESSAGE" : this.returnText);
	}
}
